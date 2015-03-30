package mas.globalSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.globalSchedulingproxy.agent.GlobalSchedulingAgent;
import mas.globalSchedulingproxy.gui.GSAproxyGUI;
import mas.jobproxy.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.JobQueryObject;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;

import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class QueryFromLSA extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;

	private AID blackboard_AID;
	private int step = 0;
	private MessageTemplate mt;
	private int MachineCount = 0;
	private int repliesCnt;
	private job queryJob;
	private ACLMessage[] LSAqueryResponse;
	private BeliefBase bfBase;
	private Logger log = LogManager.getLogger();

	@Override
	public EndState getEndState() {
		return (step >= 3 ? EndState.SUCCESSFUL : null);
	}

	@Override
	public void init(PlanInstance PI) {

		bfBase = PI.getBeliefBase();
		this.queryJob = (job) bfBase.getBelief(ID.GlobalScheduler.BeliefBaseConst.GSAqueryJob).
				getValue();

		blackboard_AID = new AID(ID.Blackboard.LocalName, false);
		mt = MessageTemplate.MatchConversationId(MessageIds.msgLSQueryResponse);
	}

	@Override
	public void action() {

		switch(step) {
		case 0:
			this.MachineCount = (int) bfBase.getBelief(ID.GlobalScheduler.BeliefBaseConst.NoOfMachines).
			getValue();

			if (MachineCount != 0) {
				JobQueryObject queryForm = new JobQueryObject.Builder().
						currentJob(this.queryJob).build();

				ZoneDataUpdate QueryRequest = new ZoneDataUpdate.
						Builder(ID.GlobalScheduler.ZoneData.QueryRequest).
						value(queryForm).
						Build();

				AgentUtil.sendZoneDataUpdate(blackboard_AID, QueryRequest, myAgent);
				step = 1;
			}

		case 1:
			try {
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					LSAqueryResponse[repliesCnt] = reply;
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
			JobQueryObject response = getQueryResponse(LSAqueryResponse);
			log.info(response);
			GSAproxyGUI.showQueryResult(response);
			step = 3;
			break;
		}
	}

	private JobQueryObject getQueryResponse(ACLMessage[] LSAqueryResponse2) {

		JobQueryObject response = null;
		
		for(int i = 0; i < LSAqueryResponse2.length; i++) {
			try {
				JobQueryObject queryResponse = (JobQueryObject) LSAqueryResponse2[i].getContentObject();
				job j = (queryResponse).getCurrentJob();
				if(j != null) {
					response = queryResponse;
					log.info(j.getJobNo() + " is at " + queryResponse.getCurrentMachine().getLocalName());
					if(queryResponse.isOnMachine()) {
						log.info(j.getJobNo() + " is currently under process at " +
								queryResponse.getCurrentMachine().getLocalName());
					}
				}
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	@Override
	public boolean done() {
		return step >= 3;
	}

}
