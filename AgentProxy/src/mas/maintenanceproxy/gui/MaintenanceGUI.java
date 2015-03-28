package mas.maintenanceproxy.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import net.miginfocom.swing.MigLayout;

public class MaintenanceGUI extends JFrame{

	private static final long serialVersionUID = 1L;
	
	private LocalMaintenanceAgent mAgent;
	
	private JTabbedPane tPanes;
	private String[] tabTitles = {"Main","Maintenance Schedules"};
	private JPanel[] panelsForTab;
	private JScrollPane scroller;
	
	private JButton btnSendPrevMaint;
	
	private JPanel mainPanel;
	// time in a week in milliseconds.
	// this is the time between two consecutive preventive maintenance schedules
	private long pmInterval = 604800000L;
	
	//
	private SpinnerListModel spinnerListModel;
	private SpinnerNumberModel spinnerNumberModel;
	private String[] months;

	private JSpinner jSpinnerMonth;
	private JSpinner jSpinnerYear;
	
	private CorrectiveMaintenancePanel correctivePanel;

	private CalendarPanel calendarPanel;
	private JPanel schedulePanel;
	private JPanel jpNorth;
	private int dayInWeek;
	private long pmDuration;
	private int hourDay;
	private int minuteDay;
	
	private ArrayList<Date> preventiveMaintenanceSchedule;

	public MaintenanceGUI(LocalMaintenanceAgent lmAgent) {
		
		this.mAgent = lmAgent;
				
		correctivePanel = new CorrectiveMaintenancePanel(lmAgent);
		
		mainPanel = new JPanel(new BorderLayout());
		schedulePanel = new JPanel(new BorderLayout());
		btnSendPrevMaint = new JButton("Send Preventive Maintenance Job");
		preventiveMaintenanceSchedule = new ArrayList<Date>();
		this.tPanes = new JTabbedPane(JTabbedPane.TOP);
		panelsForTab = new JPanel[tabTitles.length];
		
		initCalenderPane();
		
		for (int i = 0, n = tabTitles.length; i < n; i++ ) {
			panelsForTab[i] = new JPanel(new MigLayout());
		}

		mainPanel.add(btnSendPrevMaint, BorderLayout.CENTER);
		correctivePanel.setVisible(false);
		panelsForTab[0].add(correctivePanel);
		
		schedulePanel.add(jpNorth,BorderLayout.NORTH);
		schedulePanel.add(calendarPanel);
		panelsForTab[1].add(schedulePanel);
		
		for (int i = 0, n = tabTitles.length; i < n; i++) {
			this.tPanes.addTab(tabTitles[i],panelsForTab[i] );
		}

		add(this.tPanes);
		showGui();
	}
	
	private void initCalenderPane() {
		months = new DateFormatSymbols().getMonths();
		spinnerListModel = new SpinnerListModel(Arrays.asList(months).subList(0, 12));
		spinnerNumberModel = new SpinnerNumberModel(2012, 0, 3000, 1);

		jSpinnerMonth = new JSpinner(spinnerListModel);
		jSpinnerYear = new JSpinner(spinnerNumberModel);

		calendarPanel = new CalendarPanel();

		jpNorth = new JPanel(new GridLayout(1, 2));
		jpNorth.add(jSpinnerMonth);
		jpNorth.add(jSpinnerYear);

		calendarPanel.setMonth(spinnerListModel.
				getList().indexOf(spinnerListModel.getValue()));

		calendarPanel.setYear(((Integer)spinnerNumberModel.
				getValue()).intValue());

		JSpinner.NumberEditor numberEditor = new JSpinner.NumberEditor(jSpinnerYear, "####");
		jSpinnerYear.setEditor(numberEditor);

		jSpinnerMonth.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				calendarPanel.setMonth(spinnerListModel.getList().indexOf(spinnerListModel.getValue()));
			}
		});

		jSpinnerYear.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				calendarPanel.setYear(((Integer)spinnerNumberModel.getValue()).intValue());
			}
		});
	}
	
	public void showRepairTimeInput() {
		correctivePanel.setVisible(true);
	}

	private void showGui() {
		
		setTitle(" Local Maintenance Agent ");
		setPreferredSize(new Dimension(800,600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}

}
