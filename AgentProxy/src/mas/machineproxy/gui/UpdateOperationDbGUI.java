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

import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mas.jobproxy.JobGNGattribute;
import mas.jobproxy.jobDimension;
import mas.localSchedulingproxy.database.OperationDataBase;
import mas.localSchedulingproxy.database.OperationInfo;
import mas.util.TableUtil;
import net.miginfocom.swing.MigLayout;

public class UpdateOperationDbGUI extends JFrame {

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

	private JPanel buttonPanel;
	private JPanel rightPanel;

	private DisplayOperationPanel displayDataPanel;
	private AddNewOperationPanel addOperationPanel;

	private JScrollPane rightPanelScroller;

	private boolean displayDataSaved = false;
	private boolean operationDataSaved = false;

	private ArrayList<jobDimension> mDimensions;
	private ArrayList<JobGNGattribute> gngAttributes;

	public UpdateOperationDbGUI(String agentName) {

		this.aName = agentName;

		mDimensions = new ArrayList<jobDimension>();
		gngAttributes = new ArrayList<JobGNGattribute>();
		operationIDs = new ArrayList<String>();

		rightPanel = new JPanel(new BorderLayout());

		displayDataPanel = new DisplayOperationPanel();
		addOperationPanel = new AddNewOperationPanel();

		// create a placeholder for operation names on left hand side
		operationsData = new JPanel(new MigLayout());
		this.operationsDataScroller = new JScrollPane(operationsData);

		// create a placeholder for components on right hand side
		initOptionsPanel();
		buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(optionsPanel, BorderLayout.CENTER);
		rightPanel.add(buttonPanel, BorderLayout.NORTH);
		this.rightPanelScroller = new JScrollPane(rightPanel);

		// add both the place holders to split pane
		hSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, operationsDataScroller, rightPanelScroller);
		hSplitPane.setEnabled(false);

		add(hSplitPane);
		new readOperationDatabase().execute();
		showGui();
	}

	private class readOperationDatabase extends SwingWorker<OperationDataBase, String> {
		String path = "resources/database/" + aName + "_db.data";

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
			acceptedJobList.addListSelectionListener(new ListSelectionHandler());
			acceptedJobList.setCellRenderer(new customListRenderer());
			operationsData.add(acceptedJobList);

			hSplitPane.setDividerLocation(0.35);
			super.done();
		}
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

	private void addOperation() {
		if(TableUtil.checkIfExists(rightPanel, displayDataPanel)) {
			rightPanel.remove(displayDataPanel);
			checkDisplayOperationSave(displayDataPanel);
		}

		rightPanel.add(addOperationPanel);
		rightPanel.revalidate();
		rightPanel.repaint();
	}

	private void checkNewOperationSave(AddNewOperationPanel comp) {

		if(! comp.datasaved()) {
			int dialogButton = JOptionPane.YES_NO_OPTION;
			int dialogResult = JOptionPane.showConfirmDialog (comp,
					"Would You Like to Save your Previous changes First?","Warning",
					dialogButton);

			if(dialogResult == JOptionPane.YES_OPTION) {

				OperationInfo info = comp.getOperationInfo();
				String id = comp.getOperationId();
				ops.put(id, info);
			}
		}
		comp.reset();
	}

	private void checkDisplayOperationSave(DisplayOperationPanel comp) {

		if(! comp.datasaved()) {
			int dialogButton = JOptionPane.YES_NO_OPTION;
			int dialogResult = JOptionPane.showConfirmDialog (comp,
					"Would You Like to Save your Previous changes First?","Warning",
					dialogButton);

			if(dialogResult == JOptionPane.YES_OPTION) {

				OperationInfo info = comp.getOperationInfo();
				String id = comp.getOperationId();
				ops.put(id, info);
			}
		}
		comp.reset();
	}

	private void showOperation(String id, OperationInfo op) {

		// check if the panel was already added i.e. some input was being entered
		// check if that input is saved or not
		if(TableUtil.checkIfExists(rightPanel, addOperationPanel)) {
			checkNewOperationSave(addOperationPanel);
			rightPanel.remove(addOperationPanel);
			rightPanel.add(displayDataPanel);
			displayDataPanel.populate(id,op);
			
		} else if(TableUtil.checkIfExists(rightPanel, displayDataPanel)) {
			checkDisplayOperationSave(displayDataPanel);
			displayDataPanel.populate(id,op);
		}
		else {
			rightPanel.add(displayDataPanel);
			displayDataPanel.populate(id,op);
		}

		rightPanel.revalidate();
		rightPanel.repaint();
	}

	private void showGui() {
		setTitle("Update Job Operation Database");
		setPreferredSize(new Dimension(width, height));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
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

	class ListSelectionHandler implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) { 

			int idx = acceptedJobList.getSelectedIndex();
			if (idx != -1) {
				String opItem = operationIDs.get(idx);
				OperationInfo info = ops.getOperationInfo(opItem);
				showOperation(opItem,info);
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
