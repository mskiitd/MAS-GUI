package mas.customerproxy.plan;

import java.awt.TrayIcon.MessageType;

import com.alee.log.Log;

import mas.customerproxy.gui.CustomerProxyGUI;
import mas.jobproxy.Batch;
import mas.util.ID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.UnreadableException;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * Plan to handle rejected order from GSA. If GSA is unable to process the batch ordered by customer, it will straightforward
 * reject it and update its zonedata. Customer will be notified about this in this plan.
 */
public class HandleRejectedOrder extends OneShotBehaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private CustomerProxyGUI customer_gui;
	private Batch rejectedBatch;

	@Override
	public EndState getEndState() {
		return null;
	}

	@Override
	public void init(PlanInstance PI) {
		customer_gui=(CustomerProxyGUI)PI.getBeliefBase().getBelief(ID.Customer.
				BeliefBaseConst.CUSTOMER_GUI).getValue();
		try {
			rejectedBatch=(Batch)((MessageGoal)PI.getGoal()).getMessage().getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void action() {
		String message = "batch with ID " + rejectedBatch.getBatchId() + " was rejected";
		customer_gui.showNotification("Batch Rejected ", message, MessageType.INFO);
		Log.info("displayed notification");
	}

}
