package mas.machineproxy.gui;

import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class CustomButton extends JButton{

	private static final long serialVersionUID = 1L;
	
	public CustomButton() {
		super();
	}
	
	public CustomButton(ImageIcon icon) {
		super(icon);
	}

	@Override
	protected void paintComponent(Graphics g) {
		setOpaque( false );
		super.paintComponent( g );
		setOpaque( true );
	}
}

