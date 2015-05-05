package mas.machineproxy;

/**
 * 
 * @author Anand Prajapati
 * Enum constants to define current state of machine
 * It can be one of the following:-
 * Idle
 * Processing a job
 * Failed
 * Under maintenance
 * Paused due to delaying maintenance too much
 *
 */
public enum MachineStatus {
	IDLE,
	PROCESSING,
	FAILED,
	UNDER_MAINTENANCE,
	PAUSED,
}
