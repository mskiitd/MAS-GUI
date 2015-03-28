package mas.jobproxy;

public interface operationInterface {
	public long getProcessingTime();
	public String getJobOperationType();
	public long getDueDate();
	public void setDueDate(long globalDueDate);
}
