package xlink.agent.main;

import org.pf4j.PluginManager;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import xlink.agent.pf4j.XlinkCmAgentPluginManager;
import xlink.agent.shell.ShellServer;
import xlink.cm.ptp.platform.PtpFramework;
import xlink.core.derby.DerbyTableTools;
import xlink.core.utils.NoProguard;
import xlink.mqtt.client.MqttClientManager;
import xlink.mqtt.client.thread.AsyncThreadPool;
import xlink.mqtt.client.thread.LogicThreadPool;
import xlink.mqtt.client.thread.MqttTokenCheckThread;
import xlink.mqtt.client.utils.LogHelper;

@NoProguard
public class XagentMain {

	public static PluginManager pluginManger;

public static void main(String[] args) {
		try {
			EventLoopGroup bossGroup = new NioEventLoopGroup();
			EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 4);
			EventLoopGroup clientWorker = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
			final LogicThreadPool logicThreadPool = new LogicThreadPool(Args.LOGIC_THREAD_SIZE, Args.LOGIC_QUEUE_SIZE);
			final AsyncThreadPool asyncThreadPool = new AsyncThreadPool(Args.ASYNC_THREAD_SIZE, Args.ASYNC_QUEUE_SIZE, logicThreadPool, null);
			// 初始化ptp框架
			final LogicThreadPool ptplogicThreadPool = new LogicThreadPool(Args.LOGIC_THREAD_SIZE, Args.LOGIC_QUEUE_SIZE);
			PtpFramework.instance().group(bossGroup, workerGroup,clientWorker).setLogicThreadPool(ptplogicThreadPool);
			// 初始化derby
			DerbyTableTools.instance().init();
			//初始化Mqtt客户端
			MqttClientManager.instance().setAsyncThreadPool(asyncThreadPool);
			MqttClientManager.instance().setLogicThreadPool(logicThreadPool);
			MqttClientManager.instance().setNettyEventgroup(workerGroup);
			// 启动MqttToken检查线程
			new MqttTokenCheckThread().start();
			// 开始加载插件
			pluginManger = new XlinkCmAgentPluginManager();
			pluginManger.loadPlugins();
			pluginManger.startPlugins();

			ShellServer.instance().start(3099);

			// LogHelper.LOGGER().info("------------------------------------");
			// Thread.sleep(30000);

			// pluginManger.stopPlugin("ptp-demo");
			// Pf4jUpdate.udpate(pluginManger);
		} catch (Exception e) {
			LogHelper.LOGGER().error("service error ", e);
		}

	}

}
