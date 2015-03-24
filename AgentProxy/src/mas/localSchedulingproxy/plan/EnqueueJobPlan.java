package mas.localSchedulingproxy.plan;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mas.job.job;
import mas.util.ID;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * @author Anand Prajapati
 *
 * this picks a job from the queue and sends it to the machine for processing
 */

public class EnqueueJobPlan extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private ArrayList<job> jobQueue;
	private BeliefBase bfBase;
	private Logger log;
	private job comingJob;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {

		bfBase = pInstance.getBeliefBase();
		ACLMessage msg = ((MessageGoal)pInstance.getGoal()).
				getMessage();

		try {
			comingJob = (job) msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}

		jobQueue = (ArrayList<job>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.jobQueue).
				getValue();

		log = LogManager.getLogger();
	}

	@Override
	public void action() {
//		log.info(comingJob.getBidWinnerLSA());
		if(comingJob.getBidWinnerLSA().equals(myAgent.getAID())){
			log.info("Adding the job to queue of machine of " + myAgent.getLocalName());
			jobQueue.add(comingJob);
			/**
			 * update the belief base
			 */
			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.jobQueue, jobQueue);	
		}
	}
}
