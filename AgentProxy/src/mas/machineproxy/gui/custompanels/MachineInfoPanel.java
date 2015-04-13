package mas.machineproxy.gui.custompanels;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

public class MachineInfoPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel lblCurrJobNum,lblCurrOperation,lblCurrBatchNum, lblCustomerId;
	private String pref1 = "Customer Id : ",
			pref2 = "Job Id : ",
			pref3 = "Operation : ",
			pref4 = "Batch Id : ";
	
	public MachineInfoPanel() {

		setLayout(new MigLayout("",
				"[]10",
				""
				));

		lblCurrJobNum = new JLabel();
		lblCurrOperation = new JLabel();
		lblCurrBatchNum = new JLabel();
		lblCustomerId = new JLabel();
		
		add(lblCustomerId,"wrap");
		add(lblCurrBatchNum,"wrap");
		add(lblCurrJobNum,"wrap");
		add(lblCurrOperation,"wrap");
		
		setBackground(Color.WHITE);
		Component[] comps = getComponents();
		for(int i = 0; i < comps.length; i++) {
			comps[i].setForeground(Color.BLACK);
		}
		System.out.println(getPreferredSize());
	}
	
	public void setbatch(String batch) {
		lblCurrBatchNum.setText(pref4 + batch);
	}
	
	public void setOperation(String op) {
		lblCurrOperation.setText(pref3 + op);
	}
	
	public void setJobId(String jobId) {
		lblCurrJobNum.setText(pref2 + jobId); 
	}
	
	public void setCustomer(String cust){
		lblCustomerId.setText(pref1 + cust); 
	}
	
	public void resetCustomer() {
		lblCurrJobNum.setText(pref2);
	}
	
	public void resetBatch() {
		lblCurrBatchNum.setText(pref4);
	}
	
	/**
	 * Runs on EDT
	 */
	public void reset() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				lblCurrBatchNum.setText(pref4);
				lblCurrJobNum.setText(pref2);
				lblCurrOperation.setText(pref3);
				lblCustomerId.setText(pref1);
			}
		});
	}

}
