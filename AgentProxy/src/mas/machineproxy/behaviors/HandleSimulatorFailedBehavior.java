package mas.machineproxy.behaviors;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import mas.machineproxy.Simulator;
import mas.machineproxy.SimulatorInternals;
import mas.machineproxy.gui.MachineGUI;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HandleSimulatorFailedBehavior extends Behaviour{

	private static final long serialVersionUID = 1L;
	private Logger log;
	private int step = 0;
	private MessageTemplate correctiveDataMsgTemplate;
	private ACLMessage correctiveDataMsg;
	private String repairData;
	private long repairTime;

	private SimulatorInternals machineInternals;
	private MachineGUI gui;

	public HandleSimulatorFailedBehavior(Simulator sim,
			SimulatorInternals internals) {

		log = LogManager.getLogger();
		this.machineInternals = internals;
		this.gui = sim.getGui();

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
			gui.machineFailed();
			log.info("******************** failure machine  : "  + machineInternals);
			ZoneDataUpdate machineFailureUpdate  = new ZoneDataUpdate.
					Builder(ID.Machine.ZoneData.machineFailures).
					value(machineInternals).
					Build();

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

				String data;
				try {
					data = (String) correctiveDataMsg.getContentObject();
					repairData = data;
					log.info("Maintenance arrived ~~~~ repair time " + repairData);
					repairTime = (long) Double.parseDouble(repairData);
					step = 3;
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
			else{
				block();
			}
			break;

		case 2:
			// block for some time in order to avoid too much CPU usage
			// this won't affect working of the behavior however
			block(15);

		case 3:
			gui.enableRepair();
			//			machineSimulator.repair();
			//			gui.machineIdle();
			step = 4;
			break;
		}
	}

	@Override
	public boolean done() {
		return step > 3;
	}
}
