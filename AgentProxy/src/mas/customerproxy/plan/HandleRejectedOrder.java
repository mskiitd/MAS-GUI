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

public class HandleRejectedOrder extends OneShotBehaviour implements PlanBody{

	private CustomerProxyGUI customer_gui;
	private Batch rejectedBatch;

	@Override
	public EndState getEndState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(PlanInstance PI) {
		customer_gui=(CustomerProxyGUI)PI.getBeliefBase().getBelief(ID.Customer.
				BeliefBaseConst.CUSTOMER_GUI).getValue();
		try {
			rejectedBatch=(Batch)((MessageGoal)PI.getGoal()).getMessage().getContentObject();
		} catch (UnreadableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void action() {
		String message="batch with ID "+rejectedBatch.getBatchId()+" was rejected";
		customer_gui.showNotification("Batch Rejected ", message, MessageType.INFO);
		Log.info("displayed notification");
	}

}
