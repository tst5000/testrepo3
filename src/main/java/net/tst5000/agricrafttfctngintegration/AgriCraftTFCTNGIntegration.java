package net.tst5000.agricrafttfctngintegration;

import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(
        modid = AgriCraftTFCTNGIntegration.MODID,
        name = AgriCraftTFCTNGIntegration.NAME,
        version = AgriCraftTFCTNGIntegration.VERSION,
        acceptedMinecraftVersions = AgriCraftTFCTNGIntegration.MC_VERSION,
        dependencies
                = "required-after:agricraft@[2.12.0-1.12.0-a6,];"
                + "required-after:tfc;"
)
@AgriPlugin
public class AgriCraftTFCTNGIntegration {
    public static final String MODID = "agricrafttfctngintegration";
    public static final String NAME = "AgriCraft TFC-TNG Integration";
    public static final String VERSION = "0.1.0";
    public static final String MC_VERSION = "[1.12.2]";

    public static final Logger LOGGER = LogManager.getLogger(AgriCraftTFCTNGIntegration.MODID);

    public AgriCraftTFCTNGIntegration() {
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info(AgriCraftTFCTNGIntegration.NAME + " says hi!");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }
}
