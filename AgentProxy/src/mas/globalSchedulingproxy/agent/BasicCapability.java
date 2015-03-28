package mas.globalSchedulingproxy.agent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BasicCapability extends AbstractGSCapability{

	private static final long serialVersionUID = 1L;
	private Logger log;

	public BasicCapability(){
		super();
	}

	@Override
	protected void setup() {
		log = LogManager.getLogger();
		super.setup();
	}
}
