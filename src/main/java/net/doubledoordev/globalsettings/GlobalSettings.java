package net.doubledoordev.globalsettings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

@Mod(
        modid = GlobalSettings.MOD_ID,
        name = GlobalSettings.MOD_NAME,
        version = GlobalSettings.VERSION,
        clientSideOnly = true
)
public class GlobalSettings
{
    public static final String MOD_ID = "globalsettings";
    public static final String MOD_NAME = "GlobalSettings";
    public static final String VERSION = "1.0.0";

    Logger log;

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
        // Lets get the users home and place a file with our settings in it.

        File settingFile = ObfuscationReflectionHelper.getPrivateValue(GameSettings.class, Minecraft.getMinecraft().gameSettings, "field_74354_ai", "optionsFile");
        log.info("Original settings path: " + settingFile.getAbsolutePath());

        // Create folder if required
        settingFile = new File(System.getProperty("user.home"), "minecraftglobalsettings");
        settingFile.mkdirs();

        // Create file if required
        settingFile = new File(settingFile, "options.txt");
        if (!settingFile.exists())
        {
            try
            {
                settingFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        ObfuscationReflectionHelper.setPrivateValue(GameSettings.class, Minecraft.getMinecraft().gameSettings, settingFile, "field_74354_ai", "optionsFile");
        log.info("New settings path: " + settingFile.getAbsolutePath());
    }
}
