package me.ialistannen.javadocbot.command.commands;

import java.util.Collection;
import me.ialistannen.javadocbot.command.Command;
import me.ialistannen.javadocbot.javadoc.JavadocManager;
import me.ialistannen.javadocbot.javadoc.model.JavadocClass;
import me.ialistannen.javadocbot.util.StringUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;

/**
 * Shows javadoc for a class
 *
 * @author jwachter
 */
public class ClassJavadoc extends Command {

  private JavadocManager javadocManager;

  /**
   * Shows Javadoc for a class
   *
   * @param javadocManager The {@link JavadocManager} to use
   */
  public ClassJavadoc(JavadocManager javadocManager) {
    super("class");
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
      message.editMessage("Arguments!").queue();
      return;
    }
    String className = args[0];
    Collection<JavadocClass> classes = javadocManager.getClass(className);
    if (classes.isEmpty()) {
      message.editMessage("Class not found!").queue();
      return;
    }
    JavadocClass javadocClass = classes.iterator().next();

    EmbedBuilder embedBuilder = new EmbedBuilder()
        .setAuthor(
            StringUtil.stripFormatting(javadocClass.getNameWithModifiers()),
            javadocClass.getUrl(),
            getIconUrlForClass(javadocClass)
        )
        .addField("Class hierarchy",
            StringUtil
                .trimToSize(
                    javadocClass.getExtendsImplements(), MessageEmbed.VALUE_MAX_LENGTH
                ),
            false
        )
        .addField("Description",
            StringUtil
                .trimToSize(javadocClass.getDescription(), MessageEmbed.VALUE_MAX_LENGTH),
            true
        );

    channel.sendMessage(new MessageBuilder()
        .append(" ")
        .setEmbed(embedBuilder.build())
        .build()
    ).queue();
  }

  /**
   * @param javadocClass The {@link JavadocClass} to get the Icon for
   * @return The icon for the class
   */
  private String getIconUrlForClass(JavadocClass javadocClass) {
    if (javadocClass.getDeclaration().contains("abstract")) {
      return "https://www.jetbrains.com/help/img/idea/2016.3/classTypeAbstract.png";
    } else if (javadocClass.getType().equalsIgnoreCase("interface")) {
      return "https://www.jetbrains.com/help/img/idea/2016.3/classTypeInterface.png";
    } else if (javadocClass.getType().equalsIgnoreCase("enum")) {
      return "https://www.jetbrains.com/help/img/idea/2016.3/classTypeEnum.png";
    } else if (javadocClass.getDeclaration().contains("final")) {
      return "https://www.jetbrains.com/help/img/idea/2016.3/classTypeFinal.png";
    }
    return "https://www.jetbrains.com/help/img/idea/2016.3/classTypeJavaClass.png";
  }

}
