package mas.machineproxy.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JPanel;

import test.AgentStarter;
import mas.jobproxy.job;
import mas.util.TableUtil;

@SuppressWarnings("serial")
public class CustomJobQueue extends JPanel {

	public static Color backgroundColor = Color.LIGHT_GRAY;
	private int parentWidth;
	private int componentsInOneLine;
	private int numLines;
	private int remainderComps;
	private int arcWidth = 6;
	private int arcHeight = arcWidth;
	private int cWidth = 78;
	private int cHeight = 50;
	public static final int padding = 8;
	private List<job> jQueue;
	public static Color boxColor = new Color(123,104,238);
	public static Color stringColor = Color.BLACK;
	public static Color firstJobColor = new Color(46, 139, 87);

	public CustomJobQueue(List<job> q) {
		this.jQueue = q;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(backgroundColor);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		g.setColor(boxColor);
		g.setFont(TableUtil.font);
		
		parentWidth = getParent().getWidth();
		componentsInOneLine = parentWidth/(cWidth + 3*padding);

		if(componentsInOneLine != 0) {
			numLines = jQueue.size()/componentsInOneLine;
			remainderComps = jQueue.size()%componentsInOneLine;
		}
		else {
			numLines = 0;
			remainderComps = jQueue.size();
		}
//		System.out.println("reaminder : " + remainderComps + " , numline " + numLines +
//				" in a lien " + componentsInOneLine);
		int i ;

		for( i = 0; i < numLines; i++ ) {
			Graphics2D graphics = (Graphics2D) g.create();
			graphics.translate(padding, (i + 1)*padding);
			// draw components
			for( int c = 0 ; c < componentsInOneLine ; c++) {
				
				if(i == 0 && c == 0 ) {
					graphics.setColor(firstJobColor);
				}
				graphics.fillRoundRect(c * cWidth  , i*cHeight ,
						cWidth , cHeight, arcWidth, arcHeight);

				graphics.setColor(stringColor);
				graphics.drawString(jQueue.get(i*componentsInOneLine + c).getJobID() + "", c*cWidth + cWidth/2,
						i*cHeight + cHeight/2);

				graphics.setColor(boxColor);
				graphics.translate(padding, 0);
			}
			g.translate(0, padding);
			// draw a divider line
			graphics.drawLine( - padding*componentsInOneLine, (i+1) * cHeight + padding,
					parentWidth , (i+1) * cHeight + padding);
		}

		// draw remaining components
		g.translate(padding, (i+1)*padding);
		for ( int j = 0; j < remainderComps; j++ ) {
			
			if(j== 0 && numLines ==0 ) {
				g.setColor(firstJobColor);
			}
			
			g.fillRoundRect(j * cWidth  , i*cHeight ,
					cWidth, cHeight, arcWidth, arcHeight);

			g.setColor(stringColor);
			g.drawString(jQueue.get(i*componentsInOneLine + j).getJobID() + "", j*cWidth + cWidth/2,
					i*cHeight + cHeight/2);
			g.setColor(boxColor);
			g.translate(padding, 0);
		}
		setPreferredSize(new Dimension((cWidth + 2*padding)*componentsInOneLine,
				(cHeight + 2*padding)*numLines));
	}
}
