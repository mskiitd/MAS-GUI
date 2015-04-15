package mas.jobproxy;

public interface operationInterface {
	
	public long getProcessingTime();
	public String getJobOperationType();
	public long getCompletionTime();
	public void setCompletionTime(long globalDueDate);
}
