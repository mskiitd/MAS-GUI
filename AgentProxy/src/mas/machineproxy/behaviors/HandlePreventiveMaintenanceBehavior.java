package mas.machineproxy.behaviors;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import mas.jobproxy.job;
import mas.machineproxy.MachineStatus;
import mas.machineproxy.Simulator;
import mas.machineproxy.behaviors.AddJobBehavior.timeProcessing;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Anand Prajapati
 *
 */

public class HandlePreventiveMaintenanceBehavior extends Behaviour{

	private static final long serialVersionUID = 1L;
	private job comingJob;
	private Logger log;
	private int step = 0;
	private MessageTemplate pmDataMsgTemplate;
	private ACLMessage maintenanceDataMsg;
	private String maintData;
	private Simulator machineSimulator;
	private int remainingMaintenanceTime;

	private ScheduledThreadPoolExecutor executor;

	public HandlePreventiveMaintenanceBehavior(job comingJob) {
		this.comingJob = comingJob;
		log = LogManager.getLogger();
		pmDataMsgTemplate = MessageTemplate.MatchConversationId(
				MessageIds.msgprevMaintData);
		machineSimulator = null;
	}

	@Override
	public void action() {
		switch(step) {
		case 0 :

			if(machineSimulator == null) { 
				machineSimulator = (Simulator) getDataStore().
						get(Simulator.simulatorStoreName);
			}

			ZoneDataUpdate maintenanceStartUpdate = new ZoneDataUpdate.
					Builder(ID.Machine.ZoneData.maintenanceStart).
					value(comingJob).Build();

			AgentUtil.sendZoneDataUpdate(Simulator.blackboardAgent ,
					maintenanceStartUpdate, myAgent);

			machineSimulator.setStatus(MachineStatus.UNDER_MAINTENANCE);
			step = 1;
			break;

		case 1:
			maintenanceDataMsg = myAgent.receive(pmDataMsgTemplate);
			if(maintenanceDataMsg != null) {
				// parse the received data and perform maintenance of the machine
				log.info("Maintenenace data arrived");
				try {
					maintData = (String) maintenanceDataMsg.getContentObject();
					remainingMaintenanceTime = (int) Double.parseDouble(maintData);
					step = 2;

					if( remainingMaintenanceTime > 0 ) {
						executor = new ScheduledThreadPoolExecutor(1);
						executor.scheduleAtFixedRate(new timeProcessing(), 0,
								Simulator.TIME_STEP, TimeUnit.MILLISECONDS);
						step = 2;
					}
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
			else {
				block();
			}
			break;

		case 2:
			// block for some time in order to avoid too much CPU usage
			// this won't affect working of the behavior however
			block(15);
			break;
			
		case 3:
			/**
			 * perform the maintenance for the machine now
			 */
			machineSimulator.repair();
			step = 4;
			break;
		}
	}

	@Override
	public boolean done() {
		return step >= 4;
	}

	class timeProcessing implements Runnable {

		@Override
		public void run() {
			/**
			 * If machine is failed it won't do anything.
			 * Executor will just keep scheduling this task
			 */
			//			log.info("remProcessingTime="+processingTime);
			if( remainingMaintenanceTime > 0 ) {
				remainingMaintenanceTime = remainingMaintenanceTime - Simulator.TIME_STEP; 
			} else if( remainingMaintenanceTime <= 0 ) {
				step = 3;
				executor.shutdown();
			} 
		}
	}
}
