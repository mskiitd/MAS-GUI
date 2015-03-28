package mas.machineproxy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;

import mas.jobproxy.OperationType;
import mas.machineproxy.component.IComponent;

public class SimulatorInternals implements Serializable{

	private static final long serialVersionUID = 1L;

	// machine's type i.e. ability to perform number of operations
	private EnumSet<OperationType> supportedOperations;

	// list of components of machine
	private ArrayList<IComponent> myComponents;

	//starting time of this machine agent 
	private long epochTime;

	// status of this machine i.e. working/failed etc.
	private MachineStatus status;
	
	public SimulatorInternals() {
		this.myComponents = new ArrayList<IComponent>();
//		this.supportedOperations = EnumSet.of( OperationType.Operation_1,
//				OperationType.Operation_2, OperationType.Operation_3 );
		
		this.status = MachineStatus.IDLE;
	}

	public EnumSet<OperationType> getSupportedOperations() {
		return supportedOperations;
	}

	public void setSupportedOperations(EnumSet<OperationType> supportedOperations) {
		this.supportedOperations = supportedOperations;
	}

	public ArrayList<IComponent> getComponents() {
		return myComponents;
	}

	public void setMyComponents(ArrayList<IComponent> myComponents) {
		this.myComponents = myComponents;
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
