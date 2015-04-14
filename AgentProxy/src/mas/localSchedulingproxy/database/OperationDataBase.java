package mas.localSchedulingproxy.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import mas.jobproxy.Batch;

public class OperationDataBase implements Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<OperationItemId, OperationInfo> localJobData;

	public OperationDataBase() {
		localJobData = new HashMap<OperationItemId, OperationInfo>();
	}

	public boolean contains(OperationItemId operation) {
		return localJobData.containsKey(operation);
	}

	public void put(OperationItemId opt, OperationInfo opInfo ) {
		localJobData.put(opt, opInfo);
	}

	public OperationInfo getOperationInfo(OperationItemId opt) {
		OperationInfo inf = localJobData.get(opt);
		return inf;
	}

	public void removeOperation(OperationItemId opt) {
		if(localJobData.containsKey(opt)) {
			localJobData.remove(opt);
		}
	}

	public ArrayList<OperationInfo> getOperationList() {
		ArrayList<OperationInfo> arrList = new ArrayList<OperationInfo>(localJobData.values());

		return arrList;
	}

	public ArrayList<OperationItemId> getOperationTypes() {
		
		ArrayList<OperationItemId> arrList = new ArrayList<OperationItemId>();
		for(OperationItemId s : localJobData.keySet()) {
			arrList.add(s);
		}

		return arrList;
	}

	public OperationInfo getOperationInfo(Batch comingBatch) {
		String cust = comingBatch.getCustomerId();
		String operation = comingBatch.getCurrentOperationType();
		
		OperationItemId id = new OperationItemId(operation, cust);
		return this.localJobData.get(id);
	}
	
	public int size() {
		return localJobData.size();
	}

	public boolean contains(Batch batchToBidFor) {
		String cust = batchToBidFor.getCustomerId();
		String op = batchToBidFor.getCurrentOperationType();
		OperationItemId id = new OperationItemId(op,cust);
		return localJobData.containsKey(id);
	}
}
