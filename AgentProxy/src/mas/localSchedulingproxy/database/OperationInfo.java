package mas.localSchedulingproxy.database;

import java.io.Serializable;
import java.util.ArrayList;

import mas.jobproxy.JobGNGattribute;
import mas.jobproxy.jobDimension;

/**
 * @author Anand Prajapati
 * Information about single operation of job
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
	
	/**
	 * @return processingCost per unit time for this operation
	 */
	public double getProcessingCost() {
		return processingCost;
	}

	/**
	 * @param processingCost
	 * Sets processingCost per unit time for this operation
	 */
	public void setProcessingCost(double processingCost) {
		this.processingCost = processingCost;
	}

	/**
	 * @return processing time for this operation
	 */
	public long getProcessingTime() {
		return processingTime;
	}

	/**
	 * @param processTime
	 * Set processing time for this operation
	 */
	public void setProcessingTime(long processTime) {
		this.processingTime = processTime;
	}

	/**
	 * @return List of dimensions pertaining to this operation
	 */
	public ArrayList<jobDimension> getDimensions() {
		return mDimensions;
	}

	/**
	 * @param dimensions
	 * Set List of dimensions pertaining to this operation
	 */
	public void setDimensions(ArrayList<jobDimension> dimensions) {
		this.mDimensions = dimensions;
	}

	/**
	 * @return List of Go/No-Go attributes pertaining to this operation
	 */
	public ArrayList<JobGNGattribute> getGngAttributes() {
		return gngAttributes;
	}

	/**
	 * @param gngAttributes
	 * 
	 * Sets List of Go/No-Go attributes pertaining to this operation
	 */
	public void setGngAttributes(ArrayList<JobGNGattribute> gngAttributes) {
		this.gngAttributes = gngAttributes;
	}

}
