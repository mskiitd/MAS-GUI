package mas.globalSchedulingproxy.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mas.jobproxy.Batch;
import mas.util.JobQueryObject;
import net.miginfocom.swing.MigLayout;
import uiconstants.Labels;

public class JobQueryReplyFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	private JScrollPane scroller;
	private JPanel mainInfoPanel;

	private JLabel lblHeading;

	private JLabel lblJobIDHeading;
	private JLabel lblJobID;

	private JLabel lblCPNHeading;
	private JLabel lblCPN;

	private JLabel lblDueDateHeading;
	private JLabel lblDueDate;

	private JLabel lblPenaltyHeading;
	private JLabel lblPenaltyRate;

	private JLabel lblOperationsHeading;
	private JLabel lblOperations;

	private JLabel lblOperationsDoneHeading;
	private JLabel lblOperationsDone;
	
	private JLabel lblCurrentOperationheading;
	private JLabel lblCurrentOperation;
	
	private JLabel lblCurrentMachineHeading;
	private JLabel lblCurrentMachine;
	
	public JobQueryReplyFrame( JobQueryObject response) {

		this.scroller = new JScrollPane();
		this.mainInfoPanel = new JPanel(new MigLayout("","","10"));

		this.lblHeading = new JLabel("Job Query Results");

		this.lblJobIDHeading = new JLabel(Labels.CustomerLabels.jobID);
		this.lblCPNHeading = new JLabel(Labels.CustomerLabels.jobPriority);
		this.lblDueDateHeading = new JLabel(Labels.CustomerLabels.jobDueDate);
		this.lblPenaltyHeading = new JLabel(Labels.CustomerLabels.jobPenalty);
		this.lblOperationsHeading = new JLabel(Labels.CustomerLabels.jobOperationHeading);
		this.lblOperationsDoneHeading = new JLabel("Completed Operations : ");
		this.lblCurrentOperationheading = new JLabel("Current Operations : ");
		this.lblCurrentMachineHeading = new JLabel("Current Machine : ");

		this.lblJobID = new JLabel();
		this.lblCPN = new JLabel();
		this.lblDueDate = new JLabel();
		this.lblPenaltyRate = new JLabel();
		this.lblOperations = new JLabel();
		this.lblOperationsDone = new JLabel();
		this.lblCurrentOperation = new JLabel();
		this.lblCurrentMachine = new JLabel();

		if(response != null && response.getCurrentJob() != null ) {
			Batch theJob = response.getCurrentJob();
			lblCPN.setText(String.valueOf(theJob.getCPN()) );
			lblJobID.setText(theJob.getBatchId());
			lblDueDate.setText(String.valueOf(theJob.getDueDateByCustomer()));
			lblPenaltyRate.setText(String.valueOf(theJob.getPenaltyRate()));
			this.lblOperations.setText(String.valueOf(
					theJob.getFirstJob().getOperations()));
			
			String currOps="[";
			for(int i=0;i<theJob.getCurrentJobNo();i++){
				currOps=currOps+theJob.getCurrentOperationType();
				if(i!=(theJob.getNumOperations()-1)){
					currOps=currOps+",";
				}
			}
			currOps=currOps+"]";
			this.lblOperationsDone.setText(currOps);
			
			this.lblCurrentOperation.setText(theJob.getCurrentOperationType());
			
			if(response.isOnMachine()){
				this.lblCurrentMachine.setText(response.getCurrentMachine().getLocalName()+" (loaded)");
			}
			else{
				this.lblCurrentMachine.setText(response.getCurrentMachine().getLocalName());
			}
		}

//		mainInfoPanel.add(lblHeading);

		mainInfoPanel.add(lblJobIDHeading);
		mainInfoPanel.add(lblJobID,"wrap");

		mainInfoPanel.add(lblCPNHeading);
		mainInfoPanel.add(lblCPN,"wrap");

		mainInfoPanel.add(lblPenaltyHeading);
		mainInfoPanel.add(lblPenaltyRate,"wrap");

		/*mainInfoPanel.add(lblDueDateHeading);
		mainInfoPanel.add(lblDueDateHeading,"wrap");*/
		
		mainInfoPanel.add(lblOperationsHeading);
		mainInfoPanel.add(lblOperations,"wrap");
		
		mainInfoPanel.add(lblOperationsDoneHeading);
		mainInfoPanel.add(lblOperationsDone,"wrap");
		
		mainInfoPanel.add(lblCurrentOperationheading);
		mainInfoPanel.add(lblCurrentOperation,"wrap");
		
		mainInfoPanel.add(lblCurrentMachineHeading);
		mainInfoPanel.add(lblCurrentMachine,"wrap");

		this.scroller = new JScrollPane(mainInfoPanel);

		add(scroller);
		showGui();
	}

	private void showGui() {
		setTitle(" Batch Status ");
//		setPreferredSize(new Dimension(700,500));
		pack();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}	
}
