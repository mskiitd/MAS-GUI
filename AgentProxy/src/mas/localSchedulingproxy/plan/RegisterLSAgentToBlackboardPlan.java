package mas.localSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.blackboard.nameZoneData.NamedZoneData;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.SubscriptionForm;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class RegisterLSAgentToBlackboardPlan extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private int step;
	private Logger log=LogManager.getLogger();

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

		AID bb_aid = AgentUtil.findBlackboardAgent(myAgent);

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
				new NamedZoneData.Builder(ID.LocalScheduler.ZoneData.jobForMachine).
				MsgID(MessageIds.msgjobForMachine).
				appendValue(false).
				build();

		NamedZoneData[] ZoneDataNames =  { ZoneDataName1,
				ZoneDataName2, ZoneDataName3, ZoneDataName4 };

		AgentUtil.makeZoneBB(myAgent,ZoneDataNames);

		AID gSchedulingTarget = new AID(ID.GlobalScheduler.LocalName, AID.ISLOCALNAME);
		
		String suffix=myAgent.getLocalName().split("#")[1];

		AID simulatorTarget = new AID(ID.Machine.LocalName+"#"+suffix, AID.ISLOCALNAME);


		// subscription form for global scheduling agent
		log.info(myAgent.getLocalName()+" subscribing "+simulatorTarget.getLocalName());
		SubscriptionForm gSchedulingSubform = new SubscriptionForm();
		String[] gSchedulingParams = { ID.GlobalScheduler.ZoneData.askBidForJobFromLSA,
				ID.GlobalScheduler.ZoneData.GetWaitingTime , ID.GlobalScheduler.ZoneData.jobForLSA,
				ID.GlobalScheduler.ZoneData.GSAConfirmedOrder };
		gSchedulingSubform.AddSubscriptionReq(gSchedulingTarget, gSchedulingParams);

		AgentUtil.subscribeToParam(myAgent, bb_aid, gSchedulingSubform);

		// subscription form for simulator
		SubscriptionForm simulatorSubform = new SubscriptionForm();
		String[] simulatorParams = { ID.Machine.ZoneData.finishedJob,
				ID.Machine.ZoneData.askJobFromLSA, ID.Machine.ZoneData.askJobFromLSA };
		simulatorSubform.AddSubscriptionReq(simulatorTarget, simulatorParams);

		AgentUtil.subscribeToParam(myAgent, bb_aid, simulatorSubform);
	}
}
