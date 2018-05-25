package cn.xlink.cmmqttclient.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.xlink.cmmqttclient.core.utils.tuple.FiveTupleList;
import cn.xlink.cmmqttclient.core.utils.tuple.SixTupleList;
import cn.xlink.cmmqttclient.core.utils.tuple.ThreeTupleList;
import cn.xlink.cmmqttclient.core.utils.tuple.TowTupleList;

public class ContainerGetter {
	public static <K, V> HashMap<K, V> hashMap() {
		return new HashMap<K, V>();
	}

	public static <K, V> HashMap<K, V> hashMap(int capacity) {
		return new HashMap<K, V>(capacity);
	}

	public static <K extends Enum<K>, V> EnumMap<K, V> enumMap(Class<K> type) {
		return new EnumMap<K, V>(type);
	}

	public static <K, V> ConcurrentHashMap<K, V> concurHashMap() {
		return new ConcurrentHashMap<K, V>();
	}

	public static <K, V> TreeMap<K, V> treeMap() {
		return new TreeMap<>();
	}

	public static <K, V> TreeMap<K, V> treeMap(Comparator<K> comparator) {
		return new TreeMap<>(comparator);
	}

	public static <K, V> ConcurrentHashMap<K, V> concurHashMap(int capacity) {
		return new ConcurrentHashMap<>(capacity);
	}

	public static <T> ArrayList<T> arrayList(Collection<T> list) {
		return new ArrayList<T>(list);
	}

	public static <T> LinkedHashSet<T> linkedHashSet() {
		return new LinkedHashSet<>();
	}

	public static <K, V> LinkedHashMap<K, V> linkedHashMap() {
		return new LinkedHashMap<>();
	}

	public static <T> LinkedHashSet<T> linkedHashSet(Collection<T> set) {
		return new LinkedHashSet<>(set);
	}

	public static <T> HashSet<T> hashSet(Collection<T> set) {
		return new HashSet<T>(set);
	}
	
	public static <T> CopyOnWriteArraySet<T> copyOnWriteArraySet(){
		return new CopyOnWriteArraySet<T>();
	}
	
	public static <T> ArrayList<T> arrayList() {
		return new ArrayList<T>();
	}

	public static <T> ArrayList<T> arrayList(int capacity) {
		return new ArrayList<T>(capacity);
	}

	public static <T> LinkedList<T> linkedList() {
		return new LinkedList<T>();
	}

	public static <T> LinkedList<T> linkedList(Collection<T> collection) {
		return new LinkedList<T>(collection);
	}

	public static <T> HashSet<T> hashSet(int capacity) {
		return new HashSet<T>(capacity);
	}

	public static <T> HashSet<T> hashSet() {
		return new HashSet<T>();
	}

	public static <T> TreeSet<T> treeSet() {
		return new TreeSet<T>();
	}

	public static <T> TreeSet<T> treeSet(Comparator<T> comparator) {
		return new TreeSet<>(comparator);
	}

	public static <T> ConcurrentLinkedQueue<T> concurLinkedQueue() {
		return new ConcurrentLinkedQueue<T>();
	}

	public static <T> PriorityQueue<T> priorityQueue(Comparator<T> comparator) {
		return new PriorityQueue<T>(11, comparator);
	}

	public static <T> PriorityQueue<T> priorityQueue() {
		return new PriorityQueue<>();
	}

	public static <T> CopyOnWriteArrayList<T> copyOnWriteArrayList() {
		return new CopyOnWriteArrayList<T>();
	}

	public static <T> CopyOnWriteArrayList<T> copyOnWriteArrayList(Collection<T> coll) {
		return new CopyOnWriteArrayList<T>(coll);
	}

	public static <A, B, C> ThreeTupleList<A, B, C> threeTupleList() {
		return new ThreeTupleList<A, B, C>();
	}

	public static <A, B, C, D, E> FiveTupleList<A, B, C, D, E> fiveTupleList() {
		return new FiveTupleList<A, B, C, D, E>();
	}

	public static <A, B, C, D, E, F> SixTupleList<A, B, C, D, E, F> sixTupleList() {
		return new SixTupleList<A, B, C, D, E, F>();
	}

	public static <A, B> TowTupleList<A, B> towTupleList() {
		return new TowTupleList<A, B>();
	}

	public static <T> Set<T> treeSet(Collection<T> coll) {
		TreeSet<T> treeSet = new TreeSet<>(coll);
		return treeSet;
	}
	
	
	public static JSONObject JSONObject(){
		return new JSONObject();
	}
	
	public static JSONArray JSONArray(){
		return new JSONArray();
	}
	
	public static <T> ArrayBlockingQueue<T> arrayBlockingQueue(int capacity){
		return new ArrayBlockingQueue<T>(capacity);
	}

}