package mas.machineproxy.behaviors;

import jade.core.behaviours.OneShotBehaviour;
import mas.machineproxy.Simulator;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GiveMeJobBehavior extends OneShotBehaviour{

	private static final long serialVersionUID = 1L;
	private Logger log;

	@Override
	public void action() {
		
		log = LogManager.getLogger();
		log.info("asking for job from LSA : ");
		
		ZoneDataUpdate giveJobIndicator = new ZoneDataUpdate.Builder(ID.Machine.ZoneData.askJobFromLSA)
			.value("1").Build();
		
		/*ZoneDataUpdate giveJobIndicator = new ZoneDataUpdate(
				ID.Machine.ZoneData.askJobFromLSA, "1");*/

		AgentUtil.sendZoneDataUpdate(Simulator.blackboardAgent,
				giveJobIndicator, myAgent);
	}
}
