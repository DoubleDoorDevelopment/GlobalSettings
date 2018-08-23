package net.doubledoordev.globalsettings;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

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
     static final String VERSION = "1.0.0";

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
        if (util.checkMasterFile())
        {
            util.getAllOptions();
            GlobalSettings.log.warn("Auto loading file check OK!");
            if (util.shouldAutoLoad())
            {
                GlobalSettings.log.warn("Auto loading master settings!");
                util.replaceVanillaOptions();
            }
        } else util.makeMaster();
    }
}
