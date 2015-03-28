package mas.machineproxy.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import mas.jobproxy.job;
import mas.localSchedulingproxy.agent.LocalSchedulingAgent;

@SuppressWarnings("serial")
public class MachineGUI extends JFrame {

	private LocalSchedulingAgent lSchAgent;

	private JSplitPane parentPanel;
	private JPanel mcPanel;
	private JPanel machineSubPanel;
	private JScrollPane queueScroller;
	private JLabel lblMachineIcon;
	private JLabel lblMachineStatus;
	private BufferedImage machineIcon;
	private CustomJobQueue queuePanel;
	private ArrayList<job> jobQ;

	public MachineGUI(LocalSchedulingAgent agent) {

		this.lSchAgent = agent;

		this.mcPanel = new JPanel(new GridBagLayout());
		this.machineSubPanel = new JPanel(new GridBagLayout());
		this.lblMachineStatus = new JLabel();

		try {
			machineIcon = ImageIO.read(new File("resources/machine1.png"));
			lblMachineIcon = new JLabel(new ImageIcon(machineIcon));
			lblMachineIcon.setVerticalAlignment(SwingConstants.CENTER);
			lblMachineIcon.setHorizontalAlignment(SwingConstants.LEFT);

			lblMachineStatus.setHorizontalAlignment(SwingConstants.CENTER);

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

			mcPanel.add(machineSubPanel);
			mcPanel.setBorder((new EmptyBorder(10,10,10,10) ));
		} catch (IOException e) {
			e.printStackTrace();
		}
		jobQ = new ArrayList<job>();

		this.queuePanel = new CustomJobQueue(jobQ);
		queuePanel.setBorder((new EmptyBorder(10,10,10,10) ));

		this.queueScroller = new JScrollPane(queuePanel);

		this.parentPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mcPanel, queueScroller);
		machineIdle();
		add(parentPanel);
		showGui();
	}

	public void machineFailed() {
		this.lblMachineStatus.setText("Failed");
	}

	public void machineProcessing() {
		this.lblMachineStatus.setText("Processing");
	}

	public void machineMaintenance() {
		this.lblMachineStatus.setText("Under Maintenance");
	}

	public void machineIdle() {
		this.lblMachineStatus.setText("Idle");
	}

	/**
	 * Update the current queue for the machine with the new queue in the GUI
	 * @param newQueue
	 */
	public void updateQueue(ArrayList<job> newQueue) {
		jobQ.clear();
		jobQ.addAll(newQueue);
		queuePanel.revalidate();
		queuePanel.repaint();
	}

	public void addJobToQueue(job comingJob) {
		jobQ.add(comingJob);
		queuePanel.revalidate();
		queuePanel.repaint();
	}

	public void removeFromQueue(job j) {
		if(jobQ.contains(j)) {
			jobQ.remove(j);
			queuePanel.revalidate();
			queuePanel.repaint();
		}
	}

	private void showGui() {
		setTitle(" Machine GUI ");
		setPreferredSize(new Dimension(600,500));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}

}
