package test;

import jade.BootProfileImpl;
import jade.core.Agent;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;
import jade.wrapper.StaleProxyException;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import mas.blackboard.blackboard;
import mas.customerproxy.agent.CustomerAgent;
import mas.globalSchedulingproxy.agent.GlobalSchedulingAgent;
import mas.localSchedulingproxy.agent.LocalSchedulingAgent;
import mas.machineproxy.Simulator;
import mas.machineproxy.gui.MachineGUI;
import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import mas.maintenanceproxy.gui.MaintenanceGUI;
import mas.util.ID;
import mas.util.ID.Maintenance;
import mas.util.TableUtil;
import net.miginfocom.swing.MigLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sun.launcher.resources.launcher;

import com.alee.extended.breadcrumb.WebBreadcrumbPanel;
import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.radiobutton.WebRadioButton;
import com.alee.utils.SwingUtils;

public class AgentStarter {

	private static Map<String, Agent> agents;
	private ProfileImpl bootProfile;
	private Logger log;
	private jade.core.Runtime runtime;
	private static ArrayList<LocalSchedulingAgent> lAgents;
	public static PlatformController controller;
	private enum agentToStart{
		GSA,Machine,customer
	}

	public static void start(agentToStart agentNameToStart){
		
		agents = new HashMap<String, Agent>();
		lAgents = new ArrayList<LocalSchedulingAgent>();
		
		switch(agentNameToStart){
		case GSA:
			agents.put(ID.Blackboard.LocalName, new blackboard());
			agents.put(ID.GlobalScheduler.LocalName, new GlobalSchedulingAgent());
			break;
			
		case Machine:
			LocalSchedulingAgent lagent1 = new LocalSchedulingAgent();
			lAgents.add(lagent1);
			agents.put(ID.LocalScheduler.LocalName + "#1", lagent1);
			agents.put(ID.Maintenance.LocalName + "#1", new LocalMaintenanceAgent());

//			LocalSchedulingAgent lagent2 = new LocalSchedulingAgent();
//			lAgents.add(lagent2);
//			agents.put(ID.LocalScheduler.LocalName+"#2", lagent2);
//			agents.put(ID.Maintenance.LocalName+"#2", new LocalMaintenanceAgent());

			break;
			
		case customer:
			agents.put(ID.Customer.LocalName, new CustomerAgent());		
			break;
			
			
		}
		
		TableUtil.loadFont();
		new AgentStarter();
//		MachineGUI gui = new MachineGUI(new LocalSchedulingAgent());
			createSimulator();
	}
	
	public static void startAllAgents(){
		
		agents = new HashMap<String, Agent>();
		lAgents = new ArrayList<LocalSchedulingAgent>();
		
		
			agents.put(ID.Blackboard.LocalName, new blackboard());
			agents.put(ID.GlobalScheduler.LocalName, new GlobalSchedulingAgent());
			
			LocalSchedulingAgent lagent1 = new LocalSchedulingAgent();
			lAgents.add(lagent1);
			agents.put(ID.LocalScheduler.LocalName + "#1", lagent1);
			agents.put(ID.Maintenance.LocalName + "#1", new LocalMaintenanceAgent());

//			LocalSchedulingAgent lagent2 = new LocalSchedulingAgent();
//			lAgents.add(lagent2);
//			agents.put(ID.LocalScheduler.LocalName+"#2", lagent2);
//			agents.put(ID.Maintenance.LocalName+"#2", new LocalMaintenanceAgent());

			
			agents.put(ID.Customer.LocalName, new CustomerAgent());		
			
		
		
		TableUtil.loadFont();
		new AgentStarter();
//		MachineGUI gui = new MachineGUI(new LocalSchedulingAgent());

			createSimulator();
		
	}

	public static void createSimulator() {
		for(int i=0; i < lAgents.size(); i++) {
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
//		ChooseAgentFrame CAF=new ChooseAgentFrame();
		createConfigurationFrame();
		
		
/*		
		TableUtil.loadFont();
		new AgentStarter();
//		MachineGUI gui = new MachineGUI(new LocalSchedulingAgent());
		try {
			Thread.sleep(2000);
			createSimulator();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
	}

	private static void createConfigurationFrame() {
		final JFrame configFrame=new JFrame();
		
		configFrame.setLayout(new MigLayout());
		configFrame.setTitle("Configuration");
		final WebBreadcrumbPanel panel1 = new WebBreadcrumbPanel ( new VerticalFlowLayout() );
		panel1.add(new JLabel("Choose Agent to start"));
		final WebRadioButton GSAbtn = new WebRadioButton ( "Global Scheduling Agent" );
        panel1.add ( GSAbtn );
		final WebRadioButton machineBtn = new WebRadioButton ( "Machine" ); 
        panel1.add (machineBtn);
        final WebRadioButton customerbtn = new WebRadioButton ( "Customer" ); 
        panel1.add (customerbtn);
        final WebRadioButton allAgentsbtn = new WebRadioButton ( "All Agents" ); 
        panel1.add (allAgentsbtn);
        SwingUtils.groupButtons ( panel1 );
		
        configFrame.add(panel1, "wrap");
        
        JButton submit = new JButton();
        submit.setText("OK, start this agent");
        ActionListener selectionListener =new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(GSAbtn.isSelected()){
					start(agentToStart.GSA);
					configFrame.dispose();
				}
				else if(machineBtn.isSelected()){
					
					configFrame.dispose();
					showIPFrame(agentToStart.Machine);
				}
				else if(customerbtn.isSelected()){
					
					configFrame.dispose();
					showIPFrame(agentToStart.customer);
					
				}
				else if(allAgentsbtn.isSelected()){
					startAllAgents();
				}
				
			}
		};
		submit.addActionListener(selectionListener);
		configFrame.add(submit, "wrap");
        
//        validate();
		configFrame.setMinimumSize(new Dimension(250, 200));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        configFrame.setLocation(dim.width/2-configFrame.getSize().width/2, dim.height/2-configFrame.getSize().height/2);
        configFrame.setVisible(true);
		
	}

	protected static void showIPFrame(final agentToStart a) {
		
		final JFrame showIPframe=new JFrame("IP configuration");
		showIPframe.setLayout(new MigLayout());
		JLabel msg=new JLabel("Enter IP address");
		JTextArea ipJtext = new JTextArea(1, 15);
		ipJtext.setEditable(true);
		
		JButton OKbtn= new JButton("OK");
		OKbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showIPframe.dispose();
				start(a);
			}
		});
		showIPframe.add(msg,"wrap");
		showIPframe.add(ipJtext,"wrap");
		showIPframe.add(OKbtn);
		showIPframe.setVisible(true);
		showIPframe.setMinimumSize(new Dimension(200, 200));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		showIPframe.setLocation(dim.width/2-showIPframe.getSize().width/2, 
				dim.height/2-showIPframe.getSize().height/2);
	}

	public AgentStarter() {
		log = LogManager.getLogger(this.getClass());
		//		log.info(log.isInfoEnabled());

		List<String> params = new ArrayList<String>();
//						params.add("-gui");
		//		params.add("-detect-main:false");

		this.bootProfile = new BootProfileImpl(params.toArray(new String[0]));

		this.runtime = jade.core.Runtime.instance();

		controller = runtime.createMainContainer(bootProfile);		


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
