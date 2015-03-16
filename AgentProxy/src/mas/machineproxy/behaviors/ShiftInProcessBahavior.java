package mas.machineproxy.behaviors;

import jade.core.behaviours.CyclicBehaviour;
import mas.machineproxy.MachineStatus;
import mas.machineproxy.Methods;
import mas.machineproxy.Simulator;

public class ShiftInProcessBahavior extends CyclicBehaviour {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean mean,sd;
	int count;
	double expRandom;
	private int step = 0;
	private long timeToOccur;
	private Simulator machineSimulator;

	/**
	 * shifter shifts the process mean or standard deviation
	 * @param byMean
	 * @param bySd
	 */
	public ShiftInProcessBahavior(boolean byMean, boolean bySd ) {
		this.mean = byMean;
		this.sd = bySd;
		
	}

	@Override
	public void action() {

		switch(step) {
		case 0:
			machineSimulator = (Simulator) getDataStore().get(Simulator.simulatorStoreName);
			expRandom = Methods.rexp(1/machineSimulator.getRateShift(), 1)[0];
			timeToOccur = (long) Math.max(1, expRandom*1000);
			step = 1;
			break;
		case 1:
			if(machineSimulator.getStatus() != MachineStatus.FAILED &&
			timeToOccur >= 0) {
				timeToOccur = timeToOccur - Simulator.TIME_STEP;
				block(Simulator.TIME_STEP);
			}
			else if(timeToOccur < 0) {
				step = 2;
			}
			else {
				// machine is failed
				block(Simulator.TIME_STEP);
			}
			break;
		case 2:

			if(mean == true) {
				machineSimulator.setMean_shift(machineSimulator.getMean_shift()*(1.0 + 
						(Methods.normalRandom(machineSimulator.getMean_shiftInMean(),
								machineSimulator.getSd_shiftInMean())/100.0) ) );	
			}
			if(sd == true) {
				machineSimulator.setSd_shift( machineSimulator.getSd_shift()*(1.0 + 
						(Methods.normalRandom(machineSimulator.getMean_shiftInSd(),
								machineSimulator.getSd_shiftInSd())/100.0) ) );
			}

			step = 0;
			break;
		}
	}
}
