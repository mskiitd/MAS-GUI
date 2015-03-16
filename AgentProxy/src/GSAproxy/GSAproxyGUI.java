package GSAproxy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import uiconstants.Labels;
import mas.globalSchedulingproxy.GlobalSchedulingAgent;
import mas.job.job;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class GSAproxyGUI extends JFrame{

	private JScrollPane scroller;
	private JPanel queryJobsPanel;
	private JButton btnQueryJob;
	
	private JList<Object> jobsInSystemList;
	private Vector<Object> acceptedJobs;
	private listModel jobsQueryListModel;
	private GlobalSchedulingAgent gAgent;

	public GSAproxyGUI(GlobalSchedulingAgent gAgent) {
		
		this.gAgent = gAgent;
		this.queryJobsPanel = new JPanel(new MigLayout());
		this.btnQueryJob = new JButton(Labels.GSLabels.queryForJobLabel);

		queryJobsPanel = new JPanel(new BorderLayout());
		acceptedJobs = new Vector<Object>();
		
		jobsQueryListModel = new listModel();
		jobsInSystemList = new JList<Object>(jobsQueryListModel);
		jobsInSystemList.setCellRenderer(new customListRenderer());

		queryJobsPanel.add(jobsInSystemList);
		queryJobsPanel.add(btnQueryJob);
		
		this.scroller = new JScrollPane(queryJobsPanel);
		add(this.scroller);
		showGui();
	}
	
	public void addJobToList(job j) {
		acceptedJobs.addElement(j);
		revalidate();
	}
	
	public void completedJob(job j) {
		
	}

	private void showGui() {
		setPreferredSize(new Dimension(800,600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}
	
	class listModel extends AbstractListModel<Object> {

		@Override
		public Object getElementAt(int index) {
			return acceptedJobs.get(index);
		}

		@Override
		public int getSize() {
			return acceptedJobs.size();
		}
	}
	
	class customListRenderer extends JLabel  implements ListCellRenderer<Object> {

		private final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

		public customListRenderer() {
			setOpaque(true);
			setIconTextGap(12);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			job entry = (job) value;
			setText(entry.getJobNo() + "");
			if (isSelected) {
				setBackground(HIGHLIGHT_COLOR);
				setForeground(Color.white);
			} else {
				setBackground(Color.white);
				setForeground(Color.black);
			}
			return this;
		}
	}
}
