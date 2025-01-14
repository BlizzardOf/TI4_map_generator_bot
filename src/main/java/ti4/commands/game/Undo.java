package ti4.commands.game;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ti4.helpers.Constants;
import ti4.map.Game;
import ti4.map.GameManager;
import ti4.map.GameSaveLoadManager;
import ti4.message.MessageHelper;

public class Undo extends GameSubcommandData{
    public Undo() {
        super(Constants.UNDO, "Undo the last action");
        addOptions(new OptionData(OptionType.STRING, Constants.LATEST_COMMAND, "For Reference Only - Autocomplete shows the last command.").setRequired(true).setAutoComplete(true));
        addOptions(new OptionData(OptionType.STRING, Constants.CONFIRM, "Confirm undo command with YES").setRequired(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        GameManager gameManager = GameManager.getInstance();
        Game userActiveGame = gameManager.getUserActiveGame(event.getUser().getId());
        if (userActiveGame == null){
            MessageHelper.replyToMessage(event, "Must set active Game");
            return;
        }
        if (!event.getChannel().getName().startsWith(userActiveGame.getName()+"-")){
            MessageHelper.replyToMessage(event, "Undo must be executed in game channel only!");
            return;
        }

        OptionMapping option = event.getOption(Constants.CONFIRM);
        if (option == null || !"YES".equals(option.getAsString())){
            MessageHelper.replyToMessage(event, "Must confirm with YES");
            return;
        }

        

        GameSaveLoadManager.undo(userActiveGame, event);
    }
}
