package mas.util;

import jade.core.AID;

import java.util.ArrayList;

import mas.job.jobOperation;

public class JobQueryObject {

	private ArrayList<jobOperation> operationsDone;
	private jobOperation currentOperation;
	private AID currentMachine;
	
	public JobQueryObject() {
		this.operationsDone = new ArrayList<jobOperation>();
		currentMachine = new AID();
		currentOperation = new jobOperation();
	}
	
	public ArrayList<jobOperation> getOperationsDone() {
		return operationsDone;
	}
	public void setOperationsDone(ArrayList<jobOperation> operationsDone) {
		this.operationsDone = operationsDone;
	}
	public jobOperation getCurrentOperation() {
		return currentOperation;
	}
	public void setCurrentOperation(jobOperation currentOperation) {
		this.currentOperation = currentOperation;
	}
	public AID getCurrentMachine() {
		return currentMachine;
	}
	public void setCurrentMachine(AID currentMachine) {
		this.currentMachine = currentMachine;
	}
	
	
}
