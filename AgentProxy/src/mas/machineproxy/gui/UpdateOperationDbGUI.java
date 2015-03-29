package mas.machineproxy.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import mas.jobproxy.JobGNGattribute;
import mas.jobproxy.job;
import mas.jobproxy.jobDimension;
import net.miginfocom.swing.MigLayout;
import uiconstants.Labels;

public class UpdateOperationDbGUI extends JFrame{

	private static final long serialVersionUID = 1L;
	private ArrayList<job> q;
	
	private JList<Object> acceptedJobList;
	private JSplitPane splitPane;
	private int width = 800;
	private int height = 600;
	
	private JPanel operationsData;
	private JScrollPane operationsDataScroller;
	
	private JPanel optionsPanel;
	private BufferedImage addIcon;
	private BufferedImage saveIcon;
	
	private JPanel displayData;
	private JPanel addNewJobPanel;
	private JPanel addNewJobScroller;
	private JScrollPane displayDataScroller;
	
	private JLabel lblOperationID;
	private JLabel lblOperationPtime;
	private JLabel lblOperationCost;
	
	private JComboBox<?> cbxOperationType;
	private JTextField txtProcessingTime;
	private JTextField txtOperationCost;
	
	private ArrayList<jobDimension> mDimensions;
	private ArrayList<JobGNGattribute> gngAttributes;
	
	public UpdateOperationDbGUI() {
			
		mDimensions = new ArrayList<jobDimension>();
		gngAttributes = new ArrayList<JobGNGattribute>();
		
		txtProcessingTime = new JTextField(Labels.defaultJTextSize);
		txtOperationCost = new JTextField(Labels.defaultJTextSize);
		
		lblOperationID = new JLabel();
		lblOperationCost = new JLabel();
		lblOperationPtime = new JLabel();
		
		initOptionsPanel();
		
		q = new ArrayList<job>();
		q.add(new job.Builder("j1").build() );
		q.add(new job.Builder("j2").build() );
		q.add(new job.Builder("j3").build() );
		q.add(new job.Builder("j4").build() );
		
		listModel acceptedJobsListModel = new listModel();
		acceptedJobList = new JList<Object>(acceptedJobsListModel);
		
		acceptedJobList.setCellRenderer(new customListRenderer());
		
		operationsData = new JPanel(new MigLayout());
		operationsData.add(acceptedJobList);
		
		displayData = new JPanel(new MigLayout());
		
		this.operationsDataScroller = new JScrollPane(operationsData);
		this.displayDataScroller = new JScrollPane(displayData);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, operationsDataScroller, displayDataScroller);
		splitPane.setEnabled(false);
		
		add(splitPane);
		showGui();
	}

	private void initOptionsPanel() {
		try {
			addIcon = ImageIO.read(new File("plus_64.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		optionsPanel = new JPanel(new MigLayout());
		
		
	}
	
	private void showGui() {
		setTitle("Update Job Operation Database");
		setPreferredSize(new Dimension(width, height));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}

	class listModel extends AbstractListModel<Object> {
		
		private static final long serialVersionUID = 1L;
		
		@Override
		public Object getElementAt(int index) {
			return q.get(index);
		}
		
		@Override
		public int getSize() {
			return q.size();
		}
	}
	
	class customListRenderer extends JobOperationItem implements ListCellRenderer<Object> {
		
		public customListRenderer() {
			super();
			setOpaque(true);
		}

		private static final long serialVersionUID = 1L;
		private final Color HIGHLIGHT_COLOR = new Color(132, 112, 255);
		private final Color BackGround_COLOR = new Color(238, 233, 233);
		private final Color ForeGround_COLOR = new Color(205, 201, 201);
		
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			
			job entry = (job) value;
			setDisplay(entry);
			
			if (isSelected) {
				setBackground(HIGHLIGHT_COLOR);
				setForeground(ForeGround_COLOR);
			} else {
				setBackground(BackGround_COLOR);
				setForeground(ForeGround_COLOR);
			}
			setPreferredSize(new Dimension(width/3,height/6));
			return this;
		}
	}
}
