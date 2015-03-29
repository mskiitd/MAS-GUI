package mas.localSchedulingproxy.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class OperationDataBase implements Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<String, OperationInfo> localJobData;

	public OperationDataBase() {
		localJobData = new HashMap<String, OperationInfo>();
	}

	public boolean contains(String operation) {
		return localJobData.containsKey(operation);
	}

	public void put(String opt, OperationInfo opInfo ) {
		localJobData.put(opt, opInfo);
	}

	public OperationInfo getOperationInfo(String opt) {
		OperationInfo inf = localJobData.get(opt);
		return inf;
	}

	public void removeOperation(String opt) {
		if(localJobData.containsKey(opt)) {
			localJobData.remove(opt);
		}
	}

	public ArrayList<OperationInfo> getOperationList() {
		ArrayList<OperationInfo> arrList = new ArrayList<OperationInfo>(localJobData.values());

		return arrList;
	}

	public ArrayList<String> getOperationTypes() {
		
		ArrayList<String> arrList = new ArrayList<String>();
		for(String s : localJobData.keySet()) {
			arrList.add(s);
		}

		return arrList;
	}

	public int size() {
		return localJobData.size();
	}

	public Object get(int index) {
		return null;
	}
}
