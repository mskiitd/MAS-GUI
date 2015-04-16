package mas.machineproxy.behaviors;

import jade.core.behaviours.Behaviour;
import mas.machineproxy.Simulator;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Anand Prajapati
 * this behavior marks the machine as ready for accepting a new batch
 */
public class GiveMeJobBehavior extends Behaviour{

	private static final long serialVersionUID = 1L;
	private Logger log;
	private boolean done;
	private int step;
	private Simulator machineSimulator;

	public GiveMeJobBehavior(Simulator sim) {
		step = 0;
		done = false;
		this.machineSimulator = sim;
		log = LogManager.getLogger();
		log.info("asking for job from LSA : ");
	}

	@Override
	public void action() {
		switch(step) {
		case 0:
			ZoneDataUpdate giveJobIndicator = new ZoneDataUpdate.
			Builder(ID.Machine.ZoneData.askJobFromLSA).
			value("1").Build();

			AgentUtil.sendZoneDataUpdate(Simulator.blackboardAgent,
					giveJobIndicator, myAgent);
			step = 1;
			break;
		case 1:
			if(machineSimulator.getCurrentBatch() == null) {
				step = 0;
				block(1000);
			}
			else {
				done = true;
			}
			break;
		}

	}

	@Override
	public boolean done() {
		return done;
	}
}
