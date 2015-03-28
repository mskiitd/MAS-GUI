package mas.customerproxy.plan;

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
import jade.core.AID;
import jade.core.behaviours.Behaviour;

public class CustomerSendNegotiationJobPlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private Logger log;
	private BeliefBase bfBase;
	private AID bba;
	private job negotiationJob;
	private boolean done = false;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();
		this.bba = (AID) bfBase
				.getBelief(ID.Customer.BeliefBaseConst.blackboardAgent)
				.getValue();

		this.negotiationJob = (job) bfBase.
				getBelief(ID.Customer.BeliefBaseConst.CURRENT_NEGOTIATION_JOB).
				getValue();

	}

	@Override
	public void action() {

		if(negotiationJob != null) {
			log.info("Customer:Sending job for negotiation : " + negotiationJob);
			ZoneDataUpdate negotiationJobDataUpdate = new ZoneDataUpdate.Builder(
					ID.Customer.ZoneData.customerJobsUnderNegotiation).
					value(negotiationJob).Build();

			AgentUtil.sendZoneDataUpdate( this.bba,
					negotiationJobDataUpdate,myAgent);
			
			bfBase.updateBelief(ID.Customer.BeliefBaseConst.CURRENT_NEGOTIATION_JOB, null);
			
			done = true;
		} else {
			log.info("Customer : reading job negotiation : " + negotiationJob);
			this.negotiationJob = (job) bfBase.
					getBelief(ID.Customer.BeliefBaseConst.CURRENT_NEGOTIATION_JOB).
					getValue();
			block(50);
		}
	}

	@Override
	public boolean done() {
		return done;
	}

}
