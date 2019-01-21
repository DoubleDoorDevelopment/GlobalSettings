package net.doubledoordev.globalsettings;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
        modid = GlobalSettings.MOD_ID,
        name = GlobalSettings.MOD_NAME,
        version = GlobalSettings.VERSION,
        clientSideOnly = true
)
public class GlobalSettings
{
     static final String MOD_ID = "globalsettings";
     static final String MOD_NAME = "GlobalSettings";
    static final String VERSION = "3.0.2";

     static Logger log;

     static Utils util = new Utils();

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MOD_ID)
    public static GlobalSettings INSTANCE;

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        log = event.getModLog();
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

    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event)
    {
        // Do our magic if we have a master file and auto load is enabled. Otherwise just make one.
        if (util.checkMasterFile())
        {
            util.getAllOptions();
            GlobalSettings.log.info("Auto loading file check OK!");
            if (util.shouldAutoLoad())
            {
                GlobalSettings.log.info("Auto loading master settings!");
                util.replaceVanillaOptions();
                GlobalSettings.log.info("Loaded Global options!");
            }
        } else util.makeMaster();
    }
}
