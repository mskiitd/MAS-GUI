package mas.maintenanceproxy.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import net.miginfocom.swing.MigLayout;
import uiconstants.Labels;

public class CorrectiveMaintenanceGUI extends JFrame{

	private static final long serialVersionUID = 1L;

	private long repairTime;
	
	private JScrollPane scroller;
	private JPanel myPanel;
	private JButton btnConfirm;
	private JLabel lblRepairTime;
	private JTextField txtRepairTime;
	private LocalMaintenanceAgent mAgent;
	
	public CorrectiveMaintenanceGUI(LocalMaintenanceAgent mAgent) {

		this.myPanel = new JPanel(new MigLayout());
		this.mAgent = mAgent;
		
		this.btnConfirm = new JButton("Confirm");
		this.lblRepairTime = new JLabel(Labels.MaintenanceLabels.repairTimeLabel);
		
		myPanel.add(lblRepairTime,"wrap");

		myPanel.add(txtRepairTime,"wrap");
		myPanel.add(btnConfirm,"wrap");
		
		this.scroller = new JScrollPane(this.myPanel);

		add(scroller);
		showGui();
	}

	private void showGui() {
		setPreferredSize(new Dimension(600,500));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}

	class buttonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// handle confirm repair time 
			if(e.getSource().equals(btnConfirm)) {
				double rTime = 60*1000*Double.parseDouble(txtRepairTime.getText());
				repairTime = (long)rTime;
				
				mAgent.sendCorrectiveMaintenanceRepairTime(repairTime);
				
				dispose();
			}
		}
	};
}
