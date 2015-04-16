package mas.globalSchedulingproxy.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Format;
import java.text.SimpleDateFormat;
import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import mas.globalSchedulingproxy.goal.QueryJobGoal;
import mas.util.ID;
import net.miginfocom.swing.MigLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alee.extended.menu.DynamicMenuType;
import com.alee.extended.menu.WebDynamicMenu;
import com.alee.extended.menu.WebDynamicMenuItem;
import com.alee.log.Log;
import com.alee.utils.SwingUtils;


public class CurrentJobTableRenderer extends AbstractCellEditor implements TableCellEditor, TableCellRenderer{

	private static final long serialVersionUID = 1L;
	private JLabel batchID,dueDate,/*startDate,*/priorityText,batchNo;
	private JPanel tile;
//	private JButton more;
	private JobTile jobTileInCell;
	private String PriorityNoText;
	private final Format formatter = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
	protected Logger log=LogManager.getLogger();
    
	public CurrentJobTableRenderer(){
		tile=new JPanel(new MigLayout("",
				"[]10",
				""
				));
		batchID=new JLabel();
		batchID.setName("JobID");
		dueDate=new JLabel();
		dueDate.setName("Due date by customer");
		/*startDate=new JLabel();
		startDate.setName("Start date by customer");*/
		
		priorityText=new JLabel();
		priorityText.setName("prioityText");
		batchNo=new JLabel();
		batchNo.setName("BatchNoOfJob");

	}
	
	private void updateData(JobTile feed, boolean isSelected, JTable table) {
		this.jobTileInCell = feed;
		
		batchID.setText("Batch ID : "+jobTileInCell.getBatchID());
		dueDate.setText("Due date : "+formatter.format(jobTileInCell.getCustDueDate()));
//		startDate.setText("Start Date : "+formatter.format(jobTileInCell.getCustStartDate()));
//		more.setText("more");
		priorityText.setText("Priority : "+ Integer.toString((int)jobTileInCell.getPriority()));

		PriorityNoText="<html><b>"+Integer.toString((int)jobTileInCell.getPriority())+"</b></html>";
		batchNo.setText("Batch No.: "+jobTileInCell.getBatch().getBatchNumber());
		
		tile.add(batchID);
//		tile.add(more, "align right, wrap");
		tile.add(batchNo,"wrap");
		tile.add(dueDate,"wrap");
//		tile.add(startDate);
		tile.add(priorityText);
		
		
		
	   tile.addMouseListener ( new MouseAdapter ()
        {
            

			@Override
            public void mousePressed ( final MouseEvent e )
            {
             if(SwingUtils.isRightMouseButton(e)){
            	createMenu (jobTileInCell).showMenu ( e.getComponent (), e.getPoint ());
            	log.info(jobTileInCell.getBatch().getBatchNumber());
             }
             else{
     			WebLafGSA.unloadCurrentJobInfoPanel();
     			WebLafGSA.createCurrentJobInfoPanel(jobTileInCell);
             }
                    
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
		log.info(isSelected);
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


	  protected WebDynamicMenu createMenu (final JobTile jobTileInsideCell) 
	  //don't why it was needed to be declared as final
	    {
	        final WebDynamicMenu menu = new WebDynamicMenu ();
	        menu.setType ( ( DynamicMenuType ) DynamicMenuType.roll );
	        menu.setHideType ( ( DynamicMenuType ) DynamicMenuType.roll );
	        menu.setRadius (50);
	        menu.setStepProgress ( 0.07f );

	            final ImageIcon MoreIcon =  new ImageIcon("resources/changeDueDate.png","Change Batch Due Date");
	            final ActionListener moreAction = new ActionListener ()
	            {
	                @Override
	                public void actionPerformed ( final ActionEvent e )
	                {
	                	WebLafGSA.unloadCurrentJobInfoPanel();
	                	WebLafGSA.getGSA().addGoal(new QueryJobGoal(jobTileInsideCell.getBatch(),
	                			ID.GlobalScheduler.requestType.changeDueDate));
	                	Log.info("added change due date goal");
	                }
	            };
	            final WebDynamicMenuItem moreIconItem = new WebDynamicMenuItem ( MoreIcon, moreAction );
	            moreIconItem.setMargin ( new Insets ( 8, 8, 8, 8 ) );
	            menu.addItem ( moreIconItem );
	            
	            final ImageIcon QueryIcon=new ImageIcon("resources/query.png","Query Batch");
	            final ActionListener QueryAction = new ActionListener ()
	            {
	                @Override
	                public void actionPerformed ( final ActionEvent e )
	                {
	                	WebLafGSA.unloadCurrentJobInfoPanel();
	                	WebLafGSA.getGSA().addGoal(new QueryJobGoal(jobTileInsideCell.getBatch(),
	                			ID.GlobalScheduler.requestType.currentStatus));
	                	Log.info("added query goal");
	                }
	            };
	            final WebDynamicMenuItem QueryItem = new WebDynamicMenuItem ( QueryIcon, QueryAction );
	            QueryItem.setMargin ( new Insets ( 8, 8, 8, 8 ) );
	            menu.addItem ( QueryItem );
	        
	        final ImageIcon CancelJobIcon=new ImageIcon("resources/cancel.png","Cancel Batch");
            final ActionListener CancelJobAction = new ActionListener ()
            {
                @Override
                public void actionPerformed ( final ActionEvent e )
                {
                	WebLafGSA.unloadCurrentJobInfoPanel();
                	WebLafGSA.getGSA().addGoal(new QueryJobGoal(jobTileInsideCell.getBatch(),
                			ID.GlobalScheduler.requestType.cancelBatch));
                	Log.info("added cancel batch goal");
                }
            };
            final WebDynamicMenuItem cancelJobItem = new WebDynamicMenuItem ( CancelJobIcon, CancelJobAction );
            cancelJobItem.setMargin ( new Insets ( 8, 8, 8, 8 ) );
            menu.addItem ( cancelJobItem );
	            
	        return menu;
	    }


}
