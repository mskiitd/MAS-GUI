package mas.job;

import jade.core.AID;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/** Represents a manufacturing shop floor job
 */

public class job implements Serializable{

	private static final long serialVersionUID = 1L;

	private static final double lowRegretMultiplier = 1,
			MediumRegretMultiplier = 2,
			HighRegretMultiplier = 3;

	//Required parameters
	private int jobNo;
	private String jobID;
	private double CPN;
	private double Cost;
	private double penaltyRate;
	private Date startTime;
	private Date jobDuedate;
	private ArrayList<jobOperation> operations;
	private Date generationTime;
	private Date completionTime;

	//Optional parameters initialized to default values
	public int currentOperationNumber = 0;
	public int acceptance = 0;
	public double slack;
	private double regret;
	private int position;
	private double BidByLSA ;

	private double waitingTime;
	private double profit;
	private AID WinnerLSA;
	private AID LSABidder;

	private double deliveryTime;
	private double deliveryStatus;

	public static class Builder {
		//Required parameters
		private int jobNo;
		private String jobID;
		private double CPN;
		private double Cost;
		private double Penalty;
		private Date dDate;
		private Date genTime;
		// Optional parameters - initialized to default values
		private ArrayList<jobOperation> jOperations;

		public Builder(String jobID) {
			this.jobID = jobID;
			this.jOperations = new ArrayList<jobOperation>();
		}

		public Builder jobCost(double val)
		{ Cost = val; return this; }

		public Builder jobCPN(double val)
		{ CPN = val; return this; }

		public Builder jobPenalty(double val)
		{ Penalty = val; return this; }

		public Builder jobDueDateTime(Date val)
		{ dDate = val; return this; }

		public Builder jobDueDateTime(long val)
		{ dDate = new Date(val); return this; }

		public Builder jobGenTime(Date val)
		{ genTime = val; return this; }

		public Builder jobGenTime(Long val)
		{ genTime = new Date(val); return this; }

		public Builder jobOperation(ArrayList<jobOperation> val)
		{ jOperations.addAll(val); return this; }

		public job build() {
			return new job(this);
		}
	}
	private job(Builder builder) {
		jobID = builder.jobID;
		jobNo = builder.jobNo;
		CPN = builder.CPN;
		Cost = builder.Cost;
		penaltyRate = builder.Penalty;
		jobDuedate = builder.dDate;
		generationTime = builder.genTime;
		this.operations = new ArrayList<jobOperation>();
		operations.addAll(builder.jOperations);
	}

	public double getRegretMultiplier(){
		if(this.regret < 1.0)
			return lowRegretMultiplier;
		else if( this.regret < 1.1)
			return MediumRegretMultiplier;
		else
			return HighRegretMultiplier;
	}

	@Override
	public boolean equals(Object o) {
		if( o == this )
			return true;
		if( !(o instanceof job))
			return false;

		job j = (job)o;
		return (this.jobID == j.jobID) &&
				(this.jobNo == j.jobNo);
	}
	
	public double getPenaltyRate() {
		return penaltyRate;
	}

	public void setPenaltyRate(double penaltyRate) {
		this.penaltyRate = penaltyRate;
	}
	
	public void setCurrentOperationDueDate(long dueDate) {
		this.operations.get(currentOperationNumber).setDueDate(dueDate);
	}
	
	public long getCurrentOperationDueDate() {
		return this.operations.get(currentOperationNumber).getDueDate();
	}
	
	public void setCurrentOperationStartTime(long startTime) {
		this.operations.get(currentOperationNumber).setStartTime(startTime);
	}
	
	public long getCurrentOperationStartTime() {
		return this.operations.get(currentOperationNumber).getStartTime();
	}

	public ArrayList<jobDimension> getCurrentOperationDimensions() {
		return this.operations.get(this.currentOperationNumber).getjDims();
	}
	
	public ArrayList<jobAttribute> getCurrentOperationAttributes() {
		return this.operations.get(currentOperationNumber).getjAttributes();
	}
	
	public void setCurrentOperationDimension(ArrayList<jobDimension> jDim) {
		this.operations.get(currentOperationNumber).setjDims(jDim);
	}
	
	public void setCurrentOperationAttributes(ArrayList<jobAttribute> jAtt) {
		this.operations.get(currentOperationNumber).setjAttributes(jAtt);
	}

	public ArrayList<jobOperation> getOperations() {
		return operations;
	}

	public jobOperation getCurrentOperation() {
		if(this.currentOperationNumber < operations.size())
			return operations.get(this.currentOperationNumber);
		return null;
	}
	
	public int getCurrentOperationNumber() {
		return currentOperationNumber;
	}

	public void setCurrentOperationNumber(int currentOperationNumber) {
		this.currentOperationNumber = currentOperationNumber;
	}

	public long getCurrentOperationProcessTime() {
		return operations.get(this.currentOperationNumber).getProcessingTime();
	}
	
	public long getProcessTime(int index) {
		return this.operations.get(index).getProcessingTime();
	}
	
	public void setCurrentOperationProcessingTime(long processingTime) {
		operations.get(currentOperationNumber).setProcessingTime(processingTime);
	}
	
	public long getTotalProcessingTime() {
		long total = 0;
		for(int i = 0 ; i < operations.size(); i++){
			total += operations.get(i).getProcessingTime();
		}
		return total;
	}
	
	public void setOperations(ArrayList<jobOperation> operations) {
		this.operations = operations;
	}

	public int getPosition() {
		return position;
	}

	public double getSlack() {
		return slack;
	}

	public void setSlack(double slack) {
		this.slack = slack;
	}

	public double getRegret() {
		return regret;
	}

	public void setRegret(double regret) {
		this.regret = regret;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getJobID(){
		return this.jobID;
	}

	public int getJobNo() {
		return jobNo;
	}

	public void setJobNo(int jobNo) {
		this.jobNo = jobNo;
	}

	public double getCPN() {
		return CPN;
	}

	public void setCPN(double cPN) {
		CPN = cPN;
	}

	public Date getStartTime() {
		return startTime;
	}
	
	public void setJobStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setJobStartTime(long startTime) {
		this.startTime = new Date(startTime);
	}

	public Date getGenerationTime() {
		return generationTime;
	}

	public void setGenerationTime(Date generationTime) {
		this.generationTime = generationTime;
	}

	public void setGenerationTime(long generationTime) {
		this.generationTime = new Date(generationTime);
	}

	public Date getCompletionTime() {
		return completionTime;
	}
	
	public long getCompletionTime(int index) {
		return this.operations.get(index).getCompletionTime();
	}
	
	public long getStartTime(int index) {
		return this.operations.get(index).getStartTime();
	}

	public void setCompletionTime(Date completionTime) {
		this.completionTime = completionTime;
	}

	public void setCompletionTime(long completionTime) {
		this.completionTime = new Date(completionTime);
	}

	public int getAcceptance() {
		return acceptance;
	}

	public void setAcceptance(int acceptance) {
		this.acceptance = acceptance;
	}

	public double getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(double waitingTime) {
		this.waitingTime = waitingTime;
	}

	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	public Date getJobDuedate() {
		return jobDuedate;
	}

	public void setJobDuedate(Date duedate) {
		this.jobDuedate = duedate;
	}

	public void setJobDuedate(long duedate) {
		this.jobDuedate.setTime(duedate);
	}

	public double getCost() {
		return Cost;
	}

	public void setCost(double cost) {
		Cost = cost;
	}
	public double getBidByLSA() {
		return BidByLSA;
	}

	public void setBidByLSA(double bidByLSA) {
		BidByLSA = bidByLSA;
	}

	/**
	 * 
	 * @return  LSA which won bid
	 */
	public AID getBidWinnerLSA() {
		return WinnerLSA;
	}

	public void setBidWinnerLSA(AID winner_LSA){
		this.WinnerLSA=winner_LSA;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	/**
	 * LSA which is sending bid proposal (bid winners hasn't been announced yet)
	 * @param LSA
	 * 
	 */
	public void setLSABidder(AID LSA) { 
		this.LSABidder = LSA; 

	}

	public AID getLSABidder(){
		return this.LSABidder;
	}

}

