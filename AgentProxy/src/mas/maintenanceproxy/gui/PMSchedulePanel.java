package mas.maintenanceproxy.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PMSchedulePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel lblHeading;
	private JLabel lblNextSchedule;
	private long remainingTime;
	private int interval = 500;

	public PMSchedulePanel() {
		lblHeading = new JLabel("<html><h1>Time remaining for next Schedule :</h1></html>");
		lblNextSchedule = new JLabel("<html><h1></h1></html>");

		add(lblHeading);
		add(lblNextSchedule, "wrap");

		ActionListener counter = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remainingTime = remainingTime - interval;
				lblNextSchedule.setText(String.format("%d hr %d min %d sec",
						remainingTime/(1000*60*60), (remainingTime%(1000*60*60))/(1000*60),
						((remainingTime%(1000*60*60))%(1000*60))/1000));
			}
		};
		new Timer(interval, counter).start();
	}

	public void setNextMaintTime(long time) {
		remainingTime = time;
	}

	@Override
	protected void printComponent(Graphics g) {
		super.printComponent(g);

		Graphics2D g2 = (Graphics2D) g.create();
		GradientPaint paint =new GradientPaint(0.0f, 0.0f, new Color(0xF2F2F2),
				0.0f, getHeight(), new Color(0xD7D7D7));
		g2.setPaint(paint);
		g2.fillRect(0, 0, getWidth(), getHeight());

	}

	public void setSchedule(long time) {
		lblNextSchedule.setText("<html><h1>" +
				new Date(time) + "</h1></html>");
	}
}
