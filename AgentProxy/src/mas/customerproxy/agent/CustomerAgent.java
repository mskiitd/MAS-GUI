package mas.customerproxy.agent;

import javax.swing.SwingUtilities;
import jade.core.AID;
import mas.customerproxy.goal.SendNegotiationJobGoal;
import mas.customerproxy.goal.dispatchJobGoal;
import mas.customerproxy.gui.CancelOrderGoal;
import mas.customerproxy.gui.ChangeDueDateGoal;
import mas.customerproxy.gui.CustomerProxyGUI;
import mas.jobproxy.job;
import mas.util.AgentUtil;
import mas.util.ID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bdi4jade.core.BeliefBase;
import bdi4jade.core.Capability;

public class CustomerAgent extends AbstractCustomerAgent {

	private static final long serialVersionUID = 1L;
	private Logger log;
	private BeliefBase bfBase;
	public static CustomerProxyGUI mygui;
	private AID blackboard;
	
	public void addJobToBeliefBase(job j) {
		log.info("Adding generated job " + j + " to belief base");
		bfBase.updateBelief(ID.Customer.BeliefBaseConst.CURRENT_JOB2SEND, j);
		
		addGoal(new dispatchJobGoal());
	}
	
	public void cancelOrder(job j) {
		log.info("Cancelling order : " + j + " adding belief base");
		bfBase.updateBelief(ID.Customer.BeliefBaseConst.CANCEL_ORDER, j);
		
		addGoal(new CancelOrderGoal());
	}
	
	public void changeDueDate(job j) {
		log.info("Change due date for order : " + j + " adding belief base");
		bfBase.updateBelief(ID.Customer.BeliefBaseConst.CHANGE_DUEDATE_JOB, j);
		
		addGoal(new ChangeDueDateGoal());
	}
	
	public void confirmJob(job j) {
		log.info("Adding Confirmed job " + j + " to belief base");
		bfBase.updateBelief(ID.Customer.BeliefBaseConst.CURRENT_CONFIRMED_JOB, j);
		
		addGoal(new SendNegotiationJobGoal());
	}
	
	public void negotiateJob(job j) {
		bfBase.updateBelief(ID.Customer.BeliefBaseConst.CURRENT_NEGOTIATION_JOB, j);
		this.addGoal(new SendNegotiationJobGoal());
	}
	
	@Override
	protected void init() {
		
		super.init();

		Capability bCap = new basicCapability();
		addCapability(bCap);
		log = LogManager.getLogger();

		blackboard = AgentUtil.findBlackboardAgent(this);
		
		bfBase = getRootCapability().getBeliefBase();
		bfBase.updateBelief(
				ID.Customer.BeliefBaseConst.blackboardAgent, blackboard);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mygui = new CustomerProxyGUI(CustomerAgent.this);
			}
		});

	}
}
