package mas.globalSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.util.Date;
import mas.jobproxy.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class HandleCompletedOrderbyLSAPlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;

	private Logger log;
	private AID blackboard;
	private int NoOfMachines;
	private String msgReplyID;
	private MessageTemplate mt;
	private int step = 0;
	private int MachineCount;
	private ACLMessage[] LSAbids;
	private BeliefBase bfBase;

	// The counter of replies from seller agents
	private int repliesCnt = 0; 
	private job order;
	private String dueDateMethod = null;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance PI) {
		log = LogManager.getLogger();
		bfBase = PI.getBeliefBase();

		ACLMessage msg = ((MessageGoal) PI.getGoal()).getMessage();
		try {
			order = (job) (msg.getContentObject());
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		blackboard = (AID) bfBase.getBelief(ID.GlobalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();

		msgReplyID = msg.getReplyWith();

		mt = MessageTemplate.and(MessageTemplate.MatchConversationId(MessageIds.msgbidForJob),
				MessageTemplate.MatchReplyWith(msgReplyID));
	}

	@Override
	public void action() {

		if(order.isComplete()){
			step = 3;
		}
		
		switch (step) {
		case 0:
			this.MachineCount = (int) bfBase.getBelief(ID.GlobalScheduler.BeliefBaseConst.NoOfMachines).
			getValue();

			if (MachineCount != 0) {
				//				log.info("due date: "+order.getDuedate());
				order.setJobStartTimeByCust(new Date(System.currentTimeMillis()));

				ZoneDataUpdate zdu = new ZoneDataUpdate.
						Builder(ID.GlobalScheduler.ZoneData.askBidForJobFromLSA).
						value(order).
						setReplyWith(msgReplyID).
						Build();

				AgentUtil.sendZoneDataUpdate(blackboard, zdu, myAgent);

				LSAbids = new ACLMessage[MachineCount];
				step = 1;
			}

			break;
		case 1:
			try {

				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					LSAbids[repliesCnt] = reply;
					repliesCnt++;

					if (repliesCnt == MachineCount) {
						step = 2;
					}
				}
				else {
					block();
				}
			} catch (Exception e3) {

			}
			break;
		case 2:
			try {

				ACLMessage BestBid = ChooseBid(LSAbids);
				job JobForBidWinner = (job) (BestBid.getContentObject());
				JobForBidWinner.setBidWinnerLSA(JobForBidWinner.getLSABidder());

				log.info(JobForBidWinner.getLSABidder().getLocalName() + " won bid with " + 
						JobForBidWinner.getBidByLSA());

				ZoneDataUpdate jobForLSAUpdate = new ZoneDataUpdate.
						Builder(ID.GlobalScheduler.ZoneData.jobForLSA).
						value(JobForBidWinner).
						setReplyWith(msgReplyID).
						Build();

				AgentUtil.sendZoneDataUpdate(blackboard, jobForLSAUpdate,myAgent);
				
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			step = 3;
			break;

		case 3:
			ZoneDataUpdate jobCompletionNotification = new ZoneDataUpdate.
			Builder(ID.GlobalScheduler.ZoneData.completedJobByGSA).setReplyWith(msgReplyID).
			value(order).Build();

			step = 4;
			log.info("all operations of " + order.getJobNo() + " completed");
			break;
		}
	}

	@Override
	public boolean done() {
		return step >= 3;
	}


	private ACLMessage ChooseBid(ACLMessage[] LSA_bids) {
		ACLMessage MinBid = LSA_bids[0];
		for (int i = 0; i < LSA_bids.length; i++) {
			try {
				log.info(((job) (LSA_bids[i].getContentObject())).getLSABidder().getLocalName() +" sent bid= "+ ((job) (LSA_bids[i].getContentObject())).getBidByLSA());

				if (((job) (LSA_bids[i].getContentObject())).getBidByLSA() < ((job) (MinBid
						.getContentObject())).getBidByLSA()) {
					MinBid = LSA_bids[i];
				}
			} catch (UnreadableException e) {
				e.printStackTrace();
			}

		}
		return MinBid;

	}
}
