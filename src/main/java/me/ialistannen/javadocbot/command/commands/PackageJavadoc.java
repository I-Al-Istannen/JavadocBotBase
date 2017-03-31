package me.ialistannen.javadocbot.command.commands;

import java.util.Optional;
import me.ialistannen.javadocbot.command.Command;
import me.ialistannen.javadocbot.javadoc.JavadocManager;
import me.ialistannen.javadocbot.javadoc.model.Package;
import me.ialistannen.javadocbot.util.MessageUtil;
import me.ialistannen.javadocbot.util.StringUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

/**
 * @author jwachter
 */
public class PackageJavadoc extends Command {

  private JavadocManager javadocManager;

  public PackageJavadoc(JavadocManager javadocManager) {
    super("package");
    this.javadocManager = javadocManager;
  }

  /**
   * Executes the command
   *
   * @param channel The channel it ocurred in
   * @param message The message that triggered it
   * @param args The arguments
   */
  @Override
  public void execute(MessageChannel channel, Message message, String[] args) {
    if (args.length < 1) {
      channel.sendMessage("**Not enough arguments**").queue(MessageUtil.selfDestructing());
      return;
    }

    String packageName = args[0];

    Optional<Package> packageOptional = javadocManager.getPackage(packageName);
    if (!packageOptional.isPresent()) {
      channel.sendMessage("**Package not found!**").queue(MessageUtil.selfDestructing());
      return;
    }

    Package javadocPackage = packageOptional.get();

    EmbedBuilder embedBuilder = new EmbedBuilder()
        .setAuthor(javadocPackage.getName(), javadocPackage.getUrl(), null)
        .setDescription(
            StringUtil.trimToSize(javadocPackage.getDescription(),
                2000 - javadocPackage.getName().length())
        );
    channel.sendMessage(
        new MessageBuilder()
            .setEmbed(
                embedBuilder.build()
            )
            .build()
    ).queue();
  }
}
