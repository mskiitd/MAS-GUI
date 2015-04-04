package mas.globalSchedulingproxy.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alee.log.Log;

import mas.jobproxy.Batch;
import mas.jobproxy.job;

public class CurrentJobTileRenderer extends AbstractTableModel implements TableModel {

	List<JobTile> batchTiles=null;
	private Logger log=LogManager.getLogger(); 
	
	public CurrentJobTileRenderer(){
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
			batchTiles.add(new JobTile(b));
			super.fireTableRowsInserted(getRowCount()+1, getRowCount()+1);
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
			super.fireTableRowsDeleted(count, count);
		}
		}
	}
