package mas.customerproxy.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mas.job.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class DispatchJobPlan extends OneShotBehaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private job jobToDispatch;
	private AID bba;
	private Logger log;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();
		//		log.info(bfBase.getBelief(basicCapability.CURR_JOB));

		jobToDispatch = (job) bfBase
				.getBelief(ID.Customer.BeliefBaseConst.CURRENT_JOB)
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

		ZoneDataUpdate jobOrderZoneDataUpdate = new ZoneDataUpdate(
				ID.Customer.ZoneData.newWorkOrderFromCustomer,
				jobToDispatch);
		
//		log.info("Job dispatching custoemr " + jobToDispatch );

		AgentUtil.sendZoneDataUpdate(this.bba,jobOrderZoneDataUpdate, myAgent);
	}
}
