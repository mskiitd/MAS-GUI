package mas.maintenanceproxy.gui.preventive;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.maintenanceproxy.classes.PMaintenance;

public class PrevMaintTableModel extends AbstractTableModel implements TableModel {

	private static final long serialVersionUID = 1L;
	ArrayList<PMaintenance> maintSchedules;
	private Logger log; 

	public PrevMaintTableModel() {
		log = LogManager.getLogger();
		this.maintSchedules = new ArrayList<PMaintenance>();
	}

	@Override
	public Class<?> getColumnClass(int index) {
		return PMaintenance.class ;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public String getColumnName(int arg0) {
		return "<html><h2>Completed Preventive Maintenance</h2></html>";
	}

	@Override
	public int getRowCount() {
		return maintSchedules.size();	
	}

	@Override
	public Object getValueAt(int tileIndex, int columnIndex) {
		return maintSchedules.get(tileIndex);
	}

	@Override
	public boolean isCellEditable(int columnIndex, int rowIndex) {
		return true;
	}

	public void addMaintJob(PMaintenance pm){
		maintSchedules.add(pm);

		super.fireTableRowsInserted(0, getRowCount()-1);
		super.fireTableCellUpdated(0, getRowCount()-1);
		super.fireTableDataChanged();
	}

}
