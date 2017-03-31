package me.ialistannen.javadocbot.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.ialistannen.javadocbot.command.Command;
import me.ialistannen.javadocbot.command.commands.ClassJavadoc;
import me.ialistannen.javadocbot.command.commands.MethodJavadoc;
import me.ialistannen.javadocbot.command.commands.PackageJavadoc;
import me.ialistannen.javadocbot.javadoc.JavadocManager;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author jwachter
 */
public class CommandListener extends ListenerAdapter {

  private JavadocManager javadocManager = new JavadocManager();
  private List<Command> commands = new ArrayList<>();

  public CommandListener() {
    commands.add(new ClassJavadoc(javadocManager));
    commands.add(new MethodJavadoc(javadocManager));
    commands.add(new PackageJavadoc(javadocManager));
    javadocManager.index();
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
//    Just me
    if (!event.getAuthor().getId().equals("author id")) {
      return;
    }

    String strippedContent = event.getMessage().getStrippedContent();
    if (!strippedContent.startsWith("me.")) {
      return;
    }
    strippedContent = strippedContent.substring("me.".length());

    String[] split = strippedContent.split(" ");

    for (Command command : commands) {
      if (command.getKeyword().equalsIgnoreCase(split[0])) {
        command.execute(
            event.getChannel(), event.getMessage(),
            split.length == 1
                ? new String[0]
                : Arrays.copyOfRange(split, 1, split.length)
        );
      }
    }
  }
}
