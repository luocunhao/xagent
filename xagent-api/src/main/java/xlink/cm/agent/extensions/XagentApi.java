package xlink.cm.agent.extensions;

import java.util.Date;
import java.util.Map;

import javax.net.ssl.SSLEngine;

import xlink.cm.agent.ptp.IPtpClientProtocolProcessor;
import xlink.cm.agent.ptp.IPtpDecoder;
import xlink.cm.agent.ptp.IPtpEncoder;
import xlink.cm.agent.ptp.IPtpProtocolProcessor;
import xlink.cm.agent.ptp.PtpCertificateType;
import xlink.cm.agent.ptp.PtpClient;
import xlink.cm.agent.ptp.PtpClientStrategy;
import xlink.cm.agent.ptp.PtpDatapointSetListener;
import xlink.cm.agent.ptp.PtpFuture;
import xlink.cm.agent.ptp.PtpServer;
import xlink.cm.agent.ptp.PtpServerStrategy;
import xlink.cm.agent.ptp.dataStruture.DDevice;
import xlink.cm.agent.ptp.dataStruture.DPtpDatapoint;
import xlink.cm.agent.ptp.dataStruture.DatapointSyncResult;
import xlink.cm.agent.ptp.dataStruture.DeviceActivateResult;
import xlink.cm.agent.ptp.dataStruture.DeviceOfflineResult;
import xlink.cm.agent.ptp.dataStruture.DeviceOnlineResult;

/**
 * Xlink的功能接口.
 * 
 * <p>
 * Xlink Api
 * </p>
 * <ul>
 * <li></li>
 * </ul>
 * <br>
 * <strong>Time</strong>&nbsp;&nbsp;&nbsp;&nbsp;${date}<br>
 * <strong>copyright</strong>&nbsp;&nbsp;&nbsp;&nbsp;2016, XXXX有限公司<br>
 *
 * @version .0.01
 * @author XLINK
 */

public interface XagentApi {
  /**
   * 创建ptpServer实例,支持多种证书验证
   * 
   * @param pluginId ptp插件Id
   * @param cId XLINK授权证书ID
   * @param cKey XLINK授权证书KEY
   * @param certType 证书的类型
   * @param port ptpServer监听端口
   * @param decoder ptpServer的协议解码器
   * @param encoder ptpServer的协议编码器
   * @param processor ptpServer的消息处理器
   * @param keepAlive 心跳周期
   * @param ptpServerStrategy ptpServer的生成策略
   * @return
   * @throws Exception
   */
  public PtpServer createServerWithCertType(String pluginId, String cId, String cKey,PtpCertificateType certType, int port,
      IPtpDecoder decoder, IPtpEncoder encoder, IPtpProtocolProcessor processor, int keepAlive,
      PtpServerStrategy ptpServerStrategy)
      throws Exception;

  /**
   * 创建ptpServer实例,仅支持产品证书
   * 
   * @param pluginId ptp插件Id
   * @param cId XLINK授权证书ID
   * @param cKey XLINK授权证书KEY
   * @param port ptpServer监听端口
   * @param decoder ptpServer的协议解码器
   * @param encoder ptpServer的协议编码器
   * @param processor ptpServer的消息处理器
   * @param keepAlive 心跳周期
   * @param ptpServerStrategy ptpServer的生成策略
   * @return
   * @throws Exception
   */
  public PtpServer createServer(String pluginId, String cId, String cKey, int port,
      IPtpDecoder decoder, IPtpEncoder encoder, IPtpProtocolProcessor processor, int keepAlive,
      PtpServerStrategy ptpServerStrategy)
      throws Exception;
  
  
  /**
   * 获取Xlink平台的设备信息<br>
   * 该接口是从Xagent本地缓存中查找设备信息
   * 
   * @param productId xlink的产品ID
   * @param identify 设备的识别号，厂商自定义
   * @return
   */
  public DDevice getDeviceInfo(String productId, String identify) throws Exception;

  /**
   * 获取Xlink平台的设备信息<br>
   * 先从本地缓存中查找。如果没有，再向Xlink Api服务查找。但是调用者必须提供对应的accessToken
   * 
   * @param productId xlink的产品ID
   * @param identify identify 设备的识别号，厂商自定义
   * @param accessToken 调用Xlink Api服务所需要的凭证
   * @return
   * @throws Exception 如果失败，会抛出{@link xlink.cm.agent.ptp.exception.DeviceNotFoundException}
   */
  public DDevice getDeviceInfo(String productId, String identify, String accessToken)
      throws Exception;

  /**
   * 向XLINK平台激活设备
   * 
   * @param productId xlink的产品ID
   * @param identify 设备的识别号，厂商自定义
   * @param wifiFirmware wifi mode，可选(不需要时设置为null)
   * @param wifiVersion wifi固件版本号，可选(不需要时设置为0)
   * @param mcuVersion mcu mode，可选(不需要时设置为null)
   * @param mcuVersion mcu固件版本号，可选(不需要时设置为0)
   * @param sn 设备序列号，可选(不需要时设置为null)
   * @param gatewayDeviceId 网关ID，可选(不需要时设置为0)
   * @param activeIp 激活时设备的IP地址，可选(不需要时设置为null)
   * @return
   */

  public PtpFuture<DeviceActivateResult> activateDevice(String productId, String identify,
      byte wifiFirmware, int wifiVersion, byte mcuFirmware, int mcuVersion, String sn,
      int gatewayDeviceId, String activeIp)
      throws Exception;

  /**
   * 发送设备上线消息
   * 
   * @param deviceId xlink平台的设备ID
   * @param ip 设备上线时的IP地址,XXX.XXX.XXX.XXX格式。如果没有可以设置为null
   */
  public PtpFuture<DeviceOnlineResult> deviceOnline(int deviceId, String ip) throws Exception;


  /**
   * 发送设备下线消息
   * 
   * @param deviceId xlink平台的设备ID
   */
  public PtpFuture<DeviceOfflineResult> deviceOffline(int deviceId) throws Exception;

  /**
   * 同步设备的数据端点
   * 
   * @param deviceId xlink平台的deviceId
   * @param updateTime 数据端点数据上报的时间
   * @param datapointValue 数据端点的键值对, key表示数据端点的Index，value表示该Index对应的数据端点值。
   */

  public PtpFuture<DatapointSyncResult> datapointSync(int deviceId, Date updateTime,
      Map<Integer, DPtpDatapoint> datapointValue) throws Exception;

  /**
   * 用于接收xlink平台发送过来的设置数据端点的请求
   * 
   * @param datapointSetLinstener 监听器
   */
  public void setDatapointSetListener(PtpDatapointSetListener datapointSetLinstener) throws Exception;

  /**
   * 向xlink平台回复应用设置数据端点的结果
   * 
   * @param deviceId 设备的ID
   * @param messageId 数据端点设置的请求Id
   * @param result 回复结果 0表示成功，1表示失败
   */
  public void replyDatapointSet(int deviceId, int messageId, int result) throws Exception;
  
  /**
   * 创建ptpclient实例
   * 
   * @param pluginId ptp插件Id
   * @param cId XLINK授权证书ID
   * @param cKey XLINK授权证书KEY
   * @param certType 证书的类型
   * @param hostName 目标服务器的域名
   * @param port ptpServer监听端口
   * @param decoder ptpServer的协议解码器
   * @param encoder ptpServer的协议编码器
   * @param processor ptpServer的消息处理器
   * @param keepAlive 心跳周期
   * @param ptpServerStrategy ptpServer的生成策略
   * @param sslEngine ssl加密引擎，如果不需要加密，可为null
   * @return
   * @throws Exception
   */
  public PtpClient createClient(String pluginId, String cId, String cKey,PtpCertificateType certType,String hostName, int port,
      IPtpDecoder decoder, IPtpEncoder encoder, IPtpClientProtocolProcessor processor, int keepAlive,
      PtpClientStrategy ptpClientStrategy,SSLEngine sslEngine)
      throws Exception;
}
