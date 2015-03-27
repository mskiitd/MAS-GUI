package mas.globalSchedulingproxy.plan;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.UnreadableException;
import mas.jobproxy.job;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class NegotiateViaGuiPlan extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private job JobUnderNegotiation;
	private Logger log = LogManager.getLogger();
	private String replyWith = null;
	
	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance PI) {
		
		try {
			this.JobUnderNegotiation = (job)((MessageGoal)(PI.getGoal())).
					getMessage().getContentObject();
			
			log.info(JobUnderNegotiation.getJobDuedatebyCust());
			
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		
		replyWith = ((MessageGoal)PI.getGoal()).getMessage().getReplyWith();
	}

	@Override
	public void action() {
		
	}
}
