package mas.globalSchedulingproxy.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.jobproxy.Batch;

public class NegotiationJobTileTableModel extends AbstractTableModel implements TableModel {

	private static final long serialVersionUID = 1L;

	List<JobTile> batchTiles;
	private Logger log;

	public NegotiationJobTileTableModel() {
		this.batchTiles = new ArrayList<JobTile>();
		log = LogManager.getLogger(); 
	}

	@Override
	public Class<?> getColumnClass(int index) {
		return JobTile.class ;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public String getColumnName(int arg0) {
		return "<html><b>NEGOTIATION BIDS</b></html>";
	}

	@Override
	public int getRowCount() {
		return batchTiles.size();	
	}

	@Override
	public Object getValueAt(int jobTileIndex, int columnIndex) {
		return batchTiles.get(jobTileIndex);
	}

	@Override
	public boolean isCellEditable(int columnIndex, int rowIndex) {
		return true;
	}

	public void addBatch(Batch b) {
		batchTiles.add(new JobTile(b));
		super.fireTableRowsInserted(0, getRowCount()-1);
	}

	public void removeJob(Batch j) {
		log.info("fired remove job");
		int count=0;
		while(count<batchTiles.size() && (!j.getBatchId().equals(batchTiles.get(count).getBatchID()))){
			count++;
		}
		log.info("count="+count);
		if(count!=batchTiles.size()){
			batchTiles.remove(count);
			super.fireTableRowsDeleted(0, count);
		}
	}
}
