package mas.machineproxy.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import mas.util.TableUtil;
import net.miginfocom.swing.MigLayout;
import uiconstants.Labels;

public class AddNewOperationPanel extends JPanel {

	private static final long serialVersionUID = 1L;

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
	private JButton btnAddAttribute;
	private JButton btnDelAttribute;
	private JPanel attributePanel;

	public AddNewOperationPanel() {

		setLayout(new MigLayout());
		AddDelDimensionListener dListener = new AddDelDimensionListener();
		try {
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

		add(lblAddNewHeading, "wrap");

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
		
		setVisible(true);
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

	public void save() {
		// TODO Auto-generated method stub
		
	}
}
