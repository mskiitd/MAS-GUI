package customerProxy;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

import mas.customerproxy.CustomerAgent;
import mas.customerproxy.goal.dispatchJobGoal;
import mas.job.job;
import net.miginfocom.swing.MigLayout;

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
	private JTextField txtDueDate;
	private JTextField txtNumOps;
	private JTextField txtPenalty;

	public DefineJobFrame(CustomerAgent cAgent) {

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
		this.txtDueDate = new JTextField(Labels.defaultJTextSize);
		this.txtJobID = new JTextField(Labels.defaultJTextSize);
		this.txtNumOps = new JTextField(Labels.defaultJTextSize/2);
		this.txtPenalty = new JTextField(Labels.defaultJTextSize);

		this.sendJob = new JButton(Labels.sendJobButton);
		sendJob.addActionListener(new sendJobListener());

		myPanel.add(lblHeading,"wrap");

		myPanel.add(lblJobID);
		myPanel.add(txtJobID,"wrap");

		myPanel.add(lblCPN);
		myPanel.add(txtCPN,"wrap");

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
		showGui();
	}

	class AddOperationListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			NumOps = Integer.parseInt(txtNumOps.getText());
			DefineJobOperationsFrame ops = new 
					DefineJobOperationsFrame(generatedJob, NumOps);
		}

	}

	class sendJobListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(sendJob)) {
				// build the job
				job jobToSend = new job.Builder(JobID).
						build();

				cAgent.addJobToBeliefBase(jobToSend);
				cAgent.addGoal(new dispatchJobGoal());

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
