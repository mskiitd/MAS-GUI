package configuration;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import test.AgentStarter;
import net.miginfocom.swing.MigLayout;

/**
 * For input about IP address of MAS remote container
 */
public class ShowIPFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private AgentToStart agent;
	private JTextArea ipJtext,portJText;
	private Logger log=LogManager.getLogger();
	private FileOutputStream outputStream;
	private Properties prop = new Properties();
	
	public ShowIPFrame(AgentToStart a) {
		
		this.agent = a;
		setTitle("IP configuration");
		setLayout(new MigLayout());
		JLabel msg = new JLabel("Enter IP address");
		JLabel msg2 = new JLabel("Enter Jade port");
		ipJtext = new JTextArea(1, 15);
		ipJtext.setEditable(true);
		portJText = new JTextArea(1, 4);
		portJText.setEditable(true);

		JButton OKbtn = new JButton("OK");
		OKbtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							outputStream= new FileOutputStream("resources/mas.properties");
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
						
						AgentStarter.ipAddress = ipJtext.getText();
						prop.setProperty("ipAddress", AgentStarter.ipAddress);
						AgentStarter.JadePort = Integer.parseInt(portJText.getText());
						prop.setProperty("jadePort", Integer.toString(AgentStarter.JadePort));
						try {
							prop.store(outputStream, "Comment");
						} catch (IOException e) {
							e.printStackTrace();
						}
						try {
							outputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						AgentStarter.start(agent);
					}
				}).start();
			}
		});
		add(msg);
		add(ipJtext,"wrap");
		add(msg2);
		add(portJText,"wrap");
		add(OKbtn);
		
		ImageIcon img = new ImageIcon("resources/smartManager.png");
		this.setIconImage(img.getImage());
		
		showGui();
	}
	
	private void showGui() {
		setTitle("IP configuration");
//		setPreferredSize(new Dimension(600,500));
		
		String propFileName = "mas.properties";
 
		InputStream inputStream=null;
		try {
			inputStream = new FileInputStream("resources/mas.properties");
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
 
		if (inputStream != null) {
			try {
				prop.load(inputStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			log.error("mas.properties file is not found");
		}
 
		String IPAddress = prop.getProperty("ipAddress");
		String JADEport = prop.getProperty("jadePort");
		
		ipJtext.setText(IPAddress);
		portJText.setText(JADEport);
		
		
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}
}
