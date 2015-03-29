package mas.machineproxy.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import javax.swing.JComponent;
import mas.jobproxy.job;

public class JobOperationList extends JComponent {

	private static final long serialVersionUID = 1L;

	public static Color backgroundColor = Color.white;
	private int parentWidth;
	private int cHeight = 120;
	public static final int xpadding = 1;
	public static final int ypadding = 3;

	private ArrayList<job> jQueue;
	public static Color boxColor = new Color(192,192,192);
	public static Color stringColor = Color.BLACK;

	public static Color selectedColor = new Color(128, 128, 128);

	private ArrayList<JobOperationItem> items;

	public JobOperationList(ArrayList<job> q) {
		items = new ArrayList<JobOperationItem>();
		this.jQueue = q;

		for(int i = 0; i < q.size(); i++ ) {
			JobOperationItem item = new JobOperationItem(q.get(i));
			add(item);
			items.add(item);
		}
	}

	public Object objectAtPoint(Point p) {
		double px = p.getX();
		double py = p.getY();

		int idx = -1;

		idx = (int) (py/(cHeight + ypadding)) ;
		if(idx != -1 && px >= 0 &&
				idx < jQueue.size() )
			return jQueue.get(idx);
		return null;
	}

	@Override
	protected void paintComponent(Graphics g) {

		g.setColor(backgroundColor);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		for(int i = 0; i < items.size(); i++ ) {

			Graphics2D graphics = (Graphics2D) g.create();

			graphics.translate(xpadding, i * (cHeight + ypadding) );
			items.get(i).paintComponent(graphics);

			graphics.setColor(Color.black);
			graphics.translate(0, ypadding);
			graphics.drawLine(-xpadding, (i + 1)*cHeight, parentWidth, (i + 1)*cHeight);
		}

		setPreferredSize(new Dimension( parentWidth + 2*xpadding,
				(cHeight + 2*ypadding)*items.size()));
	}

}
