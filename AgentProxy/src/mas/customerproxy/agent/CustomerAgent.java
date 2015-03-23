package mas.customerproxy.agent;

import javax.swing.SwingUtilities;

import jade.core.AID;
import mas.customerproxy.gui.CustomerProxyGUI;
import mas.job.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;
import bdi4jade.core.Capability;

public class CustomerAgent extends AbstractCustomerAgent {

	private static final long serialVersionUID = 1L;
	private Logger log;
	private BeliefBase bfBase;
	private CustomerProxyGUI mygui;
	private AID blackboard;
	

	public void addJobToBeliefBase(job j) {
		log.info("Adding job " + j + " to belief base");
		bfBase = getRootCapability().getBeliefBase();
		bfBase.updateBelief(ID.Customer.BeliefBaseConst.CURRENT_JOB, j);
	}
	
	public void cancelOrder(job j) {
		
	}
	
	public void changeDueDate(job j) {
		
	}
	
	public void confirmJob(job j) {
		ZoneDataUpdate confirmedJobDataUpdate = new ZoneDataUpdate(
				ID.Customer.ZoneData.customerConfirmedJobs,
				j);

		AgentUtil.sendZoneDataUpdate( blackboard,
				confirmedJobDataUpdate,CustomerAgent.this);
	}
	
	public void negotiateJob(job j) {
		ZoneDataUpdate negotiationJobDataUpdate = new ZoneDataUpdate(
				ID.Customer.ZoneData.customerJobsUnderNegotiation,
				j);

		AgentUtil.sendZoneDataUpdate( blackboard,
				negotiationJobDataUpdate,CustomerAgent.this);
	}
	
	@Override
	protected void init() {
		
		super.init();
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				mygui = new CustomerProxyGUI(CustomerAgent.this);
			}
		});

		Capability bCap = new basicCapability();
		addCapability(bCap);
		log = LogManager.getLogger();

		AID bba = AgentUtil.findBlackboardAgent(this);
		bCap.getBeliefBase().updateBelief(
				ID.Customer.BeliefBaseConst.blackboardAgent, bba);

	}
}
