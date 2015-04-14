package mas.localSchedulingproxy.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class OperationDataBase implements Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<OperationItemId, OperationInfo> localJobData;

	public OperationDataBase() {
		localJobData = new HashMap<OperationItemId, OperationInfo>();
	}

	public boolean contains(OperationItemId opId) {
		return localJobData.containsKey(opId);
	}

	public void put(OperationItemId opt, OperationInfo opInfo ) {
		localJobData.put(opt, opInfo);
	}

	public OperationInfo getOperationInfo(OperationItemId opId) {
		OperationInfo inf = localJobData.get(opId);
		return inf;
	}

	public void removeOperation(OperationItemId opId) {
		if(localJobData.containsKey(opId)) {
			localJobData.remove(opId);
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

	public int size() {
		return localJobData.size();
	}
}
