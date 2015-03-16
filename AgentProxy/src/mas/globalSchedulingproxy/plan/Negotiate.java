package mas.globalSchedulingproxy.plan;
/*
 * 
 *  This plan is not being used currently 
 *  
 *  
 *  
 *  */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mas.job.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.UnreadableException;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class Negotiate extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	//	private AID CustomerAgent;
	private AID bb;
	private job JobUnderNegotiation;
	private Logger log=LogManager.getLogger();
	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance PI) {
		bb = (AID)PI.getBeliefBase().getBelief(ID.Blackboard.LocalName).getValue();
		try {
			this.JobUnderNegotiation = (job)((MessageGoal)(PI.getGoal())).
					getMessage().getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void action() {
		ZoneDataUpdate update=new ZoneDataUpdate(ID.GlobalScheduler.ZoneData.GSAjobsUnderNegaotiation, JobUnderNegotiation); 
		//Negotiation logic under development
		AgentUtil.sendZoneDataUpdate(bb, update, myAgent);
	}
}
