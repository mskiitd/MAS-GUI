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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import mas.jobproxy.OperationType;
import mas.jobproxy.job;
import mas.jobproxy.jobDimension;
import mas.jobproxy.jobOperation;
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

	private ArrayList<jobOperation> jobOps;

	private JLabel lblHeading;

	private boolean dataOk = false;

	public DefineJobOperationsFrame(job passedJob, int numOps, job populatingJob) {

		this.passedJob = passedJob;
		this.NumOps = numOps;

		if(populatingJob != null) {
			jobOps = populatingJob.getOperations();
		}

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
			if(jobOps != null && i < jobOps.size()) {
				opSpinner.setSelectedItem(jobOps.get(i).getJobOperationType());
			}
			operationBox.add(opSpinner);

			JTextField pTime = new JTextField(Labels.defaultJTextSize);

			if(jobOps != null && i < jobOps.size()) {
				pTime.setText(String.valueOf(jobOps.get(i).getProcessingTime()));
			}
			txtprocessingTimeList.add(pTime);

			JTextField targetDim = new JTextField(Labels.defaultJTextSize);
			if(jobOps != null && i < jobOps.size()) {
				targetDim.setText(String.valueOf(jobOps.get(i).getjDims().get(0).getTargetDimension()));
			}

			txtTargetDimensionList.add(targetDim);

			JTextField att = new JTextField(Labels.defaultJTextSize);
			if(jobOps != null && i < jobOps.size()) {
				att.setText(String.valueOf(jobOps.get(i).getjDims().get(0).getAttribute()));
			}

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

	private void populateOperations() {

		boolean x1 = true;

		operations.clear();

		for(int i = 0 ; i < NumOps ; i++ ) {
			jobOperation op = new jobOperation();

			x1 = x1 & checkProcTime(op,i);

			x1 = x1 & checkDimension(op, i);

			if(x1)
				operations.add(op);
		}
		if(x1)
			passedJob.setOperations(operations);
		
		dataOk = x1;
	}

	private boolean checkDimension(jobOperation op,int i) {
		boolean status = true;
		if(txtTargetDimensionList.get(i).getText().matches("-?\\d+(\\.\\d+)?")) {
			double targetDim = Double.parseDouble(txtTargetDimensionList.get(i).getText());
			jobDimension dimension = new jobDimension();
			dimension.setTargetDimension(targetDim);
			op.addjDim(dimension);
		}else {
			JOptionPane.showMessageDialog(this, "Invalid input for dimension !!");
			status = false;
		}
		return status;
	}

	private boolean checkProcTime(jobOperation op,int i) {
		boolean status = true;
		if(txtprocessingTimeList.get(i).getText().matches("-?\\d+(\\.\\d+)?")) {
			long pTime = Long.parseLong(txtprocessingTimeList.get(i).getText());
			op.setProcessingTime(pTime);
		}
		else {
			JOptionPane.showMessageDialog(this, "Invalid input for Processing time !!");
			status = false;
		}
		return status;
	}

	class submitOperationsDataListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(done)) {
				// build the job
				populateOperations();
				if(dataOk)
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
