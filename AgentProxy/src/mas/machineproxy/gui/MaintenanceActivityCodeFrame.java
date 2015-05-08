package mas.machineproxy.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mas.maintenanceproxy.classes.PMaintenance;
import mas.util.formatter.stringformatter.FormattedStringField;
import net.miginfocom.swing.MigLayout;
import uiconstants.Labels;

/**
 * @author Anand Prajapati
 * <p>
 * GUI for entering maintenance activity code after preventive maintenance is done on the machine.
 * </p>
 */

public class MaintenanceActivityCodeFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private String activityCode;

	private JScrollPane scroller;
	private JPanel myPanel;
	private JButton btnConfirm;
	private JLabel lblActivityCode;
	private FormattedStringField txtActivityCode;
	private boolean dataOk = true;
	
	private PMaintenance localPm;

	public MaintenanceActivityCodeFrame(PMaintenance pm) {
		ImageIcon img = new ImageIcon("resources/smartManager.png","Logo icon");
		this.setIconImage(img.getImage());
		
		this.localPm = pm;
		setLayout(new BorderLayout());
		this.myPanel = new JPanel(new MigLayout());

		this.btnConfirm = new JButton("Confirm");
		this.lblActivityCode = new JLabel("Activity Code");
		this.txtActivityCode = new FormattedStringField();
		txtActivityCode.setColumns(Labels.defaultJTextSize);
		btnConfirm.addActionListener(new buttonListener());

		myPanel.add(lblActivityCode,"wrap");
		myPanel.add(txtActivityCode,"wrap");
		
		myPanel.add(btnConfirm,"wrap");

		this.scroller = new JScrollPane(this.myPanel);

		add(scroller,BorderLayout.CENTER);
		showGui();
	}

	private void showGui() {

		setTitle(" Enter Activity Code ");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(300,200));
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}

	private void checkActivityCode() {
		if(!txtActivityCode.getText().isEmpty()) {
			activityCode = txtActivityCode.getText();
			dataOk = true;
		}else {
			JOptionPane.showMessageDialog(this, "Invalid input for Activity Code !!",
					"Error" , JOptionPane.ERROR_MESSAGE );
			dataOk = false;
		}
	}

	class buttonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// handle confirm repair time 
			if(e.getSource().equals(btnConfirm)) {
				checkActivityCode();
				if(dataOk) {
					localPm.setActivityCode(activityCode);
					dispose();
				}
			}
		}
	};
}
