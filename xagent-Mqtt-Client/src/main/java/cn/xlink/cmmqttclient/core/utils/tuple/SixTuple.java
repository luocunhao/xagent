package cn.xlink.cmmqttclient.core.utils.tuple;

public class SixTuple<A, B, C, D, E, F> extends FiveTuple<A, B, C, D, E> {

	private static final long serialVersionUID = 1L;
	public final F sixth;

	public SixTuple(A first, B second, C thrid, D fouth, E fifth, F sixth) {
		super(first, second, thrid, fouth, fifth);
		this.sixth = sixth;
	}
}
