package mas.globalSchedulingproxy.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import mas.jobproxy.job;

public class NegotiationJobTileRenderer extends AbstractTableModel implements TableModel {

	List<JobTile> jobTiles=null; 
	
	public NegotiationJobTileRenderer(){
		this.jobTiles=new ArrayList<JobTile>();
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
		return "<html><b>BIDS</b></html>";
	}

	@Override
	public int getRowCount() {
		if(jobTiles==null){
			return 0;
		}
		else{
			return jobTiles.size();	
		}
	}

	@Override
	public Object getValueAt(int jobTileIndex, int columnIndex) {
		if(jobTiles==null){
			return null;
		}
		else{
			return jobTiles.get(jobTileIndex);
		}
	}

	@Override
	public boolean isCellEditable(int columnIndex, int rowIndex) {
		return true;
	}


	public void addRow(JobTile[] tiles){
		for(int i=0;i<tiles.length;i++){
			jobTiles.add(tiles[i]);
			super.fireTableRowsInserted(getRowCount()+1, getRowCount()+1);
		}
		
		
	}

	public void removeJob(job j) {
		int count=0;
		while(count<jobTiles.size() && (j.getJobID()!=jobTiles.get(count).getBatchID())){
			count++;
			
		}
		if(count!=jobTiles.size()){
			jobTiles.remove(count);
			super.fireTableRowsDeleted(count+1, count+2);
		}
		}
	}
