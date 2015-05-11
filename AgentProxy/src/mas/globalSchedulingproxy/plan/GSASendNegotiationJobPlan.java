package mas.globalSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import mas.jobproxy.Batch;
import mas.jobproxy.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;
/**
 * Handles negotiation with customer
 * @author NikhilChilwant
 *
 */
public class GSASendNegotiationJobPlan extends Behaviour implements PlanBody {
	
	private static final long serialVersionUID = 1L;
	private Logger log;
	private BeliefBase bfBase;
	private AID bba;
	private Batch negotiationJob;
	private boolean done = false;

	@Override
	public EndState getEndState() {
		return (done ? EndState.SUCCESSFUL : null);
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();
		this.bba = (AID) bfBase
				.getBelief(ID.GlobalScheduler.BeliefBaseConst.blackboardAgent)
				.getValue();

		this.negotiationJob = (Batch) bfBase.
				getBelief(ID.GlobalScheduler.BeliefBaseConst.Current_Negotiation_Batch).
				getValue();

	}

	@Override
	public void action() {

		if(negotiationJob != null) {
			log.info("GSA : sending negotiation reply to customer : " + negotiationJob.getDueDateByCustomer() );
			ZoneDataUpdate negotiationJobDataUpdate = new ZoneDataUpdate.
					Builder(ID.GlobalScheduler.ZoneData.GSAjobsUnderNegaotiation).
					value(negotiationJob).
					Build();

			AgentUtil.sendZoneDataUpdate( this.bba,
					negotiationJobDataUpdate,myAgent);
			
			bfBase.updateBelief(ID.GlobalScheduler.BeliefBaseConst.Current_Negotiation_Batch, null);
			done = true;
		}else {
			log.info("GSA : reading negotiation : " + negotiationJob );
			this.negotiationJob = (Batch) bfBase.
					getBelief(ID.GlobalScheduler.BeliefBaseConst.Current_Negotiation_Batch).
					getValue();
			block(200);
		}
	}

	@Override
	public boolean done() {
		return done;
	}
}
