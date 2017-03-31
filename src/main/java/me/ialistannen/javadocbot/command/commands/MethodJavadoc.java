package me.ialistannen.javadocbot.command.commands;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import me.ialistannen.javadocbot.command.Command;
import me.ialistannen.javadocbot.javadoc.JavadocManager;
import me.ialistannen.javadocbot.javadoc.model.JavadocClass;
import me.ialistannen.javadocbot.javadoc.model.JavadocMethod;
import me.ialistannen.javadocbot.util.MessageUtil;
import me.ialistannen.javadocbot.util.StringUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;

/**
 * @author jwachter
 */
public class MethodJavadoc extends Command {

  private JavadocManager javadocManager;

  public MethodJavadoc(JavadocManager javadocManager) {
    super("method");
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
      message.editMessage("!Arguments!").queue();
      return;
    }
    if (!args[0].contains("#")) {
      message.editMessage("No '#' found.").queue();
      return;
    }

    String className = args[0].split("#")[0];
    String methodName = args[0].split("#")[1];

    Collection<JavadocClass> classes = javadocManager.getClassEndingIn(className);
    if (classes.isEmpty()) {
      message.editMessage("Class not found!").queue();
      return;
    }

    if (classes.size() > 1) {
      MessageBuilder messageBuilder = new MessageBuilder()
          .append("**Found multiple classes:**")
          .append("\n")
          .appendCodeBlock(
              classes.stream()
                  .map(aClass -> aClass.getParentPackage().getName() + "." + aClass.getName())
                  .collect(Collectors.joining("\n")),
              ""
          );
      channel.sendMessage(messageBuilder.build()).queue(MessageUtil.selfDestructing());
      return;
    }

    JavadocClass javadocClass = classes.iterator().next();

    Optional<JavadocMethod> methodOptional = javadocManager.getMethod(javadocClass, methodName);
    if (!methodOptional.isPresent()) {
      message.editMessage("Method not found!").queue();
      return;
    }
    JavadocMethod method = methodOptional.get();

    EmbedBuilder embedBuilder = new EmbedBuilder()
        .setAuthor(
            StringUtil.trimToSize(
                StringUtil.stripFormatting(method.getDeclarationWithoutExceptions()
                ),
                256
            ),
            method.getUrl(),
            getIconForMethod(method)
        );
    if (!method.getExceptions().isEmpty()) {
      embedBuilder.addField(
          "Exceptions",
          StringUtil.trimToSize(method.getExceptions(), MessageEmbed.VALUE_MAX_LENGTH),
          false
      );
    }
    embedBuilder.addField(
        "Description",
        StringUtil.trimToSize(method.getDescription(), MessageEmbed.VALUE_MAX_LENGTH),
        true
    );
    channel.sendMessage(new MessageBuilder()
        .append(" ")
        .setEmbed(embedBuilder.build())
        .build()).queue();
  }

  /**
   * @param method The method to get the icon for
   * @return The icon for the method
   */
  private String getIconForMethod(JavadocMethod method) {
    if (method.getDeclaration().contains("abstract")) {
      return "https://www.jetbrains.com/help/img/idea/2016.3/method_abstract.png";
    }
    return "https://www.jetbrains.com/help/img/idea/2016.3/method.png";
  }
}
