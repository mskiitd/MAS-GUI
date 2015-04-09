package mas.maintenanceproxy.gui.preventive;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Format;
import java.text.SimpleDateFormat;

import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import mas.maintenanceproxy.classes.PMaintenance;
import mas.maintenanceproxy.gui.MaintenanceGUI;
import net.miginfocom.swing.MigLayout;


public class PrevMaintTableRenderer extends AbstractCellEditor implements TableCellEditor, TableCellRenderer{

	private static final long serialVersionUID = 1L;

	private JLabel lblActualStartDate,lblActualFinishDate;
	private JPanel tile;
	private PMaintenance pMaintTileCell;
	private final Format formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public PrevMaintTableRenderer(){
		tile=new JPanel(new MigLayout("",
				"[]10",
				""
				));
		lblActualStartDate = new JLabel();
		lblActualStartDate.setName("Actual Start Date");
		lblActualFinishDate = new JLabel();
		lblActualFinishDate.setName("Actual Completion Date");
	}

	private void updateData(PMaintenance feed, boolean isSelected, JTable table) {
		this.pMaintTileCell = feed;

		lblActualStartDate.setText("Actual Start Date : " + pMaintTileCell.getActualStartTime());
		lblActualFinishDate.setText("Actual Completion Date : "+formatter.format(pMaintTileCell.getActualFinishTime()));

		tile.add(lblActualStartDate,"wrap");
		tile.add(lblActualFinishDate,"wrap");

		tile.addMouseListener ( new MouseAdapter() {
			@Override
			public void mousePressed ( final MouseEvent e ) {
				MaintenanceGUI.unloadPrevMaintSchedule();
				MaintenanceGUI.createPrevMaintPanel(pMaintTileCell);
			}
		} );

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
		
		MaintenanceGUI.unloadPrevMaintSchedule();
		PMaintenance feed = (PMaintenance)value;

		updateData(feed, true, table);
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

