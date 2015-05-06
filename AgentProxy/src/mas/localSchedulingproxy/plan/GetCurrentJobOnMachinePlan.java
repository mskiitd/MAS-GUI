package mas.localSchedulingproxy.plan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.UnreadableException;
import mas.jobproxy.job;
import mas.util.ID;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * @author Anand Prajapati
 * Plan to read current job being processed on the machine.
 * It receives this value from the blackboard.
 */
public class GetCurrentJobOnMachinePlan extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private job currentJobOnMach;
	private Logger log;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {

		bfBase = pInstance.getBeliefBase();
		try {
			currentJobOnMach = (job) ((MessageGoal) pInstance.getGoal()).getMessage().getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		log = LogManager.getLogger();
	}

	@Override
	public void action() {
		bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.currentJobOnMachine,
				currentJobOnMach);
		log.info("Updating current job on machine : " + currentJobOnMach);
	}

}
