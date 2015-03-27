package test;

import jade.BootProfileImpl;
import jade.core.Agent;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import mas.blackboard.blackboard;
import mas.customerproxy.agent.CustomerAgent;
import mas.globalSchedulingproxy.agent.GlobalSchedulingAgent;
import mas.jobproxy.job;
import mas.localSchedulingproxy.agent.LocalSchedulingAgent;
import mas.machineproxy.Simulator;
import mas.machineproxy.gui.MachineGUI;
import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import mas.util.ID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alee.laf.WebLookAndFeel;

public class AgentStarter {

	private static final Map<String, Agent> agents;
	private ProfileImpl bootProfile;
	private Logger log;
	private jade.core.Runtime runtime;
	public static Font font;

	static {
		agents = new HashMap<String, Agent>();
		agents.put("blackboard", new blackboard());
		agents.put("customer", new CustomerAgent());

		agents.put(ID.GlobalScheduler.LocalName, new GlobalSchedulingAgent());
		agents.put(ID.Machine.LocalName+"#1", new Simulator());
		agents.put(ID.LocalScheduler.LocalName+"#1", new LocalSchedulingAgent());
		agents.put(ID.Maintenance.LocalName+"#1", new LocalMaintenanceAgent());
	};

	public static void setUIFont (javax.swing.plaf.FontUIResource f){
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get (key);
			if (value != null && value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put (key, f);
		}
	} 

	public static void main(String[] args) {
		/*PropertyConfigurator.configure(AgentStarter.class
				.getResource("log4j.properties"));		*/

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				try {
					ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/Inconsolata.otf")));

					File font_file = new File("resources/Inconsolata.otf");
					font = Font.createFont(Font.TRUETYPE_FONT, font_file).
							deriveFont(Font.PLAIN, 18f);
					
					  WebLookAndFeel.install ();
//
//					for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//						if ("Nimbus".equals(info.getName())) {
//							UIManager.setLookAndFeel(info.getClassName());
//							break;
//						}
//					}
					setUIFont (new javax.swing.plaf.FontUIResource(font));
				} catch (FontFormatException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		} );

		//		MachineGUI m = new MachineGUI(new LocalSchedulingAgent());
		//		ArrayList<job> j = new ArrayList<job>();
		//		j.add(new job.Builder("1").jobCPN(1).build());
		//		
		//		j.add(new job.Builder("2").jobCPN(2).build());
		//		m.updateQueue(j);
		new AgentStarter();
		//				GSAproxyGUI ggui = new GSAproxyGUI(new GlobalSchedulingAgent());
	}

	public AgentStarter() {
		log = LogManager.getLogger(this.getClass());
		//		log.info(log.isInfoEnabled());

		List<String> params = new ArrayList<String>();
		//		params.add("-gui");
		//		params.add("-detect-main:false");

		log.info("parameters for console :" + params);

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