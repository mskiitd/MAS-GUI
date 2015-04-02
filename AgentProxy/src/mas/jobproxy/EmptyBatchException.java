package mas.jobproxy;

public class EmptyBatchException extends Exception{

	private static final long serialVersionUID = 1L;

	  public EmptyBatchException(String message) {
	        super(message);
	    }
	
	@Override
	public String getMessage() {
		return super.getMessage();
	}

}
