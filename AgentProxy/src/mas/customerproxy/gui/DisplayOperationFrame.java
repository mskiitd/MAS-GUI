package mas.customerproxy.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JLabel;
import mas.jobproxy.job;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class DisplayOperationFrame extends JFrame{

	
	public DisplayOperationFrame(job passedJob, int numOps, job populatingJob) {
		setLayout(new MigLayout("","10","10"));
		
		for(int i = 0 ; i < numOps; i++) {
			JLabel opTitle = new JLabel("operation #" + i + " : ");
			add(opTitle);
			
			JLabel opNamelbl =new JLabel(passedJob.getOperations().get(i).
					getJobOperationType());
			add(opNamelbl,"wrap");
		}
		
		setTitle("Operation details");
//		setPreferredSize(new Dimension(600,500));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}
}
