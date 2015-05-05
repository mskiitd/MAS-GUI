package mas.machineproxy;

import java.io.Serializable;

/**
 * @author Anand Prajapati
 * 
 * This class contains internal details of simulator which are used to asses health of simulator by maintenance agent.
 *
 */
public class SimulatorInternals implements Serializable{

	private static final long serialVersionUID = 1L;

	//starting time of this machine agent 
	private long epochTime;

	// status of this machine i.e. working/failed etc.
	private MachineStatus status;
	
	public SimulatorInternals() {
		this.status = MachineStatus.IDLE;
	}

	/**
	 * @return time when simulator was started
	 */
	public long getEpochTime() {
		return epochTime;
	}

	/**
	 * @param epochTime
	 * Sets time when simulator starts
	 */
	public void setEpochTime(long epochTime) {
		this.epochTime = epochTime;
	}

	/**
	 * @return current status of simulator
	 */
	public MachineStatus getStatus() {
		return status;
	}

	/**
	 * @param status
	 * </br> Sets current status of simulator
	 */
	public void setStatus(MachineStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return new StringBuilder().
				append(status).
				toString();
	}
	
	
}
