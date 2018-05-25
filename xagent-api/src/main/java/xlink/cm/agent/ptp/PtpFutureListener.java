package xlink.cm.agent.ptp;

import java.util.concurrent.Future;

/**
 * PtpFuture监听
 * 
 * @author xlink
 *
 * @param <F>
 */
public interface PtpFutureListener<F extends Future<?>> {

  /**
   * 操作完成后的回调
   * 
   * @param future
   * @throws Exception
   */
  void operationComplete(F future) throws Exception;

}
