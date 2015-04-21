package mas.localSchedulingproxy.plan;

import jade.core.behaviours.Behaviour;
import mas.localSchedulingproxy.agent.LocalSchedulingAgent;
import mas.localSchedulingproxy.behavior.JobSchedulingTickerBehavior;
import mas.util.ID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class BatchSchedulingPlan extends Behaviour implements PlanBody  {

	private Logger log;

	private JobSchedulingTickerBehavior scheduling;
	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private boolean done = false;
	private Double period = null;

	@Override
	public EndState getEndState() {
		return (done ? EndState.SUCCESSFUL : EndState.FAILED);
	}

	@Override
	public void init(PlanInstance pInstance) {
		bfBase = pInstance.getBeliefBase();
		log = LogManager.getLogger();
	}

	@Override
	public void action() {
		if(period == null) {
			period = (Double) bfBase.
					getBelief(ID.LocalScheduler.BeliefBaseConst.schedulingInterval).
					getValue();
			block(100);
		} else {
			scheduling = new JobSchedulingTickerBehavior(myAgent,
					LocalSchedulingAgent.schedulingPeriod, bfBase);

			myAgent.addBehaviour(scheduling);
			done = true;
		}
	}

	@Override
	public boolean done() {
		return done;
	}

}
