package mas.machineproxy.gui.custompanels;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JPanel;
import mas.util.TableUtil;
import net.miginfocom.swing.MigLayout;

public class FadingPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel lblCurrOp;
	private JLabel lblCurrJob;

	public FadingPanel() {
		lblCurrJob = new JLabel();
		lblCurrOp = new JLabel();

		lblCurrJob.setFont(TableUtil.headings);
		lblCurrOp.setFont(TableUtil.headings);

		setLayout(new MigLayout());
		add(lblCurrJob,"wrap");
		add(lblCurrOp);
	}

	public void setCurrentOperation(String jobNo, String opId) {
		lblCurrJob.setText("Current Job No : " + jobNo);
		lblCurrOp.setText("Current Operation : " + opId);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Color bgColor = getBackground();

		for(int alpha = bgColor.getAlpha(); alpha >= 0; alpha--) {
			setBackground(new Color(
					bgColor.getRed(),
					bgColor.getGreen(),
					bgColor.getBlue(),
					alpha));
		}
	}

	public void reset() {
		lblCurrJob.setText("");
		lblCurrOp.setText("");
	}
}
