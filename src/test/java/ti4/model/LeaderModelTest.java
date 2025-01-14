package ti4.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ti4.generator.Mapper;
import ti4.generator.PositionMapper;
import ti4.generator.TileHelper;
import ti4.helpers.AliasHandler;
import ti4.helpers.Storage;
import ti4.message.BotLogger;

public class LeaderModelTest {

    @BeforeAll
    public static void init() {
        TileHelper.init();
        PositionMapper.init();
        Mapper.init();
        AliasHandler.init();
        Storage.init();
    }

    @Test
    public void testLeaders() {
        for (LeaderModel model : Mapper.getLeaders().values()) {
            assertTrue(model.isValid(), model.getAlias() + ": invalid");
            assertTrue(validateFaction(model), model.getAlias() + ": invalid FactionID");
            assertTrue(validateHomebrewReplacesID(model), model.getAlias() + ": invalid HomebrewReplacesID");
        }
    }

    private boolean validateFaction(LeaderModel model) {
        if (model.getFaction().isEmpty()) return true;
        if (Mapper.isValidFaction(model.getFaction()) || "keleres".equals(model.getFaction())) return true;
        BotLogger.log("Tech **" + model.getAlias() + "** failed validation due to invalid FactionID: `" + model.getFaction() + "`");
        return false;
    }

    private boolean validateHomebrewReplacesID(LeaderModel techModel) {
        if (techModel.getHomebrewReplacesID().isEmpty()) return true;
        if (Mapper.isValidTech(techModel.getHomebrewReplacesID().get())) return true;
        BotLogger.log("Tech **" + techModel.getAlias() + "** failed validation due to invalid HomebrewReplacesID ID: `" + techModel.getHomebrewReplacesID().get() + "`");
        return false;
    }
}
