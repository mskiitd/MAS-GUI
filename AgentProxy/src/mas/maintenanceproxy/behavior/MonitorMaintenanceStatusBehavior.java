package mas.maintenanceproxy.behavior;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.Timer;
import java.util.TimerTask;

import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import mas.maintenanceproxy.classes.MaintStatus;
import mas.maintenanceproxy.classes.MaintenanceResponse;
import mas.maintenanceproxy.classes.PMaintenance;
import mas.maintenanceproxy.gui.MaintenanceGUI;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;

public class MonitorMaintenanceStatusBehavior extends Behaviour{

	private static final long serialVersionUID = 1L;

	private int step = 0;
	private String maintId;
	private MessageTemplate pmConfirmation;
	private ACLMessage msg;
	private boolean done = false;
	private PMaintenance prevMaint;
	private MaintenanceGUI gui;
	private StopWatch timeStopWatch;
	private int ticks = 0;
	private int interval = 1000;
	private BeliefBase bfBase;
	private AID blackboard;
	private Logger log;

	public MonitorMaintenanceStatusBehavior(PMaintenance maintenance, BeliefBase bbase) {
		this.maintId = maintenance.getMaintId();
		this.bfBase = bbase;
		this.log = LogManager.getLogger();

		this.gui = (MaintenanceGUI) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.gui_maintenance).
				getValue();

		this.blackboard = (AID) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.blackboardAgentAID).
				getValue();

		this.pmConfirmation = MessageTemplate.and(
				MessageTemplate.MatchConversationId(MessageIds.msgMaintConfirmationLSA),
				MessageTemplate.MatchReplyWith(maintId) );

		timeStopWatch = new StopWatch();
		timeStopWatch.start();

		new Timer().schedule(counter, 0, interval);
	}

	TimerTask counter = new TimerTask() {
		@Override
		public void run() {
			if(timeStopWatch.getTime() > LocalMaintenanceAgent.warningPeriod) {

				ticks ++;
				if(gui != null) {
					ZoneDataUpdate maintResponse = null;
					MaintenanceResponse reponse = new MaintenanceResponse(ticks);
					switch(ticks) {
					case 1:
						reponse.setMsg("Maintenance is pending. First window has passed.");
						maintResponse = new ZoneDataUpdate.Builder(
								ID.Maintenance.ZoneData.machineStatus).
								value(reponse).
								Build();
						break;
					case 2:
						reponse.setMsg("Maintenance is pending. Second window has passed.");
						maintResponse = new ZoneDataUpdate.Builder(
								ID.Maintenance.ZoneData.machineStatus).
								value(reponse).
								Build();
						break;
					case 3:
						reponse.setMsg("Perform Maintenance first.");
						maintResponse = new ZoneDataUpdate.Builder(
								ID.Maintenance.ZoneData.machineStatus).
								value(reponse).
								Build();
						
						done = true;
						counter.cancel();
						break;
					}

					AgentUtil.sendZoneDataUpdate(blackboard ,maintResponse, myAgent);
				}
				timeStopWatch.reset();
				timeStopWatch.start();
			}
		}
	};

	@Override
	public void action() {
		switch(step) {

		case 0:
			msg = myAgent.receive(pmConfirmation);
			if(msg != null) {
				try {
					prevMaint = (PMaintenance) msg.getContentObject();

					if(prevMaint.getMaintStatus() == MaintStatus.COMPLETE) {
						if(gui != null) {
							log.info("Maintenance done");
							gui.addMaintJobToDisplay(prevMaint);
						}

						done = true;
					} else if( prevMaint.getMaintStatus() == MaintStatus.UNDER_MAINTENANCE) {
						step = 1;
						log.info("Under maintenance");
					}
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
			else {
				block();
			}
			break;

		case 1:
			// stop monitoring time as the maintenance has started for the machine
			timeStopWatch.stop();
			step = 0;
			break;
		}
	}

	@Override
	public boolean done() {
		return done ;
	}

}
