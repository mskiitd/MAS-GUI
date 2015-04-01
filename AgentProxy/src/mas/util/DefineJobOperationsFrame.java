package mas.util;

import java.awt.Dimension;
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

import mas.jobproxy.Batch;
import mas.jobproxy.OperationType;
import mas.jobproxy.job;
import mas.jobproxy.jobOperation;
import net.miginfocom.swing.MigLayout;
import uiconstants.Labels;

@SuppressWarnings("serial")
public class DefineJobOperationsFrame extends JFrame{
	private Batch passedBatch;

	private JScrollPane scroller;
	private JPanel myPanel;
	private JButton done;

	private int NumOps;
	private ArrayList<jobOperation> operations;
	private ArrayList<JLabel> lblOpeartionTitleList;
	private ArrayList<JLabel> lblOpeartionTypeList;
//	private ArrayList<JLabel> lblptimeList;
//	private ArrayList<JLabel> lblTargetDimList;
//	private ArrayList<JLabel> lblAttList;

	private ArrayList<JComboBox<?> > operationBoxList;
//	private ArrayList<JTextField> txtprocessingTimeList;
//	private ArrayList<JTextField> txtTargetDimensionList;
//	private ArrayList<JTextField> txtAttributeList;

	private ArrayList<jobOperation> batchOperations;

	private JLabel lblHeading;

	private boolean dataOk = false;
	private String jobIdJob;

	public DefineJobOperationsFrame(Batch passedJob, int numOps, Batch populatingBatch) {

		this.passedBatch = passedJob;
		this.NumOps = numOps;

		if(populatingBatch != null) {
			batchOperations = populatingBatch.getSampleJob().getOperations();
			jobIdJob = passedJob.getBatchId().split("o")[0];
		}

		this.scroller = new JScrollPane();
		this.myPanel = new JPanel(new MigLayout());

		operations = new ArrayList<jobOperation>();

		lblOpeartionTypeList = new ArrayList<JLabel>();
//		lblptimeList = new ArrayList<JLabel>();
//		lblTargetDimList = new ArrayList<JLabel>();
//		lblAttList = new ArrayList<JLabel>();

		operationBoxList = new ArrayList<JComboBox<?> >();
		lblOpeartionTitleList = new ArrayList<JLabel>();
//		txtprocessingTimeList = new  ArrayList<JTextField>();
//		txtTargetDimensionList = new ArrayList<JTextField>();
//		txtAttributeList = new ArrayList<JTextField>();

		this.done = new JButton(Labels.CustomerLabels.jobOpeationsDoneButton);
		this.lblHeading = new JLabel("Operations of Job");

		done.addActionListener(new submitOperationsDataListener());

		this.lblHeading.setFont(TableUtil.headings);
		myPanel.add(lblHeading,"span, wrap");
		
		for(int i = 0 ; i < numOps ; i++) {

			JLabel opType = new JLabel("Operation Type");
//			JLabel ptime = new JLabel("Processing Time");
//			JLabel tdim = new JLabel("Target Dimension");
//			JLabel aName = new JLabel("Attribute Name");

			lblOpeartionTypeList.add(opType);
//			lblptimeList.add(ptime);
//			lblTargetDimList.add(tdim);
//			lblAttList.add(aName);

			JLabel lblOpHeading = new JLabel("Operation-" + (i + 1));
			lblOpHeading.setFont(TableUtil.headings);
			
			lblOpeartionTitleList.add(lblOpHeading);

			ArrayList<String> possibleOps = new ArrayList<String>();
			for (OperationType op : OperationType.values()) {
				String jobIdEnum = op.name().split("o")[0];
				if(jobIdEnum.equals(jobIdJob)) {
					possibleOps.add(op.name());
				}
			}
			JComboBox<?> opSpinner = new JComboBox<Object>(possibleOps.toArray());
			
			if(batchOperations != null && i < batchOperations.size()) {
				opSpinner.setSelectedItem(batchOperations.get(i).getJobOperationType());
			}
			operationBoxList.add(opSpinner);

//			JTextField pTime = new JTextField(Labels.defaultJTextSize);
//
//			if(jobOps != null && i < jobOps.size()) {
//				pTime.setText(String.valueOf(jobOps.get(i).getProcessingTime()));
//			}
//			txtprocessingTimeList.add(pTime);

//			JTextField targetDim = new JTextField(Labels.defaultJTextSize);
//			if(jobOps != null && i < jobOps.size()) {
//				targetDim.setText(String.valueOf(jobOps.get(i).getjDims().get(0).getTargetDimension()));
//			}
//
//			txtTargetDimensionList.add(targetDim);
//
//			JTextField att = new JTextField(Labels.defaultJTextSize);
//			if(jobOps != null && i < jobOps.size()) {
//				att.setText(String.valueOf(jobOps.get(i).getjDims().get(0).getAttribute()));
//			}
//
//			txtAttributeList.add(att);
		}

		for(int i = 0 ; i < numOps ; i++) {
			myPanel.add(lblOpeartionTitleList.get(i),"wrap");

			myPanel.add(lblOpeartionTypeList.get(i));
			myPanel.add(operationBoxList.get(i),"gapleft 30, wrap");

//			myPanel.add(lblptimeList.get(i));
//			myPanel.add(txtprocessingTimeList.get(i),"wrap");
//
//			myPanel.add(lblAttList.get(i));
//			myPanel.add(txtAttributeList.get(i),"wrap");
//
//			myPanel.add(lblTargetDimList.get(i));
//			myPanel.add(txtTargetDimensionList.get(i),"wrap");
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
			op.setJobOperationType((String) operationBoxList.get(i).getSelectedItem());
			operations.add(op);
//			x1 = x1 & checkProcTime(op,i);
//			x1 = x1 & checkDimension(op, i);
//			if(x1)
//				operations.add(op);
		}
		if(x1)
			passedBatch.setOperations(operations);
		
		dataOk = x1;
	}

	private boolean checkDimension(jobOperation op,int i) {
		boolean status = true;
//		if(txtTargetDimensionList.get(i).getText().matches("-?\\d+(\\.\\d+)?")) {
//			double targetDim = Double.parseDouble(txtTargetDimensionList.get(i).getText());
//			jobDimension dimension = new jobDimension();
//			dimension.setTargetDimension(targetDim);
//			op.addjDim(dimension);
//		}else {
//			JOptionPane.showMessageDialog(this, "Invalid input for dimension !!");
//			status = false;
//		}
		return status;
	}

	private boolean checkProcTime(jobOperation op,int i) {
		boolean status = true;
//		if(txtprocessingTimeList.get(i).getText().matches("-?\\d+(\\.\\d+)?")) {
//			long pTime = Long.parseLong(txtprocessingTimeList.get(i).getText());
//			op.setProcessingTime(pTime);
//		}
//		else {
//			JOptionPane.showMessageDialog(this, "Invalid input for Processing time !!");
//			status = false;
//		}
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
		setTitle(" Define Job Operations ");
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
