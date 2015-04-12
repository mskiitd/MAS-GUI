package mas.machineproxy.behaviors;

import jade.core.behaviours.OneShotBehaviour;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import mas.machineproxy.Simulator;
import mas.machineproxy.parametrer.RootCause;
import net.miginfocom.swing.MigLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GetRootCauseDataBehavior extends OneShotBehaviour{

	private static final long serialVersionUID = 1L;
	private  ArrayList<ArrayList<JTextField> > txt_meanShiftParameters = null;
	private  ArrayList<ArrayList<JTextField> > txt_sdShiftParameters = null;
	private  ArrayList<ArrayList<JCheckBox> > chk_paramsAffected = null;
	private  int numRootCauses = 0;
	private int numMachineParameters = 0;
	private Logger log;
	public boolean autoInput = true;
	private Simulator machineSimulator;

	@Override
	public void action() {
		machineSimulator = (Simulator) getParent().getDataStore().get(Simulator.simulatorStoreName);
		log = LogManager.getLogger();
		numMachineParameters = machineSimulator.getMachineParameters().size();
		
		final JFrame win = new JFrame();
		final JPanel firstPanel = new JPanel (new MigLayout());

		JLabel lbl_numRootCauses = new JLabel ("Number of Root causes");
		final JTextField txt_numRootCauses = new JTextField("2",10);
		final JButton btn_submit= new JButton("Submit");

		win.setSize(600,500);
		win.setLayout(new MigLayout());
		win.setLocationRelativeTo(null);
		win.setVisible(true);

		firstPanel.add(lbl_numRootCauses);
		firstPanel.add(txt_numRootCauses);
		firstPanel.add(btn_submit);

		win.add(firstPanel);

		btn_submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton btn_rootcause = new JButton("Submit");

				if(e.getSource() == btn_submit) {
					numRootCauses = Integer.parseInt(txt_numRootCauses.getText());

					JPanel rootCausePanel = new JPanel (new MigLayout());
					JScrollPane scroller = new JScrollPane(rootCausePanel);

					win.remove(firstPanel);

					ArrayList<JLabel> lbl_rootCause = new ArrayList<JLabel>();
					txt_meanShiftParameters = new ArrayList<ArrayList<JTextField>>();
					txt_sdShiftParameters = new ArrayList<ArrayList<JTextField>>();
					chk_paramsAffected = new ArrayList<ArrayList<JCheckBox>>();

					Font headingFont = new Font("CalibriLight",Font.BOLD,20);

					for(int causeIndex = 0 ; causeIndex < numRootCauses ; causeIndex++ ) {

						lbl_rootCause.add(new JLabel("Root Cause "+ (causeIndex + 1)) );
						lbl_rootCause.get(causeIndex).setFont(headingFont);
						rootCausePanel.add(lbl_rootCause.get(causeIndex),"wrap");

						ArrayList<JCheckBox> tempChecklist = new ArrayList<JCheckBox>();
						ArrayList<JTextField> tempMeanShift = new ArrayList<JTextField>();
						ArrayList<JTextField> tempSdShift = new ArrayList<JTextField>();

						int paramIndex ;
						tempChecklist.clear();
						tempMeanShift.clear();
						tempSdShift.clear();

						for (paramIndex = 0 ; paramIndex < numMachineParameters ; paramIndex++) {

							tempChecklist.add(new JCheckBox(
									machineSimulator.getMachineParameters().get(paramIndex).getname()));
							tempMeanShift.add(new JTextField(
									"0",10) );
							tempSdShift.add(new JTextField(
									"1",10));
						}

						chk_paramsAffected.add(tempChecklist);
						txt_meanShiftParameters.add(tempMeanShift);
						txt_sdShiftParameters.add(tempSdShift);

						for (paramIndex = 0 ; paramIndex < numMachineParameters ; paramIndex++) {

							chk_paramsAffected.get(causeIndex).get(paramIndex).
							addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									for(int i = 0 ; i < numRootCauses ; i++) {
										for (int j=0 ; j < numMachineParameters ; j++) {
											if(e.getSource() == chk_paramsAffected.get(i).get(j)) {

												if(chk_paramsAffected.get(i).get(j).isSelected()){

													txt_meanShiftParameters.get(i).get(j).setVisible(true);
													txt_sdShiftParameters.get(i).get(j).setVisible(true);
												}
												else {
													txt_meanShiftParameters.get(i).get(j).setVisible(false);
													txt_sdShiftParameters.get(i).get(j).setVisible(false);
												}
											}
										}
									}
								}
							});

							rootCausePanel.add(chk_paramsAffected.get(causeIndex).get(paramIndex));
							rootCausePanel.add(txt_meanShiftParameters.get(causeIndex).get(paramIndex));
							rootCausePanel.add(txt_sdShiftParameters.get(causeIndex).get(paramIndex),"wrap");

							txt_meanShiftParameters.get(causeIndex).get(paramIndex).setVisible(false);
							txt_sdShiftParameters.get(causeIndex).get(paramIndex).setVisible(false);
						}
					}

					rootCausePanel.add(btn_rootcause);
					win.add(scroller);
					win.revalidate();
					btn_rootcause.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							for(int i= 0 ; i < numRootCauses ; i++) {
								ArrayList<RootCause> templist = new ArrayList<RootCause>();
								for (int j = 0 ; j < numMachineParameters ; j++) {
									if(chk_paramsAffected.get(i).get(j).isSelected()) {
										double a,b;
										a = Double.parseDouble(txt_meanShiftParameters.get(i).get(j).getText());
										b = Double.parseDouble(txt_sdShiftParameters.get(i).get(j).getText());
										RootCause temp = new RootCause(j,a,b);
										templist.add(temp);
									}
								}
								machineSimulator.addmParameterRootCause(templist);
							}
							win.dispose();
						}
					});
					
					if(autoInput)
						btn_rootcause.doClick();
				}
			}
		});
		
		if(autoInput)
			btn_submit.doClick();
//			log.info("Root cause input completed");
	}
}
