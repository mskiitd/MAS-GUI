package mas.machineproxy.behaviors;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import mas.blackboard.nameZoneData.NamedZoneData;
import mas.machineproxy.Simulator;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.SubscriptionForm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegisterMachine2BlackBoardBehvaior extends OneShotBehaviour{

	private static final long serialVersionUID = 1L;
	private Logger log;

	@Override
	public void action() {
		
		log = LogManager.getLogger();
		/**
		 * first find blackboard and store it's AID
		 */
		AID bb_aid = AgentUtil.findBlackboardAgent(myAgent);
		Simulator.blackboardAgent = bb_aid;
		log.info("blackboard is : " + Simulator.blackboardAgent);

		/**
		 *  Now create zones on blackboard where data of machine will be kept for
		 *  other agents to locate and receive 
		 */
		NamedZoneData ZoneDataName1 = 
				new NamedZoneData.Builder(ID.Machine.ZoneData.myHealth).
				MsgID(MessageIds.msgmyHealth).
				appendValue(false).
				build();

		NamedZoneData ZoneDataName2 = 
				new NamedZoneData.Builder(ID.Machine.ZoneData.finishedBatch).
				MsgID(MessageIds.msgfinishedBatch).
				appendValue(true).
				build();

		NamedZoneData ZoneDataName3 = 
				new NamedZoneData.Builder(ID.Machine.ZoneData.inspectionStart).
				MsgID(MessageIds.msginspectionStart).
				appendValue(false).
				build();
		
		NamedZoneData ZoneDataName4 = 
				new NamedZoneData.Builder(ID.Machine.ZoneData.maintenanceStart).
				MsgID(MessageIds.msgmaintenanceStart).
				appendValue(false).
				build();
		
		NamedZoneData ZoneDataName5 = 
				new NamedZoneData.Builder(ID.Machine.ZoneData.machineFailures).
				MsgID(MessageIds.msgmachineFailures).
				appendValue(false).
				build();
		
		NamedZoneData ZoneDataName6 =
				new NamedZoneData.Builder(ID.Machine.ZoneData.askJobFromLSA).
				MsgID(MessageIds.msgaskJobFromLSA).
				appendValue(false).
				build();
		
		NamedZoneData ZoneDataName7 =
				new NamedZoneData.Builder(ID.Machine.ZoneData.prevMaintConfirmation).
				MsgID(MessageIds.msgPrevMaintConfirmation).
				appendValue(false).
				build();

		NamedZoneData[] ZoneDataNames =  { ZoneDataName1, ZoneDataName2,
				ZoneDataName3, ZoneDataName4, ZoneDataName5, ZoneDataName6,
				ZoneDataName7 };

		AgentUtil.makeZoneBB(myAgent,ZoneDataNames);
	}
}
