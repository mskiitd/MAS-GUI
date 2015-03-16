package mas.globalSchedulingproxy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import mas.globalSchedulingproxy.agent.GlobalSchedulingAgent;
import mas.job.job;
import uiconstants.Labels;

@SuppressWarnings("serial")
public class GSAproxyGUI extends JFrame{

	private JScrollPane scroller;
	private JPanel queryJobsPanel;
	private JButton btnQueryJob;
	private JTable jobsInSystemTable;
	private tableModel jobsQueryTableModel;
	private String[] tableHeaders = {"Job No","Job ID" , "CPN" , "Penalty Rate",
			"Due Date", "Operations"};

	private Vector<String> tableHeadersVector;

	private Vector<Object> acceptedJobs;
	private listModel jobsQueryListModel;
	private GlobalSchedulingAgent gAgent;

	public GSAproxyGUI(GlobalSchedulingAgent gAgent) {

		this.gAgent = gAgent;
		this.queryJobsPanel = new JPanel(new BorderLayout());
		this.btnQueryJob = new JButton(Labels.GSLabels.queryForJobLabel);

		acceptedJobs = new Vector<Object>();
		tableHeadersVector = new Vector<String>();
		Collections.addAll(tableHeadersVector, tableHeaders);

		this.jobsQueryTableModel = new tableModel();
		this.jobsInSystemTable = new JTable(this.jobsQueryTableModel);

		this.jobsInSystemTable.getColumnModel().getColumn(0).setPreferredWidth(10);
		this.jobsInSystemTable.getColumnModel().getColumn(1).setPreferredWidth(5);
		this.jobsInSystemTable.getColumnModel().getColumn(2).setPreferredWidth(10);
		this.jobsInSystemTable.getColumnModel().getColumn(3).setPreferredWidth(100);
		this.jobsInSystemTable.getColumnModel().getColumn(4).setPreferredWidth(140);
		this.jobsInSystemTable.getColumnModel().getColumn(5).setPreferredWidth(250);

		jobsInSystemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jobsInSystemTable.addMouseListener(new rightClickListener());

		this.jobsInSystemTable.setRowHeight(30);
		this.jobsInSystemTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		queryJobsPanel.add(jobsInSystemTable.getTableHeader(), BorderLayout.NORTH);
		queryJobsPanel.add(jobsInSystemTable);
		//		queryJobsPanel.add(btnQueryJob);

		this.scroller = new JScrollPane(queryJobsPanel);
		add(this.scroller);

		addJobToList(new job.Builder("1").jobCPN(1).jobDueDateTime(new Date()).build());
		addJobToList(new job.Builder("2").jobCPN(1).jobDueDateTime(new Date()).build());
		showGui();
	}

	class rightClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {
			
			int r = jobsInSystemTable.rowAtPoint(e.getPoint());
			if (r >= 0 && r < jobsInSystemTable.getRowCount()) {
				jobsInSystemTable.setRowSelectionInterval(r, r);
			} else {
				jobsInSystemTable.clearSelection();
			}

			int rowindex = jobsInSystemTable.getSelectedRow();
			if (rowindex < 0)
				return;
			
			if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
				JPopupMenu popup = new JPopupMenu();
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
			JTable table =(JTable) e.getSource();
			Point p = e.getPoint();
			int row = table.rowAtPoint(p);
			System.out.println("clicked");
		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

	}

	public void addJobToList(job j) {
		acceptedJobs.addElement(j);
		revalidate();
	}

	public void completedJob(job j) {

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

	class tableModel extends AbstractTableModel {

		@Override
		public int getColumnCount() {
			return tableHeadersVector.size();
		}

		@Override
		public int getRowCount() {
			return acceptedJobs.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value;
			job j = (job) acceptedJobs.get(row);
			switch(col) {
			case 0:
				value =j.getJobNo();
				break;
			case 1:
				value = j.getJobID();
				break;
			case 2:
				value = j.getCPN();
				break;
			case 3:
				value = j.getPenaltyRate();
				break;
			case 4:
				value = j.getJobDuedate();
				break;
			case 5:
				value = j.getOperations();
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
			setText("Job No :" + entry.getJobNo() + " Job ID :" + entry.getJobID() + 
					" Due Date: " + entry.getJobDuedate() + " CPN : "+ entry.getCPN() +
					" Penalty Rate : " + entry.getPenaltyRate() );
			//					" Operations : " + entry.getOperations());
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
}
