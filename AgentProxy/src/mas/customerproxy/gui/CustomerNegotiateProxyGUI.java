package mas.customerproxy.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;

import mas.customerproxy.agent.CustomerAgent;
import mas.jobproxy.Batch;
import mas.jobproxy.job;
import mas.jobproxy.jobOperation;
import mas.util.DateLabelFormatter;
import mas.util.DefineJobOperationsFrame;
import mas.util.TableUtil;
import mas.util.formatter.doubleformatter.FormattedDoubleField;
import mas.util.formatter.integerformatter.FormattedIntegerField;
import mas.util.formatter.stringformatter.FormattedStringField;
import net.miginfocom.swing.MigLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import com.alee.extended.label.WebHotkeyLabel;

import uiconstants.Labels;

/**
 * @author Anand Prajapati
 * GUI which displays the batch under negotiation between customer and GSA.
 * This displays information about batch. Customer can make changes to the batch and then
 * either send it back for negotiation to GSA or confirm it or reject it.
 *
 */
public class CustomerNegotiateProxyGUI extends JFrame{

	private static final long serialVersionUID = 1L;
	private CustomerAgent cAgent;
	private BufferedImage plusButtonIcon;

	private JScrollPane scroller;
	private JPanel myPanel;
	private JPanel operationPanel;
	private JPanel btnPanel;
	private JButton confirmJob;
	private JButton negotiateJob;
	private JButton btnCancelNegotiation;
	public UtilDateModel dateModel;
	public Properties dateProperties;
	private JDatePanelImpl datePanel ;
	private JDatePickerImpl datePicker;
	private JSpinner timeSpinner;

	private int NumOps;

	private JLabel lblHeading;
	private JLabel lblJobID;
	private JLabel lblJobNo;
	private JLabel lblCPN;
	private JLabel lblDueDate;
	private JLabel lblOpsHeading;
	private JLabel lblPenalty;
	private JLabel lblWaitingTimeHeading;
	private JLabel lblBatchSize;
	private JLabel lblCustomerIdHeading;
	private JButton btnOperationPlus;

	private WebHotkeyLabel lblCustomerId;
	private FormattedStringField txtJobID;
	private FormattedIntegerField txtJobNo;
	private FormattedDoubleField txtCPN;
	private FormattedIntegerField txtNumOps;
	private JTextField txtWaitingTime;
	private FormattedIntegerField txtBatchSize;
	private FormattedDoubleField txtPenaltyRate;

	private Batch populatingBatch;
	private boolean dataOk = true;
	private boolean operationDataOk = false;

	private Logger log;
	private job generatedJob;

	public CustomerNegotiateProxyGUI(CustomerAgent cAgent, Batch passedBatch) {

		log = LogManager.getLogger();

		this.populatingBatch = passedBatch;
		if(populatingBatch != null) {
			generatedJob = populatingBatch.getFirstJob();
		}

		this.scroller = new JScrollPane();
		this.myPanel = new JPanel(new MigLayout());
		btnPanel = new JPanel(new FlowLayout());
		operationPanel = new JPanel(new MigLayout());
		this.cAgent = cAgent;
		this.confirmJob = new JButton("Confirm");
		this.negotiateJob = new JButton("Send For Negotiation");
		this.btnCancelNegotiation = new JButton("Reject Negotiation");

		dateModel = new UtilDateModel();

		dateProperties = new Properties();
		dateProperties.put("text.today", "Today");
		dateProperties.put("text.month", "Month");
		dateProperties.put("text.year", "Year");

		if(populatingBatch != null) {
			Calendar dudate = Calendar.getInstance();
			dudate.setTime(populatingBatch.getDueDateByCustomer());

			dateModel.setDate(dudate.get(Calendar.YEAR),
					dudate.get(Calendar.MONDAY),
					dudate.get(Calendar.DAY_OF_MONTH));

			dateModel.setSelected(true);
		}

		datePanel = new JDatePanelImpl(dateModel, dateProperties);

		datePicker = new JDatePickerImpl(datePanel,
				new DateLabelFormatter());

		timeSpinner = new JSpinner( new SpinnerDateModel() );
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm:ss");
		timeSpinner.setEditor(timeEditor);
		timeSpinner.setValue(new Date());

		//		try {
		//			plusButtonIcon = ImageIO.read(new File("resources/plusbutton.png"));
		//			btnOperationPlus = new JButton(new ImageIcon(plusButtonIcon));
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		}

		this.lblHeading = new JLabel(Labels.CustomerLabels.jobGenerateHeading);
		this.lblCPN = new JLabel(Labels.CustomerLabels.jobPriority);
		this.lblDueDate = new JLabel(Labels.CustomerLabels.jobDueDate);
		this.lblJobID = new JLabel(Labels.CustomerLabels.BatchID);
		this.lblJobNo = new JLabel(Labels.CustomerLabels.batchNo);
		this.lblOpsHeading = new JLabel(Labels.CustomerLabels.jobOperationHeading);
		this.lblPenalty = new JLabel(Labels.CustomerLabels.jobPenalty);
		this.lblBatchSize = new JLabel(Labels.CustomerLabels.batchSize);
		this.lblCustomerIdHeading = new JLabel("Customer Id : ");
		this.lblCustomerId = new WebHotkeyLabel();

		this.lblWaitingTimeHeading = new JLabel("Expected Time by GSA : ");
		this.txtWaitingTime = new JTextField(Labels.defaultJTextSize*2);

		this.txtCPN = new FormattedDoubleField();
		txtCPN.setColumns(Labels.defaultJTextSize);

		this.txtJobID = new FormattedStringField();
		txtJobID.setColumns(Labels.defaultJTextSize);

		this.txtJobNo = new FormattedIntegerField();
		txtJobNo.setColumns(Labels.defaultJTextSize);

		this.txtNumOps = new FormattedIntegerField();
		txtNumOps.setColumns(Labels.defaultJTextSize/2);

		this.txtPenaltyRate = new FormattedDoubleField();
		txtPenaltyRate.setColumns(Labels.defaultJTextSize);

		this.txtBatchSize = new FormattedIntegerField();
		txtBatchSize.setColumns(Labels.defaultJTextSize/2);

		this.lblHeading.setFont(TableUtil.headings);
		myPanel.add(lblHeading,"wrap");

		myPanel.add(lblCustomerIdHeading);
		myPanel.add(lblCustomerId,"wrap");

		myPanel.add(lblJobID);
		myPanel.add(txtJobID,"wrap");

		myPanel.add(lblJobNo);
		myPanel.add(txtJobNo,"wrap");

		myPanel.add(lblCPN);
		myPanel.add(txtCPN,"wrap");

		myPanel.add(lblPenalty);
		myPanel.add(txtPenaltyRate,"wrap");

		myPanel.add(lblBatchSize);
		myPanel.add(txtBatchSize,"wrap");

		myPanel.add(lblWaitingTimeHeading);
		myPanel.add(txtWaitingTime,"wrap");

		myPanel.add(lblDueDate);
		myPanel.add(datePicker);
		myPanel.add(timeSpinner,"wrap");

		myPanel.add(lblOpsHeading,"wrap");
		myPanel.add(operationPanel,"wrap");

		btnPanel.add(confirmJob);
		btnPanel.add(negotiateJob);
		btnPanel.add(btnCancelNegotiation);

		myPanel.add(btnPanel);

		this.scroller = new JScrollPane(myPanel);
		add(scroller);

		buttonListener clickListener = new buttonListener();
		confirmJob.addActionListener(clickListener);
		negotiateJob.addActionListener(clickListener);
		btnCancelNegotiation.addActionListener(clickListener);

		_populate();
		showGui();
	}

	private void _populate() {
		if(populatingBatch != null) {
			lblCustomerId.setText(populatingBatch.getCustomerId());

			txtJobID.setText(populatingBatch.getBatchId());
			txtJobID.setEnabled(false);

			txtJobNo.setText(String.valueOf(populatingBatch.getBatchNumber()));
			txtJobNo.setEnabled(false);

			txtWaitingTime.setText(String.valueOf(new Date(populatingBatch.getExpectedDueDate())) ) ;
			txtWaitingTime.setEnabled(false);

			txtCPN.setText(String.valueOf(populatingBatch.getCPN()));
			txtPenaltyRate.setText(String.valueOf(populatingBatch.getPenaltyRate()));

			txtNumOps.setText(String.valueOf(populatingBatch.getFirstJob().getOperations().size()));

			Calendar c1 = Calendar.getInstance();
			c1.setTime(populatingBatch.getDueDateByCustomer());

			timeSpinner.setValue(populatingBatch.getDueDateByCustomer());

			datePicker.getModel().
			setDate(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH));

			txtBatchSize.setText(String.valueOf(populatingBatch.getBatchCount()));

			ArrayList<jobOperation> ops = populatingBatch.getFirstJob().getOperations();
			operationPanel.removeAll();
			for(int i = 0; i < ops.size(); i++ ) {
				WebHotkeyLabel lblOp = new WebHotkeyLabel(ops.get(i).getJobOperationType());
				operationPanel.add(lblOp,"span " + ops.size());
			}
		}
	}

	/**
	 * Create new batch with its parameters read from the fields in GUI
	 */
	private void createJobFromParams() {

		boolean x2 = true, x3 = true,x4 = true,x5 = true;

		x2 = checkPenaltyRate();
		if(x2) {
			x3 = checkCPN();
		}
		if(x2 & x3) {
			x4 = checkDueDate();

			//			if(x4) {
			//				x5 = checkJobOperations();
			//			}
		}
		dataOk = x2&x3&x4&x5;

		if(dataOk) {
			dataOk = dataOk & checkBatchSize();
		}
	}

	/**
	 * @return True if the batch size is a valid integer
	 * </br> Sets batch size of the batch to the value in the field
	 */
	private boolean checkBatchSize() {
		boolean status = true;
		if(! txtBatchSize.getText().matches("-?\\d+?") ) {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(CustomerNegotiateProxyGUI.this,
							"Invalid input for batch size.",  "Error" , JOptionPane.ERROR_MESSAGE );
				}
			});

			status = false;
		}else {
			populatingBatch.setBatchId(populatingBatch.getBatchId());
			populatingBatch.clearAllJobs();
			int bSize = Integer.parseInt(txtBatchSize.getText());
			ArrayList<job> jobs = new ArrayList<job>();
			for(int i = 0; i < bSize ; i++ ) {
				job j = new job(generatedJob);
				jobs.add(j);
			}
			populatingBatch.setJobsInBatch(jobs);
		}

		return status;
	}


	//	private boolean checkJobOperations() {
	//		boolean status = true;
	//		if(generatedJob.getOperations() == null || generatedJob.getOperations().isEmpty()) {
	//
	//			SwingUtilities.invokeLater(new Runnable() {
	//				@Override
	//				public void run() {
	//					JOptionPane.showMessageDialog(CustomerNegotiateProxyGUI.this,
	//							"Please Give job Operation Details !!", "Error" , JOptionPane.ERROR_MESSAGE );
	//				}
	//			});
	//
	//			status = false;
	//		}
	//		return status;
	//	}

	/**
	 * @return True if the due date is in the poper format.
	 * Sets due date of batch to the value in the field
	 */
	private boolean checkDueDate() {
		boolean status = true;
		Date time = (Date) timeSpinner.getValue();
		Date jobDueDate = (Date) datePicker.getModel().getValue();

		if(time == null || jobDueDate == null) {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(CustomerNegotiateProxyGUI.this,
							"Invalid input for due date !!", "Error" , JOptionPane.ERROR_MESSAGE );
				}
			});

			status = false;
		} else {

			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			c1.setTime(time);
			c2.setTime(jobDueDate);

			Calendar calTime = Calendar.getInstance();
			calTime.set(
					c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),c2.get(Calendar.DAY_OF_MONTH),
					c1.get(Calendar.HOUR_OF_DAY), c1.get(Calendar.MINUTE), c1.get(Calendar.SECOND));

			if(calTime.getTimeInMillis() < System.currentTimeMillis()) {

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(CustomerNegotiateProxyGUI.this,
								"Please enter a due date after current Date.", "Error" , JOptionPane.ERROR_MESSAGE );
					}
				});

				status = false;
			}else {
				populatingBatch.setDueDateByCustomer(calTime.getTime());
			}
		}
		return status;
	}

	/**
	 * @return True if penalty rate entered by customer is in the proper format and set penalty rate of 
	 * current batch to the value in the field
	 */
	private boolean checkPenaltyRate() {
		boolean status = true;
		if(! txtPenaltyRate.getText().matches("-?\\d+(\\.\\d+)?") ) {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(CustomerNegotiateProxyGUI.this,
							"Invalid input for penalty rate !!", "Error", JOptionPane.ERROR_MESSAGE );
				}
			});

			status = false;
		}else {
			populatingBatch.setPenaltyRate(Double.parseDouble(
					txtPenaltyRate.getText() ) );
		}
		return status;
	}

	/**
	 * @return True if CPN entered by customer is valid. Otherwise show an error message and return false
	 * . Sets value of CPN of batch to the value in the field, if in proper format.
	 */
	private boolean checkCPN() {
		boolean status = true;
		if(! txtCPN.getText().matches("-?\\d+(\\.\\d+)?") ) {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(CustomerNegotiateProxyGUI.this,
							"Invalid input for CPN !!", "Error" , JOptionPane.ERROR_MESSAGE );
				}
			});

			status = false;
		}else {
			populatingBatch.setCPN(Double.parseDouble(
					txtCPN.getText() ) );
		}
		return status;
	}

	//	class AddOperationListener implements ActionListener {
	//
	//		@Override
	//		public void actionPerformed(ActionEvent e) {
	//			checkOperations();
	//
	//			if(operationDataOk) {
	//				DefineJobOperationsFrame ops = new 
	//						DefineJobOperationsFrame(populatingBatch.getFirstJob(), NumOps, populatingBatch.getFirstJob());
	//			}
	//		}
	//	}

	//	private void checkOperations() {
	//		boolean  x2 = true;
	//
	//		if(! txtNumOps.getText().matches("-?\\d+?")) {
	//
	//			SwingUtilities.invokeLater(new Runnable() {
	//				@Override
	//				public void run() {
	//					JOptionPane.showMessageDialog(CustomerNegotiateProxyGUI.this, 
	//							"Invalid input for number of operations.", "Error", JOptionPane.ERROR_MESSAGE );
	//				}
	//			});
	//
	//			x2 = false;
	//		} else {
	//			NumOps = Integer.parseInt(txtNumOps.getText());
	//		}
	//		operationDataOk = x2;
	//	}

	/**
	 * Initialized the parameters of display of the frame and make it visible at appropriate location 
	 * with desired size
	 */
	private void showGui() {
		setTitle("Customer - Negotiation Job");
		//		setPreferredSize(new Dimension(600,500));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}

	/**
	 * Action listener for buttons in this UI
	 * </br> Confirm 
	 * </br> Send For Negotiation 
	 * </br> Reject
	 */
	class buttonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// handle create job button pressed event
			if(e.getSource().equals(confirmJob)) {

				createJobFromParams();
				log.info("data format : " + dataOk);
				if(dataOk) {
					log.info("Confirming the job : " + generatedJob);

					new Thread(new Runnable() {
						@Override
						public void run() {
							cAgent.confirmJob(populatingBatch);
						}
					}).start();

					dispose();
				}

			} else if(e.getSource().equals(negotiateJob)) {

				createJobFromParams();
				log.info("Negotiation data format : " + dataOk);
				if(dataOk) {
					log.info("Negotiating the job : " + populatingBatch);

					new Thread(new Runnable() {
						@Override
						public void run() {
							cAgent.negotiateJob(populatingBatch);
						}
					}).start();

					dispose();
				}
			} else if(e.getSource().equals(btnCancelNegotiation)) {

				new Thread(new Runnable() {
					@Override
					public void run() {
						cAgent.rejectNegotiation();
					}
				}).start();

				dispose();
			}
		}
	};
}
