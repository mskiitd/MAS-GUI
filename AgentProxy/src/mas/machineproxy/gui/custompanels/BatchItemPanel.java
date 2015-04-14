package mas.machineproxy.gui.custompanels;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import mas.jobproxy.Batch;
import net.miginfocom.swing.MigLayout;
import com.alee.extended.label.WebHotkeyLabel;

public class BatchItemPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel lblBatchIdHeading;
	private JLabel lblCustomerIdHeading;
	private JLabel lblBatchCountHeading;

	private WebHotkeyLabel lblBatchId;
	private WebHotkeyLabel lblCustomerId;
	private WebHotkeyLabel lblBatchCount;

	public BatchItemPanel(Batch b) {
		setLayout(new MigLayout());
		lblBatchIdHeading = new JLabel("Id: ");
		lblCustomerIdHeading = new JLabel("Customer: ");
		lblBatchCountHeading = new JLabel("Count : ");

		lblBatchId = new WebHotkeyLabel();
		lblCustomerId = new WebHotkeyLabel();
		lblBatchCount = new WebHotkeyLabel();
		if(b != null) {
			lblBatchId.setText(b.getBatchId());
			lblCustomerId.setText(b.getCustomerId());
			lblBatchCount.setText(String.valueOf(b.getBatchCount()));
		}

		add(lblBatchIdHeading);
		add(lblBatchId,"wrap");

		add(lblCustomerIdHeading);
		add(lblCustomerId,"wrap");

		add(lblBatchCountHeading);
		add(lblBatchCount,"wrap");

		setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
	}

	public void reset() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				lblBatchId.setText("");
				lblCustomerId.setText("");
				lblBatchCount.setText("");
			}
		});
	}

	public void setBatch(final Batch b) {
		if(b != null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					lblBatchId.setText(b.getBatchId());
					lblCustomerId.setText(b.getCustomerId());
					lblBatchCount.setText(String.valueOf(b.getBatchCount()));

				}
			});
		}
	}

}
