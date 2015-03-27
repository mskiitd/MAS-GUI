package mas.customerproxy.agent;

import javax.swing.SwingUtilities;

import jade.core.AID;
import mas.customerproxy.goal.dispatchJobGoal;
import mas.customerproxy.gui.CustomerProxyGUI;
import mas.customerproxy.plan.DispatchJobPlan;
import mas.jobproxy.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;
import bdi4jade.core.Capability;
import bdi4jade.plan.PlanInstance;

public class CustomerAgent extends AbstractCustomerAgent {

	private static final long serialVersionUID = 1L;
	private Logger log;
	private BeliefBase bfBase;
	public static CustomerProxyGUI mygui;
	private AID blackboard;
	
	public void addJobToBeliefBase(job j) {
		log.info("Adding job " + j + " to belief base");
		bfBase = getRootCapability().getBeliefBase();
		bfBase.updateBelief(ID.Customer.BeliefBaseConst.CURRENT_JOB, j);
		
		log.info("dispatching goal " );
		
		String replyWith = Integer.toString(j.getJobNo());
		
		ZoneDataUpdate jobOrderZoneDataUpdate = new ZoneDataUpdate.
				Builder(ID.Customer.ZoneData.newWorkOrderFromCustomer).
				value(j).
				setReplyWith(replyWith).
				Build();

		AgentUtil.sendZoneDataUpdate(this.blackboard,jobOrderZoneDataUpdate, this);
	}
	
	public void cancelOrder(job j) {
	}
	
	public void changeDueDate(job j) {
	}
	
	public void confirmJob(job j) {
		
		ZoneDataUpdate confirmedJobDataUpdate = new ZoneDataUpdate.Builder(
				ID.Customer.ZoneData.customerConfirmedJobs).
				value(j).Build();

		AgentUtil.sendZoneDataUpdate( blackboard,
				confirmedJobDataUpdate,CustomerAgent.this);
	}
	
	public void negotiateJob(job j) {
		
		ZoneDataUpdate negotiationJobDataUpdate = new ZoneDataUpdate.Builder(
				ID.Customer.ZoneData.customerJobsUnderNegotiation).
				value(j).Build();

		AgentUtil.sendZoneDataUpdate( blackboard,
				negotiationJobDataUpdate,CustomerAgent.this);
	}
	
	@Override
	protected void init() {
		
		super.init();

		Capability bCap = new basicCapability();
		addCapability(bCap);
		log = LogManager.getLogger();

		blackboard = AgentUtil.findBlackboardAgent(this);
		
		bCap.getBeliefBase().updateBelief(
				ID.Customer.BeliefBaseConst.blackboardAgent, blackboard);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mygui = new CustomerProxyGUI(CustomerAgent.this);
			}
		});

	}
}
