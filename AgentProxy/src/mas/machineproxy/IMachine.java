package mas.machineproxy;

import java.util.ArrayList;

import mas.machineproxy.component.IComponent;

public interface IMachine {

	public long getStartTime();
	public MachineStatus getStatus();
}
