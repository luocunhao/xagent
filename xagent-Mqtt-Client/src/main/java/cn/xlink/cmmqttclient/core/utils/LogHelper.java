package cn.xlink.cmmqttclient.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogHelper {
  private static final Logger logger = LoggerFactory.getLogger(LogHelper.class);
  private static final LogHelper singleton = new LogHelper();

  private LogHelper() {

  }

  public static LogHelper LOGGER() {
    return singleton;
  }

  public void debug(String msg) {
    logger.debug(msg);
  }

  public void info(String msg) {
    logger.info(msg);
  }

  public void warn(String msg) {
    logger.warn(msg);
  }

  public void debug(String format, Object... arguments) {
    logger.debug(format, arguments);

  }


  public void info(String format, Object... arguments) {
    logger.info(format, arguments);

  }


  public void warn(String format, Object... arguments) {
    logger.warn(format, arguments);

  }


  public void error(String msg, Throwable t) {
    logger.error(msg, t);

  }

  public void error(String format, Object... arguments) {
    logger.error(String.format(format, arguments));

  }

  public void error(Throwable t, String format, Object... arguments) {
    logger.error(String.format(format, arguments), t);

  }


}
