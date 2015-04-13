package mas.globalSchedulingproxy.plan;

import java.io.IOException;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import mas.blackboard.nameZoneData.NamedZoneData;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class RegisterAgentToBlackboardPlan extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	int step;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance planInstance) {
		step = 0;
	}

	@Override
	public void action() {

		ACLMessage msg2 = new ACLMessage(ACLMessage.CFP);
		msg2.setConversationId(MessageIds.RegisterMe);

		NamedZoneData ZoneDataName1 = new NamedZoneData.Builder
				(ID.GlobalScheduler.ZoneData.GSAConfirmedOrder).
				MsgID(MessageIds.msgGSAConfirmedOrder).
				build();

		NamedZoneData ZoneDataName2 = new NamedZoneData.Builder(
				ID.GlobalScheduler.ZoneData.askBidForJobFromLSA).
				MsgID(MessageIds.msgaskBidForJobFromLSA).
				build();

		NamedZoneData ZoneDataName3 = new NamedZoneData.Builder(
				ID.GlobalScheduler.ZoneData.GetWaitingTime).
				MsgID(MessageIds.msgGetWaitingTime).
				build();

		NamedZoneData ZoneDataName4 = new NamedZoneData.Builder(
				ID.GlobalScheduler.ZoneData.GSAjobsUnderNegaotiation).
				MsgID(MessageIds.msgGSAjobsUnderNegaotiation).
				build();

		NamedZoneData ZoneDataName5 = new NamedZoneData.Builder(
				ID.GlobalScheduler.ZoneData.jobForLSA).
				MsgID(MessageIds.msgjobForLSA).
				build();

		NamedZoneData ZoneDataName6 = new NamedZoneData.Builder(
				ID.GlobalScheduler.ZoneData.QueryRequest).
				MsgID(MessageIds.msgGSAQuery).build();

		NamedZoneData ZoneDataName7 = new NamedZoneData.Builder(
				ID.GlobalScheduler.ZoneData.CallBackJobs).
				MsgID(MessageIds.msgCallBackReqByGSA).build();

		NamedZoneData ZoneDataName8 = new NamedZoneData.Builder(
				ID.GlobalScheduler.ZoneData.completedJobByGSA).
				MsgID(MessageIds.msgJobCompletion).build();

		NamedZoneData ZoneDataName9 = new NamedZoneData.Builder(
				ID.GlobalScheduler.ZoneData.dueDateChangeBatches).
				MsgID(MessageIds.msgChangeDueDate).build();

		NamedZoneData ZoneDataName10 = new NamedZoneData.Builder(
				ID.GlobalScheduler.ZoneData.rejectedOrders).
				MsgID(MessageIds.RejectedOrder).build();

		NamedZoneData[] ZoneDataNames={ZoneDataName1, ZoneDataName2,
				ZoneDataName3,ZoneDataName4,
				ZoneDataName5,ZoneDataName6, ZoneDataName7, ZoneDataName8 
				,ZoneDataName9, ZoneDataName10};
		try {
			msg2.setContentObject(ZoneDataNames);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		AgentUtil.makeZoneBB(myAgent,ZoneDataNames);
	}

}
