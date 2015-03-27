package mas.machineproxy.behaviors;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.StringTokenizer;

import mas.jobproxy.job;
import mas.machineproxy.MachineStatus;
import mas.machineproxy.Simulator;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HandleInspectionJobBehavior extends Behaviour{

	private static final long serialVersionUID = 1L;
	private job comingJob;
	boolean IsJobComplete;
	private Logger log;
	private int step = 0;
	private MessageTemplate InspectionDataMsgTemplate;
	private ACLMessage inspectioneDataMsg;
	private String inspectionData;
	private StringTokenizer token;
	private Simulator machineSimulator;

	public HandleInspectionJobBehavior(job comingJob) {

		this.comingJob = comingJob;
		this.IsJobComplete = false;
		log = LogManager.getLogger();
		InspectionDataMsgTemplate = MessageTemplate.MatchConversationId(
				MessageIds.msginspectionJobData);
		machineSimulator = null;
	}

	@Override
	public void action() {
		switch(step){
		case 0:
			if(machineSimulator == null) {
				machineSimulator = (Simulator) getDataStore().
						get(Simulator.simulatorStoreName);
			}
			
			ZoneDataUpdate inspectionZoneUpdate = new ZoneDataUpdate.Builder(ID.Machine.ZoneData.inspectionStart)
				.value(comingJob).Build();
			/*ZoneDataUpdate inspectionZoneUpdate = new ZoneDataUpdate(
					ID.Machine.ZoneData.inspectionStart,
					comingJob);*/

			AgentUtil.sendZoneDataUpdate(Simulator.blackboardAgent ,
					inspectionZoneUpdate, myAgent);

			log.info("recieving inspection data for machine");
			machineSimulator.setStatus(MachineStatus.UNDER_MAINTENANCE);
			step = 1;
			break;

		case 1:
			inspectioneDataMsg = myAgent.receive(InspectionDataMsgTemplate);

			if(inspectioneDataMsg != null) {

				inspectionData = inspectioneDataMsg.getContent();
				token = new StringTokenizer(inspectionData);
				long procTime = Long.parseLong(token.nextToken());	
				comingJob.setCurrentOperationProcessingTime(procTime);
				step ++;
				log.info("Starting inspection of machine");
			}
			else
				block();
			break;
		}
	}

	@Override
	public boolean done() {
		return step >= 2;
	}
}
