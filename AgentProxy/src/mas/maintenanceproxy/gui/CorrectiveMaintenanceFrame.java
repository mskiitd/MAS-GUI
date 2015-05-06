package mas.maintenanceproxy.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import mas.util.formatter.doubleformatter.FormattedDoubleField;
import net.miginfocom.swing.MigLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uiconstants.Labels;

/**
 * @author Anand Prajapati
 * <p>
 * GUI to take input of repair time when the machine fails.
 * </p>
 */

public class CorrectiveMaintenanceFrame extends JFrame{

	private static final long serialVersionUID = 1L;

	private Logger log;

	private long repairTime;

	private JScrollPane scroller;
	private JPanel myPanel;
	private JButton btnConfirm;
	private JLabel lblRepairTime;
	private FormattedDoubleField txtRepairTime;
	private LocalMaintenanceAgent mAgent;
	private JFrame parent;
	private boolean dataOk = true;

	public CorrectiveMaintenanceFrame(LocalMaintenanceAgent mAgent, JFrame parent) {

		log = LogManager.getLogger();
		setLayout(new BorderLayout());
		this.myPanel = new JPanel(new MigLayout());
		this.mAgent = mAgent;
		this.parent = parent;

		this.btnConfirm = new JButton("Confirm");
		this.lblRepairTime = new JLabel(Labels.MaintenanceLabels.repairTimeLabel);
		this.txtRepairTime = new FormattedDoubleField();
		txtRepairTime.setColumns(Labels.defaultJTextSize);
		btnConfirm.addActionListener(new buttonListener());

		myPanel.add(lblRepairTime,"wrap");

		myPanel.add(txtRepairTime,"wrap");
		myPanel.add(btnConfirm,"wrap");

		this.scroller = new JScrollPane(this.myPanel);

		add(scroller,BorderLayout.CENTER);
		showGui();
	}

	/**
	 * Initialized the parameters of display of the frame and make it visible at appropriate location 
	 * with desired size
	 */
	private void showGui() {
		
		setTitle(" Machine Failed ");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(300,200));
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}

	/**
	 * Check if repair time format is correct. If correct then set the repair time to the value in the field,
	 * else show an error message and return.
	 */
	private void checkRepairTime() {
		if(txtRepairTime.getText().matches("-?\\d+(\\.\\d+)?")) {
			double rTime = 60 * 1000 * Double.parseDouble(txtRepairTime.getText());
			repairTime = (long) rTime;
			dataOk = true;
		}else {
			System.out.println("error");
			JOptionPane.showMessageDialog(this, "Invalid input for repair time !!",
					"Error" , JOptionPane.ERROR_MESSAGE );
			dataOk = false;
		}
	}

	/**
	 * Action listener for send button
	 */
	class buttonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// handle confirm repair time 
			if(e.getSource().equals(btnConfirm)) {
				checkRepairTime();
				if(dataOk) {
					log.info("sending corrective maintenance data !!");
					parent.setEnabled(true);
					mAgent.sendCorrectiveMaintenanceRepairTime(repairTime);
					CorrectiveMaintenanceFrame.this.setVisible(false);
				}
			}
		}
	};
}
