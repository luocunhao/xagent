package xlink.mqtt.client.utils;

public class Tuple {
	public static <A, B> TowTuple<A, B> tuple(A a, B b) {
		return new TowTuple<A, B>(a, b);
	}

}
