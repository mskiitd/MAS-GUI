package mas.machineproxy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import mas.jobproxy.Batch;
import mas.jobproxy.job;
import mas.localSchedulingproxy.agent.LocalSchedulingAgent;
import mas.machineproxy.Simulator;
import mas.machineproxy.gui.custompanels.FadingPanel;
import mas.util.TableUtil;

@SuppressWarnings("serial")
public class MachineGUI extends JFrame {

	private LocalSchedulingAgent lAgent;
	private Simulator machineSimulator;
	private buttonPanelListener buttonPanelHandler;

	private FadingPanel currentOpPanel;
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

	private int width = 600, height = 500;

	public MachineGUI(LocalSchedulingAgent agent) {

		this.lAgent = agent;

		this.mcPanel = new JPanel(new BorderLayout());
		this.machineSubPanel = new JPanel(new GridBagLayout());
		this.lblMachineStatus = new JLabel();
		this.currentOpPanel = new FadingPanel();
		initButtons();

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

			mcPanel.add(machineSubPanel, BorderLayout.CENTER);
			mcPanel.add(upperButtonPanel, BorderLayout.PAGE_START);
			mcPanel.add(lowerButtonPanel, BorderLayout.PAGE_END);

		} catch (IOException e) {
			e.printStackTrace();
		}
		jobQ = new ArrayList<Batch>();

		this.queuePanel = new CustomJobQueue(jobQ);
		queuePanel.setBorder((new EmptyBorder(10,10,10,10) ));

		this.queueScroller = new JScrollPane(queuePanel);

		this.parentPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(mcPanel), queueScroller);
		this.parentPanel.setDividerLocation(0.25);
		this.parentPanel.setEnabled(false);
		machineIdle();
		add(parentPanel);
		showGui();
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

	public void machineFailed() {
		this.lblMachineStatus.setText("Failed");
		this.lblMachineStatus.setForeground(failColor);
	}

	public void machineProcessing(String id, String operation) {
		this.currentOpPanel.setCurrentOperation(id, operation);
		this.lblMachineStatus.setText("Processing");
		this.lblMachineStatus.setForeground(CustomJobQueue.firstJobColor);
	}

	public void machineMaintenance() {
		this.lblMachineStatus.setText("Under Maintenance");
		this.lblMachineStatus.setForeground(maintenanceColor);
	}

	public void machineIdle() {
		this.currentOpPanel.reset();
		this.lblMachineStatus.setText("Idle");
		this.lblMachineStatus.setForeground(idleColor);
	}

	class buttonPanelListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(btnLoadJob)) {
				if(machineSimulator != null) {
					machineSimulator.loadJob();
					btnUnloadJob.setEnabled(true);
					btnLoadJob.setEnabled(false);
				}

			} else if(e.getSource().equals(btnUnloadJob)) {

				if(machineSimulator != null) {
					machineSimulator.unloadJob();
					btnUnloadJob.setEnabled(false);
					btnLoadJob.setEnabled(true);
				}

			} else if(e.getSource().equals(btnFailMachineButton)) {

				if(machineSimulator != null) {
					machineSimulator.FailTheMachine();
					machineFailed();
					btnFailMachineButton.setEnabled(false);
					btnRepairMachine.setEnabled(true);
					btnLoadJob.setEnabled(false);
					btnUnloadJob.setEnabled(false);
				}

			} else if(e.getSource().equals(btnRepairMachine)) {

				if(machineSimulator != null) {
					machineSimulator.repair();
					btnUnloadJob.setEnabled(true);
					btnFailMachineButton.setEnabled(true);
					btnRepairMachine.setEnabled(false);
				}
			}
		}
	}

	/**
	 * Update the current queue for the machine with the new queue in the GUI
	 * @param newQueue
	 */
	public void updateQueue(ArrayList<Batch> newQueue) {
		jobQ.clear();
		jobQ.addAll(newQueue);
		queuePanel.revalidate();
		queuePanel.repaint();
	}

	public void addJobToQueue(Batch comingJob) {
		jobQ.add(comingJob);
		queuePanel.revalidate();
		queuePanel.repaint();
	}

	public void removeFromQueue(Batch j) {
		if(jobQ.contains(j)) {
			jobQ.remove(j);
			queuePanel.revalidate();
			queuePanel.repaint();
		}
	}

	private void showGui() {
		setTitle(" Machine GUI ");
		setJMenuBar(createMenuBar());
		setPreferredSize(new Dimension(width,height));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;
		//Create the menu bar.
		menuBar = new JMenuBar();

		//Build the first menu.
		menu = new JMenu("Options");

		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
				"The only menu in this program that has menu items");
		menuBar.add(menu);

		BufferedImage upIcon;
		menuItemListener mListener = new menuItemListener();
		try {
			upIcon = ImageIO.read(new File("resources/upload_48.jpg"));
			ImageIcon icon = new ImageIcon(upIcon);
			menuItem = new JMenuItem("Add job to Database", icon);
			menuItem.setMnemonic(KeyEvent.VK_B);
			menu.add(menuItem);

			menuItem.addActionListener(mListener);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return menuBar;
	}

	class menuItemListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			UpdateOperationDbGUI updatedb = new UpdateOperationDbGUI(lAgent.getLocalName());
		}

	}
}
