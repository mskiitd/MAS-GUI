package mas.customerproxy.plan;

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
import jade.core.AID;
import jade.core.behaviours.Behaviour;

public class SendConfirmedOrderPlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private Logger log;
	private BeliefBase bfBase;
	private AID bba;
	private job ConfirmedJob;
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

		this.ConfirmedJob = (job) bfBase.
				getBelief(ID.Customer.BeliefBaseConst.CURRENT_CONFIRMED_JOB).
				getValue();

		replyWith = String.valueOf(this.ConfirmedJob.getJobNo() );

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
			this.ConfirmedJob = (job) bfBase.
					getBelief(ID.Customer.BeliefBaseConst.CURRENT_CONFIRMED_JOB).
					getValue();
		}
	}

	@Override
	public boolean done() {
		return done;
	}
}
