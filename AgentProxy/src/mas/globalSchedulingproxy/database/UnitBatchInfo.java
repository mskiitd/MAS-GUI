package mas.globalSchedulingproxy.database;

import java.io.Serializable;
import java.util.ArrayList;
import mas.jobproxy.jobOperation;

/**
 * @author Anand Prajapati
 * Batch database for unit batch from some customer
 */
public class UnitBatchInfo implements Serializable{

	private static final long serialVersionUID = 1L;

	private ArrayList<jobOperation> operations;

	public UnitBatchInfo() {
		setOperations(new ArrayList<jobOperation>());
	}

	public ArrayList<jobOperation> getOperations() {
		return operations;
	}

	public void setOperations(ArrayList<jobOperation> operations) {
		this.operations = operations;
	}
}
