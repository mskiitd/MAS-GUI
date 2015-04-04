package mas.globalSchedulingproxy.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import mas.jobproxy.job;
import net.miginfocom.swing.MigLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alee.extended.menu.DynamicMenuType;
import com.alee.extended.menu.WebDynamicMenu;
import com.alee.extended.menu.WebDynamicMenuItem;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.text.WebTextField;
import com.alee.log.Log;
import com.alee.utils.SwingUtils;


public class NegotitationJobTileCell extends AbstractCellEditor implements TableCellEditor, TableCellRenderer{
//same as cyrrentJobTileCell without dynamic menu
	private JLabel jobID,dueDate,startDate,priorityText,priorityNo;
	private JPanel tile;
//	private JButton more;
	private JobTile jobTileInCell;
	private String PriorityNoText;
	private final Format formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
	public NegotitationJobTileCell(){
		tile=new JPanel(new MigLayout("",
				"[]10",
				""
				));
		jobID=new JLabel();
		jobID.setName("JobID");
		dueDate=new JLabel();
		dueDate.setName("Due date by customer");
		startDate=new JLabel();
		startDate.setName("Start date by customer");
		
		priorityText=new JLabel();
		priorityText.setName("prioityText");
		priorityNo=new JLabel();
		priorityNo.setName("PriorityNoOfJob");

	}
	
	private void updateData(JobTile feed, boolean isSelected, JTable table) {
		this.jobTileInCell = feed;
		
		jobID.setText("Job ID : "+jobTileInCell.getBatchID());
		dueDate.setText("Due date : "+formatter.format(jobTileInCell.getCustDueDate()));
		startDate.setText("Start Date : "+formatter.format(jobTileInCell.getCustStartDate()));
//		more.setText("more");
		priorityText.setText("Priority : "+ Integer.toString((int)jobTileInCell.getPriority()));

		PriorityNoText="<html><b>"+Integer.toString((int)jobTileInCell.getPriority())+"</b></html>";
		priorityNo.setText(PriorityNoText);
		
		tile.add(jobID,"wrap");
//		tile.add(more, "align right, wrap");
		tile.add(dueDate,"wrap");
		tile.add(startDate,"wrap");
		tile.add(priorityText);
//		tile.add(priorityNo);
		
		
	   tile.addMouseListener ( new MouseAdapter ()
        {
            @Override
            public void mousePressed ( final MouseEvent e )
            {
            	WebLafGSA.unloadNegotiationInfoPanel();
     			WebLafGSA.creatNegotiationInfoPanel(jobTileInCell);
            }
        }
         );
		 
		
		if (isSelected) {
			tile.setBackground(table.getSelectionBackground());
			Component[] comps=tile.getComponents();
			for(int i=0;i<comps.length;i++){
					comps[i].setForeground(Color.WHITE);
			}
			
			
		}else{
			tile.setBackground(table.getSelectionForeground());
			Component[] comps=tile.getComponents();
			for(int i=0;i<comps.length;i++){
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
		//returns component in editatble format. So button will get clicked
		WebLafGSA.unloadCurrentJobInfoPanel();
		JobTile feed = (JobTile)value;
		
		updateData(feed, true, table);//updateData(feed, isSelected, table); is wrong
		//as isSelected=false initially. So, even if u click, background colour will not change
//		table.setRowHeight(tile.getHeight()); //table row height changed as per height of job tile
		return tile;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		//only renders component. So button will not be clicked
		JobTile feed = (JobTile)value;
		updateData(feed, isSelected, table);
		return tile;	
		}

}

