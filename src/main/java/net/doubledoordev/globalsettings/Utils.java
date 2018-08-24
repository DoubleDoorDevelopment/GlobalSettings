package net.doubledoordev.globalsettings;

import com.google.common.base.Splitter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static net.minecraft.client.settings.GameSettings.COLON_SPLITTER;

public class Utils
{

    private File masterFolder;
    private File masterSettingFile;
    File vanillaSettings = ObfuscationReflectionHelper.getPrivateValue(GameSettings.class, Minecraft.getMinecraft().gameSettings, "field_74354_ai", "optionsFile");
    private Map<String, String> masterOptions = new HashMap<>();
    private Map<String, String> options = new HashMap<>();
    private List<String> masterOptionsRaw;
    private List<String> optionsRaw;
    private Properties properties = new Properties();
    private static final Splitter EQUALS_SPLITTER = Splitter.on('=');

    // We need to set the location of the master options file.
    void setMasterFile()
    {
        String optionsLocationProperty = System.getProperty("master.properties.location");

        // First we check to see if there is a system property available.
        if (optionsLocationProperty == null)
        {
            // If our property is null we will default to the user home.
            masterFolder = new File(System.getProperty("user.home"), "minecraftGlobalSettings");
            masterFolder.mkdir();
            masterSettingFile = new File(masterFolder, "masterOptions.txt");
            GlobalSettings.log.warn("No java property and/or non-linux environment! Master settings home is: " + masterSettingFile);
        }
        // If we find one we will use that.
        // TODO: If this path fails it might crash or cause issues, We need to check that.
        else
        {
            // Check to see if we are running in a linux environment and apply the proper location for these.
            if (System.getProperty("os.arch").equals("Linux"))
            {
                masterFolder = new File(System.getenv("XDG_CONFIG_HOME"),"minecraftGlobalSettings");
                masterFolder.mkdir();
                masterSettingFile = new File(masterFolder, "masterOptions.txt");
                GlobalSettings.log.warn("Linux Environment Variable found! Master settings home is: " + masterSettingFile);
            }
            else
            {
                masterFolder = new File(optionsLocationProperty);
                masterFolder.mkdirs();
                masterSettingFile = new File(masterFolder, "masterOptions.txt");
                GlobalSettings.log.warn("Property found! Master settings home is: " + masterSettingFile);
            }
        }
    }

    // Getting the master file location as a file.
     File getMasterFile()
    {
        // We should check if the master file exists before returning it.
        if (!masterSettingFile.exists())
        {
            GlobalSettings.log.warn("Master is missing! Can't return master!");
        }
        // If our file exists return it.
        GlobalSettings.log.warn("Got Master!");
        return masterSettingFile;
    }

    // Does our file exist?
    boolean checkMasterFile()
    {
        if (!masterSettingFile.exists())
        {
            GlobalSettings.log.warn("Master is missing!");
            return false;
        }
        // If our file exists return it.
        GlobalSettings.log.warn("Master exists!");
        return true;
    }

    // We need to make the master file so we have something to write to.
    void makeMaster()
    {
            try
            {
                masterSettingFile.createNewFile();
                GlobalSettings.log.warn("Creating master file in: " + masterSettingFile);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
    }

    // This will update the master file from the settings file by comparing values in the settings to the master.
    void updateMaster()
    {
            for (Map.Entry<String, String> option : options.entrySet())
            {
                    masterOptions.put(option.getKey(), option.getValue());
                    GlobalSettings.log.warn("Adding option: " + option.getKey());
            }
    }

    // We will now take the master map and put it in a properties object for saving.
    void saveMaster()
    {
       properties.clear();
       properties.putAll(masterOptions);
        FileOutputStream fileOutputStream = null;
        try
        {
            fileOutputStream = new FileOutputStream(masterSettingFile);
            properties.store(fileOutputStream, null);
            GlobalSettings.log.warn("Saving file!");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fileOutputStream != null)
            {
                try
                {
                    fileOutputStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    // This populates the maps for getting value pairs for both the vanilla settings and the master settings.
    void getAllOptions()
    {
        // We need to catch file errors.
        FileInputStream masterFileInput = null;
        FileInputStream vanillaFileInput = null;
        try
        {
            masterFileInput = new FileInputStream(masterSettingFile);
            vanillaFileInput = new FileInputStream(vanillaSettings);

            // Read the master and vanilla settings file and put them into a list for manipulation.
            masterOptionsRaw = IOUtils.readLines(masterFileInput, "UTF-8");
            optionsRaw = IOUtils.readLines(vanillaFileInput, "UTF-8");

            // If our master file exists we need to split the values and populate the map with them for comparing. Also handles bad values.
            if (masterSettingFile.exists())
            {
                for (String s : masterOptionsRaw)
                {
                    try
                    {
                        Iterator<String> iterator = EQUALS_SPLITTER.omitEmptyStrings().limit(2).split(s).iterator();
                        masterOptions.put(iterator.next(), iterator.next());
                    }
                    catch (Exception e)
                    {
                        GlobalSettings.log.warn("Skipping bad master option: {}", s);
                    }
                }
            }

            // Take the vanilla options and do the same, Split and store.
            for (String s : optionsRaw)
            {
                try
                {
                    Iterator<String> iterator = COLON_SPLITTER.omitEmptyStrings().limit(2).split(s).iterator();
                    options.put(iterator.next(), iterator.next());
                }
                catch (Exception e)
                {
                    GlobalSettings.log.warn("Skipping bad vanilla option: {}", s);
                }
            }
        }
        catch (IOException e)
        {
            e.getStackTrace();
        }
        finally
        {
            if (masterFileInput != null)
            {
                try
                {
                    masterFileInput.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (vanillaFileInput != null)
            {
                try
                {
                    vanillaFileInput.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    boolean shouldAutoLoad()
    {
        GlobalSettings.log.warn("Checking for auto load value...");
        if (masterOptions.containsKey("autoLoad"))
        {
            GlobalSettings.log.warn("Auto loading option exists!");
            if (masterOptions.get("autoLoad").equals("true"))
            {
                GlobalSettings.log.warn("Auto loading true!");
                return true;
            }
            else
            {
                GlobalSettings.log.warn("Auto loading is disabled!");
                return false;
            }
        }
        else
        {
            GlobalSettings.log.warn("Auto loading option is missing! Auto loading disabled. Please enable if you would like auto load to function!");
            return false;
        }
    }

    void updateAutoLoad()
    {
        if (!masterOptions.containsKey("autoLoad"))
        {
            masterOptions.put("autoLoad", "true");
            GlobalSettings.log.warn("Auto Load key is missing! Adding.");
        }
        else if (masterOptions.get("autoLoad").equals("true"))
        {
            masterOptions.replace("autoLoad", "false");
            GlobalSettings.log.warn("Auto Load key exists! Setting to false.");
        }
        else
        {
            masterOptions.replace("autoLoad", "true");
            GlobalSettings.log.warn("Auto Load key exists! Setting to true.");
        }
    }

    void replaceVanillaOptions()
    {
        // Writes all the options in a vanilla format to the options.txt file
        PrintWriter printWriter = null;
        try
        {
            printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.vanillaSettings), StandardCharsets.UTF_8));

            for (Map.Entry<String, String> option : masterOptions.entrySet())
            {
                printWriter.println(option.getKey() + ":" + option.getValue());
            }
            GlobalSettings.log.warn("Replaced vanilla file!");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
           if (printWriter != null)
               printWriter.close();
        }

        // We need to load our settings now so it is applied to minecraft.
        Minecraft.getMinecraft().gameSettings.loadOptions();
        // Then we need to apply the sound changes to the game else they never update.
        for (String sound: SoundCategory.getSoundCategoryNames())
            Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.getByName(sound), Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.getByName(sound)));
    }
}
