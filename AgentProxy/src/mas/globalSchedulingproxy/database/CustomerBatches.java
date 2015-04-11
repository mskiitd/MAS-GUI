package mas.globalSchedulingproxy.database;

import java.util.HashMap;

/**
 * @author Anand Prajapati
 * consists of database of batches for one customer
 */

public class CustomerBatches {

	private HashMap<String, UnitBatchInfo> batchData;

	public CustomerBatches() {
		batchData = new HashMap<String, UnitBatchInfo>();
	}

	public boolean contains(String batchId) {
		return batchData.containsKey(batchId);
	}

	public void put(String batchId, UnitBatchInfo batchOpInfo ) {
		batchData.put(batchId, batchOpInfo);
	}

	public UnitBatchInfo getBatchInfo(String batchId) {
		return batchData.get(batchId);
	}

	public void removeCustomerBatch(String batchId) {
		if(batchData.containsKey(batchId)) {
			batchData.remove(batchId);
		}
	}

	public int size() {
		return batchData.size();
	}

	@Override
	public String toString() {
		return batchData.toString();
	}
	
}
