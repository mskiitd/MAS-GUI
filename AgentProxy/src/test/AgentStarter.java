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

import javax.swing.SwingUtilities;

import mas.blackboard.blackboard;
import mas.customerproxy.agent.CustomerAgent;
import mas.globalSchedulingproxy.agent.GlobalSchedulingAgent;
import mas.localSchedulingproxy.agent.LocalSchedulingAgent;
import mas.machineproxy.Simulator;
import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import mas.util.ID;
import mas.util.TableUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import configuration.AgentToStart;
import configuration.ConfigFrame;
import configuration.MainContainer;
import configuration.Utils;

public class AgentStarter {

	private static Map<String, Agent> agents;
	private Profile bootProfile;
	private Logger log;
	private jade.core.Runtime runtime;
	private static ArrayList<LocalSchedulingAgent> lAgents;
	public static PlatformController controller;
	private static ConfigFrame config;
	public static MainContainer container;
	public static String ipAddress ;
	public static int JadePort;

	public static void start(AgentToStart agentNameToStart) {
		agents = new HashMap<String, Agent>();
		lAgents = new ArrayList<LocalSchedulingAgent>();

		switch(agentNameToStart) {

		case GSA:
			agents.put(ID.Blackboard.LocalName, new blackboard());
			agents.put(ID.GlobalScheduler.LocalName, new GlobalSchedulingAgent());
			container = MainContainer.local;
			break;

		case Machine:
			LocalSchedulingAgent lagent1 = new LocalSchedulingAgent();
			lAgents.add(lagent1);
			agents.put(ID.LocalScheduler.LocalName + "#1", lagent1);
			agents.put(ID.Maintenance.LocalName + "#1", new LocalMaintenanceAgent());
			container = MainContainer.remote;
			
//			LocalSchedulingAgent lagent2 = new LocalSchedulingAgent();
//			lAgents.add(lagent2);
//			agents.put(ID.LocalScheduler.LocalName+"#2", lagent2);
//			agents.put(ID.Maintenance.LocalName+"#2", new LocalMaintenanceAgent());
			break;

		case customer:
			agents.put(ID.Customer.LocalName+"#1", new CustomerAgent());
//			agents.put(ID.Customer.LocalName+"#2", new CustomerAgent());
			container = MainContainer.remote;
			break;

		case All:
			agents = new HashMap<String, Agent>();
			lAgents = new ArrayList<LocalSchedulingAgent>();

			agents.put(ID.Blackboard.LocalName, new blackboard());
			agents.put(ID.GlobalScheduler.LocalName, new GlobalSchedulingAgent());

			LocalSchedulingAgent lagent_1 = new LocalSchedulingAgent();
			lAgents.add(lagent_1);
			agents.put(ID.LocalScheduler.LocalName + "#1", lagent_1);
			agents.put(ID.Maintenance.LocalName + "#1", new LocalMaintenanceAgent());

//			LocalSchedulingAgent lagent_2 = new LocalSchedulingAgent();
//			lAgents.add(lagent_2);
//			agents.put(ID.LocalScheduler.LocalName+"#2", lagent_2);
//			agents.put(ID.Maintenance.LocalName+"#2", new LocalMaintenanceAgent());
			agents.put(ID.Customer.LocalName+"#1", new CustomerAgent());	
//			agents.put(ID.Customer.LocalName+"#2", new CustomerAgent());
			container = MainContainer.local;
			break;
		}
		new AgentStarter();
		createSimulator();
	}

	public static void createSimulator() {
		for(int i = 0; i < lAgents.size(); i++) {
			AgentController ac;
			try {
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
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				config = new ConfigFrame();
			}
		});
	}

	public AgentStarter() {
		log = LogManager.getLogger();

		this.runtime = jade.core.Runtime.instance();
		if(container == MainContainer.local) {
			
			bootProfile = new ProfileImpl(true);
			bootProfile.setParameter(Profile.GUI,String.valueOf(true) );
			bootProfile.setParameter(Profile.LOCAL_HOST, "10.204.153.24");
			bootProfile.setParameter(Profile.LOCAL_PORT, String.valueOf(1090) );
			controller = runtime.createMainContainer(bootProfile);
			log.info("Host IP address of the system is : " + Utils.getMyIp());
		}else {
			bootProfile = new ProfileImpl(false);
			bootProfile.setParameter(Profile.MAIN_HOST, ipAddress);
			bootProfile.setParameter(Profile.MAIN_PORT,String.valueOf(JadePort));
			controller = runtime.createAgentContainer(bootProfile);
		}

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
