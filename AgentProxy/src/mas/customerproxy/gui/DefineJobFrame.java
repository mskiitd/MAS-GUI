package mas.customerproxy.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;

import mas.customerproxy.agent.CustomerAgent;
import mas.jobproxy.Batch;
import mas.jobproxy.job;
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

import uiconstants.Labels;

@SuppressWarnings("serial")
public class DefineJobFrame extends JFrame{

	private CustomerAgent cAgent;
	private job generatedJob;
	private BufferedImage plusButtonIcon;

	private JScrollPane scroller;
	private JPanel myPanel;
	private JPanel operationPanel;
	private JButton sendJob;
	public UtilDateModel dateModel;
	public Properties dateProperties;
	private JDatePanelImpl datePanel ;
	private JDatePickerImpl datePicker;
	private JSpinner timeSpinner;

	private int NumOps;

	private JLabel lblHeading;
	private JLabel lblJobID;
	private JLabel lblCPN;
	private JLabel lblDueDate;
	private JLabel lblOpsHeading;
	private JLabel lblPenalty;
	private JLabel lblBatchSize;
	private JButton btnOperationPlus;

	private FormattedStringField txtJobID;
	private FormattedDoubleField txtCPN;
	private FormattedIntegerField txtNumOps;
	private FormattedDoubleField txtPenalty;
	private FormattedIntegerField txtBatchSize;

	private Batch populatingBatch;
	private boolean dataOk = true;
	private boolean operationDataOk = false;

	private Logger log;

	public DefineJobFrame(CustomerAgent cAgent, Batch passedBatch) {

		log = LogManager.getLogger();

		this.populatingBatch = passedBatch;

		this.scroller = new JScrollPane();
		this.myPanel = new JPanel(new MigLayout());
		operationPanel = new JPanel(new MigLayout());
		this.cAgent = cAgent;
		this.sendJob = new JButton(Labels.sendJobButton);

		dateModel = new UtilDateModel();
		dateProperties = new Properties();
		dateProperties.put("text.today", "Today");
		dateProperties.put("text.month", "Month");
		dateProperties.put("text.year", "Year");

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
		this.lblOpsHeading = new JLabel(Labels.CustomerLabels.jobOperationHeading);
		this.lblPenalty = new JLabel(Labels.CustomerLabels.jobPenalty);
		this.lblBatchSize = new JLabel(Labels.CustomerLabels.batchSize);

		this.txtCPN = new FormattedDoubleField();
		txtCPN.setColumns(Labels.defaultJTextSize);

		this.txtJobID = new FormattedStringField();
		txtJobID.setColumns(Labels.defaultJTextSize);

		this.txtNumOps = new FormattedIntegerField();
		txtNumOps.setColumns(Labels.defaultJTextSize/2);

		this.txtPenalty = new FormattedDoubleField();
		txtPenalty.setColumns(Labels.defaultJTextSize);

		this.txtBatchSize = new FormattedIntegerField();
		txtBatchSize.setColumns(Labels.defaultJTextSize);
		txtBatchSize.setValue(1);

		this.sendJob = new JButton(Labels.sendJobButton);
		sendJob.addActionListener(new sendJobListener());

		this.lblHeading.setFont(TableUtil.headings);
		myPanel.add(lblHeading,"wrap");

		myPanel.add(lblJobID);
		myPanel.add(txtJobID,"wrap");

		myPanel.add(lblCPN);
		myPanel.add(txtCPN,"wrap");

		myPanel.add(lblPenalty);
		myPanel.add(txtPenalty,"wrap");

		myPanel.add(lblBatchSize);
		myPanel.add(txtBatchSize,"wrap");

		myPanel.add(lblDueDate);
		myPanel.add(datePicker);
		myPanel.add(timeSpinner,"wrap");

		//		operationPanel.add(lblOpsHeading);
		//		operationPanel.add(txtNumOps);
		//		operationPanel.add(btnOperationPlus,"wrap");

		//		btnOperationPlus.addActionListener(new AddOperationListener());

		//		myPanel.add(operationPanel,"wrap");

		myPanel.add(sendJob);

		this.scroller = new JScrollPane(myPanel);
		add(scroller);
		_populate();
		showGui();
	}

	private void _populate() {
		if(populatingBatch != null) {
			txtJobID.setText(populatingBatch.getBatchId() );
			txtCPN.setText(String.valueOf(populatingBatch.getCPN()) );
			txtPenalty.setText(String.valueOf(populatingBatch.getPenaltyRate()) );
			txtNumOps.setText(String.valueOf(populatingBatch.getSampleJob().getOperations().size()) );
		}
	}

	private void createJobFromParams() {

		new Thread(new Runnable() {
			@Override
			public void run() {

				boolean x1 = true;
				if(generatedJob == null) {
					if(txtJobID.getText().isEmpty()) {

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								JOptionPane.showMessageDialog(DefineJobFrame.this,
										"Please enter batch ID !!");
							}
						});

						x1 = false;
					}else {
						generatedJob = new job.Builder(txtJobID.getText().toString()).build();

						if(populatingBatch != null) {
							populatingBatch.setBatchId(txtJobID.getText().toString());
						}else {
							populatingBatch = new Batch(txtJobID.getText());
						}
						boolean x2 = false,x3 = true,x4 = true,x5 = true;
						x2 = checkPenaltyRate();
						if(x2) {
							x3 = checkCPN();
						}
						populatingBatch.setGenerationTime(new Date());
						populatingBatch.setBatchNumber(CustomerProxyGUI.countBatch);
						if(x2 & x3) {
							x4 = checkDueDate();
						}

						dataOk = x2 & x3 & x4 & x5;

						if(dataOk) {
							dataOk = dataOk & checkBatchSize();
						}

						populatingBatch.setCustomerId(cAgent.getLocalName());
					}
				}
				else {
					boolean x2 = true,x3 = true,x4 = true,x5 = true;
					x2 = checkPenaltyRate();
					if(x2) {
						x3 = checkCPN();
					}
					populatingBatch.setGenerationTime(new Date());
					populatingBatch.setBatchNumber(CustomerProxyGUI.countBatch);
					if(x2 & x3) {
						x4 = checkDueDate();
					}
					dataOk = x1 & x2 & x3 & x4 & x5;

					if(dataOk) {
						dataOk = dataOk & checkBatchSize();
					}
					populatingBatch.setCustomerId(cAgent.getLocalName());
				}
			}
		}).start();

	}

	private boolean checkDueDate() {
		boolean status = true;
		Date time = (Date) timeSpinner.getValue();
		Date jobDueDate = (Date) datePicker.getModel().getValue();

		if(time == null || jobDueDate == null) {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(DefineJobFrame.this,
							"Invalid input for due date.", "Error" , JOptionPane.ERROR_MESSAGE );
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
						JOptionPane.showMessageDialog(DefineJobFrame.this,
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

	private boolean checkPenaltyRate() {
		boolean status = true;
		if(! txtPenalty.getText().matches("-?\\d+(\\.\\d+)?") ) {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(DefineJobFrame.this,
							"Invalid input for penalty rate !!", "Error", JOptionPane.ERROR_MESSAGE );
				}
			});

			status = false;
		}else {
			populatingBatch.setPenaltyRate(Double.parseDouble(
					txtPenalty.getText() ) );
		}
		return status;
	}

	private boolean checkCPN() {
		boolean status = true;
		if(! txtCPN.getText().matches("-?\\d+(\\.\\d+)?") ) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(DefineJobFrame.this,
							"Invalid input for CPN.", "Error", JOptionPane.ERROR_MESSAGE );
				}
			});

			status = false;
		}else {
			populatingBatch.setCPN(Double.parseDouble(
					txtCPN.getText() ) );
		}
		return status;
	}

	private boolean checkBatchSize() {
		boolean status = true;
		if(! txtBatchSize.getText().matches("-?\\d+?") ) {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(DefineJobFrame.this,
							"Invalid input for batch size.", "Error" , JOptionPane.ERROR_MESSAGE );
				}
			});

			status = false;
		}else {
			populatingBatch.clearAllJobs();
			int bSize = Integer.parseInt(txtBatchSize.getText());
			ArrayList<job> jobs = new ArrayList<job>();
			for(int i = 0; i < bSize ; i++ ) {
				jobs.add(generatedJob);
			}
			populatingBatch.setJobsInBatch(jobs);
		}

		return status;
	}

	class AddOperationListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			checkOperations();

			if(operationDataOk) {
				DefineJobOperationsFrame ops = new 
						DefineJobOperationsFrame(generatedJob, NumOps, populatingBatch.getSampleJob());
			}
		}
	}

	private void checkOperations() {
		boolean x1 = true, x2 = true;
		if(generatedJob == null) {
			if(txtJobID.getText().isEmpty()) {

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(DefineJobFrame.this,
								"Please enter job ID.","Error" , JOptionPane.ERROR_MESSAGE );						
					}
				});

				x1 = false;
			}else {
				generatedJob = new job.Builder(txtJobID.getText().toString()).build();
			}
		}
		if(! txtNumOps.getText().matches("-?\\d+?")) {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(DefineJobFrame.this,
							"Invalid input for number of operations.", "Error" , JOptionPane.ERROR_MESSAGE );					
				}
			});

			x2 = false;
		} else {
			NumOps = Integer.parseInt(txtNumOps.getText());
		}
		operationDataOk = x1&x2;
	}

	class sendJobListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// build the job
			createJobFromParams();
			log.info("data format : " + dataOk);
			if(dataOk) {
				dispose();

				new Thread(new Runnable() {
					@Override
					public void run() {
						cAgent.sendGeneratedBatch(populatingBatch);
						CustomerProxyGUI.countBatch ++ ;
					}
				}).start();
			}
		}
	}

	private void showGui() {
		setTitle(" Define Job ");
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
