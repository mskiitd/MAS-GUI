package mas.machineproxy.gui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import mas.jobproxy.Batch;
import mas.localSchedulingproxy.agent.LocalSchedulingAgent;
import mas.localSchedulingproxy.goal.FinishMaintenanceGoal;
import mas.localSchedulingproxy.goal.StartMaintenanceGoal;
import mas.machineproxy.MachineStatus;
import mas.machineproxy.Simulator;
import mas.machineproxy.gui.custompanels.MachineInfoPanel;
import mas.machineproxy.gui.custompanels.MaintMsgPanel;
import mas.maintenanceproxy.classes.MaintenanceResponse;
import mas.util.TableUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alee.extended.layout.VerticalFlowLayout;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

@SuppressWarnings("serial")
public class MachineGUI extends JFrame {

	private static TrayIcon customerTrayIcon;
	private Logger log;

	private JLayeredPane layers;
	private MaintMsgPanel maintMsgPanel;
	private LocalSchedulingAgent lAgent;
	private Simulator machineSimulator;
	private buttonPanelListener buttonPanelHandler;
	private BufferedImage upIcon, startIcon, finishIcon;

	private JMenuItem menuItemUpload, menuItemPmStart, menuItemPmDone;

	private MachineInfoPanel currentOpPanel;
	private JSplitPane parentPanel;

	private JPanel mcPanel;
	private JPanel machineSubPanel;

	private JPanel upperButtonPanel;
	private JPanel lowerButtonPanel;

	private JButton btnFailMachineButton;
	private JButton btnLoadJob;
	private JButton btnUnloadJob;
	private JButton btnRepairMachine;
	private BufferedImage loadJobIcon;
	private BufferedImage repairIcon;

	private JScrollPane queueScroller;
	private JLabel lblMachineIcon;
	private JLabel lblMachineStatus;
	private BufferedImage machineIcon;
	private CustomJobQueue queuePanel;
	private ArrayList<Batch> jobQ;

	public static Color failColor = Color.RED;
	public static Color maintenanceColor = Color.yellow;
	public static Color idleColor = Color.BLACK;

	private String currentBatchId = null;
	private String currOperationId = null;

	private int maintJobCounter = 0;
	private int screenWidth;
	private int screenHeight;
	private static String notificationSound = "resources/notification.wav";;
	private static AudioStream audioStream;
	
	private static Dimension windowSize;

	public MachineGUI(LocalSchedulingAgent agent) {

		this.lAgent = agent;
		
		log = LogManager.getLogger();

		layers = new JLayeredPane();
		maintMsgPanel = new MaintMsgPanel();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = (int) screenSize.getWidth();
		screenHeight = (int) screenSize.getHeight();
		windowSize = new Dimension(screenWidth/2,screenHeight);
		
		this.mcPanel = new JPanel(new VerticalFlowLayout());
		this.machineSubPanel = new JPanel(new GridBagLayout());
		this.lblMachineStatus = new JLabel();
		this.currentOpPanel = new MachineInfoPanel();
		initButtons();
		loadTrayIcon();

		try {
			machineIcon = ImageIO.read(new File("resources/machine1.png"));
			lblMachineIcon = new JLabel(new ImageIcon(machineIcon));
			lblMachineIcon.setVerticalAlignment(SwingConstants.CENTER);
			lblMachineIcon.setHorizontalAlignment(SwingConstants.LEFT);

			lblMachineStatus.setHorizontalAlignment(SwingConstants.CENTER);
			lblMachineStatus.setFont(TableUtil.headings);

			GridBagConstraints statusConstraints = new GridBagConstraints();
			statusConstraints.fill = GridBagConstraints.HORIZONTAL;
			statusConstraints.gridx = 0;
			statusConstraints.gridy = 0;

			GridBagConstraints iconConstraints = new GridBagConstraints();
			iconConstraints.fill = GridBagConstraints.HORIZONTAL;
			iconConstraints.gridx = 0;
			iconConstraints.gridy = 1;

			machineSubPanel.add(lblMachineStatus, statusConstraints);
			machineSubPanel.add(lblMachineIcon, iconConstraints);
			mcPanel.setBorder((new EmptyBorder(5,5,5,5) ));

			mcPanel.add(upperButtonPanel);
			mcPanel.add(Box.createRigidArea(new Dimension(mcPanel.getWidth(),50)) );

			mcPanel.add(new JScrollPane(currentOpPanel));
			mcPanel.add(Box.createRigidArea(new Dimension(mcPanel.getWidth(),50)) );

			mcPanel.add(machineSubPanel);
			mcPanel.add(Box.createRigidArea(new Dimension(mcPanel.getWidth(),120)) );

			mcPanel.add(lowerButtonPanel);

		} catch (IOException e) {
			e.printStackTrace();
		}
		jobQ = new ArrayList<Batch>();

		this.queuePanel = new CustomJobQueue(jobQ);
		queuePanel.setBorder((new EmptyBorder(10,10,10,10) ));

		this.queueScroller = new JScrollPane(queuePanel);

		this.parentPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(mcPanel), queueScroller);
		this.parentPanel.setDividerLocation(0.30);
		machineIdle();
		
		parentPanel.setBounds(0, 0,(int) windowSize.getWidth(),(int) windowSize.getHeight());
		layers.add(parentPanel, new Integer(0), 0);
		
		maintMsgPanel.setOpaque(false);
		maintMsgPanel.setVisible(false);
		maintMsgPanel.setBounds(0, 0,(int) windowSize.getWidth(),(int) windowSize.getHeight());
		layers.add(maintMsgPanel, new Integer(1), 1);
		
		add(layers);
		showGui();
	}

	private void loadTrayIcon() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				Image image = Toolkit.getDefaultToolkit().getImage("resources/repair_64.png");
				customerTrayIcon = new TrayIcon(image, lAgent.getLocalName());
				if (SystemTray.isSupported()) {
					SystemTray tray = SystemTray.getSystemTray();

					customerTrayIcon.setImageAutoSize(true);
					try {
						tray.add(customerTrayIcon);
					} catch (AWTException e) {
						log.info("TrayIcon could not be added.");
					}
				}
			}
		}).start();
	}

	public Simulator getMachineSimulator() {
		return machineSimulator;
	}

	public void setMachineSimulator(Simulator machineSimulator) {
		this.machineSimulator = machineSimulator;
	}

	private void initButtons() {
		try {
			loadJobIcon = ImageIO.read(new File("resources/load_64.png"));
			repairIcon = ImageIO.read(new File("resources/repair_64.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		buttonPanelHandler = new buttonPanelListener();
		btnFailMachineButton = new JButton("Machine Failed");

		btnLoadJob = new JButton("Load Job");
		btnLoadJob.setIcon(new ImageIcon(loadJobIcon));
		btnLoadJob.setOpaque(false);
		btnLoadJob.setFocusPainted(false);
		btnLoadJob.setContentAreaFilled(false); 
		btnLoadJob.setBorderPainted(false); 

		btnUnloadJob = new JButton("Unload Job");
		//		btnUnloadJob.setIcon();
		btnUnloadJob.setOpaque(false);
		btnUnloadJob.setFocusPainted(false);
		btnUnloadJob.setContentAreaFilled(false); 
		btnUnloadJob.setBorderPainted(false);

		btnRepairMachine = new JButton("Repair");
		btnRepairMachine.setIcon(new ImageIcon(repairIcon));
		btnRepairMachine.setOpaque(false);
		btnRepairMachine.setFocusPainted(false);
		btnRepairMachine.setContentAreaFilled(false);
		btnRepairMachine.setBorderPainted(false);

		upperButtonPanel = new JPanel(new GridBagLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				GradientPaint paint =new GradientPaint(0.0f, 0.0f, new Color(0xF2F2F2),
						0.0f, getHeight(), new Color(0xD7D7D7));
				g2.setPaint(paint);
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.drawLine(0, getHeight(), getWidth(), getHeight());
			}
		};

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;

		upperButtonPanel.add(btnFailMachineButton,constraints);
		constraints.gridx = 1;
		upperButtonPanel.add(btnRepairMachine,constraints);
		constraints.gridy = 1;
		constraints.gridx = 0;
		upperButtonPanel.add(currentOpPanel,constraints);

		lowerButtonPanel = new JPanel(new GridBagLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				GradientPaint paint =new GradientPaint(0.0f, 0.0f, new Color(0xF2F2F2),
						0.0f, getHeight(), new Color(0xD7D7D7));
				g2.setPaint(paint);
				g2.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		btnUnloadJob.setEnabled(false);
		btnRepairMachine.setEnabled(false);

		btnLoadJob.addActionListener(buttonPanelHandler);
		btnUnloadJob.addActionListener(buttonPanelHandler);
		btnFailMachineButton.addActionListener(buttonPanelHandler);
		btnRepairMachine.addActionListener(buttonPanelHandler);

		constraints.gridx = 0;
		lowerButtonPanel.add(btnLoadJob, constraints);
		constraints.gridx = 1;
		lowerButtonPanel.add(btnUnloadJob, constraints);
	}

	/**
	 * Runs on EDT
	 */
	public void machineFailed() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				lblMachineStatus.setText("Failed");
				lblMachineStatus.setForeground(failColor);
			}
		});
	}

	/**
	 * Runs on EDT
	 * @param id
	 * @param operation
	 */
	public void machineProcessing(String id, String operation) {
		this.currentBatchId = id;
		this.currOperationId = operation;

		currentOpPanel.setJobId(id);
		currentOpPanel.setOperation(operation);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				lblMachineStatus.setText("Processing");
				lblMachineStatus.setForeground(CustomJobQueue.firstJobColor);
			}
		});
	}

	public void setCustomerId(String id) {
		currentOpPanel.setCustomer(id);
	}

	public void setBatch(String id) {
		currentOpPanel.setbatch(id);
	}

	/**
	 * Runs on EDT
	 */
	public void machineMaintenance() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				lblMachineStatus.setText("Under Maintenance");
				lblMachineStatus.setForeground(maintenanceColor);
			}
		});
	}
	/**
	 * Runs on EDT
	 */
	public void machineIdle() {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				currentOpPanel.reset();
				lblMachineStatus.setText("Idle");
				lblMachineStatus.setForeground(idleColor);
				btnLoadJob.setEnabled(true);
				btnUnloadJob.setEnabled(false);
			}
		});
	}

	class buttonPanelListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(btnLoadJob)) {
				if(machineSimulator != null) {
					btnUnloadJob.setEnabled(true);
					btnLoadJob.setEnabled(false);

					new Thread(new Runnable() {
						@Override
						public void run() {
							machineSimulator.loadJob();
						}
					}).start();
				}
				else{
					log.info("machineSimulator = " + machineSimulator);
				}

			} else if(e.getSource().equals(btnUnloadJob)) {

				if(machineSimulator != null) {

					btnUnloadJob.setEnabled(false);
					btnLoadJob.setEnabled(true);

					new Thread(new Runnable() {
						@Override
						public void run() {
							machineSimulator.unloadJob();
						}
					}).start();
				}

			} else if(e.getSource().equals(btnFailMachineButton)) {

				if(machineSimulator != null) {

					machineFailed();
					btnFailMachineButton.setEnabled(false);
					btnLoadJob.setEnabled(false);
					btnUnloadJob.setEnabled(false);

					new Thread(new Runnable() {
						@Override
						public void run() {
							machineSimulator.setStatus(MachineStatus.FAILED);
						}
					}).start();
				}

			} else if(e.getSource().equals(btnRepairMachine)) {

				if(machineSimulator != null) {

					machineResume();
					btnFailMachineButton.setEnabled(true);
					btnRepairMachine.setEnabled(false);

					new Thread(new Runnable() {
						@Override
						public void run() {
							machineSimulator.repair();
						}
					}).start();

				}
			}
		}
	}

	/**
	 * Runs on EDT
	 */
	public void enableRepair() {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				btnRepairMachine.setEnabled(true);
			}
		});
	}

	/**
	 * Runs on EDT
	 */
	private void machineResume() {
		if(this.currentBatchId != null) {
			machineProcessing(this.currentBatchId,this.currOperationId);

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					btnUnloadJob.setEnabled(true);
				}
			});

		} else {
			machineIdle();
		}
	}

	/**
	 * Update the current queue for the machine with the new queue in the GUI.
	 * Runs on EDT
	 * @param newQueue
	 */
	public void updateQueue(ArrayList<Batch> newQueue) {

		jobQ.clear();
		jobQ.addAll(newQueue);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				queuePanel.revalidate();
				queuePanel.repaint();
			}
		});
	}

	/**
	 * Runs on EDT
	 * @param comingJob
	 */
	public void addBatchToQueue(Batch comingJob) {
		jobQ.add(comingJob);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				queuePanel.revalidate();
				queuePanel.repaint();
			}
		});
	}
	/**
	 * Runs on EDT
	 * @param j
	 */
	public void removeFromQueue(Batch j) {
		if(jobQ.contains(j)) {
			jobQ.remove(j);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					queuePanel.revalidate();
					queuePanel.repaint();
				}
			});
		}
	}

	private void showGui() {
		setTitle(" Machine GUI : ");// + lAgent.getLocalName().split("#")[1]);
		setJMenuBar(createMenuBar());
		setPreferredSize(windowSize);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		setLocation(0,0);
		super.setVisible(true);
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu menu;
		//Create the menu bar.
		menuBar = new JMenuBar();

		//Build the first menu.
		menu = new JMenu("Options");

		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
				"The only menu in this program that has menu items");
		menuBar.add(menu);

		menuItemListener mListener = new menuItemListener();
		try {
			upIcon = ImageIO.read(new File("resources/upload_48.jpg"));
			startIcon = ImageIO.read(new File("resources/start_48.png"));
			finishIcon = ImageIO.read(new File("resources/finish_48.png"));

			ImageIcon icon = new ImageIcon(upIcon);
			menuItemUpload = new JMenuItem("Add job to Database", icon);
			menuItemUpload.setMnemonic(KeyEvent.VK_B);
			menuItemUpload.addActionListener(mListener);

			ImageIcon startIc = new ImageIcon(startIcon);
			ImageIcon finishIc = new ImageIcon(finishIcon);
			menuItemPmStart = new JMenuItem("<html><p>Maintenance Start <b>" + maintJobCounter + "</b></p></html>",
					startIc);
			menuItemPmDone = new JMenuItem("Maintenance Done", finishIc);
			menuItemPmDone.setEnabled(false);
			menuItemPmStart.addActionListener(mListener);
			menuItemPmDone.addActionListener(mListener);

			menu.add(menuItemUpload);
			menu.add(menuItemPmStart);
			menu.add(menuItemPmDone);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return menuBar;
	}
	/**
	 * Runs on EDT
	 */
	public void maintJobArrived() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				menuItemPmStart.setText("<html><p>Maintenance Start <b>" + ++maintJobCounter + "</b></p></html>");
				showNotification("Maintenance Job", "Maintenance Has Arrived.", MessageType.INFO);
			}
		});
	}

	/**
	 * Runs on EDT
	 */
	private void maintJobDone() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				menuItemPmStart.setText("<html><p>Maintenance Start <b>" + --maintJobCounter + "</b></p></html>");
				showNotification("Maintenance Job", "Maintenance Has Finished.", MessageType.INFO);
			}
		});
	}

	public static void showNotification(String title, String message,TrayIcon.MessageType type){

		switch(type) {
		case ERROR :
			customerTrayIcon.displayMessage(title,message, TrayIcon.MessageType.ERROR);
			break;

		case INFO:
			customerTrayIcon.displayMessage( title,message, TrayIcon.MessageType.INFO);
			break;

		case WARNING:
			customerTrayIcon.displayMessage( title,message, TrayIcon.MessageType.WARNING);
			break;

		case NONE:
			customerTrayIcon.displayMessage( title,message, TrayIcon.MessageType.NONE);
			break;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				InputStream in = null;
				try {
					in = new FileInputStream(notificationSound);
					audioStream = new AudioStream(in);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// play the audio clip with the audio player class
				AudioPlayer.player.start(audioStream);
			}
		}).start();
	}

	class menuItemListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			if(e.getSource().equals(menuItemUpload)) {

				UpdateOperationDbGUI updatedb = new UpdateOperationDbGUI(lAgent.getLocalName());

			} else if(e.getSource().equals(menuItemPmStart)) {

				lAgent.addGoal(new StartMaintenanceGoal());

			} else if(e.getSource().equals(menuItemPmDone)) {
				maintJobDone();
				lAgent.addGoal(new FinishMaintenanceGoal());
			}
		}
	}

	/**
	 * display a notification or pop up as warning for delayed maintenance
	 * @param response
	 */
	public void delayedMaintWarning(MaintenanceResponse response) {
		int degree = response.getDegree();
		switch(degree) {
		case 1:
			showNotification("Preventive Maintenance", response.getMsg(),
					MessageType.WARNING);
			break;
		case 2:
			pendingMaintPopUp(response.getMsg());
			break;
		case 3:
			markMachineDown(response.getMsg());
			break;
		}
	}

	private void markMachineDown(String msg) {
		final String msgToShow = msg;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				maintMsgPanel.setVisible(true);
				JOptionPane.showMessageDialog(MachineGUI.this, msgToShow, "Dialog", JOptionPane.WARNING_MESSAGE);
				showNotification("Machine Paused", "Maintenance is pending.", MessageType.WARNING);
				revalidate();
				repaint();
			}
		});
		machineSimulator.setStatus(MachineStatus.PAUSED);
	}

	public void resumeMachine() {
		machineSimulator.setStatus(MachineStatus.IDLE);
	}

	public void enablePmDone() {
		menuItemPmStart.setEnabled(false);
		menuItemPmDone.setEnabled(true);
	}

	public void enablePmStart() {
		maintMsgPanel.setVisible(false);
		menuItemPmStart.setEnabled(true);
		menuItemPmDone.setEnabled(false);
	}

	public void showNoMaintJobPopup() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(MachineGUI.this, "No Maintenance Job to be done.");
			}
		});
	}

	public void pendingMaintPopUp(final String msg) {
		final String msgToShow = msg;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(MachineGUI.this, msgToShow, "Dialog", JOptionPane.WARNING_MESSAGE);
				showNotification("Maintenance Job", "Maintenance is Pending.", MessageType.WARNING);				
			}
		});

	}

	public boolean isMachinePaused() {
		return machineSimulator.getStatus() == MachineStatus.PAUSED;
	}
}
