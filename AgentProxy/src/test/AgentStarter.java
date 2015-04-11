package test;

import jade.BootProfileImpl;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mas.customerproxy.agent.CustomerAgent;
import mas.localSchedulingproxy.agent.LocalSchedulingAgent;
import mas.machineproxy.Simulator;
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
	private static ArrayList<LocalSchedulingAgent> lAgents;
	public static PlatformController controller;
	public static String Ip = "192.168.137.1";
	public static String JadePort = "1099";

	static {
		agents = new HashMap<String, Agent>();
		lAgents = new ArrayList<LocalSchedulingAgent>();
		agents.put(ID.Customer.LocalName, new CustomerAgent());

		LocalSchedulingAgent lagent1 = new LocalSchedulingAgent();
		lAgents.add(lagent1);
		agents.put(ID.LocalScheduler.LocalName + "#1", lagent1);
		agents.put(ID.Maintenance.LocalName + "#1", new LocalMaintenanceAgent());

		//		LocalSchedulingAgent lagent2 = new LocalSchedulingAgent();
		//		lAgents.add(lagent2);
		//		agents.put(ID.LocalScheduler.LocalName+"#2", lagent2);
		//		agents.put(ID.Maintenance.LocalName+"#2", new LocalMaintenanceAgent());

	};

	public static void createSimulator() {
		for(int i = 0 ; i < lAgents.size(); i++) {
			AgentController ac;
			try {
				//				System.out.println(lAgents.get(i) + "@@@@@@@@@@@@@@@@" + controller);
				ac = ((AgentContainer) controller)
						.acceptNewAgent(ID.Machine.LocalName + "#" + (i + 1) ,
								new Simulator(lAgents.get(i) ) );
				ac.start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		/*PropertyConfigurator.configure(AgentStarter.class
				.getResource("log4j.properties"));		*/
		TableUtil.loadFont();
		new AgentStarter();
		//		MachineGUI gui = new MachineGUI(new LocalSchedulingAgent());
		try {
			Thread.sleep(2000);
			createSimulator();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public AgentStarter() {
		log = LogManager.getLogger(this.getClass());
		//		log.info(log.isInfoEnabled());

		List<String> params = new ArrayList<String>();
		params.add("-gui");
		//		params.add("-detect-main:false");

		this.runtime = jade.core.Runtime.instance();
		bootProfile = new ProfileImpl(Ip, 1099, null, false);

		bootProfile.setParameter("hostID", Ip + ":" + JadePort + "/JADE");
		bootProfile.setParameter(Profile.CONTAINER_NAME,  Ip + ":" + JadePort + "/JADE");

		controller = runtime.createAgentContainer(bootProfile);

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
