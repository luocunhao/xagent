package xlink.cm.agent.ptp;

import java.util.concurrent.Future;


/**
 * Ptp的异步返回结果
 * 
 * @author xlink
 *
 * @param <V>
 */
public interface PtpFuture<V> extends Future<V> {

  /**
   * 是否执行成功
   * 
   * @return
   */
  boolean isSuccess();

  /**
   * 获取抛出的异常
   * 
   * @return
   */
  Throwable cause();

  /**
   * 添加监听器
   * 
   * @param listener
   * @return
   */
  Future<V> addListener(PtpFutureListener<? extends Future<? super V>> listener);

  /**
   * 添加监听器
   * 
   * @param listener
   * @return
   */
  @SuppressWarnings("unchecked")
  Future<V> addListeners(PtpFutureListener<? extends Future<? super V>>... listeners);

  /**
   * 移除监听器
   * 
   * @param listener
   * @return
   */
  Future<V> removeListener(PtpFutureListener<? extends Future<? super V>> listener);

  /**
   * 移除监听器
   * 
   * @param listener
   * @return
   */
  @SuppressWarnings("unchecked")
  Future<V> removeListeners(PtpFutureListener<? extends Future<? super V>>... listeners);

  /**
   * 同步阻塞，让调用该future的线程阻塞，直到获取返回结果。
   * 
   * @return
   * @throws InterruptedException
   */
  PtpFuture<V> sync() throws InterruptedException;
}
