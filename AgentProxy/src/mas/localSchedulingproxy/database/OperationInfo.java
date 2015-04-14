package mas.localSchedulingproxy.database;

import java.io.Serializable;
import java.util.ArrayList;

import mas.jobproxy.JobGNGattribute;
import mas.jobproxy.jobDimension;

/**
 * @author Anand Prajapati
 * info about single operation
 */
public class OperationInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private long processingTime;
	private ArrayList<jobDimension> mDimensions;
	private ArrayList<JobGNGattribute> gngAttributes;
	private double processingCost;
	
	public OperationInfo() {
		mDimensions = new ArrayList<jobDimension>();
		gngAttributes = new ArrayList<JobGNGattribute>();
	}
	
	public double getProcessingCost() {
		return processingCost;
	}

	public void setProcessingCost(double processingCost) {
		this.processingCost = processingCost;
	}

	public long getProcessingTime() {
		return processingTime;
	}

	public void setProcessingTime(long processTime) {
		this.processingTime = processTime;
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
