package configuration;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import test.AgentStarter;
import net.miginfocom.swing.MigLayout;

public class ShowIPFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	AgentToStart agent;
	
	public ShowIPFrame(AgentToStart a) {

		this.agent = a;
		setTitle("IP configuration");
		setLayout(new MigLayout());
		JLabel msg = new JLabel("Enter IP address");
		JTextArea ipJtext = new JTextArea(1, 15);
		ipJtext.setEditable(true);

		JButton OKbtn = new JButton("OK");
		OKbtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						AgentStarter.start(agent);
					}
				}).start();
			}
		});
		add(msg,"wrap");
		add(ipJtext,"wrap");
		add(OKbtn);
		showGui();
	}
	
	private void showGui() {
		setTitle("IP configuration");
//		setPreferredSize(new Dimension(600,500));
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}
}
