package mas.customerproxy.plan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import mas.blackboard.nameZoneData.NamedZoneData;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.SubscriptionForm;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class RegisterCustomerAgentToBlackboardPlan extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private Logger log;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance planInstance) {
		log=LogManager.getLogger();
	}

	@Override
	public void action() {
		
		AID bb_aid = AgentUtil.findBlackboardAgent(myAgent);

		NamedZoneData ZoneDataName1 = 
				new NamedZoneData.Builder(ID.Customer.ZoneData.customerConfirmedJobs).
				MsgID(MessageIds.msgcustomerConfirmedJobs).
				appendValue(false).
				build();

		NamedZoneData ZoneDataName2 = 
				new NamedZoneData.Builder(ID.Customer.ZoneData.newWorkOrderFromCustomer).
				MsgID(MessageIds.msgnewWorkOrderFromCustomer).
				appendValue(false).
				build();

		NamedZoneData ZoneDataName3 = 
				new NamedZoneData.Builder(ID.Customer.ZoneData.customerJobsUnderNegotiation).
				MsgID(MessageIds.msgcustomerJobsUnderNegotiation).
				appendValue(false).
				build();
		
		NamedZoneData ZoneDataName4 = 
				new NamedZoneData.Builder(ID.Customer.ZoneData.customerCanceledOrders).
				MsgID(MessageIds.msgcustomerCanceledOrders).
				appendValue(false).
				build();
		
		NamedZoneData ZoneDataName5 = 
				new NamedZoneData.Builder(ID.Customer.ZoneData.customerChangeDDorders).
				MsgID(MessageIds.msgcustomerChangeDDorders).
				appendValue(false).
				build();

		NamedZoneData[] ZoneDataNames =  { ZoneDataName1,
				ZoneDataName2,ZoneDataName3, ZoneDataName4, ZoneDataName5 };

		
		AgentUtil.makeZoneBB(myAgent,ZoneDataNames);

		SubscriptionForm subform = new SubscriptionForm();
		AID target = new AID(ID.GlobalScheduler.LocalName, AID.ISLOCALNAME);
		
		String[] params = { ID.GlobalScheduler.ZoneData.GSAjobsUnderNegaotiation,
				ID.GlobalScheduler.ZoneData.GSAConfirmedOrder };

		subform.AddSubscriptionReq(target, params);

		AgentUtil.subscribeToParam(myAgent, bb_aid, subform);
	}
}
