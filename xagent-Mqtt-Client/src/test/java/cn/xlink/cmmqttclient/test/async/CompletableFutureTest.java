package cn.xlink.cmmqttclient.test.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import cn.xlink.cmmqttclient.Future.IMqttActionListener;
import cn.xlink.cmmqttclient.exception.XlinkMqttException;
import cn.xlink.cmmqttclient.thread.AsyncThreadPool;
import cn.xlink.cmmqttclient.token.MqttToken;
import cn.xlink.cmmqttclient.token.MqttTokenManager;
import cn.xlink.cmmqttclient.token.MqttTokenType;

public class CompletableFutureTest {

  @Test
  public void completableTest()  {
    try {
      
      AsyncThreadPool asyncTheadPool = new AsyncThreadPool(4,3000);
      MqttToken token = MqttTokenManager.instance().createMqttToken("123412", MqttTokenType.XLINK_CM);
          token.setCallbackListener(new IMqttActionListener() {

        @Override
        public void onSucess(MqttToken<?> actionToken) {
          try {
            System.out.println("token run success. "+actionToken.getResult());
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }

        @Override
        public void onFailure(MqttToken<?> actionToken, XlinkMqttException exception) {
          System.out.println("token run error.");
         exception.printStackTrace();
          
        }
        
      });;
      CompletableFuture<Void> future = CompletableFuture.runAsync(()->{
        System.out.println("completableFuture test.");
        
        try {
          
          Thread.currentThread().sleep(3000);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        System.out.println("completableFuture test closed.");
        //token.markComplete(new SubResultMessage());
        token.completeException(new XlinkMqttException(1,new Exception()));
        }, asyncTheadPool);
     
     System.out.println(token.get());
     while(true) {
       
     }
      
      
    }catch(Throwable e) {
      e.printStackTrace();
    }
   
  }

  @Test
  public void thenAcceptExceptionTest() {
    AsyncThreadPool asyncTheadPool = new AsyncThreadPool(4,3000);
    final AtomicInteger i = new AtomicInteger();
    i.set(2);
    CompletableFuture.supplyAsync(()-> {
      System.out.println("start test");
      
      System.out.println("first i=" + i.get());
      i.addAndGet(3);
      return i;
    }, asyncTheadPool).thenAccept(index ->{
      
      System.out.println("second i=" + index.get());
      index.addAndGet(5);
    }).whenComplete((a,throwable) ->{
      System.out.println("end i=" + i.get());
    });
    
    System.out.println("third i="+ i.get());
  }
 
}
