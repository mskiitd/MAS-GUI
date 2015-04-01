package mas.localSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;

import mas.jobproxy.Batch;
import mas.jobproxy.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.JobQueryObject;
import mas.util.ZoneDataUpdate;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class RespondToGSAQuery extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private int JobNo;
	private BeliefBase beleifBase;
	private AID machineAID=null;
	private AID blackboard_AID;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance PI) {
		try {
			JobNo = ( (JobQueryObject)((MessageGoal)PI.getGoal()).getMessage().getContentObject()).
					getCurrentJob().getBatchNumber();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}

		beleifBase = PI.getBeliefBase();
		blackboard_AID = (AID)beleifBase.getBelief(ID.LocalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();
		machineAID=(AID)beleifBase.getBelief(ID.LocalScheduler.BeliefBaseConst.machine).
				getValue();
	}

	@Override
	public void action() {
		JobQueryObject response=new JobQueryObject.Builder().currentJob(null)
				.currentMachine(null).build();

		ArrayList<Batch> jobQ = (ArrayList<Batch>)beleifBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.jobQueue).
				getValue();
		Batch currentJob = (Batch)beleifBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.currentJobOnMachine).
				getValue();

		for(int i=0;i<jobQ.size();i++){
			if(JobNo==jobQ.get(i).getBatchNumber() ){
				response=new JobQueryObject.Builder().currentJob(jobQ.get(i))
						.underProcess(false)
						.currentMachine(machineAID).build();
			}
		}

		if(currentJob.getBatchNumber() ==JobNo){
			response=new JobQueryObject.Builder().currentJob(currentJob).currentMachine(machineAID)
					.underProcess(true)
					.build();
		}

		ZoneDataUpdate queryUpdate=new ZoneDataUpdate.Builder(ID.LocalScheduler.ZoneData.QueryResponse).
				value(response).Build();
		AgentUtil.sendZoneDataUpdate(blackboard_AID, queryUpdate, myAgent);
	}

}
