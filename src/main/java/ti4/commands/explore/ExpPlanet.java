package ti4.commands.explore;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ti4.commands.units.AddRemoveUnits;
import ti4.generator.GenerateMap;
import ti4.generator.Mapper;
import ti4.helpers.AliasHandler;
import ti4.helpers.Constants;
import ti4.map.Map;
import ti4.map.MapSaveLoadManager;
import ti4.map.Player;
import ti4.map.Tile;
import ti4.message.MessageHelper;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ExpPlanet extends ExploreSubcommandData {

    public ExpPlanet() {
        super(Constants.PLANET, "Explore a specific planet.");
        addOptions(new OptionData(OptionType.STRING, Constants.TILE_NAME, "Tile containing the planet").setRequired(true),
                new OptionData(OptionType.STRING, Constants.PLANET_NAME, "Planet to explore"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String tileName = event.getOption(Constants.TILE_NAME).getAsString();
        OptionMapping planetOption = event.getOption(Constants.PLANET_NAME);
        String drawColor;
        String planetName;
        Map activeMap = getActiveMap();
        Tile tile = getTile(event, AliasHandler.resolveTile(tileName), activeMap);
        if (tile == null) return;
        if (planetOption == null) {
            Set<String> unitHolderIDs = tile.getUnitHolders().keySet();
            if (unitHolderIDs.size() == 2) {
                HashSet<String> unitHolder = new HashSet<>(unitHolderIDs);
                unitHolder.remove(Constants.SPACE);
                planetName = unitHolder.iterator().next();
            } else if (unitHolderIDs.size() > 2) {
                MessageHelper.replyToMessage(event, "System contains more than one planet, please specify");
                return;
            } else {
                MessageHelper.replyToMessage(event, "System contains no planets");
                return;
            }
        } else {
            planetName = planetOption.getAsString();
        }
        planetName = AddRemoveUnits.getPlanet(event, tile, AliasHandler.resolvePlanet(planetName));
        String planet = Mapper.getPlanet(planetName);
        if (planet == null) {
            MessageHelper.replyToMessage(event, "Invalid planet");
            return;
        }
        String[] planetInfo = planet.split(",");
        drawColor = planetInfo[1];
        String cardID = activeMap.drawExplore(drawColor);
        MessageHelper.replyToMessage(event, displayExplore(cardID));

        String message = "Card has been discarded. Resolve effects manually.";
        String card = Mapper.getExplore(cardID);
        String[] cardInfo = card.split(";");

        String color = cardInfo[1];
        String cardType = cardInfo[3];
        if (cardType.equalsIgnoreCase(Constants.FRAGMENT)) {
            Player player = activeMap.getPlayer(getUser().getId());
            message = "Gained relic fragment";
            switch (color.toLowerCase()) {
                case Constants.CULTURAL:
                    player.setCrf(player.getCrf() + 1);
                    break;
                case Constants.INDUSTRIAL:
                    player.setIrf(player.getIrf() + 1);
                    break;
                case Constants.HAZARDOUS:
                    player.setHrf(player.getHrf() + 1);
                    break;
                default:
                    message = "Invalid fragment type";
                    break;
            }
            activeMap.purgeExplore(cardID);
        } else if (cardType.equalsIgnoreCase(Constants.ATTACH)) {
            String token = cardInfo[5];
            String tokenFilename = Mapper.getAttachmentID(token);
            if (tokenFilename != null) {
            	tile.addToken(tokenFilename, planetName);
                activeMap.purgeExplore(cardID);
                message = "Token added to planet";
            } else {
            	message = "Invalid token";
            }
        } else if (cardType.equalsIgnoreCase(Constants.TOKEN)) {
            String token = cardInfo[5];
            String tokenFilename = Mapper.getTokenID(token);
            if (tokenFilename != null) {
            	tile.addToken(tokenFilename, Constants.SPACE);
                message = "Token added to map";
            } else {
            	message = "Invalid token";
            }
        }

        MapSaveLoadManager.saveMap(activeMap);
        File file = GenerateMap.getInstance().saveImage(activeMap);
        MessageHelper.replyToMessage(event, file);
        MessageHelper.sendMessageToChannel(event.getChannel(), message);
    }
}
