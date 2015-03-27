package mas.localSchedulingproxy.database;

import java.io.Serializable;
import java.util.ArrayList;
import mas.jobproxy.jobDimension;

public class OperationInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private long processTime;
	private ArrayList<jobDimension> dimensions;
	
	public OperationInfo() {
		dimensions = new ArrayList<jobDimension>();
	}

	public long getProcessTime() {
		return processTime;
	}

	public void setProcessTime(long processTime) {
		this.processTime = processTime;
	}

	public ArrayList<jobDimension> getDimensions() {
		return dimensions;
	}

	public void setDimensions(ArrayList<jobDimension> dimensions) {
		this.dimensions = dimensions;
	}
	
	
	
}
