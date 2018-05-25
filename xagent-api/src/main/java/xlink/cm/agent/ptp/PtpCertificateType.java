package xlink.cm.agent.ptp;

/**
 * 证书类型
 * @author xlink
 *
 */
public enum PtpCertificateType {
	/**
	 * 产品的ID和KEY认证，只支持单个产品的设备
	 */
	Product,
	/**
	 * 证书的ID和KEY认证，可以支持多个产品的设备
	 */
	Certificate;
}
