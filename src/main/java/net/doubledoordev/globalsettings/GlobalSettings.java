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
        File settingsFolder = new File(System.getProperty("user.home") + "/minecraftglobalsettings");
        File settings = new File(settingsFolder, "options.txt");

        // optionsFile is private thus we need to do some magic to change it.
        File settingsfile = ObfuscationReflectionHelper.getPrivateValue(GameSettings.class, Minecraft.getMinecraft().gameSettings, "field_74354_ai", "optionsFile");
        try
        {
            log.info("Original settings path: " + settingsfile.getAbsolutePath());
            // If our folder is created....
            if (settingsFolder.mkdir())
            {
                // make a settings file also.
                log.info("Folder missing, Creating folder and settings file.");
                settings.createNewFile();
            }

            // If our folder existed the above is skipped so lets check if our file is there.
            if (settings.exists())
            {
                // if our file exists set the settings to ours.
                log.info("Settings exist changing game settings to our own.");
                ObfuscationReflectionHelper.setPrivateValue(GameSettings.class, Minecraft.getMinecraft().gameSettings, settings, "field_74354_ai", "optionsFile");
            }
            // otherwise make a new file and set the settings.
            else
            {
                log.info("File missing, creating and changing game settings to our own.");
                settings.createNewFile();
                ObfuscationReflectionHelper.setPrivateValue(GameSettings.class, Minecraft.getMinecraft().gameSettings, settings, "field_74354_ai", "optionsFile");
            }

            log.info("New settings path: " + settingsfile.getAbsolutePath());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
