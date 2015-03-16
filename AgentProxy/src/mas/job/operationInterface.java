package mas.job;

public interface operationInterface {
	public long getProcessingTime();
	public OperationType getJobOperationType();
	public long getDueDate();
	public void setDueDate(long globalDueDate);
}
