package xlink.cm.exception;

public class CMMessageParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CMMessageParseException(Throwable e) {
		super(e);
	}
	public CMMessageParseException(String msg) {
		super(msg);
	}
}
