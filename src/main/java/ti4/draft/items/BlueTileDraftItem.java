package ti4.draft.items;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ti4.commands.milty.MiltyDraftManager;
import ti4.commands.milty.MiltyDraftTile;
import ti4.draft.DraftItem;
import ti4.generator.Mapper;
import ti4.generator.TileHelper;
import ti4.helpers.ButtonHelper;
import ti4.helpers.Emojis;
import ti4.map.Tile;
import ti4.model.*;

public class BlueTileDraftItem extends DraftItem {
    public BlueTileDraftItem(String itemId) {
        super(Category.BLUETILE, itemId);
    }

    @JsonIgnore
    @Override
    public String getShortDescription() {
        return TileHelper.getTile(ItemId).getName() + " (" + ItemId + ")";
    }

    @JsonIgnore
    @Override
    public String getLongDescriptionImpl() {
        TileModel tile = TileHelper.getTile(ItemId);
        StringBuilder sb = new StringBuilder();
        List<String> planetIds = tile.getPlanetIds();
        for (int i = 0; i < planetIds.size() - 1; i++) {
            buildPlanetString(Mapper.getPlanet(planetIds.get(i)), sb);
            sb.append(", ");
        }

        buildPlanetString(Mapper.getPlanet(planetIds.get(planetIds.size() - 1)), sb);

        return sb.toString();
    }

    private void buildPlanetString(PlanetModel planet, StringBuilder sb) {
        sb.append(planet.getName());
        sb.append(planetTypeEmoji(planet.getPlanetType()));
        sb.append(" (");
        sb.append(planet.getResources()).append("/").append(planet.getInfluence());
        if (planet.isLegendary()) {
            sb.append("/").append(Emojis.LegendaryPlanet);
        }
        if (planet.getTechSpecialties() != null) {
            for (var spec : planet.getTechSpecialties()) {
                sb.append("/").append(techSpecEmoji(spec));
            }
        }
        sb.append(") ");
    }

    private String planetTypeEmoji(PlanetTypeModel.PlanetType type) {
        return switch (type) {
            case CULTURAL -> Emojis.Cultural;
            case HAZARDOUS -> Emojis.Hazardous;
            case INDUSTRIAL -> Emojis.Industrial;
            default -> Emojis.GoodDog;
        };
    }

    private String techSpecEmoji(TechSpecialtyModel.TechSpecialty type) {
        return switch (type) {
            case BIOTIC -> Emojis.BioticTech;
            case CYBERNETIC -> Emojis.CyberneticTech;
            case PROPULSION -> Emojis.PropulsionTech;
            case WARFARE -> Emojis.WarfareTech;
            default -> Emojis.GoodDog;
        };
    }

    @JsonIgnore
    @Override
    public String getItemEmoji() {
        return Emojis.SemLor;
    }


    public static List<DraftItem> buildAllDraftableItems(MiltyDraftManager draftManager) {
        List<DraftItem> allItems = new ArrayList<>();
        for (MiltyDraftTile tile : draftManager.getHigh()) {
            allItems.add(DraftItem.Generate(DraftItem.Category.BLUETILE,
                    tile.getTile().getTileID()));
        }
        for (MiltyDraftTile tile : draftManager.getMid()) {
            allItems.add(DraftItem.Generate(DraftItem.Category.BLUETILE,
                    tile.getTile().getTileID()));
        }
        for (MiltyDraftTile tile : draftManager.getLow()) {
            allItems.add(DraftItem.Generate(DraftItem.Category.BLUETILE,
                    tile.getTile().getTileID()));
        }
        DraftErrataModel.filterUndraftablesAndShuffle(allItems, DraftItem.Category.BLUETILE);
        return allItems;
    }
}
