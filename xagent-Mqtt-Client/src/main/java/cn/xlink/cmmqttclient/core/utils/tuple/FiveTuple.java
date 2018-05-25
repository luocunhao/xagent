package cn.xlink.cmmqttclient.core.utils.tuple;

public class FiveTuple<A, B, C, D, E> extends FourTuple<A, B, C, D> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final E fifth;

	public FiveTuple(A a, B b, C c, D d, E e) {
		super(a, b, c, d);
		this.fifth = e;
	}

	public String toString() {
		return "(" + first + ", " + second + ", " + third + ", " + fourth + ", " + fifth + ")";
	}
}
