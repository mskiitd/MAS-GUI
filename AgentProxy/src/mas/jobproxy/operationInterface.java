package mas.jobproxy;

public interface operationInterface {
	
	public long getProcessingTime();
	public String getJobOperationType();
	public long getFinishTime();
	public void setFinishTime(long globalDueDate);
}
