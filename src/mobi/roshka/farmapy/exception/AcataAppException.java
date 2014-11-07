package mobi.roshka.farmapy.exception;

public class AcataAppException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AcataAppException(){
		
	}
	public AcataAppException(String message){
		super(message);
	}

	public AcataAppException(String message, Throwable t){
		super(message, t);
	}

}
