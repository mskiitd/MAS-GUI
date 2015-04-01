package mas.globalSchedulingproxy.plan;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.UnreadableException;
import mas.jobproxy.Batch;
import mas.jobproxy.job;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class CallBackChangeDueDatePlan extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private static int step=0;
	private Batch copyOfJobToCallBack;

	@Override
	public EndState getEndState() {
		if(step == 1) {
			return EndState.SUCCESSFUL;
		}
		else{
			return EndState.FAILED;
		}
	}

	@Override
	public void init(PlanInstance PI) {
		try {
			copyOfJobToCallBack = (Batch) ((MessageGoal)PI.getGoal()).
					getMessage().
					getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void action() {
		switch(step){
		case 0:

			break;

		case 1:

			break;

		}

	}

	@Override
	public boolean done() {
		return step==1;
	}

}
