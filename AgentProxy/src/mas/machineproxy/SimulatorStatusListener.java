package mas.machineproxy;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Anand Prajapati
 * 
 * A listener class to listen for machine's failure.
 * handle any event related to machine's failure here
 */
public class SimulatorStatusListener implements PropertyChangeListener {

	private transient Logger log;
	private Simulator machineSimulator;

	public SimulatorStatusListener(Simulator sim) {
		this.machineSimulator = sim;
		log = LogManager.getLogger();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(Simulator.machineStatusProperty)) {
			
			if(evt.getNewValue().equals(MachineStatus.FAILED)) {
//				log.info("Simulator is in failed state :" );

				machineSimulator.HandleFailure();
			} 
			else if(evt.getNewValue().equals(MachineStatus.IDLE)) {
//				log.info("Simulator is idle " );

			}
			else if(evt.getNewValue().equals(MachineStatus.PROCESSING)) {
//				log.info("Simulator is processing " );

			}
			else if(evt.getNewValue().equals(MachineStatus.UNDER_MAINTENANCE)) {
				log.info("Simulator is under maintenance " );

			}
		}
	}
}
