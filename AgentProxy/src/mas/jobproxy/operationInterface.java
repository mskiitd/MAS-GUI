package mas.jobproxy;

public interface operationInterface {
	
	/**
	 * @return processing time for this operation
	 */
	public long getProcessingTime();
	public String getJobOperationType();
	public long getCompletionTime();
	public void setCompletionTime(long globalDueDate);
}
