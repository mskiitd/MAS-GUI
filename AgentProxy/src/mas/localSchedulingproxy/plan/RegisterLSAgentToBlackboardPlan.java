package mas.localSchedulingproxy.plan;

import jade.core.behaviours.OneShotBehaviour;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mas.blackboard.nameZoneData.NamedZoneData;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class RegisterLSAgentToBlackboardPlan extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private Logger log;
	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance planInstance) {
		log = LogManager.getLogger();
	}

	@Override
	public void action() {

		NamedZoneData ZoneDataName1 = 
				new NamedZoneData.Builder(ID.LocalScheduler.ZoneData.bidForJob).
				MsgID(MessageIds.msgbidForJob).
				appendValue(true).
				build();

		NamedZoneData ZoneDataName2 = 
				new NamedZoneData.Builder(ID.LocalScheduler.ZoneData.machineJobQueue).
				MsgID(MessageIds.msgmachineJobQueue).
				appendValue(false).
				build();

		NamedZoneData ZoneDataName3 = 
				new NamedZoneData.Builder(ID.LocalScheduler.ZoneData.WaitingTime).
				MsgID(MessageIds.msgWaitingTime).
				appendValue(true).
				build();

		NamedZoneData ZoneDataName4 = 
				new NamedZoneData.Builder(ID.LocalScheduler.ZoneData.batchForMachine).
				MsgID(MessageIds.msgbatchForMachine).
				appendValue(false).
				build();

		NamedZoneData ZoneDataName5 = 
				new NamedZoneData.Builder(ID.LocalScheduler.ZoneData.finishedBatch)
		.MsgID(MessageIds.msgLSAfinishedJobs).appendValue(false)
		.build();

		NamedZoneData ZoneDataName6 =
				new NamedZoneData.Builder(ID.LocalScheduler.ZoneData.QueryResponse).
				MsgID(MessageIds.msgLSAQueryResponse)
				.appendValue(false).
				build();

		NamedZoneData ZoneDataName7 =
				new NamedZoneData.Builder(ID.LocalScheduler.ZoneData.gui_machine).
				MsgID(MessageIds.msgGuiMachine)
				.appendValue(false).
				build();

		NamedZoneData ZoneDataName8 =
				new NamedZoneData.Builder(ID.LocalScheduler.ZoneData.maintenanceJobForMachine).
				MsgID(MessageIds.msgMaintenanceJobForMachine)
				.appendValue(false).
				build();

		NamedZoneData ZoneDataName9 =
				new NamedZoneData.Builder(ID.LocalScheduler.ZoneData.MaintConfirmationLSA).
				MsgID(MessageIds.msgMaintConfirmationLSA).
				appendValue(false).
				build();

		NamedZoneData[] ZoneDataNames =  { ZoneDataName1,
				ZoneDataName2, ZoneDataName3, ZoneDataName4, ZoneDataName5, ZoneDataName6, ZoneDataName7,
				ZoneDataName8 , ZoneDataName9};

		AgentUtil.makeZoneBB(myAgent,ZoneDataNames);
	}
}
