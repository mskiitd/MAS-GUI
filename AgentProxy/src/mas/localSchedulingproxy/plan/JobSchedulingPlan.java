package mas.localSchedulingproxy.plan;

import jade.core.behaviours.Behaviour;
import java.util.ArrayList;
import mas.job.job;
import mas.localSchedulingproxy.agent.LocalSchedulingAgent;
import mas.localSchedulingproxy.behavior.JobSchedulingTickerBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class JobSchedulingPlan extends Behaviour implements PlanBody  {

	private Logger log;
	
	private JobSchedulingTickerBehavior scheduling;
	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		bfBase = pInstance.getBeliefBase();
		log = LogManager.getLogger();
	}

	@Override
	public void action() {
		scheduling = new JobSchedulingTickerBehavior(myAgent,
				LocalSchedulingAgent.schedulingPeriod, bfBase);
		
		myAgent.addBehaviour(scheduling);
	}

	@Override
	public boolean done() {
		return true;
	}

}
