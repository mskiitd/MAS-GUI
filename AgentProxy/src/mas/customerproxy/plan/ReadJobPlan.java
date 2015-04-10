package mas.customerproxy.plan;

import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;
import jade.core.behaviours.Behaviour;

public class ReadJobPlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;

	private boolean done = false;
	private BeliefBase bfBase;
	
	@Override
	public EndState getEndState() {
		return (done ? EndState.SUCCESSFUL : null);
	}

	@Override
	public void init(PlanInstance pInstance) {
		bfBase = pInstance.getBeliefBase();
	}

	@Override
	public void action() {
		
	}

	@Override
	public boolean done() {
		return done;
	}

}
