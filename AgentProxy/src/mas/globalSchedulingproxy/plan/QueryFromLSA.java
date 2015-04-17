package mas.globalSchedulingproxy.plan;

import java.awt.TrayIcon.MessageType;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.globalSchedulingproxy.goal.QueryJobGoal;
import mas.globalSchedulingproxy.gui.GSAproxyGUI;
import mas.globalSchedulingproxy.gui.WebLafGSA;
import mas.jobproxy.Batch;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.JobQueryObject;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	private int repliesCnt=0;
	private Batch queryJob;
	private ACLMessage[] LSAqueryResponse;
	private BeliefBase bfBase;
	private Logger log = LogManager.getLogger();

	private String requestType;

	private WebLafGSA weblafGSAgui;


	@Override
	public EndState getEndState() {
		return (step >= 3 ? EndState.SUCCESSFUL : null);
	}

	@Override
	public void init(PlanInstance PI) {

		bfBase = PI.getBeliefBase();
		QueryJobGoal queryGoal= ((QueryJobGoal)(PI.getGoal()));
		this.queryJob=	queryGoal.getBatchToQuery();
		requestType=queryGoal.getQueryType();
		blackboard_AID = new AID(ID.Blackboard.LocalName, false);
		mt = MessageTemplate.MatchConversationId(MessageIds.msgLSAQueryResponse);

		weblafGSAgui=(WebLafGSA)bfBase.getBelief(ID.GlobalScheduler.BeliefBaseConst
				.GSA_GUI_instance).getValue();
	}

	@Override
	public void action() {

		switch(step) {
		case 0:
			this.MachineCount = (int) bfBase.getBelief(ID.GlobalScheduler.BeliefBaseConst.NoOfMachines).
			getValue();

			if (MachineCount != 0) {

				JobQueryObject queryForm = new JobQueryObject.Builder().
						currentBatch(this.queryJob).requestType(requestType).build();

				LSAqueryResponse=new ACLMessage[MachineCount];
				ZoneDataUpdate QueryRequest = new ZoneDataUpdate.
						Builder(ID.GlobalScheduler.ZoneData.QueryRequest).
						value(queryForm).
						Build();
				log.info("sent query");
				AgentUtil.sendZoneDataUpdate(blackboard_AID, QueryRequest, myAgent);
				step = 1;
			}

		case 1:
			try {
				ACLMessage reply = myAgent.receive(mt);
				//				log.info("recieved"+reply);
				if (reply != null) {
					LSAqueryResponse[repliesCnt] = reply;
					repliesCnt++;
					if (repliesCnt == MachineCount) {
						step = 2;
						repliesCnt=0;
						//						log.info("got replies");
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
			
			if(response!=null){
				switch(response.getType()) {
				
				case ID.GlobalScheduler.requestType.currentStatus:
					GSAproxyGUI.showQueryResult(response);
					break;
	
				case ID.GlobalScheduler.requestType.cancelBatch:
					weblafGSAgui.cancelBatchUnderProcess(response.getCurrentBatch());
	
					WebLafGSA.showNotification("Batch cancelled","Batch No. "+response.getCurrentBatch().getBatchNumber()+
							" cancelled",MessageType.WARNING );
	
	
					break;
	
				case ID.GlobalScheduler.requestType.changeDueDate:
					weblafGSAgui.cancelBatchUnderProcess(response.getCurrentBatch());
					WebLafGSA.showNotification("Request","Batch No. "+response.getCurrentBatch().getBatchNumber()+
							" requested for due date change",MessageType.INFO );
	
					ZoneDataUpdate dueDateRequest=new ZoneDataUpdate.Builder(ID.GlobalScheduler.ZoneData.
							dueDateChangeBatches).value(response.getCurrentBatch()).Build();
					AgentUtil.sendZoneDataUpdate(blackboard_AID, dueDateRequest, myAgent);
					break;	
				}
			}
			else{
				log.info("ERROR : No machine had queried job");
			}
			step = 3;
			break;
		}
	}

	private JobQueryObject getQueryResponse(ACLMessage[] LSAqueryResponse2) {

		JobQueryObject response = null;

		for(int i = 0; i < LSAqueryResponse2.length; i++) {
			try {
				JobQueryObject queryResponse = (JobQueryObject) LSAqueryResponse2[i].getContentObject();
				Batch j = (queryResponse).getCurrentBatch();
				if(j != null) {
					response = queryResponse;
					if(queryResponse.isOnMachine()) {
						log.info(j.getBatchNumber() + " is currently under process at " +
								queryResponse.getCurrentMachine().getLocalName());
					}
					else{
						log.info(j.getBatchNumber() + " is currently in queue of machine " +
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
