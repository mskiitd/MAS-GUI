package mas.util;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.alee.laf.WebLookAndFeel;

public class TableUtil {
	public static Font font;
	public static Font headings;

	public static void setUIFont (FontUIResource f){
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get (key);
			if (value != null && value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put (key, f);
		}
	} 

	public static void loadFont() {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				try {
					File font_file = new File("resources/Inconsolata.otf");
					Font iconSolataFont = Font.createFont(Font.TRUETYPE_FONT, font_file);
					ge.registerFont(iconSolataFont);
					
					font = iconSolataFont.deriveFont(Font.PLAIN, 18f);
					headings = iconSolataFont.deriveFont(Font.BOLD, 20f);
					
					WebLookAndFeel.install ();
					//					for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
					//						if ("Nimbus".equals(info.getName())) {
					//							UIManager.setLookAndFeel(info.getClassName());
					//							break;
					//						}
					//					}
					TableUtil.setUIFont (new FontUIResource(font));
				} catch (FontFormatException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		} );
	}
	
	public static boolean checkIfExists(JComponent parent,JComponent child) {
		
		if(child.getParent() == null)
			return false;
		
		return child.getParent().equals(parent);
	}

	public static void setColumnWidths(JTable table) {
		TableColumnModel columnModel = table.getColumnModel();

		for (int col = 0; col < table.getColumnCount(); col++) {
			int maxWidth = 0;

			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer rend = table.getCellRenderer(row, col);
				Object value = table.getValueAt(row, col);
				Component comp = rend.getTableCellRendererComponent(table, value, false, false, row, col);
				maxWidth = Math.max(comp.getPreferredSize().width, maxWidth);
			}
			TableColumn column = columnModel.getColumn(col);
			TableCellRenderer headerRenderer = column.getHeaderRenderer();
			if (headerRenderer == null) {
				headerRenderer = table.getTableHeader().getDefaultRenderer();
			}
			Object headerValue = column.getHeaderValue();
			Component headerComp = headerRenderer.getTableCellRendererComponent(table, headerValue, false, false, 0, col);
			maxWidth = Math.max(maxWidth, headerComp.getPreferredSize().width);
			// note some extra padding
			//IntercellSpacing * 2 + 2 * 2 pixel instead of taking this value from Borders
			column.setPreferredWidth(maxWidth + 6);
		}

		table.setPreferredScrollableViewportSize(table.getPreferredSize());
	}
}
