package mas.machineproxy.behaviors;

import java.util.ArrayList;
import java.util.StringTokenizer;

import mas.machineproxy.Simulator;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class HandleSimulatorFailedBehavior extends Behaviour{

	private static final long serialVersionUID = 1L;
	private Logger log;
	private int step = 0;
	private MessageTemplate correctiveDataMsgTemplate;
	private ACLMessage correctiveDataMsg;
	private StringTokenizer token;
	private long repairTime;
	private ArrayList<Integer> componentsToRepair;
	private Simulator machineSimulator;
	private long remaintingTimeMillis;

	public HandleSimulatorFailedBehavior() {
		log = LogManager.getLogger();
		this.machineSimulator = (Simulator) getParent().
				getDataStore().get(Simulator.simulatorStoreName);
		correctiveDataMsgTemplate = MessageTemplate.MatchConversationId(
				MessageIds.msgcorrectiveMaintdata);
	}

	@Override
	public void action() {
		switch(step) {
		case 0:

			/**
			 * update zone data for machine's failure
			 */
			ZoneDataUpdate machineFailureUpdate = new ZoneDataUpdate(
					ID.Machine.ZoneData.myHealth,
					machineSimulator);

			AgentUtil.sendZoneDataUpdate(Simulator.blackboardAgent ,
					machineFailureUpdate, myAgent);
			step = 1;

			break;
		case 1:
			/**
			 * receive data for repairing the simulator
			 */
			correctiveDataMsg = myAgent.receive(correctiveDataMsgTemplate);
			if(correctiveDataMsg != null) {

				token = new StringTokenizer(correctiveDataMsg.getContent());
				repairTime = Long.parseLong(token.nextToken());
				remaintingTimeMillis = repairTime;
//				block(repairTime);
				componentsToRepair = new ArrayList<Integer>();

				while(token.hasMoreTokens()) {
					componentsToRepair.add(Integer.parseInt(token.nextToken()));
				}
				step = 2;
			}
			else{
				block();
			}
			break;

		case 2:
			/**
			 * keep the machine blocked for the required repairing time
			 */
			if(remaintingTimeMillis >= 0){
				
				remaintingTimeMillis = remaintingTimeMillis - Simulator.TIME_STEP;
				block(Simulator.TIME_STEP);
				
			} else if( remaintingTimeMillis <= 0) {
				step = 3;
			}
		case 3:
			machineSimulator.repair(componentsToRepair);
			step = 4;
			break;
		}
	}

	@Override
	public boolean done() {
		return step > 3;
	}

}
