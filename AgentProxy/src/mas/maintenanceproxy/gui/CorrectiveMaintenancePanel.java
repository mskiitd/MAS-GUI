package mas.maintenanceproxy.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import mas.util.formatter.doubleformatter.FormattedDoubleField;
import net.miginfocom.swing.MigLayout;
import uiconstants.Labels;

public class CorrectiveMaintenancePanel extends JPanel{

	private static final long serialVersionUID = 1L;

	private Logger log;

	private long repairTime;

	private JScrollPane scroller;
	private JPanel myPanel;
	private JButton btnConfirm;
	private JLabel lblRepairTime;
	private FormattedDoubleField txtRepairTime;
	private LocalMaintenanceAgent mAgent;

	private boolean dataOk = true;

	public CorrectiveMaintenancePanel(LocalMaintenanceAgent mAgent) {

		log = LogManager.getLogger();
		this.myPanel = new JPanel(new MigLayout());
		this.mAgent = mAgent;

		this.btnConfirm = new JButton("Confirm");
		this.lblRepairTime = new JLabel(Labels.MaintenanceLabels.repairTimeLabel);
		this.txtRepairTime = new FormattedDoubleField();
		txtRepairTime.setColumns(Labels.defaultJTextSize);
		btnConfirm.addActionListener(new buttonListener());

		myPanel.add(lblRepairTime,"wrap");

		myPanel.add(txtRepairTime,"wrap");
		myPanel.add(btnConfirm,"wrap");

		this.scroller = new JScrollPane(this.myPanel);

		add(scroller);
		showGui();
	}

	private void showGui() {
		
		setPreferredSize(new Dimension(600,500));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}

	private void checkRepairTime() {
		if(txtRepairTime.getText().matches("-?\\d+(\\.\\d+)?")) {
			double rTime = 60*1000*Double.parseDouble(txtRepairTime.getText());
			repairTime = (long) rTime;
			dataOk = true;
		}else {
			System.out.println("error");
			JOptionPane.showMessageDialog(this, "Invalid input for repair time !!",
					"Error" , JOptionPane.ERROR_MESSAGE );
			dataOk = false;
		}
	}

	class buttonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// handle confirm repair time 
			if(e.getSource().equals(btnConfirm)) {
				checkRepairTime();
				if(dataOk) {
					log.info("sending corrective maintenance data !!");
					mAgent.sendCorrectiveMaintenanceRepairTime(repairTime);
					CorrectiveMaintenancePanel.this.setVisible(false);
				}
			}
		}
	};
}
