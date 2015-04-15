package mas.customerproxy.plan;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;

import mas.customerproxy.agent.CustomerAgent;
import mas.customerproxy.gui.ChangeDueDateGUI;
import mas.jobproxy.Batch;
import mas.jobproxy.job;
import mas.jobproxy.jobOperation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class ChangeDueDate extends OneShotBehaviour implements PlanBody {

	private Logger log=LogManager.getLogger();
	private Batch batchToChangeDueDate;

	@Override
	public void init(PlanInstance PI) {
		try {
			batchToChangeDueDate=(Batch)((MessageGoal)(PI.getGoal())).
					getMessage().getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void action() {
		
		Batch batchTosend=batchToChangeDueDate;
		batchTosend.setBatchNumber(-1);
		
		/*batchTosend.setCost(batchToChangeDueDate.getCost());
		batchTosend.setCPN(batchToChangeDueDate.getCPN());
		batchTosend.setGenerationTime(System.currentTimeMillis());
		batchTosend.setPenaltyRate(batchToChangeDueDate.getPenaltyRate());
		batchTosend.setProfit(batchToChangeDueDate.getProfit());*/
		job tempJob=batchToChangeDueDate.getFirstJob();
		ArrayList<jobOperation> ops=tempJob.getOperations();
		for(int n=0;n<batchToChangeDueDate.getCurrentOperationNumber();n++){
			ops.remove(n);
		}
		
		tempJob.setOperations(ops);
		tempJob.resetOpnNoToZero();

		ArrayList<job> jobArray=new ArrayList<job>();
		for(int count=0;count<batchTosend.getBatchCount();count++){
			jobArray.add(tempJob);
		}
		batchTosend.setJobsInBatch(jobArray);
		if(batchTosend.getCustomerId().equals(myAgent.getLocalName())){
			ChangeDueDateGUI gui = new ChangeDueDateGUI
					((CustomerAgent)myAgent, batchTosend);
		}
		
	}

	@Override
	public EndState getEndState() {
		// TODO Auto-generated method stub
		return null;
	}



}
