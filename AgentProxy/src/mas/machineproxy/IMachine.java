package mas.machineproxy;

import java.util.ArrayList;

import mas.machineproxy.component.IComponent;

public interface IMachine {

	public ArrayList<IComponent> getComponents();
	public long getStartTime();
	public MachineStatus getStatus();
}
