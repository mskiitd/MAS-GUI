package mas.machineproxy.gui.custompanels;

import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import com.alee.extended.label.WebHotkeyLabel;

public class JobOperationItemPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private String opInfo;
	private String CustomerId;
	
	private JLabel lblCustomerIdHeading;
	private JLabel lblOpIdHeading;
	private WebHotkeyLabel lblOperationId;
	private WebHotkeyLabel lblCustomerId;

	public JobOperationItemPanel() {
		lblCustomerIdHeading = new JLabel("Customer Id :");
		lblOpIdHeading = new JLabel("Operation Id : ");
		lblCustomerId = new WebHotkeyLabel();
		lblOperationId = new WebHotkeyLabel();
		
		setLayout(new MigLayout("","10","10"));
		add(lblCustomerIdHeading);
		add(lblCustomerId,"wrap");
		
		add(lblOpIdHeading);
		add(lblOperationId,"wrap");
		setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
	}

	public JobOperationItemPanel(String job,String cust) {
		this.setDisplay(job, cust);
	}

	public void setDisplay(String j, String cust) {
		this.opInfo = j;
		this.CustomerId = cust;
		lblCustomerId.setText(this.CustomerId);
		lblOperationId.setText(this.opInfo);
	}

}

