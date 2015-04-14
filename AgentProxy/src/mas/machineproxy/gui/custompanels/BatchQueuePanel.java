package mas.machineproxy.gui.custompanels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import mas.jobproxy.Batch;

public class BatchQueuePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private ArrayList<Batch> batchQueue;

	private int parentWidth;
	public static final int padding = 10;

	public static Color backgroundColor = Color.LIGHT_GRAY;
	public static Color boxColor = new Color(123,104,238);
	public static Color stringColor = Color.BLACK;
	public static Color firstJobColor = new Color(46, 139, 87);

	public BatchQueuePanel(ArrayList<Batch> batchQ) {
		if(batchQ != null) {
			this.batchQueue = batchQ;
		} else {
			this.batchQueue = new ArrayList<Batch>();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(backgroundColor);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		removeAll();
		System.out.println(batchQueue.size());
		for(int i = 0; i < batchQueue.size() ; i++) {
			add(new BatchItemPanel(batchQueue.get(i)));
		}
		setPreferredSize(new Dimension(parentWidth, getParent().getWidth()));
	}
}
