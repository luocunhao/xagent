package xlink.agent.shell.command;

public enum CommandType {

  Unknown("unknown"),

  UpdatePlugin("updatePtp"),

  StartPlugin("startPtp"),

  StopPlugin("stopPtp"),

  InstallPlugin("installPtp")

  ;

  private String commandType;

  private CommandType(String commandType) {
    this.commandType = commandType;
  }

  public String type() {
    return this.commandType;
  }

  public static CommandType fromType(String type) {
    for (CommandType cmd : values()) {
      if (cmd.type().equals(type)) {
        return cmd;
      }
    }
    return Unknown;
  }
}
