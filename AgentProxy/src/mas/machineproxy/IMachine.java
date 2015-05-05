package mas.machineproxy;

/**
 * @author Anand Prajapati
 * 
 * Interface to be implemented by machine.
 * This interface helps maintenance agent to deal with any machine class.
 *
 */
public interface IMachine {

	/**
	 * @return start time of the machine simulator
	 */
	public long getStartTime();
	/**
	 * 
	 * @return current status of the machine simulator
	 */
	public MachineStatus getStatus();
}
