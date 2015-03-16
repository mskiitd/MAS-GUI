package mas.maintenanceproxy.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MaintenanceGUI extends JFrame{

	private static final long serialVersionUID = 1L;
	private JScrollPane scroller;

	private SpinnerListModel spinnerListModel;
	private SpinnerNumberModel spinnerNumberModel;
	private String[] months;

	private JSpinner jSpinnerMonth;
	private JSpinner jSpinnerYear;

	private CalendarPanel calendarPanel;
	private JPanel jpNorth;
	
	// time in a week in milliseconds.
	// this is the time between two consecutive preventive maintenance schedules
	private long pmInterval = 604800000L;
	private int dayInWeek;
	private long pmDuration;
	private int hourDay;
	private int minuteDay;
	private int secondsDay;
	
	private ArrayList<Date> preventiveMaintenanceSchedule;

	public MaintenanceGUI() {

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
		
//		calendarPanel.set
		
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
		
		add(calendarPanel);
		add(jpNorth, BorderLayout.NORTH);

		this.scroller = new JScrollPane(calendarPanel);
		add(this.scroller);
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

}
