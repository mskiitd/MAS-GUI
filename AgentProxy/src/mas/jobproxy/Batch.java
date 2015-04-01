package mas.jobproxy;

import jade.core.AID;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Represents a batch of jobs
 * @author Anand Prajapati
 */

public class Batch implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<job> jobsInBatch;
	private String batchId = null;
	private int batchNumber;
	
	private double CPN;
	private double Cost;
	private double penaltyRate;

	private long waitingTime;
	private long startTime;
	private long completionTime;
	private long batchProcessingTime;

	private Date dueDateByCustomer;

	private AID WinnerLSA;
	private AID LSABidder;
	private double BidByLSA ;

	private int currentJobIndex = 0;
	private boolean isBatchComplete;
	
	private int position;
	private double profit;

	public Batch() {
		jobsInBatch = new ArrayList<job>();
	}

	public double getCPN() {
		return CPN;
	}

	public void setCPN(double cPN) {
		CPN = cPN;
	}

	public double getCost() {
		return Cost;
	}

	public void setCost(double cost) {
		Cost = cost;
	}

	public double getPenaltyRate() {
		return penaltyRate;
	}

	public void setPenaltyRate(double penaltyRate) {
		this.penaltyRate = penaltyRate;
	}

	public double getBidByLSA() {
		return BidByLSA;
	}

	public void setBidByLSA(double bidByLSA) {
		BidByLSA = bidByLSA;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	public long getBatchProcessingTime() {
		return batchProcessingTime;
	}

	public void setBatchProcessingTime(long batchProcessingTime) {
		this.batchProcessingTime = batchProcessingTime;
	}

	public boolean isBatchComplete() {
		return isBatchComplete;
	}

	public job getCurrentJob() {
		return jobsInBatch.get(currentJobIndex);
	}

	public int getCurrentJobNo() {
		return currentJobIndex;
	}

	public void incrementCurrentJob() {
		this.currentJobIndex ++ ;
		/**
		 *  if index becomes >= the size of the batch it means all jobs are done
		 *  index for last job is 'size()-1'
		 */
		if(this.currentJobIndex >= this.jobsInBatch.size() ){
			isBatchComplete = true;
		}
	}

	public AID getWinnerLSA() {
		return WinnerLSA;
	}

	public void setWinnerLSA(AID winnerLSA) {
		WinnerLSA = winnerLSA;
	}
	
	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public AID getLSABidder() {
		return LSABidder;
	}

	public void setLSABidder(AID lSABidder) {
		LSABidder = lSABidder;
	}

	public Date getDueDateByCustomer() {
		return dueDateByCustomer;
	}

	public void setDueDateByCustomer(Date dueDateByCustomer) {
		this.dueDateByCustomer = dueDateByCustomer;
	}
	
	public void setDueDateMillisByCustomer(long dueDateByCustomer) {
		this.dueDateByCustomer = new Date(dueDateByCustomer);
	}

	public long getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(long waitingTime) {
		this.waitingTime = waitingTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(long completionTime) {
		this.completionTime = completionTime;
	}

	public int getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(int batchNumber) {
		this.batchNumber = batchNumber;
	}

	public void addJobToBatch(job j) {
		this.jobsInBatch.add(j);
		if(batchId == null) {
			batchId = j.getJobID();
		}
	}

	public int getBatchCount() {
		return jobsInBatch.size();
	}

	public ArrayList<job> getJobsInBatch() {
		return jobsInBatch;
	}
	public void setJobsInBatch(ArrayList<job> jobsInBatch) {
		if(jobsInBatch != null) {
			this.jobsInBatch = jobsInBatch;
			if(batchId == null && ! jobsInBatch.isEmpty() ) {
				batchId = jobsInBatch.get(0).getJobID();
			}
		}
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public void clearAllJobs() {
		this.jobsInBatch.clear();
	}

	public job getSampleJob() {

		if(! jobsInBatch.isEmpty())
			return jobsInBatch.get(0);

		return null;
	}

	public Object getBidWinnerLSA() {
		return null;
	}

	public void IncrementAllOperationNumber() {
		for(int i = 0; i < jobsInBatch.size(); i ++) {
			jobsInBatch.get(i).IncrementOperationNumber();
		}
	}
}
