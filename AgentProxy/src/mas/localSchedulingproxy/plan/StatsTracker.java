package mas.localSchedulingproxy.plan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import mas.job.job;

/**
 * @author Anand Prajapati
 * 
 *  To calculate utilization and average processing times, it stores a queue of 
 *  completed jobs with a maximum size limit of 200;
 *  
 *  list 'sizeQueue' contains snapshots of size of queue of jobs
 *  CumulatedQueueSize stores summation of queue sizes up to now
 *  Average queue size will simply be CumulatedQueueSize/(size of sizeQueue)
 */

public class StatsTracker {

	private int PERIOD = 15;
	private final int SIZE_LIMIT = 200;
	private Queue<Node> doneJobs;
	private Queue<BigDecimal> sizeQueue = new LinkedList<BigDecimal>();
	private BigDecimal cumulatedQueueSize;

	public StatsTracker() {
		this.doneJobs = new LinkedList<Node>();
		this.cumulatedQueueSize = BigDecimal.ZERO;
	}

	private class Node {
		private job jobDone;
		private int operationNumber;

		public Node(job j, int n) {
			this.jobDone = j;
			this.operationNumber = n;
		}
		
		public long getOperationProcessingTime() {
			return jobDone.getProcessTime(operationNumber);
		}
		
		public long getOperationCompletionTime() {
			return jobDone.getCompletionTime(operationNumber);
		}
		
		public long getOperationStartTime() {
			return jobDone.getStartTime(operationNumber);
		}
	}

	public void storeJob(job complete, int n) {
		if( this.doneJobs.size() >= SIZE_LIMIT){
			this.doneJobs.remove();
		}
		this.doneJobs.add(new Node(complete,n));
	}

	public void addSize(double num) {
		BigDecimal bNum = new BigDecimal(num);
		// Add passed size to cumulative size
		cumulatedQueueSize = cumulatedQueueSize.add(bNum);
		Boolean b = sizeQueue.add(bNum);
		if (sizeQueue.size() > PERIOD) {
			cumulatedQueueSize = cumulatedQueueSize.subtract(sizeQueue.remove());
		}
	}

	public BigDecimal getAverageQueueSize() {

		if (sizeQueue.isEmpty()) return BigDecimal.ZERO; 

		BigDecimal divisor = BigDecimal.valueOf(sizeQueue.size());
		return cumulatedQueueSize.divide(divisor, 2, RoundingMode.HALF_UP);
	}

	/**
	 * 
	 * @return utilization of machine based on the stored queue of jobs so far
	 */
	public double geUtilization() {
		double busyTime = 0, makeSpan = 0, utilization = 0;
		Node lastOne = null;

		if (doneJobs.size() < SIZE_LIMIT && doneJobs.size() > 0) {
			Iterator<Node> it = doneJobs.iterator();
			while(it.hasNext()){
				lastOne = it.next();
				busyTime = busyTime + lastOne.getOperationProcessingTime();
			}

			makeSpan = lastOne.getOperationCompletionTime() - 
					doneJobs.peek().getOperationStartTime();
		}
		else if (doneJobs.size() > SIZE_LIMIT) {
			/**
			 * take only SIZE_LIMIT number of jobs
			 */
			int i = 0;
			Iterator<Node> it = doneJobs.iterator();
			Node currNode = null;
			long startTime = 0;
			while(i++ < SIZE_LIMIT && it.hasNext()){
				currNode = it.next();
				if(i == 1) {
					startTime = currNode.getOperationStartTime();
				}
				busyTime = busyTime + currNode.getOperationProcessingTime();
			}
			makeSpan = currNode.getOperationCompletionTime() - startTime;

		}
		if(makeSpan > 0)
			utilization = (busyTime/makeSpan)*100;
		return utilization;
	}

	public double getAvgProcessingTime(){
		double procTimes = 0;

		if (doneJobs.size() <= SIZE_LIMIT && doneJobs.size() > 0) {
			int size = doneJobs.size();
			Iterator<Node> it = doneJobs.iterator();
			while(it.hasNext()) {
				procTimes = procTimes + it.next().getOperationProcessingTime();
			}
			procTimes = procTimes/size;
		}
		else if (doneJobs.size() > SIZE_LIMIT) {
			/**
			 * take average of processing times of only SIZE_LIMIT number of jobs
			 */
			int count = 0;
			Iterator<Node> it = doneJobs.iterator();
			while(count++ < SIZE_LIMIT && it.hasNext()){
				procTimes = procTimes + it.next().getOperationProcessingTime();
			}
			procTimes = procTimes/SIZE_LIMIT;
		}
		return procTimes;
	}
}
