package mas.globalSchedulingproxy.plan;

/**
 * When customer places order for first time,
 * this plan triggers plan of asking waiting plans from Local Scheduling agent
 **/

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.Date;

import mas.globalSchedulingproxy.agent.GlobalSchedulingAgent;
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

public class RootTakeOrderAndRaiseBid extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;

	private Logger log;
	private AID blackboard;
	private BeliefBase bfBase;
	private int NoOfMachines;
	private String msgReplyID;
	private MessageTemplate mt;
	private int step = 0;
	private int MachineCount;
	private ACLMessage[] LSAbids;

	// The counter of replies from seller agents
	private int repliesCnt = 0; 
	private job order;
	private String dueDateMethod=null;

	public void init(PlanInstance PI) {
		log = LogManager.getLogger();

		log.info("triggered by " + ((MessageGoal) PI.getGoal()).getMessage().getSender().getLocalName());

		bfBase = PI.getBeliefBase();

		dueDateMethod = (String)bfBase.getBelief(ID.GlobalScheduler.BeliefBaseConst.DueDateCalcMethod).
				getValue();

		try {
			order = (job) ((MessageGoal) PI.getGoal()).getMessage()
					.getContentObject();

		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		blackboard = (AID) bfBase.getBelief(ID.GlobalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();

		msgReplyID = Integer.toString(order.getJobNo());

		mt = MessageTemplate.and(MessageTemplate.MatchConversationId(MessageIds.msgbidForJob)
				, MessageTemplate.MatchReplyWith(msgReplyID));

		if(GlobalSchedulingAgent.mygui != null) {
			GlobalSchedulingAgent.mygui.addAcceptedJobToList(order);
		}
	}

	@Override
	public void action() {

		switch (step) {
		case 0:

			this.MachineCount = (int) bfBase.getBelief(ID.GlobalScheduler.BeliefBaseConst.NoOfMachines).
			getValue();

			if (MachineCount != 0) {

				//				log.info("due date: "+order.getDuedate());

				order.setJobStartTimeByCust(new Date(System.currentTimeMillis()));
				order = SetDueDates(order);

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
				log.info(JobForBidWinner.getLSABidder().getLocalName()+" won bid with " + 
						JobForBidWinner.getBidByLSA());

				ZoneDataUpdate jobForLSAUpdate = new ZoneDataUpdate.
						Builder(ID.GlobalScheduler.ZoneData.jobForLSA).
						value(JobForBidWinner).
						setReplyWith(msgReplyID).
						Build();

				AgentUtil.sendZoneDataUpdate(blackboard, jobForLSAUpdate,
						myAgent);

			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			step = 3;
			break;
		}
	}

	private job SetDueDates(job jobForBidWinner) {
		long totalProcessingTime = jobForBidWinner.getTotalProcessingTime();
		long totalAvailableTime = jobForBidWinner.getJobDuedatebyCust().getTime()
				-jobForBidWinner.getStartTimeByCust().getTime();

		long slack = totalAvailableTime-totalProcessingTime;
		int NoOfOps = jobForBidWinner.getOperations().size();
		long currTime = jobForBidWinner.getStartTimeByCust().getTime();

		//		log.info("due date "+new Date(jobForBidWinner.getJobDuedatebyCust().getTime())+
		//				" start time "+new Date(jobForBidWinner.getStartTimeByCust().getTime()));

		if(dueDateMethod == ID.GlobalScheduler.OtherConst.LocalDueDate){
			long slack_perOperation=(long)((double)slack)/(NoOfOps);

			for(int i=0 ;i < NoOfOps; i++){
				jobForBidWinner.getOperations().get(i).setStartTime(currTime);
				currTime = currTime + jobForBidWinner.getOperations().get(i).
						getProcessingTime() + slack_perOperation;

				jobForBidWinner.getOperations().get(i).setDueDate(currTime);
			}


		}
		else if(dueDateMethod == ID.GlobalScheduler.OtherConst.GlobalDueDate) {
			for(int i=0; i  <NoOfOps; i++) {
				jobForBidWinner.getOperations().get(i).setStartTime(currTime);
				currTime = currTime + jobForBidWinner.getOperations().get(i).getProcessingTime();
				if(i == NoOfOps-1){
					currTime = currTime + slack;
				}
				jobForBidWinner.getOperations().get(i).setDueDate(currTime);
			}
		}
		//		log.info("Job No "+jobForBidWinner.getJobNo()+" processing times:");
		return jobForBidWinner;

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
		return (step >= 3);
	}

	public EndState getEndState() {
		if (step >= 3) {
			return EndState.SUCCESSFUL;
		} else {
			return null;
		}
	}

}
