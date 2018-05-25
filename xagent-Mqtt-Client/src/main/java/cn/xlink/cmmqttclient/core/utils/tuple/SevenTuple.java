package cn.xlink.cmmqttclient.core.utils.tuple;

public class SevenTuple<A, B, C, D, E, F, G> extends SixTuple<A, B, C, D, E, F> {

	private static final long serialVersionUID = 1L;
	public final G seventh;

	public SevenTuple(A first, B second, C thrid, D fouth, E fifth, F sixth, G seventh) {
		super(first, second, thrid, fouth, fifth, sixth);
		this.seventh = seventh;
	}
}
