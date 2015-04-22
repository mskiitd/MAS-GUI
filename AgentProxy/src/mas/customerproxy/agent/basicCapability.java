package mas.customerproxy.agent;

import jade.lang.acl.MessageTemplate;
import mas.customerproxy.goal.RegisterAgentToBlackboardGoal;
import mas.customerproxy.goal.CustomerSendNegotiationJobGoal;
import mas.customerproxy.plan.NegotiationGuiPlan;
import mas.customerproxy.plan.CustomerSendNegotiationJobPlan;
import mas.util.MessageIds;
import bdi4jade.util.plan.SimplePlan;

public class basicCapability extends parentBasicCapability{

	private static final long serialVersionUID = 1L;

	/**
	 * add two plans: one for opening gui for negotiation
	 * other for sending negotiation job back to the GSA 
	 */
	public basicCapability() {
		super();

		getPlanLibrary().addPlan(new SimplePlan(MessageTemplate.MatchConversationId(
				MessageIds.msgGSAjobsUnderNegaotiation),NegotiationGuiPlan.class));

		getPlanLibrary().addPlan(new SimplePlan(CustomerSendNegotiationJobGoal.class,
				CustomerSendNegotiationJobPlan.class));
	}

	@Override
	protected void setup() {
		super.setup();
		myAgent.addGoal(new RegisterAgentToBlackboardGoal());
	}
}
