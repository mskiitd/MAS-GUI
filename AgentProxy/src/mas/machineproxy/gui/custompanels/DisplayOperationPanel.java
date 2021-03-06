package mas.machineproxy.gui.custompanels;

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
import javax.swing.SwingUtilities;

import mas.jobproxy.JobGNGattribute;
import mas.jobproxy.jobDimension;
import mas.localSchedulingproxy.database.OperationInfo;
import mas.localSchedulingproxy.database.OperationItemId;
import mas.util.TableUtil;
import mas.util.formatter.doubleformatter.FormattedDoubleField;
import mas.util.formatter.integerformatter.FormattedIntegerField;
import mas.util.formatter.stringformatter.FormattedStringField;
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
	private JLabel lblCustomerIdHeading;

	private FormattedStringField txtOperationID;
	private FormattedIntegerField txtProcessingTime;
	private FormattedDoubleField txtOperationCost;
	private FormattedStringField txtCustomerId;

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

		txtProcessingTime = new FormattedIntegerField();
		txtProcessingTime.setColumns(Labels.defaultJTextSize);

		txtOperationCost = new FormattedDoubleField();
		txtOperationCost.setColumns(Labels.defaultJTextSize);

		txtOperationID = new FormattedStringField();
		txtOperationID.setColumns(Labels.defaultJTextSize);

		txtCustomerId = new FormattedStringField();
		txtCustomerId.setColumns(Labels.defaultJTextSize);

		lblDimensionHeading = new JLabel(" Dimensions ");
		lblDimensionHeading.setFont(TableUtil.headings);

		lblCustomerIdHeading = new JLabel(" Customer Id ");
		lblAttributeHeading = new JLabel(" Attributes ");
		lblAttributeHeading.setFont(TableUtil.headings);

		lblDisplayHeading = new JLabel(" Operation Data");
		lblDisplayHeading.setFont(TableUtil.headings);

		lblOperationID = new JLabel("Operation ID : ");
		lblOperationCost = new JLabel("Processing Cost :");
		lblOperationPtime = new JLabel("Processing Time : ");

		add(btnEditOperation,"wrap");

		add(lblDisplayHeading, "wrap");

		add(lblCustomerIdHeading);
		add(txtCustomerId,"wrap");

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
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(DisplayOperationPanel.this,
								"Invalid input for dimension !!");
					}
				});
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
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(DisplayOperationPanel.this,
								"Invalid input for Attributes !!");
					}
				});
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
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(DisplayOperationPanel.this,
							"Invalid input for Processing time !!");
				}
			});
			status = false;
		}
		return status;
	}

	/**
	 * Runs on EDT
	 * @param op
	 * @return
	 */
	private boolean checkProcCost(OperationInfo op) {

		boolean status = true;
		if(txtOperationCost.getText().matches("-?\\d+(\\.\\d+)?")) {
			double pTime = Double.parseDouble(txtOperationCost.getText());
			op.setProcessingCost(pTime);
		}
		else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(DisplayOperationPanel.this,
							"Invalid input for Processing time !!");
				}
			});
			status = false;
		}
		return status;
	}

	public void populate(OperationItemId id, OperationInfo op) {
		if(op != null) {
			txtOperationID.setText(id.getOperationId());
			txtCustomerId.setText(id.getCustomerId());
			txtProcessingTime.setText(String.valueOf(op.getProcessingTime()/timeUnitConversion));
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

		if(x1 & x2 & x3 & x4 )
			return info;
		return null;
	}

	public OperationItemId getOperationId() {
		OperationItemId id = new OperationItemId();
		boolean x5 = checkOperationId(id);
		boolean x6 = checkCustomerId(id);

		if( x5 && x6) 
			return id;
		return null;
	}

	private boolean checkCustomerId(OperationItemId id) {

		boolean status = true;
		if(txtCustomerId.getText().isEmpty()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(DisplayOperationPanel.this,
							"Please enter customer ID !!");
				}
			});
			status = false;
		} else {
			id.setCustomerId(txtCustomerId.getText());
			status = true;
		}
		return status;
	}

	private boolean checkOperationId(OperationItemId id) {

		boolean status = true;
		if(txtOperationID.getText().isEmpty()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(DisplayOperationPanel.this, "Please enter job ID !!");
				}
			});
			status = false;
		} else {
			id.setOperationId(txtOperationID.getText());
			status = true;
		}
		return status;
	}

	public boolean datasaved() {
		return this.dataSaved;
	}

	/**
	 * Runs on EDT
	 */
	public void reset() {
		this.dataSaved = true;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				txtOperationCost.setText("");
				txtOperationID.setText("");
				txtProcessingTime.setText("");
			}
		});

	}
}
