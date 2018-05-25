package xlink.agent.shell.command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

public class CommandManager {

  private static final CommandManager singleton = new CommandManager();

  private Map<CommandType, Command> commandMap = new ConcurrentHashMap<CommandType, Command>();

  private CommandManager() {
    commandMap.put(CommandType.UpdatePlugin, new UpdatePtpCommand());
    commandMap.put(CommandType.StartPlugin, new StartPtpCommand());
    commandMap.put(CommandType.StopPlugin, new StopPtpCommand());
    commandMap.put(CommandType.InstallPlugin, new InstallPtpCommand());
  }

  public static CommandManager instance(){
    return singleton;
  }

  public void execute(Channel channel, CommandType commandType, String params) {
    Command command = commandMap.get(commandType);
    if (commandType == CommandType.Unknown) {
      channel.writeAndFlush(Unpooled.wrappedBuffer("unknown command".getBytes()));
      return;
    }
    String[] args = params.split(" ");
    command.execute(channel, args);

  }

}
