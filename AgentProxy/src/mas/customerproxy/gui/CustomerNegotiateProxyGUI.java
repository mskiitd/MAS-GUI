package mas.customerproxy.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import uiconstants.Labels;
import mas.customerproxy.agent.CustomerAgent;
import mas.jobproxy.job;
import net.miginfocom.swing.MigLayout;

public class CustomerNegotiateProxyGUI extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JScrollPane scroller;
	private JPanel myPanel;
	private JButton confirmJob;
	private JButton negotiateJob;
	private CustomerAgent cAgent;
	private job myJob;
	
	private String JobID;
	private double CPN;
	private long DueDate;
	private int NumOps;
	private double penaltyRate;
	private List<Long> list_ProcessingTime ;
	private List<Double> list_TargetDimensions; 

	private JLabel lblHeading;
	private JLabel lblJobID;
	private JLabel lblCPN;
	private JLabel lblDueDate;
	private JLabel lblNumOps;
	private JLabel lblPenaltyRate;
	private List<JLabel> list_lblOperationTitles;
	private List<JLabel> list_lblProcessingTime ;
	private List<JLabel> list_lblDimensionTitles ;
	private List<JLabel> list_lblTargetDimensions ;

	private JTextField txtJobID;
	private JTextField txtCPN;
	private JTextField txtDueDate;
	private JTextField txtNumOps;
	private JTextField txtPenaltyRate;
	private List<JTextField> list_txtProcessingTime ;
	private List<JTextField> list_txtTargetDimensions ;

	public CustomerNegotiateProxyGUI(CustomerAgent cAgent, job passedJob) {

		this.myPanel = new JPanel(new MigLayout());
		this.cAgent = cAgent;
		this.myJob = passedJob;
		
		this.confirmJob = new JButton("Confirm");
		this.negotiateJob = new JButton("Send For Negotiation");

		this.lblHeading = new JLabel(Labels.CustomerLabels.jobGenerateHeading);
		this.list_ProcessingTime = new ArrayList<Long>();
		this.list_TargetDimensions = new ArrayList<Double>();

		this.lblCPN = new JLabel(Labels.CustomerLabels.jobPriority);
		this.lblDueDate = new JLabel(Labels.CustomerLabels.jobDueDate);
		this.lblJobID = new JLabel(Labels.CustomerLabels.jobID);
		this.lblNumOps = new JLabel(Labels.CustomerLabels.jobOperationHeading);
		this.lblPenaltyRate = new JLabel(Labels.CustomerLabels.jobPenalty);
		this.list_lblDimensionTitles = new ArrayList<JLabel>();
		this.list_lblOperationTitles = new ArrayList<JLabel>();
		this.list_lblProcessingTime = new ArrayList<JLabel>();
		this.list_lblTargetDimensions = new ArrayList<JLabel>();

		this.txtCPN = new JTextField(Labels.defaultJTextSize);
		this.txtDueDate = new JTextField(Labels.defaultJTextSize);
		this.txtJobID = new JTextField(Labels.defaultJTextSize);
		this.txtNumOps = new JTextField(Labels.defaultJTextSize);
		this.txtPenaltyRate = new JTextField(Labels.defaultJTextSize);
		
		myPanel.add(lblHeading,"wrap");

		myPanel.add(lblJobID);
		myPanel.add(txtJobID,"wrap");

		myPanel.add(lblCPN);
		myPanel.add(txtCPN,"wrap");
		
		myPanel.add(lblPenaltyRate,"wrap");
		myPanel.add(txtPenaltyRate);

		myPanel.add(lblDueDate);
		myPanel.add(txtDueDate,"wrap");

		myPanel.add(lblNumOps);
		myPanel.add(txtNumOps,"wrap");

		this.myPanel.add(confirmJob);
		this.myPanel.add(negotiateJob);
		this.scroller = new JScrollPane(this.myPanel);

		txtCPN.setText(myJob.getCPN() + "");
		txtDueDate.setText(myJob.getJobDuedatebyCust() +"");
		txtJobID.setText(myJob.getJobID());
		txtPenaltyRate.setText(myJob.getPenaltyRate() + "");
		txtNumOps.setText(myJob.getOperations().size() + "");
		
		txtJobID.setEnabled(false);
		
		add(scroller);
		showGui();
	}

	private void showGui() {
		setPreferredSize(new Dimension(600,500));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}

	class buttonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// handle create job button pressed event
			if(e.getSource().equals(confirmJob)) {
				CPN = Double.parseDouble(txtCPN.getText());
				penaltyRate = Double.parseDouble(txtPenaltyRate.getText());
				
				myJob.setCPN(CPN);
				myJob.setPenaltyRate(penaltyRate);;
				
				cAgent.confirmJob(myJob);
				
				dispose();
			} else if(e.getSource().equals(negotiateJob)) {
				
				CPN = Double.parseDouble(txtCPN.getText());
				penaltyRate = Double.parseDouble(txtPenaltyRate.getText());
				
				myJob.setCPN(CPN);
				myJob.setPenaltyRate(penaltyRate);
				
				cAgent.negotiateJob(myJob);
				dispose();
			}
		}
	};
}