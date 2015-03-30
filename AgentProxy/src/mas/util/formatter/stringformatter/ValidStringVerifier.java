package mas.util.formatter.stringformatter;

import java.awt.Color;
import java.awt.Toolkit;

import javax.swing.InputVerifier;
import javax.swing.JFormattedTextField;

public class ValidStringVerifier extends InputVerifier {

	private static final Color INVALID_COLOR = Color.red;
	private static final Color VALID_COLOR = Color.black; 
	static private StringFormatter ef = new StringFormatter();

	public ValidStringVerifier() {

	}
	/**
	 * Check the contents to see if it is a valid email address
	 * @param jc the component to be checked
	 * @return true if valid, false if not
	 */
	public boolean verify(javax.swing.JComponent jc)
	{

		try {
			JFormattedTextField ftf = (JFormattedTextField)jc;
			String operationId = (String)ef.stringToValue(ftf.getText());
			jc.setForeground(VALID_COLOR); 
			return true;
		}
		catch(Exception e)
		{
			Toolkit.getDefaultToolkit().beep();
			jc.setForeground(INVALID_COLOR);
			return false;
		}

	}
}
