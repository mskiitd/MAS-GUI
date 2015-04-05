package mas.globalSchedulingproxy.gui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import uiconstants.Labels;

import com.alee.extended.label.WebHotkeyLabel;
import com.alee.extended.time.ClockType;
import com.alee.extended.time.WebClock;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.managers.notification.NotificationIcon;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.notification.WebNotificationPopup;

import mas.globalSchedulingproxy.agent.GlobalSchedulingAgent;
import mas.jobproxy.Batch;
import mas.jobproxy.job;
import mas.util.DateLabelFormatter;
import mas.util.DefineJobOperationsFrame;
import mas.util.TableUtil;
import mas.util.formatter.integerformatter.FormattedIntegerField;

public class WebLafGSA {

	private static GlobalSchedulingAgent GSA=null;
	private static JFrame welcomeScreenFrame=null;
	private static BorderLayout layout=null;
	
	private static WebScrollPane currentJobList=null;
	private static WebScrollPane completedJobsList=null;
	private static WebScrollPane negotiationJobList=null;
	
	private static WebPanel MainPanel=null;
	
//info about selected job tile on right side
	private static WebPanel currentJobListinfoPanel=null;
	private static WebPanel completedJobListinfoPanel=null;
	private static WebPanel NegotiationJobListinfoPanel=null;
	
	public enum notificationType{
		error, newJob, completedJob, negotiationBid
	}
	private static Logger log=LogManager.getLogger();
	
	
//table of jobTiles. Left side of frame
	private static JTable currentJobListTable=null;
	private static JTable negotiationJobListTable=null;
	private static JTable completedJobListTable=null;
	
	private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static double width = screenSize.getWidth();
    private static double height = screenSize.getHeight();
    
    public static TrayIcon GSAguiIcon ;
    
	public WebLafGSA(GlobalSchedulingAgent globalSchedulingAgent){
		this.GSA=globalSchedulingAgent;
		init();
	}
	
	private void init(){
		
		
		Image image = Toolkit.getDefaultToolkit().getImage("resources/smartMachine.png");
		GSAguiIcon= new TrayIcon(image, "GSA");
		 if (SystemTray.isSupported()) {
		      SystemTray tray = SystemTray.getSystemTray();
	
		      GSAguiIcon.setImageAutoSize(true);
		      try {
		        tray.add(GSAguiIcon);
		      } catch (AWTException e) {
		       log.info("TrayIcon could not be added.");
		      }
		    }
		
		this.welcomeScreenFrame=new WebFrame();
		this.layout=new BorderLayout();
		this.MainPanel=new WebPanel(layout);
//		this.MainPanel.setOpaque(false);
		
		currentJobListinfoPanel=new WebPanel(new MigLayout());
//		currentJobListinfoPanel.setOpaque(false);
		completedJobListinfoPanel=new WebPanel(new MigLayout());
//		completedJobListinfoPanel.setOpaque(false);
		NegotiationJobListinfoPanel=new WebPanel(new MigLayout());
//		NegotiationJobListinfoPanel.setOpaque(false);
		
		
		
		/*currentJobListinfoPanel.setBackground(Color.RED);
		completedJobListinfoPanel.setBackground(Color.BLUE);
		NegotiationJobListinfoPanel.setBackground(Color.GREEN);*/
		
		initCompletedJobListPanel();
		initCurrentJobListPanel();
		initNegotiationListPanel();
		
	    WebPanel menu=new WebPanel(new FlowLayout());
	    menu.setPreferredSize(new Dimension((int)width, 100));
	    JButton[] bottomButtons=getButtons();
	
	    Color panelColor = Color.decode("#A2A3A2");
	    menu.setBackground(panelColor);
	    
	    for(int i=0;i<bottomButtons.length;i++){
	    	menu.add(bottomButtons[i]);	
	    }
	    
	    MainPanel.add(menu, BorderLayout.SOUTH);
		MainPanel.add(currentJobList,BorderLayout.WEST);

	    welcomeScreenFrame.add(MainPanel);
		welcomeScreenFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
		welcomeScreenFrame.setVisible(true);
		
		
	}
	
	
	
	protected static void initCurrentJobListPanel() {
    	CurrentJobTileRenderer currJobTileRenderer= new CurrentJobTileRenderer();
	   	currentJobListTable=new JTable(currJobTileRenderer);
	   	currentJobListTable.setDefaultRenderer(JobTile.class, new CurrentJobTileCell());
	   	currentJobListTable.setDefaultEditor(JobTile.class, new CurrentJobTileCell());
	   	currentJobListTable.setRowHeight(110);
	   	
	   	currentJobList=new WebScrollPane(currentJobListTable);
	   	currentJobList.setPreferredWidth(350);
	}
	
	private void initCompletedJobListPanel() {
    	CompletedJobTileRenderer completedJobRenderer= new CompletedJobTileRenderer();
    	completedJobListTable=new JTable(completedJobRenderer);
	   	
    	completedJobListTable.setDefaultRenderer(JobTile.class, new CompletedJobTileCell());
    	completedJobListTable.setDefaultEditor(JobTile.class, new CompletedJobTileCell());
    	completedJobListTable.setRowHeight(110);
	   	
	   	completedJobsList=new WebScrollPane(completedJobListTable);
	   	completedJobsList.setPreferredWidth(350);
	}

	private void initNegotiationListPanel() {
		
		NegotiationJobTileRenderer negotiationRenderer= new NegotiationJobTileRenderer();
		negotiationJobListTable=new JTable(negotiationRenderer);
		negotiationJobListTable.setDefaultRenderer(JobTile.class, new NegotitationJobTileCell());
		negotiationJobListTable.setDefaultEditor(JobTile.class, new NegotitationJobTileCell());
		negotiationJobListTable.setRowHeight(110);
	   	
	   	negotiationJobList=new WebScrollPane(negotiationJobListTable);
	   	negotiationJobList.setPreferredWidth(350);
		
	}
	
	public static void unloadCurrentJobInfoPanel() {
		currentJobListinfoPanel.removeAll();
		MainPanel.remove(currentJobListinfoPanel);
		welcomeScreenFrame.validate();
		welcomeScreenFrame.repaint();
		welcomeScreenFrame.setVisible(true);
	}
	
	public static void unloadCompletedJobInfoPanel() {
		completedJobListinfoPanel.removeAll();
		MainPanel.remove(completedJobListinfoPanel);
		welcomeScreenFrame.validate();
		welcomeScreenFrame.repaint();
		welcomeScreenFrame.setVisible(true);
	}

	public static void unloadNegotiationInfoPanel() {
		NegotiationJobListinfoPanel.removeAll();
		MainPanel.remove(NegotiationJobListinfoPanel);
		welcomeScreenFrame.validate();
		welcomeScreenFrame.repaint();
		welcomeScreenFrame.setVisible(true);
	}
	

	public static void createCurrentJobInfoPanel(JobTile jobToShow){
		MigLayout migLayout=new MigLayout("","200","[30]");
		WebPanel detailsPanel=new WebPanel(migLayout);
		
		final Format formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		WebLabel JobNametextlbl,JobIDTxtlbl,jobCustStartDateTxtlbl,jobCustDueDateTextlbl,
		jobGSAStartDateTxtlbl,jobGSADueDateTxtlbl, durationTextlbl, priorityTextlbl;
		
		WebHotkeyLabel JobNamelbl,JobIDlbl,jobCustStartDatelbl,jobCustDueDatelbl,
		jobGSAStartDatelbl,jobGSADueDatelbl, durationlbl, prioritylbl;
		
		Font TextlblFont=UIManager.getDefaults().getFont("TabbedPane.font");
//		TextlblFont=TextlblFont.deriveFont(Font.PLAIN, 12);
		Font lblFont = TextlblFont.deriveFont(Font.PLAIN, 20);
		
		JobNametextlbl=new WebLabel("Job Name");
		JobNamelbl=new WebHotkeyLabel(jobToShow.getJobName());
//		JobNamelbl.setMinimumWidth(200);

		JobIDTxtlbl=new WebLabel("Job ID");
		JobIDlbl=new WebHotkeyLabel(jobToShow.getBatchID().toString());
//		JobIDlbl.setMinimumWidth(150);
		
		jobCustStartDateTxtlbl=new WebLabel("Start Date by customer");
		jobCustStartDatelbl=new WebHotkeyLabel(formatter.format(jobToShow.getCustStartDate()));
//		jobCustStartDatelbl.setMinimumWidth(150);
		
		jobCustDueDateTextlbl=new WebLabel("Due Date by customer");
		jobCustDueDatelbl=new WebHotkeyLabel(formatter.format(jobToShow.getCustDueDate()));
//		jobCustDueDatelbl.setMinimumWidth(150);
		
		jobGSAStartDateTxtlbl=new WebLabel("Start date by MAS");
		jobGSAStartDatelbl=new WebHotkeyLabel(formatter.format(jobToShow.getStartDatebyGSA()));
//		jobGSAStartDatelbl.setMinimumWidth(150);
		
		jobGSADueDateTxtlbl=new WebLabel("Due date by MAS");
		jobGSADueDatelbl=new WebHotkeyLabel(formatter.format(jobToShow.getDueDatebyGSA()));
//		jobGSADueDatelbl.setMinimumWidth(150);
		
		durationTextlbl=new WebLabel("Duration (seconds)");
		durationlbl=new WebHotkeyLabel(Double.toString(jobToShow.getProcessingTime()));
//		durationlbl.setMinimumWidth(150);
		
		priorityTextlbl=new WebLabel("Priority");
		prioritylbl=new WebHotkeyLabel(Integer.toString(jobToShow.getPriority()));
//		prioritylbl.setMinimumWidth(150);
		
/*		
		
		JobNametextlbl.setFont(TextlblFont);
		JobIDTxtlbl.setFont(TextlblFont);
		jobCustStartDateTxtlbl.setFont(TextlblFont);
		jobCustDueDateTextlbl.setFont(TextlblFont);
		durationTextlbl.setFont(TextlblFont);
		priorityTextlbl.setFont(TextlblFont);*/
		
		JobNamelbl.setFont(lblFont);
		JobIDlbl.setFont(lblFont);
		jobCustStartDatelbl.setFont(lblFont);
		jobCustDueDatelbl.setFont(lblFont);
		durationlbl.setFont(lblFont);
		prioritylbl.setFont(lblFont);
		jobGSAStartDatelbl.setFont(lblFont);
		jobGSADueDatelbl.setFont(lblFont);
		
		detailsPanel.add(JobNametextlbl,"growx");
		detailsPanel.add(JobIDTxtlbl,"growx");
		detailsPanel.add(jobCustStartDateTxtlbl,"growx");
		detailsPanel.add(jobCustDueDateTextlbl,"wrap, growx");
		
		detailsPanel.add(JobNamelbl,"growx");
		detailsPanel.add(JobIDlbl,"growx");
		detailsPanel.add(jobCustDueDatelbl,"growx");
		detailsPanel.add(jobCustStartDatelbl,"growx");
		detailsPanel.add(jobCustDueDatelbl,"wrap, growx");
		
		detailsPanel.add(durationTextlbl,"growx");
		detailsPanel.add(priorityTextlbl,"growx");
		detailsPanel.add(jobGSAStartDateTxtlbl,"growx");
		detailsPanel.add(jobGSADueDateTxtlbl,"wrap, growx");
		
		detailsPanel.add(durationlbl,"growx");
		detailsPanel.add(prioritylbl,"growx");
		detailsPanel.add(jobGSAStartDatelbl,"growx");
		detailsPanel.add(jobGSADueDatelbl,"wrap, growx");
		
		currentJobListinfoPanel.add(detailsPanel,"wrap");
		
//		JScrollPane jobTable=createsJobsTable(); //jobs in batch
		
//		infoPanel.add(jobTable);
		
		MainPanel.add(currentJobListinfoPanel,BorderLayout.CENTER);
		welcomeScreenFrame.validate();
		welcomeScreenFrame.repaint();
		welcomeScreenFrame.setVisible(true);
		
		
	}
	
	public static void creatCompletedJobInfoPanel(JobTile jobTileInCell) {
		MigLayout migLayout=new MigLayout("","200","[30]");
		WebPanel detailsPanel=new WebPanel(migLayout);
		
		final Format formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		WebLabel JobNametextlbl,JobIDTxtlbl,jobCustStartDateTxtlbl,jobCustDueDateTextlbl,
		jobGSAStartDateTxtlbl,jobGSADueDateTxtlbl, durationTextlbl, priorityTextlbl;
		
		WebHotkeyLabel JobNamelbl,JobIDlbl,jobCustStartDatelbl,jobCustDueDatelbl,
		jobGSAStartDatelbl,jobGSADueDatelbl, durationlbl, prioritylbl;
		
		Font TextlblFont=UIManager.getDefaults().getFont("TabbedPane.font");
//		TextlblFont=TextlblFont.deriveFont(Font.PLAIN, 12);
		Font lblFont = TextlblFont.deriveFont(Font.PLAIN, 20);
		
		JobNametextlbl=new WebLabel("Batch Name");
		JobNamelbl=new WebHotkeyLabel(jobTileInCell.getJobName());
//		JobNamelbl.setMinimumWidth(200);

		JobIDTxtlbl=new WebLabel("Batch ID");
		JobIDlbl=new WebHotkeyLabel(jobTileInCell.getBatchID().toString());
//		JobIDlbl.setMinimumWidth(150);
		
		jobCustStartDateTxtlbl=new WebLabel("Start Date by customer");
		jobCustStartDatelbl=new WebHotkeyLabel(formatter.format(jobTileInCell.getCustStartDate()));
//		jobCustStartDatelbl.setMinimumWidth(150);
		
		jobCustDueDateTextlbl=new WebLabel("Due Date by customer");
		jobCustDueDatelbl=new WebHotkeyLabel(formatter.format(jobTileInCell.getCustDueDate()));
//		jobCustDueDatelbl.setMinimumWidth(150);
		
		jobGSAStartDateTxtlbl=new WebLabel("Start date by MAS");
		jobGSAStartDatelbl=new WebHotkeyLabel(formatter.format(jobTileInCell.getStartDatebyGSA()));
//		jobGSAStartDatelbl.setMinimumWidth(150);
		
		jobGSADueDateTxtlbl=new WebLabel("Due date by MAS");
		jobGSADueDatelbl=new WebHotkeyLabel(formatter.format(jobTileInCell.getDueDatebyGSA()));
//		jobGSADueDatelbl.setMinimumWidth(150);
		
		durationTextlbl=new WebLabel("Duration (seconds)");
		durationlbl=new WebHotkeyLabel(Double.toString(jobTileInCell.getProcessingTime()));
//		durationlbl.setMinimumWidth(150);
		
		priorityTextlbl=new WebLabel("Priority");
		prioritylbl=new WebHotkeyLabel(Integer.toString(jobTileInCell.getPriority()));
//		prioritylbl.setMinimumWidth(150);
		
/*		
		
		JobNametextlbl.setFont(TextlblFont);
		JobIDTxtlbl.setFont(TextlblFont);
		jobCustStartDateTxtlbl.setFont(TextlblFont);
		jobCustDueDateTextlbl.setFont(TextlblFont);
		durationTextlbl.setFont(TextlblFont);
		priorityTextlbl.setFont(TextlblFont);*/
		
		JobNamelbl.setFont(lblFont);
		JobIDlbl.setFont(lblFont);
		jobCustStartDatelbl.setFont(lblFont);
		jobCustDueDatelbl.setFont(lblFont);
		durationlbl.setFont(lblFont);
		prioritylbl.setFont(lblFont);
		jobGSAStartDatelbl.setFont(lblFont);
		jobGSADueDatelbl.setFont(lblFont);
		
		detailsPanel.add(JobNametextlbl,"growx");
		detailsPanel.add(JobIDTxtlbl,"growx");
		detailsPanel.add(jobCustStartDateTxtlbl,"growx");
		detailsPanel.add(jobCustDueDateTextlbl,"wrap, growx");
		
		detailsPanel.add(JobNamelbl,"growx");
		detailsPanel.add(JobIDlbl,"growx");
		detailsPanel.add(jobCustDueDatelbl,"growx");
		detailsPanel.add(jobCustStartDatelbl,"growx");
		detailsPanel.add(jobCustDueDatelbl,"wrap, growx");
		
		detailsPanel.add(durationTextlbl,"growx");
		detailsPanel.add(priorityTextlbl,"growx");
		detailsPanel.add(jobGSAStartDateTxtlbl,"growx");
		detailsPanel.add(jobGSADueDateTxtlbl,"wrap, growx");
		
		detailsPanel.add(durationlbl,"growx");
		detailsPanel.add(prioritylbl,"growx");
		detailsPanel.add(jobGSAStartDatelbl,"growx");
		detailsPanel.add(jobGSADueDatelbl,"wrap, growx");
		
		completedJobListinfoPanel.add(detailsPanel,"wrap");
		
		MainPanel.add(completedJobListinfoPanel,BorderLayout.CENTER);
		welcomeScreenFrame.validate();
		welcomeScreenFrame.repaint();
		welcomeScreenFrame.setVisible(true);
		
	}

	public static void creatNegotiationInfoPanel(JobTile jobTileInCell) {
		
			NegotiationInfo nip=new NegotiationInfo(GSA, jobTileInCell.getBatch());
			NegotiationJobListinfoPanel=nip.getPanel();
			MainPanel.add(NegotiationJobListinfoPanel,BorderLayout.CENTER);
			welcomeScreenFrame.validate();
			welcomeScreenFrame.repaint();
			welcomeScreenFrame.setVisible(true);
	}


	private static JButton[] getButtons(){
		JButton[] buttons=new JButton[4];
		JButton About = new JButton();
	    Image img = null;
		try {
			img = ImageIO.read (new File("resources/about.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(img);
	    About.setIcon(new ImageIcon(img));
	    About.setPreferredSize(new Dimension(90,90));
	    About.setActionCommand("about");
	    About.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
					cleanMainPanel();	
            		welcomeScreenFrame.revalidate();
            		welcomeScreenFrame.repaint();
            		welcomeScreenFrame.setVisible(true);
            	
            }
        });  
	    buttons[0]=About;
	    
	    JButton Negotiation = new JButton();
	    Image negotiationImg = null;
	 		try {
	 			negotiationImg = ImageIO.read (new File("resources/negotiation.png"));
	 		} catch (IOException e) {
	 			e.printStackTrace();
	 		}
 		Negotiation.setIcon(new ImageIcon( negotiationImg));
 		Negotiation.setPreferredSize(new Dimension(90,90));
 		Negotiation.setActionCommand("negotition");
 		Negotiation.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
					cleanMainPanel();
					MainPanel.add(negotiationJobList,BorderLayout.WEST);
            		welcomeScreenFrame.revalidate();
            		welcomeScreenFrame.repaint();
            		welcomeScreenFrame.setVisible(true);
			}
		});
 		
 		buttons[1]=Negotiation;
 		
 		JButton JobManager = new JButton();
	    Image JobManagerImg = null;
	 		try {
	 			JobManagerImg = ImageIO.read (new File("resources/JobManager.png"));
	 		} catch (IOException e) {
	 			e.printStackTrace();
	 		}
	 		JobManager.setIcon(new ImageIcon( JobManagerImg));
	 		JobManager.setPreferredSize(new Dimension(90,90));
	 		JobManager.setActionCommand("jobManager");
	 		
	 		JobManager.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					cleanMainPanel();
					
					MainPanel.add(currentJobList,BorderLayout.WEST);
            		welcomeScreenFrame.revalidate();
            		welcomeScreenFrame.repaint();
            		welcomeScreenFrame.setVisible(true);
	            }

				
	        });  
	 		
 		buttons[2]=JobManager;

/* 		JButton signOut = new JButton();
	    Image signOutImg = null;
	 		try {
	 			signOutImg = ImageIO.read (new File("resources/signOut.png"));
	 		} catch (IOException e) {
	 			e.printStackTrace();
	 		}
	 		signOut.setIcon(new ImageIcon( signOutImg));
	 		signOut.setPreferredSize(new Dimension(90,90));
	 		signOut.setActionCommand("signOut");
	 		
	 		signOut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
				 	if(currentJobList!=null){
	            		MainPanel.remove(currentJobList);
	            		currentJobList=null;
	            		log.info("currentJobList "+"is not null");
				 		
		            	
	            	}
	            	else{
	            	}
	            }
	        });
	 		
 		buttons[3]=signOut;*/
 		
 		JButton completedJobs = new JButton();
	    Image CompletedJobsImg = null;
	 		try {
	 			CompletedJobsImg = ImageIO.read (new File("resources/completedJob.png"));
	 		} catch (IOException e) {
	 			e.printStackTrace();
	 		}
	 		completedJobs.setIcon(new ImageIcon( CompletedJobsImg));
	 		completedJobs.setPreferredSize(new Dimension(90,90));
	 		completedJobs.setActionCommand("completedJob");
	 		
	 		completedJobs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					cleanMainPanel();
					
					MainPanel.add(completedJobsList,BorderLayout.WEST);
            		welcomeScreenFrame.revalidate();
            		welcomeScreenFrame.repaint();
            		welcomeScreenFrame.setVisible(true);
	            }
	        });
	 		
 		buttons[3]=completedJobs;
 		
		return buttons;
	}

	public static JTable getNegotiationJobListTable() {
		return negotiationJobListTable;
	}

	public static JFrame getWelcomeScreenFrame() {
		return welcomeScreenFrame;
	}

	public static GlobalSchedulingAgent getGSA() {
		return GSA;
	}

	protected static void cleanMainPanel() {
		if(completedJobsList!=null && completedJobsList.getRootPane()!=null){
			MainPanel.remove(completedJobsList);
			MainPanel.remove(completedJobListinfoPanel);
		}
		else if(negotiationJobList!=null && negotiationJobList.getRootPane()!=null){
			MainPanel.remove(negotiationJobList);
			MainPanel.remove(NegotiationJobListinfoPanel);
		}
		else if(currentJobList!=null && currentJobList.getRootPane()!=null){
			MainPanel.remove(currentJobList);
			MainPanel.remove(currentJobListinfoPanel);
		}
	}

	
	public void addCompletedJob(Batch b) {
		CurrentJobTileRenderer CurrjobListRenderer=
				(CurrentJobTileRenderer)currentJobListTable.getModel();
    	CurrjobListRenderer.removeJob(b);
    	
    	CompletedJobTileRenderer completedJobRenderer=
    			(CompletedJobTileRenderer)completedJobListTable.getModel();
    	
    	completedJobRenderer.addBatch(b);
    	String msg="Batch No. "+b.getBatchNumber()+" completed";
		showNotification(msg, notificationType.completedJob);
	}

	public void addAcceptedJobToList(Batch order) {
		CurrentJobTileRenderer CurrJobListRenderer=(CurrentJobTileRenderer)currentJobListTable.getModel();
    	CurrJobListRenderer.addBatch(order);
    	String msg="Batch No. "+order.getBatchNumber()+" accepted";
		showNotification(msg, notificationType.newJob);
	}

	public void addNegotiationBid(Batch jobUnderNegotiation) {
		NegotiationJobTileRenderer negotiationRenderer=
				(NegotiationJobTileRenderer)negotiationJobListTable.getModel();
		negotiationRenderer.addBatch(jobUnderNegotiation);
		String msg="Bid recieved for batch no. "+jobUnderNegotiation.getBatchNumber();
		showNotification(msg, notificationType.negotiationBid);
		
	}

	public static void showNotification(String message,notificationType type){
		
		switch(type){
		case error :
			GSAguiIcon.displayMessage("ERROR",message, TrayIcon.MessageType.ERROR);
			break;
			
		case negotiationBid:
			GSAguiIcon.displayMessage( "Negotiation Bid",message, TrayIcon.MessageType.INFO);
			break;
			
		case newJob:
			Icon newJobIcon=new ImageIcon("resources/newJob.png");
			GSAguiIcon.displayMessage( "New order",message, TrayIcon.MessageType.INFO);
			break;
			
		case completedJob:
			Icon completedJobIcon=new ImageIcon("resources/completedJob.png");
			GSAguiIcon.displayMessage( "Batch completed",message, TrayIcon.MessageType.INFO);
			break;
		}
		
		String notificationSound = "resources/notification.wav";
	    InputStream in=null;
		try {
			in = new FileInputStream(notificationSound);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	 
	    // create an audiostream from the inputstream
	    AudioStream audioStream=null;
		try {
			audioStream = new AudioStream(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	 
	    // play the audio clip with the audioplayer class
	    AudioPlayer.player.start(audioStream);
	    
			}

}
