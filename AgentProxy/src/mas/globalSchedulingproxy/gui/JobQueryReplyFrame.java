package mas.globalSchedulingproxy.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.alee.extended.label.WebHotkeyLabel;

import mas.jobproxy.Batch;
import mas.util.BatchQueryObject;
import net.miginfocom.swing.MigLayout;
import uiconstants.Labels;
/**
 * Shows current status of order
 * @author NikhilChilwant
 *
 */
public class JobQueryReplyFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	private JScrollPane scroller;
	private JPanel mainInfoPanel;

	private JLabel lblHeading;

	private JLabel lblBatchIDHeading;
	private WebHotkeyLabel lblBatchID;

	private JLabel lblCPNHeading;
	private WebHotkeyLabel lblCPN;

	private JLabel lblDueDateHeading;
	private WebHotkeyLabel lblDueDate;

	private JLabel lblPenaltyHeading;
	private WebHotkeyLabel lblPenaltyRate;

	private JLabel lblOperationsHeading;
	private WebHotkeyLabel lblOperations;

	private JLabel lblOperationsDoneHeading;
	private WebHotkeyLabel lblOperationsDone;
	
	private JLabel lblCurrentOperationheading;
	private WebHotkeyLabel lblCurrentOperation;
	
	private JLabel lblCurrentMachineHeading;
	private WebHotkeyLabel lblCurrentMachine;
	
	public JobQueryReplyFrame( BatchQueryObject response) {

		this.scroller = new JScrollPane();
		this.mainInfoPanel = new JPanel(new MigLayout("","","10"));

		this.lblHeading = new JLabel("Job Query Results");

		this.lblBatchIDHeading = new JLabel(Labels.CustomerLabels.BatchID);
		this.lblCPNHeading = new JLabel(Labels.CustomerLabels.jobPriority);
		this.lblDueDateHeading = new JLabel(Labels.CustomerLabels.jobDueDate);
		this.lblPenaltyHeading = new JLabel(Labels.CustomerLabels.jobPenalty);
		this.lblOperationsHeading = new JLabel(Labels.CustomerLabels.jobOperationHeading);
		this.lblOperationsDoneHeading = new JLabel("Completed Operations : ");
		this.lblCurrentOperationheading = new JLabel("Current Operations : ");
		this.lblCurrentMachineHeading = new JLabel("Current Machine : ");

		this.lblBatchID = new WebHotkeyLabel();
		this.lblCPN = new WebHotkeyLabel();
		this.lblDueDate = new WebHotkeyLabel();
		this.lblPenaltyRate = new WebHotkeyLabel();
		this.lblOperations = new WebHotkeyLabel();
		this.lblOperationsDone = new WebHotkeyLabel();
		this.lblCurrentOperation = new WebHotkeyLabel();
		this.lblCurrentMachine = new WebHotkeyLabel();

		if(response != null && response.getCurrentBatch() != null ) {
			Batch batch = response.getCurrentBatch();
			lblCPN.setText(String.valueOf(batch.getCPN()) );
			lblBatchID.setText(batch.getBatchId());
			lblDueDate.setText(String.valueOf(batch.getDueDateByCustomer()));
			lblPenaltyRate.setText(String.valueOf(batch.getPenaltyRate()));
			this.lblOperations.setText(String.valueOf(
					batch.getFirstJob().getOperations()));
			
			String completedOperations = "[ ";
			for(int i = 0; i < batch.getCurrentOperationNumber();i++) {
				completedOperations = completedOperations + batch.getFirstJob().getOperations().get(i).getJobOperationType();
				if( i != (batch.getNumOperations()-1) ){
					completedOperations = completedOperations + ", ";
				}
			}
			completedOperations = completedOperations + " ]";
			this.lblOperationsDone.setText(completedOperations);
			
			this.lblCurrentOperation.setText(batch.getCurrentOperationType());
			
			if(response.isOnMachine()) {
				this.lblCurrentMachine.setText(response.getCurrentMachine().getLocalName()+" (Loaded)");
			}
			else{
				this.lblCurrentMachine.setText(response.getCurrentMachine().getLocalName());
			}
		}
//		mainInfoPanel.add(lblHeading);

		mainInfoPanel.add(lblBatchIDHeading);
		mainInfoPanel.add(lblBatchID,"wrap");

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
