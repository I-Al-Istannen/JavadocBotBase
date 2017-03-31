package me.ialistannen.javadocbot;

import java.util.Objects;
import javax.security.auth.login.LoginException;
import me.ialistannen.javadocbot.listener.CommandListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

/**
 * @author jwachter
 */
public class JavadocBot {

  private static JavadocBot instance;

  private JDA jda;

  private JavadocBot(String token)
      throws LoginException, InterruptedException, RateLimitedException {

    instance = this;
    Objects.requireNonNull(token, "token can not be null!");

    this.jda = new JDABuilder(AccountType.CLIENT)
        .setToken(token)
        .addListener(new CommandListener())
        .buildBlocking();
  }

  /**
   * @return The {@link JDA} instance
   */
  public JDA getJda() {
    return jda;
  }

  /**
   * @return The instance of this bot
   */
  public static JavadocBot getInstance() {
    return instance;
  }

  public static void main(String[] args)
      throws LoginException, InterruptedException, RateLimitedException {
    // create bot here new JavadocBot("token");
  }
}
