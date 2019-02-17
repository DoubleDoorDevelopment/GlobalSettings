package net.doubledoordev.globalsettings;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod("globalsettings")
public class GlobalSettings
{
     static final String MOD_ID = "globalsettings";

    static Logger LOGGER = LogManager.getLogger();

     static Utils util = new Utils();

    public GlobalSettings()
    {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    //PreInit
    private void setup(final FMLCommonSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new EventHandlers());

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

    private void doClientStuff(final FMLClientSetupEvent event)
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
        } else util.makeMaster();
    }
}
