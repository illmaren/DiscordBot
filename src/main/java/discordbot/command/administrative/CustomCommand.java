package discordbot.command.administrative;

import com.vdurmont.emoji.EmojiParser;
import discordbot.command.CommandVisibility;
import discordbot.core.AbstractCommand;
import discordbot.db.table.TGuild;
import discordbot.handler.CommandHandler;
import discordbot.handler.Template;
import discordbot.main.Config;
import discordbot.main.DiscordBot;
import discordbot.util.DisUtil;
import discordbot.util.Misc;
import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

import java.util.Arrays;

/**
 * Created on 11-8-2016
 */
public class CustomCommand extends AbstractCommand {
	private String[] valid_actions = {"add", "delete"};

	public CustomCommand() {
		super();
	}

	@Override
	public String getDescription() {
		return "Add and remove custom commands." + Config.EOL +
				"There are a few keywords you can use in commands. These tags will be replaced by its value " + Config.EOL + Config.EOL +
				"Key                Replacement\n" +
				"---                ---\n" +
				"%user%             Username \n" +
				"%user-mention%     Mentions user \n" +
				"%user-id%          ID of user\n" +
				"%nick%             Nickname\n" +
				"%discrim%          discrim\n" +
				"%guild%            Guild name\n" +
				"%guild-id%         guild id\n" +
				"%guild-users%      amount of users in the guild\n" +
				"%channel%          channel name\n" +
				"%channel-id%       channel id\n" +
				"%channel-mention%  Mentions channel\n" +
				"%rand-user%        random user in guild\n" +
				"%rand-user-online% random ONLINE user in guild";
	}

	@Override
	public String getCommand() {
		return "command";
	}

	@Override
	public String[] getUsage() {
		return new String[]{
				"command add <command> <action>  //adds a command",
				"command delete <command>        //deletes a command",
				"command                         //shows a list of existing custom commands"
		};
	}

	@Override
	public String[] getAliases() {
		return new String[]{
				"cmd"
		};
	}

	@Override
	public CommandVisibility getVisibility() {
		return CommandVisibility.PUBLIC;
	}

	@Override
	public String execute(DiscordBot bot, String[] args, MessageChannel channel, User author) {
		if (!bot.isAdmin(channel, author)) {
			return Template.get("permission_denied");
		}
		int guildId = TGuild.getCachedId(((TextChannel) channel).getGuild().getId());
		String prefix = DisUtil.getCommandPrefix(channel);
		if (args.length >= 2 && Arrays.asList(valid_actions).contains(args[0])) {
			if (args[0].equals("add") && args.length > 2) {
				String output = "";
				for (int i = 2; i < args.length; i++) {
					output += args[i] + " ";
				}
				if (args[0].startsWith(prefix)) {
					args[0] = args[0].substring(prefix.length());
				}
				CommandHandler.addCustomCommand(guildId, args[1], EmojiParser.parseToAliases(output.trim()));
				return "Added " + prefix + args[1];
			} else if (args[0].equals("delete")) {
				CommandHandler.removeCustomCommand(guildId, args[1]);
				return "Removed " + prefix + args[1];
			}
		} else if (args.length == 0 || (args.length > 0 && args[0].equalsIgnoreCase("list"))) {
			return "All custom commands: " + Config.EOL + Misc.makeTable(CommandHandler.getCustomCommands(guildId));
		} else {
			return "```" + Config.EOL +
					getDescription() + Config.EOL + "```";
		}
		return Template.get("permission_denied");
	}
}
