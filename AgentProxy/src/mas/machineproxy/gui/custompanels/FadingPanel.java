package mas.machineproxy.gui.custompanels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import net.miginfocom.swing.MigLayout;

public class FadingPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel lblCurrOp;
	private JLabel lblCurrJob;
	
	private float alpha;
	private Color bgColor;
	private float delta = 0.01f;
	
	public FadingPanel() {
		lblCurrJob = new JLabel();
		lblCurrOp = new JLabel();

		setLayout(new MigLayout());
		add(lblCurrJob,"wrap");
		add(lblCurrOp);

		bgColor = getBackground();
		alpha = bgColor.getAlpha();
		
//		new Timer(500,animator).start();
	}

	ActionListener animator = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("running " + alpha);
			alpha--;
			if(alpha < 0) {
				alpha = bgColor.getAlpha();
			}
			setBackground(new Color( bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), alpha));
		}
	};
	
	/**
	 * Runs on EDT
	 */
	public void setCurrentOperation(String jobNo, String opId) {
		final String jNum = jobNo;
		final String oId = opId;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				lblCurrJob.setText("Job No : " + jNum);
				lblCurrOp.setText("Operation : " + oId);
			}
		});

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	/**
	 * on edt
	 */
	public void reset() {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				lblCurrJob.setText("");
				lblCurrOp.setText("");
			}
		});
	}
}
