package mas.globalSchedulingproxy.agent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bdi4jade.core.BDIAgent;
/**
 * abstact class defining global scheduling agent
 * @author NikhilChilwant
 *
 */
public abstract class AbstractGlobalSchedulingAgent extends BDIAgent{


	private static final long serialVersionUID = 1L;
	private Logger log;
	@Override
	protected void init() {		
		super.init();
		log = LogManager.getLogger();
	}
}
