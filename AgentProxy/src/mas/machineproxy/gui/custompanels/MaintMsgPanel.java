package mas.machineproxy.gui.custompanels;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MaintMsgPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private Image img;
	
	private float alpha = 1f;
	private static float DELTA = -0.1f;
	private static Timer timer;

	public MaintMsgPanel() {
		try {
			img = ImageIO.read(new File("resources/maintmsg.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		timer = new Timer(200, this);
		timer.setInitialDelay(1000);
		timer.start();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(AlphaComposite.SrcOver.derive(alpha) );

		int h = img.getHeight(null);
		int w = img.getWidth(null);

		// Scale Horizontally:
		if ( w > this.getWidth() ) {
			img = img.getScaledInstance( getWidth(), -1, Image.SCALE_DEFAULT );
			h = img.getHeight(null);
		}

		// Scale Vertically:
		if ( h > this.getHeight() ) {
			img = img.getScaledInstance( -1, getHeight(), Image.SCALE_DEFAULT );
		}

		int imgX = img.getWidth(null);
		int imgY = img.getHeight(null);
		g2d.drawImage(img, (getWidth() - imgX) / 2, (getHeight() - imgY) / 2, imgX, imgY, null);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		alpha += DELTA;
		if (alpha < 0) {
			DELTA = -1*DELTA;
			alpha += DELTA;
		} 
		if(alpha > 1) {
			DELTA = -1*DELTA;
			alpha += DELTA;
		}
		repaint();
	}

}
