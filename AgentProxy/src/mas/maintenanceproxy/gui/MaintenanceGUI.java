package mas.maintenanceproxy.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.alee.laf.scroll.WebScrollPane;

import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import mas.maintenanceproxy.classes.PMaintenance;
import mas.maintenanceproxy.gui.preventive.PrevMaintTableModel;
import mas.maintenanceproxy.gui.preventive.PrevMaintTableRenderer;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

/**
 * @author Anand Prajapati
 * <p>
 * Main GUI of maintenance agent. This shows a table of all done preventive maintenance jobs and time remaining for 
 * next preventive maintenance activity generation.
 * </p>
 */

public class MaintenanceGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private LocalMaintenanceAgent mAgent;

	private JTabbedPane tPanes;
	private String[] tabTitles = {"Maintenance Schedules"};
	private JPanel[] panelsForTab;
	private JScrollPane scroller;

	private JPanel containerPanel;
	//
	private SpinnerListModel spinnerListModel;
	private SpinnerNumberModel spinnerNumberModel;
	private String[] months;

	private JSpinner jSpinnerMonth;
	private JSpinner jSpinnerYear;

	private CorrectiveMaintenanceFrame correctiveFrame;

	private PMSchedulePanel pmPanel;
	private JTable pmScheduleTable;
	private PrevMaintTableRenderer tableRenderer;
	private PrevMaintTableModel tableModel;
	private WebScrollPane maintScroller;

	private CalendarPanel calendarPanel;
	private JPanel schedulePanel;
	private JPanel jpNorth;

	private int dayInWeek;
	private long pmDuration;
	private int hourDay;
	private int minuteDay;

	private ArrayList<PMaintenance> prevMaintSchedules;

	private JLabel IITDlogolabel;

	public MaintenanceGUI(LocalMaintenanceAgent lmAgent) {

		ImageIcon img = new ImageIcon("resources/smartManager.png","Logo icon");
		this.setIconImage(img.getImage());
		
		this.mAgent = lmAgent;

		prevMaintSchedules = new ArrayList<PMaintenance>();
		pmPanel = new PMSchedulePanel();

		containerPanel = new JPanel(new BorderLayout());
		schedulePanel = new JPanel(new BorderLayout());
		this.tPanes = new JTabbedPane(JTabbedPane.TOP);
		panelsForTab = new JPanel[tabTitles.length];

		initTablePanel();
		initIITDlogo();
		//		initCalenderPane();

		for (int i = 0, n = tabTitles.length; i < n; i++ ) {
			panelsForTab[i] = new JPanel(new MigLayout());
		}
		schedulePanel.add(containerPanel,BorderLayout.NORTH);
		schedulePanel.add(maintScroller,BorderLayout.CENTER);
		//		schedulePanel.add(calendarPanel);

		panelsForTab[0].add(schedulePanel,"wrap");
		
		CC componentConstraints = new CC();
		componentConstraints.alignX("center").spanX();
		panelsForTab[0].add(IITDlogolabel,componentConstraints);

		for (int i = 0, n = tabTitles.length; i < n; i++) {
			this.tPanes.addTab(tabTitles[i],panelsForTab[i] );
		}

		add(this.tPanes);
		showGui();
	}

	private void initIITDlogo(){
		ImageIcon img2 = new ImageIcon("resources/IITDlogo.png");
		IITDlogolabel = new JLabel(""
				, img2, JLabel.CENTER);
	}
	/*
	 * Initialize the panel containing table of done preventive maintenance activities 
	 */
	private void initTablePanel() {
		tableRenderer = new PrevMaintTableRenderer();
		tableModel = new PrevMaintTableModel();
		this.pmScheduleTable = new JTable(tableModel);

		pmScheduleTable.setDefaultRenderer(PMaintenance.class, tableRenderer);
		pmScheduleTable.setRowHeight(110);

		maintScroller = new WebScrollPane(pmScheduleTable);
		maintScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		//		maintScroller.setPreferredWidth(450);

		containerPanel.add(pmPanel, BorderLayout.CENTER);
	}

	/**
	 * Runs on EDT
	 * @param prevMaint
	 * </br> Add preventive maintenance job to GUI
	 */
	public void addMaintJobToDisplay(PMaintenance prevMaint) {
		final PMaintenance pm = prevMaint;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				tableModel.addMaintJob(pm);
			}
		});
	}

//	private void initCalenderPane() {
//		months = new DateFormatSymbols().getMonths();
//		spinnerListModel = new SpinnerListModel(Arrays.asList(months).subList(0, 12));
//		spinnerNumberModel = new SpinnerNumberModel(2012, 0, 3000, 1);
//
//		jSpinnerMonth = new JSpinner(spinnerListModel);
//		jSpinnerYear = new JSpinner(spinnerNumberModel);
//
//		calendarPanel = new CalendarPanel();
//
//		jpNorth = new JPanel(new GridLayout(1, 2));
//		jpNorth.add(jSpinnerMonth);
//		jpNorth.add(jSpinnerYear);
//
//		calendarPanel.setMonth(spinnerListModel.
//				getList().indexOf(spinnerListModel.getValue()));
//
//		calendarPanel.setYear(((Integer)spinnerNumberModel.
//				getValue()).intValue());
//
//		JSpinner.NumberEditor numberEditor = new JSpinner.NumberEditor(jSpinnerYear, "####");
//		jSpinnerYear.setEditor(numberEditor);
//
//		jSpinnerMonth.addChangeListener(new ChangeListener() {
//			public void stateChanged(ChangeEvent e) {
//				calendarPanel.setMonth(spinnerListModel.getList().indexOf(spinnerListModel.getValue()));
//			}
//		});
//
//		jSpinnerYear.addChangeListener(new ChangeListener() {
//			public void stateChanged(ChangeEvent e) {
//				calendarPanel.setYear(((Integer)spinnerNumberModel.getValue()).intValue());
//			}
//		});
//	}

	/**
	 * @param prevMaintPeriod
	 * </br> Set remaining time for next preventive maintenance job generation
	 */
	public void setNextMaintTime(long prevMaintPeriod) {
		pmPanel.setNextMaintTime(prevMaintPeriod);
	}

	/**
	 * Runs on EDT
	 * </br> Pops up a windows asking for repair time when the machine is failed
	 */
	public void showRepairTimeInput() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setEnabled(false);
				correctiveFrame = new CorrectiveMaintenanceFrame(mAgent,MaintenanceGUI.this);
			}
		});
	}

	/**
	 * Initialized the parameters of display of the frame and make it visible at appropriate location 
	 * with desired size
	 */
	private void showGui() {
		setTitle(" Local Maintenance Agent # " + mAgent.getLocalName().split("#")[1] );
		setPreferredSize(new Dimension(800,600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}

	/**
	 * Remove the tray icon from action tray 
	 */
	public void clean() {
		
	}

}
