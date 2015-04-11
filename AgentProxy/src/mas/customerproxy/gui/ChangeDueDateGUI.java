package mas.customerproxy.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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
import mas.util.DateLabelFormatter;
import mas.util.TableUtil;
import mas.util.formatter.integerformatter.FormattedIntegerField;
import net.miginfocom.swing.MigLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import uiconstants.Labels;

public class ChangeDueDateGUI extends JFrame{

	private static final long serialVersionUID = 1L;
	private CustomerAgent custAgent;
	private BufferedImage plusButtonIcon;

	private JScrollPane scroller;
	private JPanel myPanel;
	private JPanel operationPanel;
	private JButton changeDueDateJob;
	private JButton cancel;
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
	private JLabel lblBatchSize;
	private JButton btnOperationPlus;

	private JTextField txtJobID;
	private JTextField txtJobNo;
	private JTextField txtCPN;
	private JTextField txtNumOps;
	private FormattedIntegerField txtBatchSize;
	private JTextField txtPenaltyRate;

	private Batch populatingBatch;
	private job populatingJob;
	private boolean dataOk = true;
	private boolean operationDataOk = false;

	private Logger log;

	public ChangeDueDateGUI(CustomerAgent cAgent, Batch passedBatch) {

		log = LogManager.getLogger();
		populatingBatch = passedBatch;

		if(populatingBatch != null) {
			this.populatingJob = populatingBatch.getSampleJob();
		}

		this.scroller = new JScrollPane();
		this.myPanel = new JPanel(new MigLayout());
		operationPanel = new JPanel(new MigLayout());
		this.custAgent = cAgent;
		this.changeDueDateJob = new JButton("Send");
		this.cancel = new JButton("Cancel");

		dateModel = new UtilDateModel();

		dateProperties = new Properties();
		dateProperties.put("text.today", "Today");
		dateProperties.put("text.month", "Month");
		dateProperties.put("text.year", "Year");

		if(populatingJob != null) {
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

		try {
			plusButtonIcon = ImageIO.read(new File("resources/plusbutton.png"));
			btnOperationPlus = new JButton(new ImageIcon(plusButtonIcon));
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.lblHeading = new JLabel(Labels.CustomerLabels.jobGenerateHeading);
		this.lblCPN = new JLabel(Labels.CustomerLabels.jobPriority);
		this.lblDueDate = new JLabel(Labels.CustomerLabels.jobDueDate);
		this.lblJobID = new JLabel(Labels.CustomerLabels.jobID);
		this.lblJobNo = new JLabel(Labels.CustomerLabels.jobNo);
		this.lblOpsHeading = new JLabel(Labels.CustomerLabels.jobOperationHeading);
		this.lblPenalty = new JLabel(Labels.CustomerLabels.jobPenalty);
		this.lblBatchSize = new JLabel(Labels.CustomerLabels.batchSize);

		this.txtCPN = new JTextField(Labels.defaultJTextSize);
		this.txtJobID = new JTextField(Labels.defaultJTextSize);
		this.txtJobNo = new JTextField(Labels.defaultJTextSize);
		this.txtNumOps = new JTextField(Labels.defaultJTextSize/2);
		this.txtPenaltyRate = new JTextField(Labels.defaultJTextSize);

		this.txtCPN.setEnabled(false);
		this.txtJobID.setEnabled(false);
		this.txtJobNo.setEnabled(false);
		this.txtNumOps.setEnabled(false);
		this.txtPenaltyRate.setEditable(false);

		this.txtBatchSize = new FormattedIntegerField();
		txtBatchSize.setColumns(Labels.defaultJTextSize/2);
		this.txtBatchSize.setEnabled(false);

		this.lblHeading.setFont(TableUtil.headings);
		myPanel.add(lblHeading,"wrap");

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

		myPanel.add(lblDueDate);
		myPanel.add(datePicker);
		myPanel.add(timeSpinner,"wrap");

		operationPanel.add(lblOpsHeading);
		operationPanel.add(txtNumOps);
		operationPanel.add(btnOperationPlus,"wrap");

		btnOperationPlus.addActionListener(new AddOperationListener());

		myPanel.add(operationPanel,"wrap");

		myPanel.add(changeDueDateJob);

		this.scroller = new JScrollPane(myPanel);
		add(scroller);

		buttonListener clickListener = new buttonListener();
		changeDueDateJob.addActionListener(clickListener);

		_populate();

		showGui();
	}

	private void _populate() {
		if(populatingJob != null) {
			txtJobID.setText(populatingBatch.getBatchId());
			txtJobID.setEnabled(false);

			txtJobNo.setText(String.valueOf(populatingBatch.getBatchNumber()));
			txtJobNo.setEnabled(false);

			txtCPN.setText(String.valueOf(populatingBatch.getCPN()));
			txtCPN.setEnabled(false);

			txtPenaltyRate.setText(String.valueOf(populatingBatch.getPenaltyRate()));
			txtPenaltyRate.setEnabled(false);

			txtNumOps.setText(String.valueOf(populatingJob.getOperations().size()));
			txtNumOps.setEnabled(false);

			Calendar c1 = Calendar.getInstance();
			c1.setTime(populatingBatch.getDueDateByCustomer());

			timeSpinner.setValue(populatingBatch.getDueDateByCustomer());

			datePicker.getModel().
			setDate(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH));

			txtBatchSize.setText(String.valueOf(populatingBatch.getBatchCount()));
			txtBatchSize.setEnabled(false);
		}
	}

	private void createJobFromParams() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean x2 = true,x3 = true,x4 = true,x5 = true;

				x2 = checkPenaltyRate();
				if(x2) {
					x3 = checkCPN();
				}
				if(x2 & x3) {
					x4 = checkDueDate();

					if(x4) {
						x5 = checkJobOperations();
					}
				}

				dataOk = x2 & x3 &x4 & x5;
			}
		}).start();
	}

	private boolean checkJobOperations() {
		boolean status = true;
		if(populatingJob.getOperations() == null ) {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(ChangeDueDateGUI.this,
							"Please Give job Operation Details !!", "Error" , JOptionPane.ERROR_MESSAGE );					
				}
			});
			status = false;

		}else {
			if(populatingJob.getOperations().isEmpty()) {

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(ChangeDueDateGUI.this,
								"Please Give job Operation Details !!","Error" , JOptionPane.ERROR_MESSAGE );
					}
				});
				status = false;
			}
		}
		return status;
	}

	private boolean checkDueDate() {
		boolean status = true;
		Date time = (Date) timeSpinner.getValue();
		Date jobDueDate = (Date) datePicker.getModel().getValue();

		if(time == null || jobDueDate == null) {

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					JOptionPane.showMessageDialog(ChangeDueDateGUI.this,
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
						JOptionPane.showMessageDialog(ChangeDueDateGUI.this,
								"Please enter a due date after current Date !!", "Error", JOptionPane.ERROR_MESSAGE );						
					}
				});

				status = false;
			}else {
				populatingJob.setJobDuedatebyCust(calTime.getTime());
			}
		}
		return status;
	}

	private boolean checkPenaltyRate() {
		boolean status = true;
		if(! txtPenaltyRate.getText().matches("-?\\d+(\\.\\d+)?") ) {

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					JOptionPane.showMessageDialog(ChangeDueDateGUI.this,
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

	private boolean checkCPN() {
		boolean status = true;
		if(! txtCPN.getText().matches("-?\\d+(\\.\\d+)?") ) {
			
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					JOptionPane.showMessageDialog(ChangeDueDateGUI.this,
							"Invalid input for CPN !!", "Error", JOptionPane.ERROR_MESSAGE );
				}
			});
			
			status = false;
		}else {
			populatingBatch.setCPN(Double.parseDouble(
					txtCPN.getText() ) );
		}
		return status;
	}

	class AddOperationListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			checkOperations();

			if(operationDataOk) {
				DisplayOperationFrame ops = new 
						DisplayOperationFrame(populatingJob, NumOps, populatingJob);
			}
		}
	}

	private void checkOperations() {
		boolean  x2 = true;

		if(! txtNumOps.getText().matches("-?\\d+?")) {
			
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(ChangeDueDateGUI.this,
							"Invalid input for number of operations !!", "Error", JOptionPane.ERROR_MESSAGE );
				}
			});
			x2 = false;
			
		} else {
			NumOps = Integer.parseInt(txtNumOps.getText());
		}
		operationDataOk = x2;
	}

	private void showGui() {
		setTitle("Customer Agent - Change Due Date");
		//		setPreferredSize(new Dimension(600,500));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
			if(e.getSource().equals(changeDueDateJob)) {

				createJobFromParams();
				if(dataOk) {
					dispose();
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							custAgent.sendGeneratedBatch(populatingBatch);
						}
					}).start();
					//					custAgent.(populatingBatch);
				}
			}

			else if(e.getSource().equals(cancel)){
				dispose();
			}
		}
	};
}

