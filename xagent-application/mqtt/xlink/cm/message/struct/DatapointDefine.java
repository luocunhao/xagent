package xlink.cm.message.struct;

import xlink.cm.message.type.DatapointType;

public class DatapointDefine {
	private final int index;
	private final DatapointType type;
	public DatapointDefine(int index, DatapointType type) {
		super();
		this.index = index;
		this.type = type;
	}
	public int getIndex() {
		return index;
	}
	public DatapointType getType() {
		return type;
	}
	
	
	
}
