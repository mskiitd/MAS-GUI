package mas.machineproxy.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import mas.jobproxy.JobGNGattribute;
import mas.jobproxy.jobDimension;
import mas.localSchedulingproxy.database.OperationInfo;
import mas.util.TableUtil;
import net.miginfocom.swing.MigLayout;
import uiconstants.Labels;

public class DisplayOperationPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private int timeUnitConversion = 1000;
	private BufferedImage editIcon;
	private JButton btnEditOperation;

	private JLabel lblDisplayHeading;
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
	private JButton btnAddAttribute;
	private JButton btnDelAttribute;
	private JPanel attributePanel;

	private String operationId;
	private boolean dataSaved = true;

	public DisplayOperationPanel() {

		setLayout(new MigLayout());
		AddDelDimensionListener dListener = new AddDelDimensionListener();
		try {
			editIcon = ImageIO.read(new File("resources/edit_64.png"));
			btnEditOperation = new JButton(new ImageIcon(editIcon));
			btnEditOperation.addActionListener(new enableEditListener());

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

		lblDisplayHeading = new JLabel(" Operation Data");
		lblDisplayHeading.setFont(TableUtil.headings);

		lblOperationID = new JLabel("Operation ID : ");
		lblOperationCost = new JLabel("Processing Cost :");
		lblOperationPtime = new JLabel("Processing Time : ");

		add(btnEditOperation,"wrap");

		add(lblDisplayHeading, "wrap");

		add(lblOperationID);
		add(txtOperationID,"wrap");

		add(lblOperationCost);
		add(txtOperationCost, "wrap");

		add(lblOperationPtime);
		add(txtProcessingTime, "wrap");

		add(lblDimensionHeading);
		add(btnAddDimension);
		add(btnDelDimension, "wrap");
		add(dimensionPanel, "wrap");

		add(lblAttributeHeading);
		add(btnAddAttribute);
		add(btnDelAttribute, "wrap");
		add(attributePanel, "wrap");
		
		txtOperationID.setEnabled(false);
	}

	private boolean checkDimension(OperationInfo op) {

		boolean status = true;
		ArrayList<jobDimension> dimList = new ArrayList<jobDimension>();
		for (Iterator<DimensionInputPanel> i = listPanelDimensions.iterator(); i.hasNext(); ) {
			jobDimension jdim = ((DimensionInputPanel) i.next() ).getDimension();
			if(jdim != null) {
				dimList.add(jdim);
			} else {
				JOptionPane.showMessageDialog(this, "Invalid input for dimension !!");
				status = false;
				break;
			}
		}
		op.setDimensions(dimList);
		return status;
	}

	private boolean checkAttribute(OperationInfo op) {

		boolean status = true;
		ArrayList<JobGNGattribute> attList = new ArrayList<JobGNGattribute>();
		
		for (Iterator<AttributeInputPanel> i = listPanelAttributes.iterator(); i.hasNext(); ) {
			
			JobGNGattribute jAttribute = ((AttributeInputPanel) i.next() ).getAttribute();
			if(jAttribute != null) {
				attList.add(jAttribute);
			} else {
				JOptionPane.showMessageDialog(this, "Invalid input for Attributes !!");
				status = false;
				break;
			}
		}
		op.setGngAttributes(attList);
		return status;
	}

	private boolean checkProcTime(OperationInfo op) {

		boolean status = true;
		if(txtProcessingTime.getText().matches("-?\\d+?")) {
			long pTime = Long.parseLong(txtProcessingTime.getText());
			op.setProcessingTime(pTime*timeUnitConversion);
		}
		else {
			JOptionPane.showMessageDialog(this, "Invalid input for Processing time !!");
			status = false;
		}
		return status;
	}
	
	private boolean checkProcCost(OperationInfo op) {

		boolean status = true;
		if(txtOperationCost.getText().matches("-?\\d+(\\.\\d+)?")) {
			double pTime = Double.parseDouble(txtOperationCost.getText());
			op.setProcessingCost(pTime);
		}
		else {
			JOptionPane.showMessageDialog(this, "Invalid input for Processing time !!");
			status = false;
		}
		return status;
	}
	
	private boolean checkOperationId() {

		boolean status = true;
		if(txtOperationID.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please enter job ID !!");
			status = false;
		} else {
			this.operationId = txtOperationID.getText();
			status = true;
		}
		return status;
	}

	public void populate(String id, OperationInfo op) {
		if(op != null) {
			this.operationId = id;
			txtOperationID.setText(id);
			txtProcessingTime.setText(String.valueOf(op.getProcessingTime()/timeUnitConversion) );
			txtOperationCost.setText(String.valueOf(op.getProcessingCost()) );

			ArrayList<JobGNGattribute> attributes = op.getGngAttributes();
			ArrayList<jobDimension> dimensions = op.getDimensions();

			listPanelAttributes.clear();
			attributePanel.removeAll();
			for(int i = 0 ; i < attributes.size(); i++ ) {
				AttributeInputPanel aInputPanel = new AttributeInputPanel(attributes.get(i));
				listPanelAttributes.add(aInputPanel);
				attributePanel.add(aInputPanel);
			}

			listPanelDimensions.clear();
			dimensionPanel.removeAll();
			for(int i = 0 ; i < dimensions.size(); i++ ) {
				DimensionInputPanel dInputPanel = new DimensionInputPanel(dimensions.get(i));
				listPanelDimensions.add(dInputPanel);
				dimensionPanel.add(dInputPanel);
			}
			setEnabledAll(false);
			revalidate();
			repaint();
		}
	}

	private void setEnabledAll(boolean val) {
		setEnabledRecursive(this, val);
		btnEditOperation.setEnabled(true);
		txtOperationID.setEnabled(false);
	}

	private void setEnabledRecursive(JComponent comp, boolean val) {
		if(comp == null) 
			return;

		for(Component child : comp.getComponents()) {
			child.setEnabled(val);
		}
	}

	class enableEditListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			setEnabledAll(true);
			dataSaved = false;
		}
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
					dimensionPanel.remove(listPanelDimensions.poll() );
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

	public OperationInfo getOperationInfo() {
		
		OperationInfo info = new OperationInfo();
		boolean x1 = checkDimension(info);
		boolean x2 = checkAttribute(info);
		boolean x3 = checkProcTime(info);
		boolean x4 = checkProcCost(info);
		boolean x5 = checkOperationId();

		if(x1 & x2 & x3 & x4 & x5)
			return info;
		return null;
	}

	public String getOperationId() {
		return this.operationId;
	}

	public boolean datasaved() {
		return this.dataSaved;
	}

	public void reset() {
		this.dataSaved = true;
		txtOperationCost.setText("");
		txtOperationID.setText("");
		txtProcessingTime.setText("");
		
	}
}
