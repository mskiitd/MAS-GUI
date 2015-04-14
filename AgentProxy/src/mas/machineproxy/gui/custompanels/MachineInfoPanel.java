package mas.machineproxy.gui.custompanels;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.alee.extended.label.WebHotkeyLabel;

import net.miginfocom.swing.MigLayout;

public class MachineInfoPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel lblPref3, lblPref2, lblPref4, lblPref1, lblPref5;
	private WebHotkeyLabel valPref1, valPref2, valPref3, valPref4, valPref5;
	
	public MachineInfoPanel() {

		setLayout(new MigLayout("",
				"[]10",
				""
				));

		lblPref3 = new JLabel("Operation : ");
		lblPref2 = new JLabel("Job Id : ");
		lblPref4 = new JLabel("Batch Id : ");
		lblPref1 = new JLabel("Customer Id : ");
		lblPref5 = new JLabel("Batch No : ");
		
		valPref1 = new WebHotkeyLabel();
		valPref2 = new WebHotkeyLabel();
		valPref3 = new WebHotkeyLabel();
		valPref4 = new WebHotkeyLabel();
		valPref5 = new WebHotkeyLabel();

		add(lblPref1);
		add(valPref1, "wrap");
		
		add(lblPref4);
		add(valPref4,"wrap");
		
		add(lblPref5);
		add(valPref5,"wrap");
		
		add(lblPref2);
		add(valPref2,"wrap");
		
		add(lblPref3);
		add(valPref3,"wrap");
		
		setBackground(Color.WHITE);
		Component[] comps = getComponents();
		for(int i = 0; i < comps.length; i++) {
			comps[i].setForeground(Color.BLACK);
		}
	}

	public void setbatch(final String batch) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				valPref4.setText(batch);
			}
		});
	}

	public void setOperation(final String op) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				valPref3.setText(op);
			}
		});
	}

	public void setJobId(final String jobId) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				valPref2.setText( jobId); 
			}
		});
	}
	
	public void setBatchNo(final String batchNo) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				valPref5.setText( batchNo); 
			}
		});
	}

	public void setCustomer(final String cust){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				valPref1.setText(cust); 
			}
		});
	}

	public void resetCustomer() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				valPref2.setText("");
			}
		});
	}

	public void resetBatch() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				valPref4.setText("");
			}
		});
	}

	/**
	 * Runs on EDT
	 */
	public void reset() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				valPref4.setText("");
				valPref2.setText("");
				valPref3.setText("");
				valPref1.setText("");
				valPref5.setText("");
			}
		});
	}

}
