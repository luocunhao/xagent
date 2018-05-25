package xlink.cm.agent.ptp;

/**
 * PtpServer生成策略
 * 
 * @author xlink
 *
 */
public enum PtpServerStrategy {

  /**
   * 基于tcp协议的ptpServer
   */
  TCP(0),
  /**
   * 基于http协议的ptpServer
   */
  HTTP(1);

  private int strategy;

  private PtpServerStrategy(int strategy) {
    this.strategy = strategy;
  }
}
