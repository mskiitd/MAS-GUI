package mas.machineproxy;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import mas.machineproxy.behaviors.HandleSimulatorFailedBehavior;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimulatorStatusListener implements PropertyChangeListener {

	private transient Logger log;
	private Simulator sim;

	public SimulatorStatusListener(Simulator sim) {
		this.sim = sim;
		log = LogManager.getLogger();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(Simulator.machineStatusProperty)) {
			
			if(evt.getNewValue().equals(MachineStatus.FAILED)) {
				log.info("Simulator is in failed state :" );

				sim.addBehaviour(new HandleSimulatorFailedBehavior());
			} 
			else if(evt.getNewValue().equals(MachineStatus.IDLE)) {
				log.info("Simulator is idle " );

			}
			else if(evt.getNewValue().equals(MachineStatus.PROCESSING)) {
				log.info("Simulator is processing " );

			}
			else if(evt.getNewValue().equals(MachineStatus.UNDER_MAINTENANCE)) {
				log.info("Simulator is under maintenance " );

			}
		}
	}
}
