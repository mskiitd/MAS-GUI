package mas.globalSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import bdi4jade.belief.BeliefSet;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class FindLocalSchedulingPlan extends Behaviour implements PlanBody{

	private AID[] machines;
	private BeliefBase bfBase;
	private BeliefSet<AID> mset;
	private static final long serialVersionUID = 1L;

	@Override
	public EndState getEndState() {
		return null;
	}

	@Override
	public void init(PlanInstance pInstance) {
		bfBase = pInstance.getBeliefBase();
//		mset = (BeliefSet<AID>) bfBase.getBelief(AbstractGSCapability.MACHINES);
	}

	@Override
	public void action() {
		
//		DFAgentDescription dfd = new DFAgentDescription();
//		ServiceDescription sd = new ServiceDescription();
//		sd.setType(BlackboardId.Agents.LocalScheduling);
//		dfd.addServices(sd);
//		SearchConstraints sc = new SearchConstraints();
//		sc.setMaxResults(new Long(1));
//
//		myAgent.send(DFService.createSubscriptionMessage(myAgent,
//				myAgent.getDefaultDF(), 
//				dfd, sc));
//		
//		myAgent.addBehaviour(new RegistrationNotification());
//		
//		DFAgentDescription df = new DFAgentDescription();
//		ServiceDescription machineService = new ServiceDescription();
//		machineService.setType(MessageIds.LocalScheulingService);
//		df.addServices(machineService);
//		
//		  try {
////			  System.out.println("Searching for Local scheduling agents");
//			  DFAgentDescription[] result = DFService.search(myAgent,df);
//			      if ((result != null) && (result.length > 0)) {
//			    	   machines = new AID[result.length];
//						for (int i = 0; i < result.length; ++i) {
//							machines[i] = result[i].getName();
//						}		
//			      }
////			      System.out.println("# Local-Scheduling Found : "+ result.length);
//			      
//			  } catch (Exception fe) {
//			      fe.printStackTrace();
//			      //doDelete();
//			  }
		}

	@Override
	public boolean done() {
//		System.out.println(" Finding Local-Scheduling plan complete");
		return true;
	}
}
