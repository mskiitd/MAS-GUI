package mas.maintenanceproxy.gui.preventive;

import java.awt.Color;
import java.awt.Component;
import java.text.Format;
import java.text.SimpleDateFormat;
import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import mas.maintenanceproxy.classes.PMaintenance;
import net.miginfocom.swing.MigLayout;

public class PrevMaintTableRenderer extends AbstractCellEditor implements TableCellEditor, TableCellRenderer{

	private static final long serialVersionUID = 1L;

	private JLabel lblActualStartDate,lblActualFinishDate;
	private JLabel lblActivityCode;
	private JPanel tile;
	private PMaintenance pMaintTileCell;
	private final Format formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public PrevMaintTableRenderer(){
		tile = new JPanel(new MigLayout("",
				"[]10",
				""
				));
		lblActualStartDate = new JLabel();
		lblActualStartDate.setName("Start Time");
		lblActualFinishDate = new JLabel();
		lblActualFinishDate.setName("Completion Time");
		lblActivityCode = new JLabel();
		lblActivityCode.setName("Activity Code");
	}

	private void updateData(PMaintenance feed, boolean isSelected, JTable table) {
		this.pMaintTileCell = feed;

		lblActualStartDate.setText("Start Time : " + formatter.format(pMaintTileCell.getActualStartTime()));
		lblActualFinishDate.setText("Completion Time : " + formatter.format(pMaintTileCell.getActualFinishTime()));
		lblActivityCode.setText("Activity Code : " + pMaintTileCell.getActivityCode());

		tile.add(lblActualStartDate,"wrap");
		tile.add(lblActualFinishDate,"wrap");
		tile.add(lblActivityCode, "wrap");

		if (isSelected) {
			tile.setBackground(table.getSelectionBackground());
			Component[] comps = tile.getComponents();
			for(int i = 0; i < comps.length; i++) {
				comps[i].setForeground(Color.WHITE);
			}

		} else {
			tile.setBackground(table.getSelectionForeground());
			Component[] comps = tile.getComponents();
			for(int i = 0;i < comps.length;i++){
				comps[i].setForeground(Color.BLACK);
			}
		}		
	}

	@Override
	public Object getCellEditorValue() {
		return null;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		
		PMaintenance feed = (PMaintenance)value;
		updateData(feed, true , table);
		return tile;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		//only renders component. So button will not be clicked
		PMaintenance feed = (PMaintenance)value;
		updateData(feed, isSelected, table);
		return tile;	
	}

}

