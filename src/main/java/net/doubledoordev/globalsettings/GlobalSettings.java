package net.doubledoordev.globalsettings;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;


@Mod("globalsettings")
public class GlobalSettings
{
    static final String MOD_ID = "globalsettings";

    static Logger LOGGER = LogManager.getLogger();

    static Utils util;

    public GlobalSettings()
    {
        // Because I'm too fucking lazy to do this proper because forge had to make a mess of client only mods because stupid.
        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            // Register the setup method for modloading
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
            // Register the doClientStuff method for modloading
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::handleConfigFile);
            // Register ourselves for server and other game events we are interested in
            MinecraftForge.EVENT_BUS.register(this);
        }

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(
                () -> "Forge making stupid decisions 101. Morons", (string, stupid) -> stupid));
    }

    //PreInit
    public void clientSetup(final FMLClientSetupEvent event)
    {
        util = new Utils();
        util.setMasterFile();
        // We need to make sure the options.txt file is there before we try to write to it because fresh instances don't have it.
        if (!util.vanillaSettings.exists())
        {
            try
            {
                util.vanillaSettings.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    // Must be done in InterModEnqueueEvent or the sounds aren't there yet to be set.
    public void handleConfigFile(final InterModEnqueueEvent event)
    {
        // Do our magic if we have a master file and auto load is enabled. Otherwise just make one.
        if (util.checkMasterFile())
        {
            util.getAllOptions();
            GlobalSettings.LOGGER.info("Auto loading file check OK!");
            if (util.shouldAutoLoad())
            {
                GlobalSettings.LOGGER.info("Auto loading master settings!");
                util.replaceVanillaOptions();
                GlobalSettings.LOGGER.info("Loaded Global options!");
            }
        }
        else util.makeMaster();
    }
}
