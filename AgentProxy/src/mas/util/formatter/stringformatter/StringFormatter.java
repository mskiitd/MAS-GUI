package mas.util.formatter.stringformatter;

import java.io.Serializable;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.DefaultFormatter;

/**
 * This class provides basic formatter services for operation id of job
 * 
 * @author Anand Prajapati
 */

public class StringFormatter extends DefaultFormatter implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** pattern used for verifying email addresses */
	private static final String IDPATTERN = "[\\dA-Z]+";
	
	/** precompiled version of the pattern */
	private static Pattern pattern = Pattern.compile(IDPATTERN,Pattern.CASE_INSENSITIVE);
	/**
	 *  Default constructor
	 */
	public StringFormatter() {

	}
	/**
	 * Converts the value object to a string
	 * @param value current value of the JFormattedTextField
	 * @return string representation
	 */
	public String valueToString(Object value ) {
		return value.toString();
	}
	/**
	 * Converts the String to an object, verifying its contents. Throws a ParseException
	 * if the string is not in valid id format.
	 * 
	 * @param text from the JFormattedTextField
	 * @return 
	 * @throws java.text.ParseException
	 */
	public Object stringToValue(String text) throws ParseException
	{
		Matcher m = pattern.matcher(text);
		if(!m.matches())
		{
			throw new ParseException(text,0);        
		}
		else
			return text;
	}
}
