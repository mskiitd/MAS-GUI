package mas.customerproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import mas.jobproxy.Batch;
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
 * Once the batch under negotiation is confirmed by customer, this plan is triggered and it updates
 * the confirmed batch into its zone-data from where other agents are notified
 */

public class SendConfirmedOrderPlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private Logger log;
	private BeliefBase bfBase;
	private AID bba;
	private Batch ConfirmedJob;
	private String replyWith;
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
				.getBelief(ID.Customer.BeliefBaseConst.blackboardAgent)
				.getValue();

		this.ConfirmedJob = (Batch) bfBase.
				getBelief(ID.Customer.BeliefBaseConst.CURRENT_CONFIRMED_JOB).
				getValue();

		replyWith = String.valueOf(this.ConfirmedJob.getBatchNumber() );

	}

	@Override
	public void action() {

		if(ConfirmedJob != null) {
			ZoneDataUpdate ConfirmedOrderZoneDataUpdate = new ZoneDataUpdate.
					Builder(ID.Customer.ZoneData.customerConfirmedJobs).
					value(ConfirmedJob).
					setReplyWith(replyWith).
					Build();

			AgentUtil.sendZoneDataUpdate(bba, ConfirmedOrderZoneDataUpdate, myAgent);
			done = true;
		} else {
			this.ConfirmedJob = (Batch) bfBase.
					getBelief(ID.Customer.BeliefBaseConst.CURRENT_CONFIRMED_JOB).
					getValue();
		}
	}

	@Override
	public boolean done() {
		return done;
	}
}
