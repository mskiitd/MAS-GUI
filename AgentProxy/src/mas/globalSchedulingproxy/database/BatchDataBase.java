package mas.globalSchedulingproxy.database;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Anand Prajapati
 *
 * consists of database of batches for each customer
 */
public class BatchDataBase implements Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<String, CustomerBatches> batches;

	public BatchDataBase() {
		batches = new HashMap<String, CustomerBatches>();
	}

	public boolean contains(String customerId) {
		return batches.containsKey(customerId);
	}

	public void put(String customerId, CustomerBatches batchesInfo ) {
		batches.put(customerId, batchesInfo);
	}

	public CustomerBatches getBatchesInfo(String customerId) {
		CustomerBatches info = batches.get(customerId);
		return info;
	}

	public void removeCustomerBatch(String customerId) {
		if(batches.containsKey(customerId)) {
			batches.remove(customerId);
		}
	}

	public int size() {
		return batches.size();
	}

}
