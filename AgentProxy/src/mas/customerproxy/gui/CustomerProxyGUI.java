package mas.customerproxy.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import mas.customerproxy.agent.CustomerAgent;
import mas.job.job;
import net.miginfocom.swing.MigLayout;
import uiconstants.Labels;

@SuppressWarnings("serial")
public class CustomerProxyGUI extends JFrame{

	private JScrollPane scroller;
	private JButton createJob;
	private CustomerAgent cAgent;
	private JTabbedPane tPanes;
	private String[] tabTitles = {"Generator","Confirmed Job", "Completed Jobs"};
	private JPanel[] panelsForTab;

	private JTable jobChooserTable;
	private Jobloader loader;
	private tableModel jobChooserTableModel;
	private Vector<job> jobVector;
	private Vector<String> tableHeadersVector;
	private JPanel jobGenPanel, buttonPanel;

	private JTable acceptedJobsTable;
	private JPanel acceptedJobsPanel;
	private AcceptedJobsTableModel acceptedJobsTableModel;
	private Vector<Object> acceptedJobVector;

	private JPanel completedJobsPanel;
	private JTable completedJobsTable;
	private CompletedJobsTableModel completedJobTableModel;
	private Vector<Object> completedJobVector;

	private int currentJobToSend = -1;
	private int currentAcceptedSelectedJob = -1;
	private int currentCompletedSelectedJob = -1;

	// menu items here
	private JMenuItem menuItemCancel ;
	private JMenuItem menuItemChangeDueDate ;
	
	public static int countJob = 0;

	public CustomerProxyGUI(CustomerAgent cAgent) {

		this.cAgent = cAgent;
		this.tPanes = new JTabbedPane(JTabbedPane.TOP);
		this.panelsForTab = new JPanel[tabTitles.length];

		menuItemCancel = new JMenuItem("Cancel Order");
		menuItemChangeDueDate = new JMenuItem("Change Due Date");

		menuItemCancel.addActionListener(new rightClickListener());
		menuItemChangeDueDate.addActionListener(new rightClickListener());

		for (int i = 0, n = tabTitles.length; i < n; i++ ) {
			panelsForTab[i] = new JPanel(new MigLayout());
		}

		_initGeneratorJobsPanel();
		panelsForTab[0].add(jobGenPanel,"wrap");
		panelsForTab[0].add(buttonPanel);

		tPanes.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		this.scroller = new JScrollPane(this.panelsForTab[0]);
		this.tPanes.addTab(tabTitles[0],this.scroller );

		_initAcceptedJobsPanel();
		panelsForTab[1].add(acceptedJobsPanel);

		_initCompletedJobsPanel();
		panelsForTab[2].add(completedJobsPanel);

		// start from 1 index as the 0th index has already been added

		for (int i = 1, n = tabTitles.length; i < n; i++) {
			this.tPanes.addTab(tabTitles[i],panelsForTab[i] );
		}

		add(this.tPanes);
		showGui();
	}

	private void _initGeneratorJobsPanel() {
		this.loader = new Jobloader();
		this.loader.readFile();
		this.jobVector = this.loader.getjobVector();
		this.tableHeadersVector = this.loader.getJobHeaders();

		this.createJob = new JButton(Labels.createJobButton);
		this.createJob.addActionListener(new createJobListener());

		this.jobChooserTableModel = new tableModel();
		this.jobChooserTable = new JTable(this.jobChooserTableModel);
		this.jobChooserTable.getColumnModel().getColumn(1).setMinWidth(350);
		this.jobChooserTable.getColumnModel().getColumn(3).setMinWidth(130);
		this.jobChooserTable.getColumnModel().getColumn(4).setMinWidth(100);
		this.jobChooserTable.setRowHeight(30);
		this.jobChooserTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.jobChooserTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.jobGenPanel = new JPanel(new BorderLayout());
		this.buttonPanel = new JPanel(new BorderLayout(1,0));

		jobGenPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		jobGenPanel.add(jobChooserTable.getTableHeader(), BorderLayout.NORTH);
		jobGenPanel.add(jobChooserTable, BorderLayout.CENTER);

		buttonPanel.add(createJob, BorderLayout.CENTER);

		jobChooserTable.getSelectionModel().
		addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {

						int[] selectedRow = jobChooserTable.getSelectedRows();

						if(selectedRow.length > 0 )
							currentJobToSend = selectedRow[0];

						System.out.println("Selected: " + selectedRow[0]);
					}
				});
	}

	private void _initAcceptedJobsPanel() {
		// setup second tab for accepted jobs
		acceptedJobsPanel = new JPanel(new BorderLayout());
		acceptedJobVector = new Vector<Object>();
		acceptedJobVector.addElement(new job.Builder("_id").
				jobCPN(10).
				build());

		acceptedJobsTableModel = new AcceptedJobsTableModel();
		acceptedJobsTable = new JTable(acceptedJobsTableModel);

		this.acceptedJobsTable.getColumnModel().getColumn(1).setMinWidth(350);
		this.acceptedJobsTable.getColumnModel().getColumn(3).setMinWidth(130);
		this.acceptedJobsTable.getColumnModel().getColumn(4).setMinWidth(100);
		this.acceptedJobsTable.setRowHeight(30);
		this.acceptedJobsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		acceptedJobsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		acceptedJobsPanel.add(acceptedJobsTable.getTableHeader(), BorderLayout.NORTH);
		acceptedJobsPanel.add(acceptedJobsTable,BorderLayout.CENTER);
		acceptedJobsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		acceptedJobsTable.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				int r = acceptedJobsTable.rowAtPoint(e.getPoint());
				if (r >= 0 && r < acceptedJobsTable.getRowCount()) {
					acceptedJobsTable.setRowSelectionInterval(r, r);
				} else {
					acceptedJobsTable.clearSelection();
				}

				int rowindex = acceptedJobsTable.getSelectedRow();
				if (rowindex < 0)
					return;
				if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
					JPopupMenu popup = createPopUpMenu();

					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
	}

	private JPopupMenu createPopUpMenu(){
		JPopupMenu menu = new JPopupMenu();

		menu.add(menuItemCancel);
		menu.add(menuItemChangeDueDate);

		return menu;
	}

	private void _initCompletedJobsPanel() {
		// setup third tab for completed jobs
		completedJobsPanel = new JPanel(new BorderLayout());
		completedJobVector = new Vector<Object>();
		completedJobVector.addElement(new job.Builder("_id").
				jobCPN(10).
				build());

		completedJobTableModel = new CompletedJobsTableModel();
		completedJobsTable = new JTable(completedJobTableModel);

		this.completedJobsTable.getColumnModel().getColumn(1).setMinWidth(350);
		this.completedJobsTable.getColumnModel().getColumn(3).setMinWidth(130);
		this.completedJobsTable.getColumnModel().getColumn(4).setMinWidth(100);
		this.completedJobsTable.setRowHeight(30);
		this.completedJobsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		completedJobsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		completedJobsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		completedJobsPanel.add(completedJobsTable.getTableHeader(), BorderLayout.NORTH);
		completedJobsPanel.add(completedJobsTable,BorderLayout.CENTER);
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

	class rightClickListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			JMenuItem menu = (JMenuItem) event.getSource();
			if (menu == menuItemCancel) {
				currentAcceptedSelectedJob = acceptedJobsTable.getSelectedRow();
				cAgent.cancelOrder((job) acceptedJobVector.get(currentAcceptedSelectedJob));
				
			} else if (menu == menuItemChangeDueDate) {
				currentAcceptedSelectedJob = acceptedJobsTable.getSelectedRow();
				cAgent.changeDueDate((job) acceptedJobVector.get(currentAcceptedSelectedJob));
				
			} 
		}
	}

	class createJobListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// handle create job button pressed event
			if(e.getSource().equals(createJob)) {
				DefineJobFrame jobFrame;
				if(currentJobToSend == -1)
					jobFrame = new DefineJobFrame(cAgent,null);
				else {
					job j = jobVector.get(currentJobToSend);
					jobFrame = new DefineJobFrame(cAgent,j);
				}
			}
		}
	};

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
				value = j.getJobID();
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

	class AcceptedJobsTableModel extends AbstractTableModel {

		@Override
		public int getColumnCount() {
			return tableHeadersVector.size();
		}

		@Override
		public int getRowCount() {
			return acceptedJobVector.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value;
			job j = (job) acceptedJobVector.get(row);
			switch(col) {
			case 0:
				value = j.getJobID();
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


	class CompletedJobsTableModel extends AbstractTableModel {

		@Override
		public int getColumnCount() {
			return tableHeadersVector.size();
		}

		@Override
		public int getRowCount() {
			return completedJobVector.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value;
			job j = (job) completedJobVector.get(row);
			switch(col) {
			case 0:
				value = j.getJobID();
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
