package xlink.mqtt.client.utils;

public class TowTuple<A, B> implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final A first;
	public final B second;

	public TowTuple(A first, B second) {
		this.first = first;
		this.second = second;
	}

	public String toString() {
		return "(" + first + ", " + second + ")";
	}
}
