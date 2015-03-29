package mas.machineproxy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.sun.corba.se.spi.orbutil.fsm.Action;

import mas.jobproxy.JobGNGattribute;
import mas.jobproxy.jobDimension;
import mas.localSchedulingproxy.database.OperationDataBase;
import mas.localSchedulingproxy.database.OperationInfo;
import mas.util.TableUtil;
import net.miginfocom.swing.MigLayout;
import uiconstants.Labels;

public class UpdateOperationDbGUI extends JFrame{

	private static final long serialVersionUID = 1L;
	private String aName;
	private OperationDataBase ops;
	private ArrayList<String> operationIDs;

	private JList<Object> acceptedJobList;
	private listModel acceptedJobsListModel;
	private JSplitPane hSplitPane;
	private int width = 800;
	private int height = 600;

	private JPanel operationsData;
	private JScrollPane operationsDataScroller;

	private JPanel optionsPanel;
	private JButton btnAddOperation;
	private JButton btnSaveAndExit;
	private BufferedImage addIcon;
	private BufferedImage saveIcon;

	private JPanel displayDataPanel;
	private JPanel buttonPanel;
	private JPanel addNewJobPanel;
	private JScrollPane addNewJobScroller;
	private JScrollPane displayDataScroller;

	private JLabel lblAddNewHeading;
	private JLabel lblOperationID;
	private JLabel lblOperationPtime;
	private JLabel lblOperationCost;
	private JLabel lblDimensionHeading;
	private JLabel lblAttributeHeading;

	private JTextField txtOperationID;
	private JTextField txtProcessingTime;
	private JTextField txtOperationCost;

	private Queue<DimensionInputPanel> listPanelDimensions;
	private BufferedImage iconBtnAddDimension;
	private JButton btnAddDimension;
	private BufferedImage iconBtnDelDimension;
	private JButton btnDelDimension;
	private JPanel dimensionPanel;

	private Queue<AttributeInputPanel> listPanelAttributes;
	private BufferedImage iconBtnAddAttribute;
	private JButton btnAddAttribute;
	private BufferedImage iconBtnDelAttribute;
	private JButton btnDelAttribute;
	private JPanel attributePanel;
	
	private ArrayList<jobDimension> mDimensions;
	private ArrayList<JobGNGattribute> gngAttributes;

	public UpdateOperationDbGUI(String agentName) {

		this.aName = agentName;
		mDimensions = new ArrayList<jobDimension>();
		gngAttributes = new ArrayList<JobGNGattribute>();
		operationIDs = new ArrayList<String>();

		displayDataPanel = new JPanel(new BorderLayout());
		buttonPanel = new JPanel(new BorderLayout());

		addNewJobPanel = new JPanel(new MigLayout());
		addNewJobScroller = new JScrollPane(addNewJobPanel);

		operationsData = new JPanel(new MigLayout());
		initAddNewJobPanel();

		initOptionsPanel();
		buttonPanel.add(optionsPanel, BorderLayout.CENTER);

		displayDataPanel.add(buttonPanel, BorderLayout.NORTH);
		displayDataPanel.add(addNewJobPanel,BorderLayout.CENTER);

		this.operationsDataScroller = new JScrollPane(operationsData);
		this.displayDataScroller = new JScrollPane(displayDataPanel);

		hSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, operationsDataScroller, displayDataScroller);
		hSplitPane.setEnabled(false);

		add(hSplitPane);
		new readOperationDatabase().execute();
		showGui();
	}

	private class readOperationDatabase extends SwingWorker<OperationDataBase, String> {
		String path = "resources/database/" + aName + "_db.mas";

		@Override
		protected OperationDataBase doInBackground() throws Exception {
			File file = new File(path);
			FileInputStream fis;
			try {
				fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis);
				ops = (OperationDataBase)ois.readObject();
				ois.close();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			if(ops != null) {
				operationIDs = ops.getOperationTypes();
			}
			return ops;
		}

		@Override
		protected void done() {
			acceptedJobsListModel = new listModel();
			acceptedJobList = new JList<Object>(acceptedJobsListModel);

			acceptedJobList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			acceptedJobList.setCellRenderer(new customListRenderer());
			operationsData.add(acceptedJobList);

			if(operationIDs.isEmpty()) {
				hSplitPane.setDividerLocation(0.30);
			}
			super.done();
		}
	}

	private void initAddNewJobPanel() {

		try {
			AddDelDimensionListener dListener = new AddDelDimensionListener();

			iconBtnAddDimension = ImageIO.read(new File("resources/plusbutton.png"));
			btnAddDimension = new JButton(new ImageIcon(iconBtnAddDimension));
			btnAddDimension.setBorder(BorderFactory.createEmptyBorder());
			btnAddDimension.setContentAreaFilled(true);
			btnAddDimension.addActionListener(dListener);

			iconBtnDelDimension = ImageIO.read(new File("resources/del_24.png"));
			btnDelDimension = new JButton(new ImageIcon(iconBtnDelDimension));
			btnDelDimension.setBorder(BorderFactory.createEmptyBorder());
			btnDelDimension.setContentAreaFilled(true);
			btnDelDimension.addActionListener(dListener);
			
			AddDelAttributeListener aListener = new AddDelAttributeListener();

			btnAddAttribute = new JButton(new ImageIcon(iconBtnAddDimension));
			btnAddAttribute.setBorder(BorderFactory.createEmptyBorder());
			btnAddAttribute.setContentAreaFilled(true);
			btnAddAttribute.addActionListener(aListener);

			btnDelAttribute = new JButton(new ImageIcon(iconBtnDelDimension));
			btnDelAttribute.setBorder(BorderFactory.createEmptyBorder());
			btnDelAttribute.setContentAreaFilled(true);
			btnDelAttribute.addActionListener(aListener);

		} catch (IOException e) {
			e.printStackTrace();
		}

		dimensionPanel = new JPanel(new MigLayout());
		attributePanel = new JPanel(new MigLayout());
		
		listPanelAttributes = new LinkedList<AttributeInputPanel>();
		listPanelDimensions = new LinkedList<DimensionInputPanel>();

		DimensionInputPanel dimPanel1 = new DimensionInputPanel();
		listPanelDimensions.add(dimPanel1);
		dimensionPanel.add(listPanelDimensions.peek(),"wrap");
		
		txtProcessingTime = new JTextField(Labels.defaultJTextSize);
		txtOperationCost = new JTextField(Labels.defaultJTextSize);
		txtOperationID = new JTextField(Labels.defaultJTextSize);

		lblDimensionHeading = new JLabel(" Dimensions ");
		lblDimensionHeading.setFont(TableUtil.headings);

		lblAttributeHeading = new JLabel(" Attributes ");
		lblAttributeHeading.setFont(TableUtil.headings);

		lblAddNewHeading = new JLabel("Define Operation Data");
		lblAddNewHeading.setFont(TableUtil.headings);

		lblOperationID = new JLabel("Operation ID : ");
		lblOperationCost = new JLabel("Processing Cost :");
		lblOperationPtime = new JLabel("Processing Time : ");

		addNewJobPanel.add(lblAddNewHeading, "wrap");

		addNewJobPanel.add(lblOperationID);
		addNewJobPanel.add(txtOperationID,"wrap");

		addNewJobPanel.add(lblOperationCost);
		addNewJobPanel.add(txtOperationCost, "wrap");

		addNewJobPanel.add(lblOperationPtime);
		addNewJobPanel.add(txtProcessingTime, "wrap");

		addNewJobPanel.add(lblDimensionHeading);
		addNewJobPanel.add(btnAddDimension);
		addNewJobPanel.add(btnDelDimension, "wrap");
		addNewJobPanel.add(dimensionPanel, "wrap");

		addNewJobPanel.add(lblAttributeHeading);
		addNewJobPanel.add(btnAddAttribute);
		addNewJobPanel.add(btnDelAttribute, "wrap");
		addNewJobPanel.add(attributePanel, "wrap");
	}

	private void initDisplayOpPanel() {

	}

	private void initOptionsPanel() {
		try {
			addIcon = ImageIO.read(new File("resources/plus_64.png"));
			saveIcon = ImageIO.read(new File("resources/save_64.png") );
		} catch (IOException e) {
			e.printStackTrace();
		}
		optionsPanel = new JPanel(new BorderLayout());
		buttonClickListener bListener = new buttonClickListener();

		btnAddOperation = new JButton(new ImageIcon(addIcon));
		btnAddOperation.setBorder(BorderFactory.createEmptyBorder());
		btnAddOperation.setContentAreaFilled(true);
		btnAddOperation.addActionListener(bListener);

		btnSaveAndExit = new JButton(new ImageIcon(saveIcon));
		btnSaveAndExit.setBorder(BorderFactory.createEmptyBorder());
		btnSaveAndExit.setContentAreaFilled(false);
		btnSaveAndExit.addActionListener(bListener);

		optionsPanel.setBorder(new EmptyBorder(10,10,10,10));
		optionsPanel.add(btnAddOperation, BorderLayout.WEST);
		optionsPanel.add(btnSaveAndExit,BorderLayout.EAST);

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

	class AddDelDimensionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			if(e.getSource().equals(btnAddDimension)) {
				DimensionInputPanel dimpanel = new DimensionInputPanel();
				listPanelDimensions.add(dimpanel);
				dimensionPanel.add(dimpanel,"wrap");

			} else if(e.getSource().equals(btnDelDimension)) {
				if(! listPanelDimensions.isEmpty()) {
					dimensionPanel.remove(listPanelDimensions.poll()  );
				}
			}
			dimensionPanel.revalidate();
			dimensionPanel.repaint();
		}
	}
	
	class AddDelAttributeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			if(e.getSource().equals(btnAddAttribute)) {
				AttributeInputPanel dimpanel = new AttributeInputPanel();
				listPanelAttributes.add(dimpanel);
				attributePanel.add(dimpanel,"wrap");

			} else if(e.getSource().equals(btnDelAttribute)) {
				if(! listPanelAttributes.isEmpty()) {
					attributePanel.remove(listPanelAttributes.poll()  );
				}
			}
			attributePanel.revalidate();
			attributePanel.repaint();
		}
	}

	class buttonClickListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(btnAddOperation)) {
				addOperation();
			} else if(e.getSource().equals(btnSaveAndExit)) {

			}
		}

	}

	private void addOperation() {

	}
	
	class ListSelectionHandler implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) { 

			int idx = acceptedJobList.getSelectedIndex();
			if (idx != -1) {

				String opItem = operationIDs.get(idx);
				OperationInfo info = ops.getOperationInfo(opItem);

			}
		}
	}

	class listModel extends AbstractListModel<Object> {

		private static final long serialVersionUID = 1L;

		@Override
		public Object getElementAt(int index) {
			return operationIDs.get(index);
		}

		@Override
		public int getSize() {
			return operationIDs.size();
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

			String entry = (String) value;
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
