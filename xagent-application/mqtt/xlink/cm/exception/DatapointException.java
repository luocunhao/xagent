package xlink.cm.exception;

public class DatapointException extends Exception {

	public enum Type {
		undefined,
		overIndex,	// 过界
		parseError,	// 解析失败
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Type exceptionType = Type.undefined;
	
	public DatapointException(Type type) {		
		exceptionType = type;
	}
	
	public Type getType() {
		return this.exceptionType;
	}

}
