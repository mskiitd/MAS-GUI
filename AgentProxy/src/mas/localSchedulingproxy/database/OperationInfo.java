package mas.localSchedulingproxy.database;

import java.io.Serializable;
import java.util.ArrayList;

import mas.jobproxy.JobGNGattribute;
import mas.jobproxy.jobDimension;

public class OperationInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private long processTime;
	private ArrayList<jobDimension> mDimensions;
	private ArrayList<JobGNGattribute> gngAttributes;
	
	public OperationInfo() {
		mDimensions = new ArrayList<jobDimension>();
		gngAttributes = new ArrayList<JobGNGattribute>();
	}

	public long getProcessTime() {
		return processTime;
	}

	public void setProcessTime(long processTime) {
		this.processTime = processTime;
	}

	public ArrayList<jobDimension> getDimensions() {
		return mDimensions;
	}

	public void setDimensions(ArrayList<jobDimension> dimensions) {
		this.mDimensions = dimensions;
	}

	public ArrayList<JobGNGattribute> getGngAttributes() {
		return gngAttributes;
	}

	public void setGngAttributes(ArrayList<JobGNGattribute> gngAttributes) {
		this.gngAttributes = gngAttributes;
	}
	
	
	
}
