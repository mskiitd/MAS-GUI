package mas.customerproxy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import uiconstants.Labels;
import mas.customerproxy.agent.CustomerAgent;
import mas.job.job;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class CustomerProxyGUI extends JFrame{

	private JScrollPane scroller;
	private JPanel myPanel;
	private JButton createJob;
	private CustomerAgent cAgent;
	private JTabbedPane tPanes;
	private String[] tabTitles = {"Generator","Confirmed Job", "Completed Jobs"};
	private JPanel[] panelsForTab;

	private JTable jobChooser;
	private Jobloader loader;
	private tableModel jobChooserTableModel;
	private Vector<job> jobVector;
	private Vector<String> tableHeadersVector;
	private JPanel tablePanel, buttonPanel;

	private JList<Object> acceptedJobList;
	private JPanel acceptedJobsPanel;
	private listModel acceptedJobsListModel;
	private Vector<Object> acceptedJobs;

	private JList<Object> completedJobList;
	private JPanel completedJobsPanel;
	private listModel completedJobsListModel;
	private Vector<Object> completedJobs;

	public CustomerProxyGUI(CustomerAgent cAgent) {

		this.myPanel = new JPanel(new MigLayout());
		this.cAgent = cAgent;
		this.tPanes = new JTabbedPane(JTabbedPane.TOP);

		this.loader = new Jobloader();
		this.loader.readFile();
		this.jobVector = this.loader.getjobVector();
		this.tableHeadersVector = this.loader.getJobHeaders();

		this.createJob = new JButton(Labels.createJobButton);
		this.createJob.addActionListener(new createJobListener());

		this.jobChooserTableModel = new tableModel();
		this.jobChooser = new JTable(this.jobChooserTableModel);
		this.jobChooser.getColumnModel().getColumn(1).setMinWidth(350);
		this.jobChooser.getColumnModel().getColumn(3).setMinWidth(130);
		this.jobChooser.getColumnModel().getColumn(4).setMinWidth(100);
		this.jobChooser.setRowHeight(30);
		this.jobChooser.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		this.tablePanel = new JPanel(new BorderLayout());
		this.buttonPanel = new JPanel(new BorderLayout(1,0));

		this.panelsForTab = new JPanel[tabTitles.length];

		tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		tablePanel.add(jobChooser.getTableHeader(), BorderLayout.NORTH);
		tablePanel.add(jobChooser, BorderLayout.CENTER);

		buttonPanel.add(createJob, BorderLayout.CENTER);

		for (int i = 0, n = tabTitles.length; i < n; i++ ) {
			panelsForTab[i] = new JPanel(new MigLayout());
		}

		panelsForTab[0].add(tablePanel,"wrap");
		panelsForTab[0].add(buttonPanel);

		tPanes.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		this.scroller = new JScrollPane(this.panelsForTab[0]);
		this.tPanes.addTab(tabTitles[0],this.scroller );

		// setup second tab for accepted jobs
		acceptedJobsPanel = new JPanel(new BorderLayout());
		acceptedJobs = new Vector<Object>();
		acceptedJobs.addElement(new job.Builder("ads").build());
		acceptedJobsListModel = new listModel();
		acceptedJobList = new JList<Object>(acceptedJobsListModel);
		acceptedJobList.setCellRenderer(new customListRenderer());

		acceptedJobsPanel.add(acceptedJobList);
		panelsForTab[1].add(acceptedJobsPanel);

		// setup third tab for completed jobs

		//----------------------------------

		for (int i = 1, n = tabTitles.length; i < n; i++) {
			this.tPanes.addTab(tabTitles[i],panelsForTab[i] );
		}

		add(this.tPanes);
		showGui();
	}

	private void showGui() {
		setPreferredSize(new Dimension(800,600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}

	class createJobListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// handle create job button pressed event
			if(e.getSource().equals(createJob)) {
				DefineJobFrame jobFrame = new DefineJobFrame(cAgent);
			}
		}
	};

	class listModel extends AbstractListModel<Object> {

		@Override
		public Object getElementAt(int index) {
			return acceptedJobs.get(index);
		}

		@Override
		public int getSize() {
			return acceptedJobs.size();
		}
	}

	class customListRenderer extends JLabel  implements ListCellRenderer<Object> {

		private final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

		public customListRenderer() {
			setOpaque(true);
			setIconTextGap(12);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			job entry = (job) value;
			setText(entry.getJobNo() + "");
			if (isSelected) {
				setBackground(HIGHLIGHT_COLOR);
				setForeground(Color.white);
			} else {
				setBackground(Color.white);
				setForeground(Color.black);
			}
			return this;
		}
	}

	class tableModel extends AbstractTableModel {

		@Override
		public int getColumnCount() {
			return tableHeadersVector.size();
		}

		@Override
		public int getRowCount() {
			return jobVector.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value;
			job j = jobVector.get(row);
			switch(col) {
			case 0:
				value =j.getJobID();
				break;
			case 1:
				value = j.getOperations();
				break;
			case 2:
				value = j.getCPN();
				break;
			case 3:
				value = j.getJobDuedate();
				break;
			case 4:
				value = j.getCost();
				break;
			default:
				value = "null";
				break;
			}
			return value;
		}

		@Override
		public String getColumnName(int column) {
			return tableHeadersVector.get(column);
		}
	}
}
