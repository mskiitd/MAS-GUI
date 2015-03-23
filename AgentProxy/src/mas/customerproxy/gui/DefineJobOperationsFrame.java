package mas.customerproxy.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import mas.job.OperationType;
import mas.job.job;
import mas.job.jobAttribute;
import mas.job.jobDimension;
import mas.job.jobOperation;
import net.miginfocom.swing.MigLayout;
import uiconstants.Labels;

@SuppressWarnings("serial")
public class DefineJobOperationsFrame extends JFrame{
	private job passedJob;

	private JScrollPane scroller;
	private JPanel myPanel;
	private JButton done;

	private int NumOps;
	private ArrayList<jobOperation> operations;
	private ArrayList<JLabel> lblOpeartionTitleList;
	private ArrayList<JLabel> lblOpeartionTypeList;
	private ArrayList<JLabel> lblptimeList;
	private ArrayList<JLabel> lblTargetDimList;
	private ArrayList<JLabel> lblAttList;

	private ArrayList<JComboBox> operationBox;
	private ArrayList<JTextField> txtprocessingTimeList;
	private ArrayList<JTextField> txtTargetDimensionList;
	private ArrayList<JTextField> txtAttributeList;

	private JLabel lblHeading;

	public DefineJobOperationsFrame(job passedJob, int numOps) {

		this.passedJob = passedJob;
		this.NumOps = numOps;

		this.scroller = new JScrollPane();
		this.myPanel = new JPanel(new MigLayout());

		operations = new ArrayList<jobOperation>();

		lblOpeartionTypeList = new ArrayList<JLabel>();
		lblptimeList = new ArrayList<JLabel>();
		lblTargetDimList = new ArrayList<JLabel>();
		lblAttList = new ArrayList<JLabel>();

		operationBox = new ArrayList<JComboBox>();
		lblOpeartionTitleList = new ArrayList<JLabel>();
		txtprocessingTimeList = new  ArrayList<JTextField>();
		txtTargetDimensionList = new ArrayList<JTextField>();
		txtAttributeList = new ArrayList<JTextField>();

		this.done = new JButton(Labels.CustomerLabels.jobOpeationsDoneButton);
		this.lblHeading = new JLabel(Labels.CustomerLabels.jobGenerateHeading);
		this.lblHeading.setFont(lblHeading.getFont().deriveFont(Font.BOLD));

		done.addActionListener(new submitOperationsDataListener());

		myPanel.add(lblHeading,"wrap");

		for(int i = 0 ; i < numOps ; i++) {

			JLabel opType = new JLabel("Operation Type");
			JLabel ptime = new JLabel("Processing Time");
			JLabel tdim = new JLabel("Target Dimension");
			JLabel aName = new JLabel("Attribute Name");

			lblOpeartionTypeList.add(opType);
			lblptimeList.add(ptime);
			lblTargetDimList.add(tdim);
			lblAttList.add(aName);

			JLabel lblOpHeading = new JLabel("Operation-" + (i + 1));
			lblOpeartionTitleList.add(lblOpHeading);

			JComboBox opSpinner = new JComboBox(OperationType.values());
			operationBox.add(opSpinner);

			JTextField pTime = new JTextField(Labels.defaultJTextSize);
			pTime.setText(String.valueOf(passedJob.getOperations().get(i).getProcessingTime()));
			txtprocessingTimeList.add(pTime);

			JTextField targetDim = new JTextField(Labels.defaultJTextSize);
			targetDim.setText(String.valueOf(passedJob.getOperations().
					get(i).getjDims().get(0).getTargetDimension()));
			txtTargetDimensionList.add(targetDim);

			JTextField att = new JTextField(Labels.defaultJTextSize);
			att.setText(String.valueOf(passedJob.getOperations().
					get(i).getjAttributes().get(0).getName()));
			txtAttributeList.add(att);
		}

		for(int i = 0 ; i < numOps ; i++) {
			myPanel.add(lblOpeartionTitleList.get(i),"wrap");

			myPanel.add(lblOpeartionTypeList.get(i));
			myPanel.add(operationBox.get(i),"wrap");

			myPanel.add(lblptimeList.get(i));
			myPanel.add(txtprocessingTimeList.get(i),"wrap");

			myPanel.add(lblAttList.get(i));
			myPanel.add(txtAttributeList.get(i),"wrap");

			myPanel.add(lblTargetDimList.get(i));
			myPanel.add(txtTargetDimensionList.get(i),"wrap");
		}
		myPanel.add(done);

		this.scroller = new JScrollPane(myPanel);
		add(scroller);
		showGui();
	}

	class submitOperationsDataListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(done)) {
				// build the job
				for(int i = 0 ; i < NumOps ; i++ ) {
					jobOperation op = new jobOperation();

					if(txtprocessingTimeList.get(i).getText().matches("\\d+")) {
						long pTime = Long.parseLong(txtprocessingTimeList.get(i).getText());
						op.setProcessingTime(pTime);
					}

					double targetDim = Double.parseDouble(txtTargetDimensionList.get(i).getText());
					jobDimension dimension = new jobDimension();
					dimension.setTargetDimension(targetDim);
					op.addjDim(dimension);

					String attribute = txtAttributeList.get(i).getText();
					jobAttribute jatt = new jobAttribute(attribute);
					op.addjAttrubute(jatt);

					operations.add(op);
				}
				passedJob.setOperations(operations);

				dispose();
			}
		}
	}

	private void showGui() {
		setPreferredSize(new Dimension(700,500));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}	
}
