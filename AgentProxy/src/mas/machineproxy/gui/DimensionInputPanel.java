package mas.machineproxy.gui;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import mas.jobproxy.jobDimension;
import net.miginfocom.swing.MigLayout;
import uiconstants.Labels;

public class DimensionInputPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel lblTitleHeading;
	private JLabel lblTarget;

	private JTextField txtName;
	private JTextField txtTarget;

	private boolean status = true;

	public DimensionInputPanel(jobDimension dimension) {

		setLayout(new MigLayout());

		lblTitleHeading = new JLabel(" Name ");
		lblTarget = new JLabel(" Target Dimension ");

		txtName = new JTextField(Labels.defaultJTextSize);
		txtTarget = new JTextField(Labels.defaultJTextSize);

		add(lblTitleHeading);
		add(txtName, "wrap");

		add(lblTarget);
		add(txtTarget);

		if(dimension != null) {
			txtName.setText(dimension.getName());
			txtTarget.setText(String.valueOf(dimension.getTargetDimension()));
		}
	}

	public DimensionInputPanel() {
		this(null);
	}

	public boolean isDataOk() {
		checkData();
		return status;
	}

	private void checkData() {
		if(! txtTarget.getText().matches("-?\\d+(\\.\\d+)?") ) {
			JOptionPane.showMessageDialog(this, "Invalid input for Target dimension !!",
					"Error" , JOptionPane.ERROR_MESSAGE );
			status = false;
		} else {
			status = true;
		}

		if(status) {
			if(txtName.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Invalid input for dimension Name !!",
						"Error" , JOptionPane.ERROR_MESSAGE );
				status = false;
			}
		}
	}

	public jobDimension getDimension() {
		jobDimension dim = new jobDimension();

		checkData();
		if(status) {
			dim.setName(txtName.getText());
			dim.setTargetDimension(Double.parseDouble(txtTarget.getText()) );

			return dim;
		}
		return null;
	}
}
