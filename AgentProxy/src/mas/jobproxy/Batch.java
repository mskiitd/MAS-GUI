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

	private static final double lowRegretMultiplier = 1,
			MediumRegretMultiplier = 2,
			HighRegretMultiplier = 3;

	private ArrayList<job> jobsInBatch;

	private String customerId = null;
	private String batchId = null;
	private int batchNo;

	private double CPN;
	private double Cost;
	private double penaltyRate;

	private long waitingTime;
	private long startTime;
	private long completionTime;
	private Date generationTime;

	private Date dueDateByCustomer;

	private AID WinnerLSA;
	private AID LSABidder;
	private double BidByLSA ;

	private double slack;
	private double regret;

	private int currentJobIndex = 0;
	private boolean isBatchComplete = false;;
	private boolean isAllJobsComplete = false;

	// position is used as an index of batch in it's queue.
	// it is used for scheduling sequence of batches
	private int position;
	private double profit;
	private int currentOperationIndex = 0;

	public Batch(String batchId) {
		this.batchId = batchId;
		jobsInBatch = new ArrayList<job>();
	}

	/**
	 * @return id of the customer who ordered this batch
	 */
	public String getCustomerId() {
		return customerId;
	}

	/**
	 * sets the id of the customer for this batchS
	 * @param customerId
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return Regret multiplier for this batch. This multiplier is used for calculation of penalty
	 * while creating schedules. Multiplier depends upon regret of the batch
	 */
	public double getRegretMultiplier() {
		if(this.regret < 1.0)
			return lowRegretMultiplier;
		else if( this.regret < 1.1)
			return MediumRegretMultiplier;
		else
			return HighRegretMultiplier;
	}

	/**
	 * @return The slack for this batch.
	 */
	public double getSlack() {
		return slack;
	}

	/**
	 * set the slack for this batch. Slack is the extra amount of time that we have before the batch gets delayed.
	 * Batch can be delayed by this much amount without being actually late or causing any penalty.
	 * 
	 * @param slack
	 */
	public void setSlack(double slack) {
		this.slack = slack;
	}

	/**
	 * @return regret for this batch
	 */
	public double getRegret() {
		return regret;
	}

	/**
	 * Sets regret for this batch. Regret for any batch is defined as <code> lateness/slack </code>
	 * @param regret
	 */
	public void setRegret(double regret) {
		this.regret = regret;
	}

	/**
	 * @return Customer priority number for the customer of this batch
	 */
	public double getCPN() {
		return CPN;
	}

	/**
	 * Set Customer priority number for the customer of this batch
	 * @param cPN
	 */
	public void setCPN(double cPN) {
		CPN = cPN;
	}

	/**
	 * @return processing cost for this batch
	 */
	public double getCost() {
		return Cost;
	}

	/**
	 * Set processing cost for this batch
	 * @param cost
	 */
	public void setCost(double cost) {
		Cost = cost;
	}

	/**
	 * @return penalty per unit time for this batch if it gets delayed
	 */
	public double getPenaltyRate() {
		return penaltyRate;
	}

	/**
	 * Set penalty per unit time for this batch in case it gets delayed
	 * @param penaltyRate
	 */
	public void setPenaltyRate(double penaltyRate) {
		this.penaltyRate = penaltyRate;
	}

	/**
	 * @return Bid by Local Scheduling agent for this batch.
	 * This is used while bidding for batches. GSA advertises new batch and all Local scheduling agents send their
	 * bid for the advertised batch.
	 */
	public double getBidByLSA() {
		return BidByLSA;
	}

	/**
	 * Set Bid by Local scheduling agent for this batch.
	 * @param bidByLSA
	 */
	public void setBidByLSA(double bidByLSA) {
		BidByLSA = bidByLSA;
	}

	/**
	 * @return position of this batch in the batch-queue of machine. This is used only while scheduling.
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Sets position of this batch in the batch-queue of machine. This is used only while scheduling.
	 * @param position
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * @return total processing time for this batch ( include processing time for all jobs and all operations)
	 */
	public long getTotalBatchProcessingTime() {
		return getFirstJob().getTotalProcessingTime() * getBatchCount();
	}

	//	public void setBatchProcessingTime(long batchProcessingTime) {
	//		this.batchProcessingTime = batchProcessingTime;
	//	}

	/**
	 * @return completion status of this batch
	 */
	public boolean isBatchComplete() {
		return getFirstJob().isComplete();
		//		return isBatchComplete;
	}

	/**
	 * @return true if all jobs within this batch are completed
	 */
	public boolean isAllJobsComplete() {
		return this.isAllJobsComplete;
	}

	/**
	 * Resets the current job index to <code> 0 </code>
	 */
	public void resetJobsComplete() {
		isAllJobsComplete = false;
		this.currentJobIndex = 0;
	}

	/**
	 * @return current job to be processed from this batch
	 */
	public job getCurrentJob() {
		return jobsInBatch.get(currentJobIndex);
	}

	/**
	 * @return index of current job to be processed from this batch
	 */
	public int getCurrentJobNo() {
		return currentJobIndex;
	}

	/**
	 * Increment the currentJobNumber by <code> 1 </code> 
	 */
	public void incrementCurrentJob() {
		this.currentJobIndex++ ;
		/**
		 *  if index becomes >= the size of the batch it means all jobs are done
		 *  index for last job is 'size()-1'
		 */
		if(this.currentJobIndex >= this.jobsInBatch.size() ){
			isAllJobsComplete = true;
		} else {
			isAllJobsComplete = false;
		}
	}

	/**
	 * @return the bid winner local scheduling agent for this batch
	 */
	public AID getWinnerLSA() {
		return WinnerLSA;
	}

	/**
	 * Set the bid winner local scheduling agent for this batch
	 * @param winnerLSA
	 */
	public void setWinnerLSA(AID winnerLSA) {
		WinnerLSA = winnerLSA;
	}

	/**
	 * @return the profit for this batch
	 */
	public double getProfit() {
		return profit;
	}

	/**
	 * Set the profit for this batch
	 * @param profit
	 */
	public void setProfit(double profit) {
		this.profit = profit;
	}

	/**
	 * @return AID of the bidder local scheduling agent for this batch
	 */
	public AID getLSABidder() {
		return LSABidder;
	}

	/**
	 * Sets AID of the bidder local scheduling agent for this batch
	 * @param lSABidder
	 */
	public void setLSABidder(AID lSABidder) {
		LSABidder = lSABidder;
	}

	/**
	 * @return Due date for this batch as specified by the customer
	 */
	public Date getDueDateByCustomer() {
		return dueDateByCustomer;
	}

	/**
	 * Sets due date for this batch as specified by the customer
	 * @param dueDateByCustomer
	 */
	public void setDueDateByCustomer(Date dueDateByCustomer) {
		this.dueDateByCustomer = dueDateByCustomer;
	}

	/**
	 * Sets due date(in milliseconds) for this batch as specified by the customer 
	 * @param dueDateByCustomer
	 */
	public void setDueDateMillisByCustomer(long dueDateByCustomer) {
		this.dueDateByCustomer = new Date(dueDateByCustomer);
	}

	/**
	 * @return Expected due date for this batch. This is used while negotiating for a new order.
	 * On arrival of a new order, GSA calculates and sets expected due date for the batch.
	 */
	public long getExpectedDueDate() {
		return waitingTime;
	}

	/**
	 * Set expected due date for this batch.
	 * @param waitingTime
	 */
	public void setExpectedDueDate(long waitingTime) {
		this.waitingTime = waitingTime;
	}

	/**
	 * @return start time(in milliseconds) of this batch
	 */
	public long getStartTimeMillis() {
		return startTime;
	}

	/**
	 * Sets start time(in milliseconds) for this batch
	 * @param startTime
	 */
	public void setStartTimeMillis(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return completion time of this batch
	 */
	public long getCompletionTime() {
		return completionTime;
	}

	/**
	 * Sets completion time for this batch
	 * @param completionTime
	 */
	public void setCompletionTime(long completionTime) {
		this.completionTime = completionTime;
	}

	/**
	 * @return batch number for this batch. Batch Number is a unique number alotted to each batch.
	 * This is used to uniquely query /cancel any batch.
	 */
	public int getBatchNumber() {
		return batchNo;
	}

	/**
	 * Sets batch number for this batch
	 * @param batchNumber
	 */
	public void setBatchNumber(int batchNumber) {
		this.batchNo = batchNumber;
	}

	/**
	 * @return Generation time for this batch
	 */
	public Date getGenerationTime() {
		return generationTime;
	}

	/**
	 * Sets generation time for this batch
	 * @param generationTime
	 */
	public void setGenerationTime(Date generationTime) {
		this.generationTime = generationTime;
	}

	/**
	 * Set generation time(in milliseconds)
	 * @param generationTime
	 */
	public void setGenerationTime(long generationTime) {
		this.generationTime = new Date(generationTime);
	}

	/**
	 * @return the size of the batch or total number of jobs within the batch
	 */
	public int getBatchCount() {
		return jobsInBatch.size();
	}

	/**
	 * @return List of all jobs within this batch
	 */
	public ArrayList<job> getJobsInBatch() {
		return jobsInBatch;
	}

	/**
	 * Sets list of jobs for this batch
	 * @param jobsInBatch
	 */
	public void setJobsInBatch(ArrayList<job> jobsInBatch) {
		this.jobsInBatch = jobsInBatch;
		for(int i = 0; i < jobsInBatch.size() ; i++) {
			jobsInBatch.get(i).setJobNo(i + 1);
		}
	}

	/**
	 * @return Id of this batch
	 */
	public String getBatchId() {
		return batchId;
	}

	/**
	 * Sets id for this batch
	 * @param batchId
	 */
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	/**
	 * Removes all jobs from the list 
	 */
	public void clearAllJobs() {
		this.jobsInBatch.clear();
	}

	/**
	 * @return first job within the list of all jobs
	 */
	public job getFirstJob() {
		if(! jobsInBatch.isEmpty())
			return jobsInBatch.get(0);

		return null;
	}

	/**
	 * @return Last job within the list of all jobs
	 */
	public job getLastJob() {
		if(! jobsInBatch.isEmpty())
			return jobsInBatch.get(jobsInBatch.size() -1);

		return null;
	}

	/**
	 * @return current operation number being done on this batch
	 */
	public int getCurrentOperationNumber() {
		return currentOperationIndex;
	}

	/**
	 * @return operation type for the current operation being done on this batch
	 */
	public String getCurrentOperationType() {
		return getFirstJob().getCurrentOperation().getJobOperationType();
	}

	/**
	 * increment current operation index by 1 
	 */
	public void IncrementOperationNumber() {
		this.currentOperationIndex ++ ;

		for(int i = 0; i < jobsInBatch.size(); i++) {
			jobsInBatch.get(i).IncrementOperationNumber();
		}
		/**
		 *  if index becomes >= the size of the operations it means all operations are done
		 *  index for last operation is 'size()-1'
		 */
		if(this.currentOperationIndex >= this.getFirstJob().getOperations().size() ) {
			isBatchComplete = true;
		} else {
			this.isBatchComplete = false;
		}
	}

	/**
	 * set Start time of current operation for the batch i.e.
	 * set start time of current operation for first job in the batch 
	 * @param startTime
	 */
	public void setCurrentOperationStartTime(long startTime) {
		this.getFirstJob().getOperations().get(currentOperationIndex).setStartTime(startTime);
	}

	/**
	 * get Start time of current operation for the batch i.e.
	 * get start time of current operation for first job in the batch 
	 */
	public long getCurrentOperationStartTime() {
		return this.getFirstJob().getOperations().get(currentOperationIndex ).getStartTime();
	}

	/**
	 * set completion time of current operation for the batch i.e.
	 * set completion time of current operation for first job in the batch 
	 * @param completiontime
	 */
	public void setCurrentOperationCompletionTime(long completionTime) {
		this.jobsInBatch.get(jobsInBatch.size() - 1).getOperations().
		get(currentOperationIndex).setCompletionTime(completionTime);
	}

	/**
	 * @return Completion time for the current operation for this batch
	 */
	public long getCurrentOperationCompletionTime() {
		return this.jobsInBatch.get(jobsInBatch.size() - 1).getOperations().
				get(currentOperationIndex).getCompletionTime();
	}

	/**
	 * @return processing time for current operation of whole batch
	 */
	public long getCurrentOperationProcessingTime() {
		return this.getFirstJob().getCurrentOperationProcessingTime() * getBatchCount();
	}

	/**
	 * set processing time for current operation for each job in the batch.
	 * processingTime is the time for one job of the corresponding operation
	 * in the batch and not for the whole batch. Processing time should be passed in 
	 * milliseconds.
	 * @param processingTime
	 */
	public void setCurrentOperationProcessingTime(long processingTime) {
		for(int i = 0; i < jobsInBatch.size() ; i++ ) {
			this.jobsInBatch.get(i).setCurrentOperationProcessingTime(processingTime);
		}
	}

	/**
	 * @return Due date for the current operation of this batch
	 */
	public long getCurrentOperationDueDate() {
		return this.jobsInBatch.get(jobsInBatch.size() - 1).getOperations().
				get(currentOperationIndex).getGorLdueDate();
	}
	
	/**
	 * Sets due date for current operation of this batch
	 * @param dueDate
	 */
	public void setCurrentOperationDueDate(long dueDate) {
		this.jobsInBatch.get(jobsInBatch.size() - 1).getOperations().
		get(currentOperationIndex).setGorLdueDate(dueDate);
	}

	/**
	 * @return Number of operations within this batch
	 */
	public int getNumOperations() {
		return getFirstJob().getOperations().size();
	}

	/**
	 * Sets list of operations to be done on this batch
	 * @param ops
	 */
	public void setOperations(ArrayList<jobOperation> ops) {
		for(int i = 0; i < jobsInBatch.size() ; i++) {
			this.jobsInBatch.get(i).setOperations(ops);
		}
	}

	/**
	 * update the job in the batch with the coming job.
	 * @param comingJob
	 */
	public void updateJob(job comingJob) {
		int jNum = comingJob.getJobNo();
		for(int i = 0 ; i < jobsInBatch.size(); i++) {
			if(jobsInBatch.get(i).getJobNo() == jNum) {
				jobsInBatch.set(i, comingJob);
			}
		}
	}

	/**
	 * Resets currentOperationIndex to <code> 0 </code>
	 */
	public void resetCurrentOperationNumber() {
		this.currentOperationIndex = 0;
		isBatchComplete = false;
		for(int i = 0; i < jobsInBatch.size(); i++) {
			jobsInBatch.get(i).resetOpnNoToZero();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((batchId == null) ? 0 : batchId.hashCode());
		result = prime * result + batchNo;
		result = prime * result
				+ ((customerId == null) ? 0 : customerId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Batch other = (Batch) obj;
		if (batchId == null) {
			if (other.batchId != null)
				return false;
		} else if (!batchId.equals(other.batchId))
			return false;
		if (batchNo != other.batchNo)
			return false;
		if (customerId == null) {
			if (other.customerId != null)
				return false;
		} else if (!customerId.equals(other.customerId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Batch [batchNo=" + batchNo + "]";
	}


}
