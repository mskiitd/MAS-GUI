package mas.globalSchedulingproxy.agent;

import jade.lang.acl.MessageTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bdi4jade.util.plan.SimplePlan;
import mas.globalSchedulingproxy.plan.AskForWaitingTime;
import mas.util.MessageIds;

public class BasicCapability extends AbstractGSCapability{

	private static final long serialVersionUID = 1L;
	private Logger log;

	public BasicCapability(){
		super();
		getPlanLibrary().addPlan(new SimplePlan(
				MessageTemplate.MatchConversationId(
						MessageIds.msgnewWorkOrderFromCustomer),AskForWaitingTime.class));
	}

	@Override
	protected void setup() {
		log = LogManager.getLogger();
		super.setup();
	}
}
