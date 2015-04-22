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
 * Plan to dispatch generated batch by customer to GSA.
 * It updates zonedata of customer with the new generated order
 */
public class DispatchJobPlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private Batch batchToDispatch;
	private AID bba;
	private Logger log;
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

		batchToDispatch = (Batch) bfBase
				.getBelief(ID.Customer.BeliefBaseConst.CURRENT_JOB2SEND)
				.getValue();

		this.bba = (AID) bfBase
				.getBelief(ID.Customer.BeliefBaseConst.blackboardAgent)
				.getValue();
	}

	/**
	 *  send the generated job to it's zonedata
	 */
	@Override
	public void action() {

		if(batchToDispatch != null) {
			log.info("customer - sending job : " + batchToDispatch);
			log.info("batch : " + batchToDispatch.getJobsInBatch());

			replyWith = Integer.toString(batchToDispatch.getBatchNumber());

			ZoneDataUpdate jobOrderZoneDataUpdate = new ZoneDataUpdate.
					Builder(ID.Customer.ZoneData.newWorkOrderFromCustomer).
					value(batchToDispatch).
					setReplyWith(replyWith).
					Build();

			AgentUtil.sendZoneDataUpdate(this.bba,jobOrderZoneDataUpdate, myAgent);
			
			bfBase.updateBelief(ID.Customer.BeliefBaseConst.CURRENT_JOB2SEND, null);
			done = true;
		} else {
			log.info("customer - sending job :  " + bfBase);
			batchToDispatch = (Batch) bfBase
					.getBelief(ID.Customer.BeliefBaseConst.CURRENT_JOB2SEND)
					.getValue();
			block(1000);
		}
	}

	@Override
	public boolean done() {
		return done;
	}
}
