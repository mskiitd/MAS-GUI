
package mas.customerproxy.gui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import mas.customerproxy.agent.CustomerAgent;
import mas.customerproxy.agent.Jobloader;
import mas.jobproxy.Batch;
import mas.util.TableUtil;
import net.miginfocom.swing.MigLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import uiconstants.Labels;

@SuppressWarnings("serial")
public class CustomerProxyGUI extends JFrame{

	private JScrollPane scroller;
	private JButton createJob;
	private CustomerAgent cAgent;
	private JTabbedPane tPanes;
	private String[] tabTitles = {"Generator","Confirmed Job", "Completed Jobs"};
	private JPanel[] panelsForTab;

	private Vector<String> acceptedJobTableHeaderVector;
	private Vector<String> completeJobTableHeaderVector;

	private JTable jobChooserTable;
	private Jobloader loader;
	private jobGeneratorTableModel jobChooserTableModel;
	private Vector<Batch> jobVector;
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

	// menu items here
	private JMenuItem menuItemCancel ;
	private JMenuItem menuItemChangeDueDate ;
	private static TrayIcon customerTrayIcon;
	private Logger log;
	protected SystemTray tray;

	public static int countBatch = 1;
	private static InputStream in;
	private static AudioStream audioStream;
	private static String notificationSound = "resources/notification.wav";

	public CustomerProxyGUI(CustomerAgent cAgent) {

		log = LogManager.getLogger();
		this.cAgent = cAgent;

		this.tPanes = new JTabbedPane(JTabbedPane.TOP);
		this.panelsForTab = new JPanel[tabTitles.length];

		menuItemCancel = new JMenuItem("Cancel Order");
		menuItemChangeDueDate = new JMenuItem("Change Due Date");

		menuItemCancel.addActionListener(new rightClickListener());
		menuItemChangeDueDate.addActionListener(new rightClickListener());

		for (int i = 1, n = tabTitles.length; i < n; i++ ) {
			panelsForTab[i] = new JPanel(new BorderLayout());
		}

		_loadIconsAndFiles();
		_initGeneratorJobsPanel();

		panelsForTab[0] = new JPanel(new MigLayout());
		panelsForTab[0].add(jobGenPanel,"wrap");
		panelsForTab[0].add(buttonPanel);

		tPanes.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		this.scroller = new JScrollPane(this.panelsForTab[0]);
		this.tPanes.addTab(tabTitles[0],this.scroller );

		_initAcceptedJobsPanel();
		panelsForTab[1].add(acceptedJobsPanel, BorderLayout.CENTER);

		_initCompletedJobsPanel();
		panelsForTab[2].add(completedJobsPanel, BorderLayout.CENTER);

		// start from 1 index as the 0th index has already been added
		for (int i = 1, n = tabTitles.length; i < n; i++) {
			this.tPanes.addTab(tabTitles[i],panelsForTab[i] );
		}

		add(this.tPanes);
		showGui();
	}

	private void _loadIconsAndFiles() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Image image = Toolkit.getDefaultToolkit().getImage("resources/customer.png");
				customerTrayIcon= new TrayIcon(image, cAgent.getLocalName());
				if (SystemTray.isSupported()) {
					tray = SystemTray.getSystemTray();

					customerTrayIcon.setImageAutoSize(true);
					try {
						tray.add(customerTrayIcon);
					} catch (AWTException e) {
						log.info("TrayIcon could not be added.");
					}
				}
			}
		}).start();
	}

	private void _initGeneratorJobsPanel() {

		this.loader = new Jobloader(cAgent.getLocalName());
		this.loader.readFile();
		this.jobVector = this.loader.getBatchVector();
		this.tableHeadersVector = this.loader.getJobHeaders();

		this.acceptedJobTableHeaderVector = this.loader.getAcceptedJobTableHeader();
		this.completeJobTableHeaderVector = this.loader.getCompleteJobTableHeader();

		this.createJob = new JButton(Labels.createJobButton);
		this.createJob.addActionListener(new createJobListener());

		this.jobChooserTableModel = new jobGeneratorTableModel();
		this.jobChooserTable = new JTable(this.jobChooserTableModel);

		this.jobChooserTable.setRowHeight(30);
		this.jobChooserTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		for(int i = 0; i < jobChooserTable.getColumnCount(); i++) {
			this.jobChooserTable.getColumnModel().getColumn(i).setMinWidth(180);
		}

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
					}
				});
	}

	private void _initAcceptedJobsPanel() {
		// setup second tab for accepted jobs
		acceptedJobsPanel = new JPanel(new BorderLayout());
		acceptedJobVector = new Vector<Object>();

		acceptedJobsTableModel = new AcceptedJobsTableModel();
		acceptedJobsTable = new JTable(acceptedJobsTableModel);

		TableUtil.setColumnWidths(acceptedJobsTable);

		this.acceptedJobsTable.setRowHeight(30);
		this.acceptedJobsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		acceptedJobsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		acceptedJobsPanel.add(new JScrollPane(acceptedJobsTable), BorderLayout.CENTER);
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

		completedJobTableModel = new CompletedJobsTableModel();
		completedJobsTable = new JTable(completedJobTableModel);

		TableUtil.setColumnWidths(completedJobsTable);
		this.completedJobsTable.setRowHeight(30);
		this.completedJobsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		completedJobsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		completedJobsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		completedJobsPanel.add(new JScrollPane(completedJobsTable), BorderLayout.CENTER);
	}

	private void showGui() {
		setTitle(cAgent.getLocalName());
		//		setPreferredSize(new Dimension(800,800));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}

	public static void showNotification(String title, String message,TrayIcon.MessageType type){

		switch(type){
		case ERROR :
			customerTrayIcon.displayMessage(title,message, TrayIcon.MessageType.ERROR);
			break;

		case INFO:
			customerTrayIcon.displayMessage( title,message, TrayIcon.MessageType.INFO);
			break;

		case WARNING:
			customerTrayIcon.displayMessage( title,message, TrayIcon.MessageType.WARNING);
			break;

		case NONE:
			customerTrayIcon.displayMessage( title,message, TrayIcon.MessageType.NONE);
			break;
		}

		try {
			in = new FileInputStream(notificationSound);
			audioStream = new AudioStream(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// play the audio clip with the audio player class
		AudioPlayer.player.start(audioStream);
	}

	public void addCompletedBatch(Batch j) {
		/**if(acceptedJobVector.contains(j)) {
			acceptedJobVector.remove(j);
		}
		 **/
		completedJobVector.addElement(j);
		TableUtil.setColumnWidths(completedJobsTable);
		completedJobsTable.revalidate();
		completedJobsTable.repaint();

		showNotification("Order Completed", "Order with ID "+j.getBatchId()+
				" completed ",MessageType.INFO); 
	}

	public void addAcceptedJob(Batch j) {
		acceptedJobVector.addElement(j);
		TableUtil.setColumnWidths(acceptedJobsTable);
		acceptedJobsTable.revalidate();
		acceptedJobsTable.repaint();
	}


	class rightClickListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			JMenuItem menu = (JMenuItem) event.getSource();
			if (menu == menuItemCancel) {
				currentAcceptedSelectedJob = acceptedJobsTable.getSelectedRow();
				cAgent.cancelOrder((Batch) acceptedJobVector.get(currentAcceptedSelectedJob));

			} else if (menu == menuItemChangeDueDate) {
				currentAcceptedSelectedJob = acceptedJobsTable.getSelectedRow();
				//				cAgent.changeDueDate((Batch) acceptedJobVector.get(currentAcceptedSelectedJob));
			} 
		}
	}

	class createJobListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// handle create job button pressed event
			if(e.getSource().equals(createJob)) {

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if(currentJobToSend == -1) {
							DefineJobFrame jobFrame = new DefineJobFrame(cAgent,null);
						}
						else {
							Batch j = jobVector.get(currentJobToSend);
							DefineJobFrame	jobFrame = new DefineJobFrame(cAgent,j);
						}
					}
				});

			}
		}
	};

	class jobGeneratorTableModel extends AbstractTableModel {

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
			Batch j = jobVector.get(row);
			switch(col) {
			case 0:
				value = j.getBatchId();
				break;
			case 1:
				value = j.getCPN();
				break;
			case 2:
				value = j.getPenaltyRate();
				break;
			case 3:
				value = j.getBatchCount();
				break;
			default:
				value = "not_found";
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
			return acceptedJobTableHeaderVector.size();
		}

		@Override
		public int getRowCount() {
			return acceptedJobVector.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value;
			Batch j = (Batch) acceptedJobVector.get(row);
			switch(col) {
			case 0:
				value = j.getBatchId();
				break;
			case 1:
				value = j.getCPN();
				break;
			case 2:
				value = j.getPenaltyRate();
				break;
			case 3:
				value = j.getBatchCount();
				break;
			case 4:
				value = j.getDueDateByCustomer();
				break;
			default:
				value = "not_found";
				break;
			}
			return value;
		}

		@Override
		public String getColumnName(int column) {
			return acceptedJobTableHeaderVector.get(column);
		}
	}

	class CompletedJobsTableModel extends AbstractTableModel {

		@Override
		public int getColumnCount() {
			return completeJobTableHeaderVector.size();
		}

		@Override
		public int getRowCount() {
			return completedJobVector.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			Object value;
			Batch j = (Batch) completedJobVector.get(row);
			switch(col) {
			case 0:
				value = j.getBatchId();
				break;
			case 1:
				value = j.getCPN();
				break;
			case 2:
				value = j.getPenaltyRate();
				break;
			case 3:
				value = j.getBatchCount();
				break;
			case 4:
				value = new Date(j.getCompletionTime());
				break;
			default:
				value = "not_found";
				break;
			}
			return value;
		}

		@Override
		public String getColumnName(int column) {
			return completeJobTableHeaderVector.get(column);		}
	}

	public void clean() {
		tray.remove(customerTrayIcon);

	}

}
