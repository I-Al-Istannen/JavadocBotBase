package me.ialistannen.javadocbot.util;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.requests.RestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A small utility for Messages
 *
 * @author jwachter
 */
public class MessageUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageUtil.class);

  /**
   * Generates a self-destructing message if used in conjunction with {@link RestAction#queue()}
   *
   * @return A consumer to generate a self-destructing message
   */
  public static Consumer<Message> selfDestructing() {
    return message -> {
      try {
        Thread.sleep(TimeUnit.SECONDS.toMillis(15));
        message.delete().queue();
      } catch (InterruptedException e) {
        LOGGER.warn("Error sleeping", e);
      }
    };
  }
}
