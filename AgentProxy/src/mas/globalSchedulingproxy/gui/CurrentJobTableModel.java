package mas.globalSchedulingproxy.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mas.jobproxy.Batch;

/*
 * Table model for current job table shown as table on left side
 */
public class CurrentJobTableModel extends AbstractTableModel implements TableModel {

	private static final long serialVersionUID = 1L;
	List<JobTile> batchTiles=null;
	private Logger log=LogManager.getLogger(); 

	public CurrentJobTableModel(){
		this.batchTiles=new ArrayList<JobTile>();
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
		return "<html><b>JOBS</b></html>";
	}

	@Override
	public int getRowCount() {
		if(batchTiles==null){
			return 0;
		}
		else{
			return batchTiles.size();	
		}
	}

	@Override
	public Object getValueAt(int jobTileIndex, int columnIndex) {
		if(batchTiles==null){
			return null;
		}
		else{
			return batchTiles.get(jobTileIndex);
		}
	}

	@Override
	public boolean isCellEditable(int columnIndex, int rowIndex) {
		return true;
	}


	public void addBatch(Batch b){
		JobTile jt=new JobTile(b);
		batchTiles.add(jt);

		super.fireTableRowsInserted(0, getRowCount()-1);
		super.fireTableCellUpdated(0, getRowCount()-1);
		super.fireTableDataChanged();
	}

	public void removeJob(Batch j) {
		int count=0;
		int tablesize=batchTiles.size()-1;

		for(int index=0;index<batchTiles.size();index++){
			log.info(batchTiles.get(index).getBatch().getBatchNumber());
		}

		while(count<batchTiles.size() && (j.getBatchNumber()!=(batchTiles.get(count)
				.getBatch().getBatchNumber()))){
			count++;
		}
		if(count!=batchTiles.size()){
			batchTiles.remove(count);
			super.fireTableRowsDeleted(0, tablesize);
			super.fireTableCellUpdated(0, getRowCount()-1);
			super.fireTableDataChanged();
		}
	}
}
