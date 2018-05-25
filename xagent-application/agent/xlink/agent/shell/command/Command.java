package xlink.agent.shell.command;

import io.netty.channel.Channel;

public interface Command {

  public String getName();

  public String getUsage();

  public CommandType getCommandType();

  public void execute(Channel channel, String... args);
}
