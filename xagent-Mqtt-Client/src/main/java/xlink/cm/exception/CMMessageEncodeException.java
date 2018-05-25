package xlink.cm.exception;

public class CMMessageEncodeException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CMMessageEncodeException(Throwable e) {
		super(e);
	}
	public CMMessageEncodeException(String msg) {
		super(msg);
	}
}
