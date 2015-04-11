package mas.customerproxy.agent;

import jade.core.AID;
import jade.domain.DFService;

import javax.swing.SwingUtilities;

import mas.customerproxy.goal.CancelOrderGoal;
import mas.customerproxy.goal.ChangeDueDateGoal;
import mas.customerproxy.goal.CustomerSendNegotiationJobGoal;
import mas.customerproxy.goal.SendConfirmedOrderGoal;
import mas.customerproxy.goal.dispatchJobGoal;
import mas.customerproxy.gui.CustomerProxyGUI;
import mas.jobproxy.Batch;
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
	public static CustomerProxyGUI customerGUI;
	private AID blackboard;

	public void sendGeneratedBatch(Batch batchOfJobs) {
		//		log.info("Adding generated job " + j + " to belief base : " +bfBase);
		bfBase.updateBelief(ID.Customer.BeliefBaseConst.CURRENT_JOB2SEND, batchOfJobs);


		addGoal(new dispatchJobGoal());
	}

	public void cancelOrder(Batch j) {
		log.info("Cancelling order : " + j + " adding belief base");
		bfBase.updateBelief(ID.Customer.BeliefBaseConst.CANCEL_ORDER, j);

		addGoal(new CancelOrderGoal());
	}

	public void changeDueDate(Batch j) {
		log.info("Change due date for order : " + j + " adding belief base");
		bfBase.updateBelief(ID.Customer.BeliefBaseConst.CHANGE_DUEDATE_JOB, j);

		addGoal(new ChangeDueDateGoal());
	}

	public void confirmJob(Batch j) {
		log.info("Adding Confirmed job " + j + " to belief base");
		bfBase.updateBelief(ID.Customer.BeliefBaseConst.CURRENT_CONFIRMED_JOB, j);

		addGoal(new SendConfirmedOrderGoal());

		// update the accepted in the GUI
		CustomerAgent.customerGUI.addAcceptedJob(j);
	}

	public void rejectNegotiation() {
		bfBase.updateBelief(ID.Customer.BeliefBaseConst.CURRENT_NEGOTIATION_BATCH, null);
	}

	public void negotiateJob(Batch batch) {
		bfBase.updateBelief(ID.Customer.BeliefBaseConst.CURRENT_NEGOTIATION_BATCH, batch);
		this.addGoal(new CustomerSendNegotiationJobGoal());
	}

	@Override
	protected void takeDown() {
		super.takeDown();
		
		try {
			DFService.deregister(this);
		}
		catch (Exception e) {
		}
		
		if(customerGUI != null) {
			customerGUI.dispose();
		}
	}

	@Override
	protected void init() {

		super.init();

		Capability bCap = new basicCapability();
		addCapability(bCap);

		log = LogManager.getLogger();

		blackboard = AgentUtil.findBlackboardAgent(this);

		bfBase = bCap.getBeliefBase();
		bfBase.updateBelief(
				ID.Customer.BeliefBaseConst.blackboardAgent, blackboard);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				customerGUI = new CustomerProxyGUI(CustomerAgent.this);
				bfBase.updateBelief(ID.Customer.BeliefBaseConst.CUSTOMER_GUI, customerGUI);
			}
		});

	}

}
