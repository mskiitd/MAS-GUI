package mas.jobproxy;

import java.util.ArrayList;

public class Batch {

	private ArrayList<job> jobsInBatch;
	private String batchId;
	
	public Batch() {
		jobsInBatch = new ArrayList<job>();
	}
	
	public ArrayList<job> getJobsInBatch() {
		return jobsInBatch;
	}
	public void setJobsInBatch(ArrayList<job> jobsInBatch) {
		this.jobsInBatch = jobsInBatch;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	
}
