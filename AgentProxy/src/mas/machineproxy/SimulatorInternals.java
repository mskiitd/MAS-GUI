package mas.machineproxy;

import java.io.Serializable;

public class SimulatorInternals implements Serializable{

	private static final long serialVersionUID = 1L;

	//starting time of this machine agent 
	private long epochTime;

	// status of this machine i.e. working/failed etc.
	private MachineStatus status;
	
	public SimulatorInternals() {
		
		this.status = MachineStatus.IDLE;
	}

	public long getEpochTime() {
		return epochTime;
	}

	public void setEpochTime(long epochTime) {
		this.epochTime = epochTime;
	}

	public MachineStatus getStatus() {
		return status;
	}

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
