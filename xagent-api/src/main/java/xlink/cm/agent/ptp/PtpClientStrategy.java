package xlink.cm.agent.ptp;

/**
 * PtpServer生成策略
 * 
 * @author xlink
 *
 */
public enum PtpClientStrategy {

  /**
   * 基于tcp协议的ptpClient
   */
  TCP(0),
;
  private int strategy;

  private PtpClientStrategy(int strategy) {
    this.strategy = strategy;
  }
}
