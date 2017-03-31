package me.ialistannen.javadocbot.command;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

/**
 * A command
 *
 * @author jwachter
 */
public abstract class Command {

  private String keyword;

  /**
   * @param keyword The keyword
   */
  public Command(String keyword) {
    this.keyword = keyword;
  }

  /**
   * @return The keyword
   */
  public String getKeyword() {
    return keyword;
  }

  /**
   * Executes the command
   *
   * @param channel The channel it ocurred in
   * @param message The message that triggered it
   * @param args The arguments
   */
  public abstract void execute(MessageChannel channel, Message message, String[] args);
}
