package cn.xlink.cmmqttclient.core.utils.tuple;

public class Tuple {
	public static <A, B> TowTuple<A, B> tuple(A a, B b) {
		return new TowTuple<A, B>(a, b);
	}

	public static <A, B, C> ThreeTuple<A, B, C> tuple(A a, B b, C c) {
		return new ThreeTuple<A, B, C>(a, b, c);
	}

	public static <A, B, C, D> FourTuple<A, B, C, D> tuple(A a, B b, C c, D d) {
		return new FourTuple<A, B, C, D>(a, b, c, d);
	}

	public static <A, B, C, D, E> FiveTuple<A, B, C, D, E> tuple(A a, B b, C c, D d, E e) {
		return new FiveTuple<A, B, C, D, E>(a, b, c, d, e);
	}

	public static <A, B, C, D, E, F> SixTuple<A, B, C, D, E, F> tuple(A a, B b, C c, D d, E e, F f) {
		return new SixTuple<A, B, C, D, E, F>(a, b, c, d, e, f);
	}

	public static <A, B, C, D, E, F, G> SevenTuple<A, B, C, D, E, F, G> tuple(A a, B b, C c, D d, E e, F f, G g) {
		return new SevenTuple<A, B, C, D, E, F, G>(a, b, c, d, e, f, g);
	}

	public static <A, B, C, D, E, F, G, H> EightTuple<A, B, C, D, E, F, G, H> tuple(A a, B b, C c, D d, E e, F f, G g, H h) {
		return new EightTuple<A, B, C, D, E, F, G, H>(a, b, c, d, e, f, g, h);
	}
}
