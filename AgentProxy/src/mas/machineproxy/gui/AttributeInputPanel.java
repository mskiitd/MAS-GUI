package mas.machineproxy.gui;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import mas.jobproxy.JobGNGattribute;
import uiconstants.Labels;

public class AttributeInputPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel lblTitleHeading;
	private JTextField txtName;

	private boolean status = true;

	public AttributeInputPanel(JobGNGattribute attribute) {

		lblTitleHeading = new JLabel(" Attribute Name ");
		txtName = new JTextField(Labels.defaultJTextSize);

		add(lblTitleHeading);
		add(txtName);

		if(attribute != null) {
			txtName.setText(attribute.getName());
		}
	}

	public AttributeInputPanel() {
		this(null);
	}
	
	public boolean isDataOk() {
		checkData();
		return status;
	}

	private void checkData() {
		if(txtName.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Invalid input for attribute Name !!",
					"Error" , JOptionPane.ERROR_MESSAGE );
			status = false;
		} else {
			status = true;
		}
	}

	public JobGNGattribute getAttribute() {
		JobGNGattribute att = new JobGNGattribute();
		checkData();
		if(status) {
			att.setName(txtName.getText());
			return att;
		} 
		return null;
	}
}
