package mas.machineproxy.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import javax.swing.JPanel;

import mas.jobproxy.Batch;
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
	private int cWidth = 120;
	private int cHeight = 80;
	public static final int padding = 10;
	private List<Batch> jQueue;
	public static Color boxColor = Color.WHITE;
	public static Color stringColor = Color.BLACK;
	public static Color firstJobColor = new Color(224, 224, 224);

	private Font font;
	private Font bfont;
	private FontRenderContext frc;

	private BasicStroke stoke;

	public CustomJobQueue(List<Batch> q) {
		this.jQueue = q;
		this.stoke = new BasicStroke(4,1,1);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(backgroundColor);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		g.setColor(boxColor);
		g.setFont(TableUtil.font);

		parentWidth = getWidth();
		componentsInOneLine = parentWidth/(cWidth + 3 * padding);

		if(componentsInOneLine != 0) {
			numLines = jQueue.size()/componentsInOneLine;
			remainderComps = jQueue.size()%componentsInOneLine;
		}
		else {
			numLines = 0;
			remainderComps = jQueue.size();
		}
		int i ;

		Graphics2D graphics = (Graphics2D) g.create();
		graphics.setStroke(this.stoke);

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		font = getFont();
		bfont = font.deriveFont(Font.BOLD, 14f);
		frc = graphics.getFontRenderContext();

		for( i = 0; i < numLines; i++ ) {

			Graphics2D graphicsClone = (Graphics2D) graphics.create();
			graphicsClone.translate(padding, (i + 1)*padding);
			graphicsClone.setColor(boxColor);
			// draw components
			for( int c = 0 ; c < componentsInOneLine ; c++) {

				if(i == 0 && c == 0 ) {
					graphicsClone.setColor(firstJobColor);
				}
				graphicsClone.fillRect(c * cWidth  , i*cHeight ,
						cWidth , cHeight); //arcWidth, arcHeight);

				graphicsClone.setColor(stringColor);
				graphicsClone.draw(new RoundRectangle2D.Double(c*cWidth, i*cHeight,
						cWidth, cHeight, arcWidth, arcHeight));

				//------Get the string to draw and draw 
				Batch b = jQueue.get(i*componentsInOneLine + c);

				String s = "ID : " + b.getBatchId()  + "\n" +
						"Count : " + b.getBatchCount() + "\n" +
						"No : " + b.getBatchNumber();

				int textwidth = (int)(font.getStringBounds(s, frc).getWidth());
				int textheight = (int)(font.getStringBounds(s, frc).getHeight());

				drawString(graphicsClone, s, c*cWidth + cWidth/2 - textwidth/6, i*cHeight + cHeight/2 - 2*textheight);

				graphicsClone.setColor(boxColor);
				graphicsClone.translate(padding, 0);
			}
			graphics.translate(0, padding);
			// draw a divider line
			graphics.setColor(stringColor);
			graphicsClone.drawLine( - padding*componentsInOneLine, (i+1) * cHeight + padding,
					parentWidth , (i+1) * cHeight + padding);
		}

		// draw remaining components
		graphics.translate(padding, (i+1)*padding);
		graphics.setColor(boxColor);
		for ( int j = 0; j < remainderComps; j++ ) {

			if(j == 0 && numLines == 0 ) {
				g.setColor(firstJobColor);
			}
			graphics.fillRoundRect(j * cWidth  , i*cHeight ,
					cWidth, cHeight, arcWidth, arcHeight);

			graphics.setColor(stringColor);
			graphics.draw(new RoundRectangle2D.Double(j * cWidth  , i*cHeight ,
					cWidth, cHeight, arcWidth, arcHeight));

			//------Get the string to draw
			Batch b = jQueue.get(i*componentsInOneLine + j);

			String s = "ID : " + b.getBatchId()  + "\n" +
					"Count : " + b.getBatchCount() + "\n" +
					"No : " + b.getBatchNumber();

			int textwidth = (int)(font.getStringBounds(s, frc).getWidth());
			int textheight = (int)(font.getStringBounds(s, frc).getHeight());

			drawString(graphics, s, j*cWidth + cWidth/2 - textwidth/6, i*cHeight + cHeight/2 - 2*textheight );

			graphics.setColor(boxColor);
			graphics.translate(padding, 0);
		}
		setPreferredSize(new Dimension((cWidth + 2*padding)*componentsInOneLine,
				(cHeight + 2*padding)*numLines));
	}

	private void drawString(Graphics graphics, String text, int x, int y) {
		graphics.setFont(bfont);
		for (String line : text.split("\n")) {
			graphics.drawString(line, x, y += graphics.getFontMetrics().getHeight());
		}
		graphics.setFont(font);
	}

}
