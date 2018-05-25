package xlink.agent.shell;

import java.io.PrintStream;

public interface ShellService {

  public String[] getCommands();

  public String getUsage(String commandName);

  public void executeCommand(String commandLine, PrintStream out, PrintStream in);

}
