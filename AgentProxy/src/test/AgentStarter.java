package test;

import jade.BootProfileImpl;
import jade.core.Agent;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mas.blackboard.blackboard;
import mas.customerproxy.agent.CustomerAgent;
import mas.globalSchedulingproxy.agent.GlobalSchedulingAgent;
import mas.localSchedulingproxy.agent.LocalSchedulingAgent;
import mas.machineproxy.Simulator;
import mas.machineproxy.gui.MachineGUI;
import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import mas.util.ID;
import mas.util.TableUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AgentStarter {

	private static final Map<String, Agent> agents;
	private ProfileImpl bootProfile;
	private Logger log;
	private jade.core.Runtime runtime;

	static {
		agents = new HashMap<String, Agent>();
		agents.put(ID.Blackboard.LocalName, new blackboard());
		agents.put(ID.Customer.LocalName, new CustomerAgent());

		agents.put(ID.GlobalScheduler.LocalName, new GlobalSchedulingAgent());
		agents.put(ID.Machine.LocalName + "#1", new Simulator());
		agents.put(ID.LocalScheduler.LocalName+"#1", new LocalSchedulingAgent());
		agents.put(ID.Maintenance.LocalName+"#1", new LocalMaintenanceAgent());
		
		/*agents.put(ID.Machine.LocalName + "#2", new Simulator());
		agents.put(ID.LocalScheduler.LocalName+"#2", new LocalSchedulingAgent());
		agents.put(ID.Maintenance.LocalName+"#2", new LocalMaintenanceAgent())*/;
	};

	public static void main(String[] args) {
		/*PropertyConfigurator.configure(AgentStarter.class
				.getResource("log4j.properties"));		*/
		TableUtil.loadFont();
//		try {
//			Thread.sleep(2000);
//			new MachineGUI(new LocalSchedulingAgent());
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		new AgentStarter();
	}

	public AgentStarter() {
		log = LogManager.getLogger(this.getClass());
		//		log.info(log.isInfoEnabled());

		List<String> params = new ArrayList<String>();
		//		params.add("-gui");
		//		params.add("-detect-main:false");

		this.bootProfile = new BootProfileImpl(params.toArray(new String[0]));

		this.runtime = jade.core.Runtime.instance();

		PlatformController controller = runtime
				.createMainContainer(bootProfile);		


		for (String agentName : agents.keySet()) {
			try {
				AgentController ac = ((AgentContainer) controller)
						.acceptNewAgent(agentName, agents.get(agentName));
				ac.start();

			} catch (Exception e) {
				log.error(e);
				System.out.println(e);
			}
		}
	}
}