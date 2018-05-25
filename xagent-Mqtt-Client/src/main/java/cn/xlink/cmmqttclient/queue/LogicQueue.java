package cn.xlink.cmmqttclient.queue;

/**
 * 

 * @ClassName LogicQueue

 * @Description 逻辑任务

 * @author linsida@xlink.cn

 * @date 2018年4月21日
 */
public abstract class LogicQueue implements Runnable {

 /**
  * 指定线程执行，如果值为-1，则不指定线程。
  */
  private int sequenceId;
  
  public LogicQueue(int sequenceId) {
    this.sequenceId = sequenceId;
  }

  public int getSequenceId() {
    return sequenceId;
  }

  public void setSequenceId(int sequenceId) {
    this.sequenceId = sequenceId;
  }
}
