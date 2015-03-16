package mas.customerproxy.plan;

import javax.swing.SwingUtilities;

import mas.customerproxy.agent.CustomerAgent;
import mas.customerproxy.gui.CustomerNegotiateProxyGUI;
import mas.customerproxy.gui.CustomerProxyGUI;
import mas.job.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.Logger;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class NegotiationPlan extends Behaviour implements PlanBody {
	private static final long serialVersionUID = 1L;
	private Logger log;
	private BeliefBase bfBase;
	private AID bba;
	private job negotiationJob;
	private CustomerNegotiateProxyGUI mygui;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {

		ACLMessage msg = ( (MessageGoal)pInstance.getGoal()).getMessage();

		try {
			negotiationJob = (job) msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}

		this.bfBase = pInstance.getBeliefBase();

		this.bba = (AID) bfBase
				.getBelief(ID.Customer.BeliefBaseConst.blackboardAgent)
				.getValue();
	}

	@Override
	public void action() {
		/**
		 * show the message to the user that this job needs to be negotiated
		 */
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				mygui = new CustomerNegotiateProxyGUI((CustomerAgent)myAgent, negotiationJob);
			}
		});
	}

	@Override
	public boolean done() {
		return true;
	}
}
