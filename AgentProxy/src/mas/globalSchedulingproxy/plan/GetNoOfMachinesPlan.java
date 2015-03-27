package mas.globalSchedulingproxy.plan;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.util.ID;
import bdi4jade.belief.Belief;
import bdi4jade.belief.TransientBelief;
import bdi4jade.core.BDIAgent;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetNoOfMachinesPlan extends Behaviour implements PlanBody{

	ArrayList list_machines =  new ArrayList();
	boolean register = true;
	long time;
	long limit; //how many seconds u want to run this Behaviour;
	private BeliefBase bb;
	private Logger log;
	@Override
	public EndState getEndState() {
		return null;
	}

	@Override
	public void init(PlanInstance arg0) {
		
		limit=10*1000;//set limit
		time=System.currentTimeMillis();
		bb=arg0.getBeliefBase();	
		log=LogManager.getLogger();
		
	}

	@Override
	public void action() {

		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(ID.Machine.Service);
		dfd.addServices(sd);

		try {

			  DFAgentDescription[] result = DFService.search(myAgent,dfd);
			  Integer NoOfMachines= result.length;
//         	 log.info(NoOfMachines);
			  Belief<Integer> new_belief=new TransientBelief<Integer>(ID.GlobalScheduler.BeliefBaseConst.NoOfMachines,
					  NoOfMachines);
	          ((BDIAgent)myAgent).getRootCapability().getBeliefBase().addOrUpdateBelief(new_belief); //update belief base      
		
           	 
           	 
			  } catch (Exception fe) {
			      fe.printStackTrace();
			      System.out.println(fe);
			  }

		
        ACLMessage msg = myAgent.receive(MessageTemplate.MatchSender(myAgent.getDefaultDF()));
        
            if (msg != null)
            {
              try {
                 DFAgentDescription[] dfds =    
                      DFService.decodeNotification(msg.getContent());
                 log.info(dfds.toString());
                 if (dfds.length > 0) {              	   
                	 Integer NoOfMachines= (Integer)((BDIAgent)myAgent).getRootCapability()
                			 .getBeliefBase().getBelief(ID.GlobalScheduler.BeliefBaseConst.NoOfMachines).getValue();
                	 NoOfMachines++;
					Belief<Integer> new_belief=new TransientBelief<Integer>(
							ID.GlobalScheduler.BeliefBaseConst.NoOfMachines, NoOfMachines);
                	 ((BDIAgent)myAgent).getRootCapability().getBeliefBase().addOrUpdateBelief(new_belief); //update belief base
                	 
                	 log.info(NoOfMachines);
                 }
               }
               catch (Exception ex) {
            	   
               }
            }
            block();
		
	}

	@Override
	public boolean done() {
		if((System.currentTimeMillis()-time)<limit){
			return false;
		}
		else{
			return true;
		}
	}

}
