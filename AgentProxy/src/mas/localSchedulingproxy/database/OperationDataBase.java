package mas.localSchedulingproxy.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Anand Prajapati
 * Database for supported operations by machine.
 * Consists of information about each operation.
 */

public class OperationDataBase implements Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<OperationItemId, OperationInfo> localJobData;

	public OperationDataBase() {
		localJobData = new HashMap<OperationItemId, OperationInfo>();
	}

	/**
	 * @param operationId
	 * @return True if operation corresponding to this operation ID is supported on this machine
	 */
	public boolean contains(OperationItemId operationId) {
		return localJobData.containsKey(operationId);
	}

	/**
	 * @param operationId
	 * @param opnInfo : Operation Information
	 * </br> Add new operation to  database of supported operations
	 */
	public void put(OperationItemId operationId, OperationInfo opnInfo ) {
		localJobData.put(operationId, opnInfo);
	}

	/**
	 * @param operationId
	 * @return Information about the job operation corresponding to the given operation id
	 */
	public OperationInfo getOperationInfo(OperationItemId operationId) {
		OperationInfo inf = localJobData.get(operationId);
		return inf;
	}

	/**
	 * @param operationId
	 * Remove the operation corresponding to the passed operation id from the database
	 */
	public void removeOperation(OperationItemId operationId) {
		if(localJobData.containsKey(operationId)) {
			localJobData.remove(operationId);
		}
	}

	/**
	 * @return List of operation information for all supported operations on this machine
	 */
	public ArrayList<OperationInfo> getOperationList() {
		ArrayList<OperationInfo> arrList = new ArrayList<OperationInfo>(localJobData.values());

		return arrList;
	}

	/**
	 * @return List of Operation Id's of all supported operations on this machine
	 */
	public ArrayList<OperationItemId> getOperationTypes() {
		
		ArrayList<OperationItemId> arrList = new ArrayList<OperationItemId>();
		for(OperationItemId s : localJobData.keySet()) {
			arrList.add(s);
		}

		return arrList;
	}

	/**
	 * @return Total number of supported operations for this machine
	 */
	public int size() {
		return localJobData.size();
	}
}
