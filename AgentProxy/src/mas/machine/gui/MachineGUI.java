package mas.machine.gui;

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

import mas.job.job;

@SuppressWarnings("serial")
public class MachineGUI extends JFrame {

	private JSplitPane parentPanel;
	private JPanel mcPanel;
	private JPanel machineSubPanel;
	private JScrollPane queueScroller;
	private JLabel lblMachineIcon;
	private JLabel lblMachineStatus;
	private BufferedImage machineIcon;
	private CustomJobQueue queuePanel;

	public MachineGUI() {
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
		List<job> q = new ArrayList<job>();
		q.add(new job.Builder("1").build());
		q.add(new job.Builder("2").build());
		q.add(new job.Builder("3").build());
		q.add(new job.Builder("4").build());
		q.add(new job.Builder("5").build());
		q.add(new job.Builder("6").build());
		q.add(new job.Builder("7").build());
		q.add(new job.Builder("8").build());
		q.add(new job.Builder("9").build());
		q.add(new job.Builder("10").build());
		q.add(new job.Builder("11").build());
		q.add(new job.Builder("12").build());
		q.add(new job.Builder("13").build());
		q.add(new job.Builder("14").build());

		this.queuePanel = new CustomJobQueue(q);
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

	private void showGui() {
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
