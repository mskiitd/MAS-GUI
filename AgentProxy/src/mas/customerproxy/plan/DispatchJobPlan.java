package mas.customerproxy.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.jobproxy.job;
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
	private String replyWith;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();

		jobToDispatch = (job) bfBase
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

		log.info("customer - sending job : " + jobToDispatch.getJobDuedatebyCust());

		replyWith = Integer.toString(jobToDispatch.getJobNo());
		
		ZoneDataUpdate jobOrderZoneDataUpdate = new ZoneDataUpdate.
				Builder(ID.Customer.ZoneData.newWorkOrderFromCustomer).
				value(jobToDispatch).
				setReplyWith(replyWith).
				Build();

		AgentUtil.sendZoneDataUpdate(this.bba,jobOrderZoneDataUpdate, myAgent);
	}
}
