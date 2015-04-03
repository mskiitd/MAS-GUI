package mas.blackboard.capability;

import jade.core.AID;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import mas.blackboard.plan.AddAgent;
import mas.blackboard.plan.SubscribeParameter;
import mas.blackboard.plan.UpdateParam;
import mas.util.ID;
import mas.util.MessageIds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;

import bdi4jade.belief.Belief;
import bdi4jade.belief.TransientBelief;
import bdi4jade.core.BDIAgent;
import bdi4jade.core.BeliefBase;
import bdi4jade.core.Capability;
import bdi4jade.core.PlanLibrary;
import bdi4jade.plan.Plan;
import bdi4jade.util.plan.SimplePlan;

public class CommunicationCenter extends Capability {

	private static final long serialVersionUID = 4783226881361023418L;
	private static Logger log;

	static {
		log = LogManager.getLogger();
	}

	public CommunicationCenter(BDIAgent bBagent) {
		super(new BeliefBase(getBeliefs(bBagent)), new PlanLibrary(getPlans()));
	}

	private static Set<Plan> getPlans() {

		Set<Plan> plans = new HashSet<Plan>();

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.RegisterMe ), AddAgent.class));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.UpdateParameter), UpdateParam.class));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.SubscribeParameter), SubscribeParameter.class));

		//log.info("Added plans for bloackboard");

		return plans;
	}

	private static Set<Belief<?>> getBeliefs(BDIAgent bBagent) {

		//  '?' means Any type extending Object (including Object)
		Set<Belief<?>> beliefs = new HashSet<Belief<?>>(); 
		// WorkspaceSet stores Workspaces

		Belief<HashMap<AID,String> > services =
				new TransientBelief<HashMap<AID,String> >(ID.Blackboard.BeliefBaseConst.serviceDiary);

		// initialize the belief
		services.setValue(new HashMap<AID, String>());
		beliefs.add(services);

		return beliefs;
	}

	protected void setup() {
	}
}