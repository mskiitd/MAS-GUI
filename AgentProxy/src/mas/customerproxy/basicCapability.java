package mas.customerproxy;

import jade.lang.acl.MessageTemplate;
import mas.customer.parentBasicCapability;
import mas.customerproxy.goal.RegisterAgentToBlackboardGoal;
import mas.customerproxy.plan.NegotiationPlan;
import mas.util.MessageIds;
import bdi4jade.util.plan.SimplePlan;

public class basicCapability extends parentBasicCapability{

	private static final long serialVersionUID = 1L;

	public basicCapability() {
		super();

		getPlanLibrary().addPlan(new SimplePlan(MessageTemplate.MatchConversationId(
				MessageIds.msgGSAjobsUnderNegaotiation),NegotiationPlan.class));
	}

	@Override
	protected void setup() {
		super.setup();
		myAgent.addGoal(new RegisterAgentToBlackboardGoal());
	}
}
