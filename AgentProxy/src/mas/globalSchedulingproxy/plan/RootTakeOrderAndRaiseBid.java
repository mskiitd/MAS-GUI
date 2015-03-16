package mas.globalSchedulingproxy.plan;

/*When customer places order for first time, this plan triggers plan of asking waiting plans from Local Scheduling agent
 * */

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.globalSchedulingproxy.agent.GlobalSchedulingAgent;
import mas.job.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BDIAgent;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class RootTakeOrderAndRaiseBid extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private Logger log;
	private AID blackboard;
	private int NoOfMachines;
	private String msgReplyID;
	private MessageTemplate mt;
	private int step = 0;
	private int MachineCount;
	private ACLMessage[] LSAbids;
	private int repliesCnt = 0; // The counter of replies from seller agents
	private job order;

	public void init(PlanInstance PI) {
		log = LogManager.getLogger();

		try {
			order = (job) ((MessageGoal) PI.getGoal()).getMessage()
					.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		blackboard = (AID) PI.getBeliefBase()
				.getBelief(ID.Blackboard.LocalName).getValue();

		msgReplyID = Integer.toString(order.getJobNo());

		mt = MessageTemplate.MatchConversationId(MessageIds.msgbidForJob);
	}

	@Override
	public void action() {

		switch (step) {
		case 0:

			this.MachineCount = (int) ((BDIAgent) myAgent).getRootCapability()
			.getBeliefBase()
			.getBelief(ID.Blackboard.BeliefBaseConst.NoOfMachines)
			.getValue();
			// log.info(MachineCount);

			if (MachineCount != 0) {
				//				log.info("due date: "+order.getDuedate());
				ZoneDataUpdate zdu = new ZoneDataUpdate(
						ID.GlobalScheduler.ZoneData.askBidForJobFromLSA, order);
				AgentUtil.sendZoneDataUpdate(blackboard, zdu, myAgent);

				LSAbids = new ACLMessage[MachineCount];
				step = 1;
				// log.info("mt="+mt);
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
			// log.info("step="+step);
			try {

				ACLMessage BestBid = ChooseBid(LSAbids);
				job JobForBidWinner = (job) (BestBid.getContentObject());
				JobForBidWinner.setBidWinnerLSA(JobForBidWinner.getLSABidder());
				log.info(JobForBidWinner.getLSABidder().getLocalName()+" won bid with "+JobForBidWinner.getBidByLSA());
				ZoneDataUpdate NegotiationUpdate = new ZoneDataUpdate(
						ID.GlobalScheduler.ZoneData.jobForLSA, JobForBidWinner);

				AgentUtil.sendZoneDataUpdate(blackboard, NegotiationUpdate,
						myAgent);
				/**
				 * job-bidder has been chosen. Now this job should be shown in the list of
				 * accepted jobs
				 */
				GlobalSchedulingAgent.addConfirmedJob(JobForBidWinner);

			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			step = 3;
			break;
		}
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

	@Override
	public boolean done() {
		return (step == 3);
	}

	public EndState getEndState() {
		if (step == 3) {
			return EndState.SUCCESSFUL;
		} else {
			return EndState.FAILED;
		}
	}

}
