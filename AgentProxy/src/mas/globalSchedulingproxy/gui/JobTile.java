package mas.globalSchedulingproxy.gui;
import java.awt.Font;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.*;
import mas.jobproxy.Batch;
import mas.jobproxy.job;

public class JobTile{

	private String jobID, jobName;
	private Date custStartDate;
	private Date custDueDate;
	private int priority;
	private int JobNo;
	private double processingTime; //in seconds
	private Date startDatebyGSA,dueDatebyGSA;//by GSA
	private Batch givenBatch;
	
	public JobTile(Batch batch){
		jobID=batch.getBatchId();
		custStartDate=new Date(batch.getStartTimeMillis()); 
		//custStartDate currently set by GSA when it recieves confirmation of order after negotiation
		custDueDate=batch.getDueDateByCustomer();
		JobNo=batch.getBatchNumber();
		priority=(int) batch.getCPN();
		processingTime=(double)batch.getTotalProcessingTime()/1000.0;
		startDatebyGSA=new Date(0);
		dueDatebyGSA=new Date(0);
		jobName="unknown";
		givenBatch=batch;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Date getCustDueDate() {
		return custDueDate;
	}

	public void setCustDueDate(Date dueDate) {
		this.custDueDate = dueDate;
	}

	public Date getCustStartDate() {
		return custStartDate;
	}

	public void setCustStartDate(Date startDate) {
		this.custStartDate = startDate;
	}

	public String getBatchID() {
		return jobID;
	}

	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	public int getJObNo() {
		return JobNo;
	}

	public double getProcessingTime() {
		return processingTime;
	}

	public void setProcessingTime(long processingTime) {
		this.processingTime = ((double)processingTime)/1000.0;
	}

	public Date getDueDatebyGSA() {
		return dueDatebyGSA;
	}

	public void setDueDatebyGSA(Date dueDate) {
		this.dueDatebyGSA = dueDate;
	}

	public Date getStartDatebyGSA() {
		return startDatebyGSA;
	}

	public void setStartDatebyGSA(Date startDate) {
		this.startDatebyGSA = startDate;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Batch getBatch() {
		return givenBatch;
	}
}
