package cn.xlink.cmmqttclient.core.utils.tuple;

public class EightTuple<A, B, C, D, E, F, G, H> extends SevenTuple<A, B, C, D, E, F, G> {

	private static final long serialVersionUID = 1L;
	public final H eighth;

	public EightTuple(A first, B second, C third, D fouth, E fifth, F sixth, G seventh, H eighth) {
		super(first, second, third, fouth, fifth, sixth, seventh);
		this.eighth = eighth;
	}
}
