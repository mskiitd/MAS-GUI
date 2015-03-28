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

import mas.customerproxy.agent.CustomerAgent;
import mas.jobproxy.job;
import mas.util.DateLabelFormatter;
import mas.util.DefineJobOperationsFrame;
import mas.util.TableUtil;
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

	private String JobID;
	private double CPN;
	private long DueDate;
	private int NumOps;
	private double Penalty;

	private JLabel lblHeading;
	private JLabel lblJobID;
	private JLabel lblCPN;
	private JLabel lblDueDate;
	private JLabel lblOpsHeading;
	private JLabel lblPenalty;
	private JButton btnOperationPlus;

	private JTextField txtJobID;
	private JTextField txtCPN;
	private JTextField txtNumOps;
	private JTextField txtPenalty;

	private job populatingJob;
	private boolean dataOk = true;
	private boolean operationDataOk = false;

	private Logger log;

	public DefineJobFrame(CustomerAgent cAgent, job populatingJob) {

		log = LogManager.getLogger();

		this.populatingJob = populatingJob;

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

		this.txtCPN = new JTextField(Labels.defaultJTextSize);
		this.txtJobID = new JTextField(Labels.defaultJTextSize);
		this.txtNumOps = new JTextField(Labels.defaultJTextSize/2);
		this.txtPenalty = new JTextField(Labels.defaultJTextSize);

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

		myPanel.add(lblDueDate);
		myPanel.add(datePicker);
		myPanel.add(timeSpinner,"wrap");

		operationPanel.add(lblOpsHeading);
		operationPanel.add(txtNumOps);
		operationPanel.add(btnOperationPlus,"wrap");

		btnOperationPlus.addActionListener(new AddOperationListener());

		myPanel.add(operationPanel,"wrap");

		myPanel.add(sendJob);

		this.scroller = new JScrollPane(myPanel);
		add(scroller);
		_populate();
		showGui();
	}

	private void _populate() {
		if(populatingJob != null) {
			txtJobID.setText(populatingJob.getJobID());
			txtCPN.setText(String.valueOf(populatingJob.getCPN()));
			txtPenalty.setText(String.valueOf(populatingJob.getPenaltyRate()));
			txtNumOps.setText(String.valueOf(populatingJob.getOperations().size()));

		}
	}

	private void createJobFromParams() {
		boolean x1 = true;
		if(generatedJob == null) {
			if(txtJobID.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Please enter job ID !!");
				x1 = false;
			}else {
				generatedJob = new job.Builder(txtJobID.getText().toString()).build();
				boolean x2 = false,x3 = false,x4 = false,x5 = false;
				x2 = checkPenaltyRate();
				if(x2) {
					x3 = checkCPN();
				}
				generatedJob.setGenerationTime(new Date());
				generatedJob.setJobNo(CustomerProxyGUI.countJob);
				if(x2 & x3) {
					x4 = checkDueDate();
					
					if(x4) {
						x5 = checkJobOperations();
					}
				}
				
				dataOk = x2&x3&x4&x5;
			}
		}
		else {
			boolean x2 = false,x3 = false,x4 = false,x5 = false;
			x2 = checkPenaltyRate();
			if(x2) {
				x3 = checkCPN();
			}
			generatedJob.setGenerationTime(new Date());
			generatedJob.setJobNo(CustomerProxyGUI.countJob);
			if(x2 & x3) {
				x4 = checkDueDate();
				
				if(x4) {
					x5 = checkJobOperations();
				}
			}
			
			dataOk = x1&x2&x3&x4&x5;
		}

	}

	private boolean checkJobOperations() {
		boolean status = true;
		if(generatedJob.getOperations() == null ) {
			JOptionPane.showMessageDialog(this, "Please Give job Operation Details !!",
					"Error" , JOptionPane.ERROR_MESSAGE );
			status = false;
		}else {
			if(generatedJob.getOperations().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Please Give job Operation Details !!",
						"Error" , JOptionPane.ERROR_MESSAGE );
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
			JOptionPane.showMessageDialog(this, "Invalid input for due date !!",
					"Error" , JOptionPane.ERROR_MESSAGE );
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
				JOptionPane.showMessageDialog(this, "Please enter a due date after current Date !!",
						"Error" , JOptionPane.ERROR_MESSAGE );
				status = false;
			}else {
				generatedJob.setJobDuedatebyCust(calTime.getTime());
			}
		}
		return status;
	}

	private boolean checkPenaltyRate() {
		boolean status = true;
		if(! txtPenalty.getText().matches("-?\\d+(\\.\\d+)?") ) {
			JOptionPane.showMessageDialog(this, "Invalid input for penalty rate !!",
					"Error" , JOptionPane.ERROR_MESSAGE );
			status = false;
		}else {
			generatedJob.setPenaltyRate(Double.parseDouble(
					txtPenalty.getText() ) );
		}
		return status;
	}

	private boolean checkCPN() {
		boolean status = true;
		if(! txtCPN.getText().matches("-?\\d+(\\.\\d+)?") ) {
			JOptionPane.showMessageDialog(this, "Invalid input for CPN !!", 
					"Error" , JOptionPane.ERROR_MESSAGE );
			status = false;
		}else {
			generatedJob.setCPN(Double.parseDouble(
					txtCPN.getText() ) );
		}
		return status;
	}

	class AddOperationListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			checkOperations();

			if(operationDataOk) {
				DefineJobOperationsFrame ops = new 
						DefineJobOperationsFrame(generatedJob, NumOps, populatingJob);
			}
		}
	}

	private void checkOperations() {
		boolean x1 = true, x2 = true;
		if(generatedJob == null) {
			if(txtJobID.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Please enter job ID !!",
						"Error" , JOptionPane.ERROR_MESSAGE );
				x1 = false;
			}else {
				generatedJob = new job.Builder(txtJobID.getText().toString()).build();
			}
		}
		if(! txtNumOps.getText().matches("-?\\d+?")) {
			JOptionPane.showMessageDialog(this, "Invalid input for number of operations !!",
					"Error" , JOptionPane.ERROR_MESSAGE );
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
				log.info("Sending the job : " + generatedJob);
				cAgent.sendGeneratedJob(generatedJob);
				CustomerProxyGUI.countJob++ ;
				dispose();
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
