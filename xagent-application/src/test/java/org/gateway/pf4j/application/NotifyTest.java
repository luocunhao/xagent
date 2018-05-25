package org.gateway.pf4j.application;

import java.util.concurrent.ConcurrentHashMap;

import xlink.core.utils.MD5Tool;

public class NotifyTest {

  private Object lock = new Object();

  public void unlock() {
    synchronized (lock) {
      lock.notifyAll();
    }
  }

  public static void main(String[] args) throws Exception {
    System.out
        .println(MD5Tool.MD5("5a55a97b4abd1f4fc5516c4c" + "d4355b83-8789-4a1c-a3e8-f2f8e8b28e51"));


    /*
     * Files.deleteIfExists(Paths.get("E:\\data1\\linsd\\gateway-pf4j")); NotifyTest test = new
     * NotifyTest(); test.unlock();
     */
  }



}


class TestThread extends Thread {
  String value;
  ConcurrentHashMap<String, String> map;

  public TestThread(String value, ConcurrentHashMap<String, String> map) {
    super();
    this.value = value;
    this.map = map;
  }


  public void run() {

    long start1 = System.currentTimeMillis();
    String a = map.put("111", value);
    System.out.println(a);
    System.out.println(System.currentTimeMillis() - start1);

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println(map.get("111"));
  }
}