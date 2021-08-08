package net.doubledoordev.globalsettings;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.common.base.Splitter;
import org.apache.commons.io.IOUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;

public class Utils
{
    private File masterFolder;
    private File masterSettingFile;
    private static final Splitter OPTION_SPLITTER = Splitter.on(':').limit(2);
    private Map<String, String> masterOptions = new HashMap<>();
    private Map<String, String> options = new HashMap<>();
    private List<String> masterOptionsRaw;
    private List<String> optionsRaw;
    private Properties properties = new Properties();
    private static final Splitter EQUALS_SPLITTER = Splitter.on('=');
    File vanillaSettings = Minecraft.getInstance().options.getFile();//ObfuscationReflectionHelper.getPrivateValue(GameSettings.class, Minecraft.getInstance().options, "field_74354_ai");
    private TranslatableComponent autoLoadFalse = new TranslatableComponent("globalsettings.autoload.button.false");
    private TranslatableComponent autoLoadTrue = new TranslatableComponent("globalsettings.autoload.button.true");

    // We need to set the location of the master options file. Java property takes precedence over everything.
    void setMasterFile()
    {
        String optionsLocationProperty = System.getProperty("master.properties.location");

        // First we check to see if there is a system property available.
        if (optionsLocationProperty == null)
        {
            setMasterLocation();
            GlobalSettings.LOGGER.info("No java property found!");
        }
        // If we do have a property then use that. We also need to check to make sure we can write there...
        else
        {
            masterFolder = new File(optionsLocationProperty);
            if (masterFolder.mkdirs() || masterFolder.canWrite())
            {
                masterFolder.mkdir();
                masterSettingFile = new File(masterFolder, "masterOptions.txt");
                GlobalSettings.LOGGER.info("Using java property! Master settings home is: " + masterSettingFile);
            }
            else
            {
                GlobalSettings.LOGGER.error("Java property is invalid or can't be written! Switching to default checks!");
                setMasterLocation();
            }
        }
    }

    // Checks for linux otherwise we use user home.
    void setMasterLocation()
    {
        // If we are in linux use the special place
        GlobalSettings.LOGGER.warn("Looking for Linux...");
        if (System.getProperty("os.name").toLowerCase().contains("linux"))
        {
            masterFolder = new File(Optional.ofNullable(System.getenv("XDG_CONFIG_DIR")).orElseGet(() -> System.getProperty("user.home") + File.separator + ".config"), "minecraftGlobalSettings");
            masterFolder.mkdir();
            masterSettingFile = new File(masterFolder, "masterOptions.txt");
            GlobalSettings.LOGGER.info("Linux environment found! Master settings home is: " + masterSettingFile);
        }
        else
        {
            // If our property is null and its not linux, we will default to the user home.
            masterFolder = new File(System.getProperty("user.home"), "minecraftGlobalSettings");
            masterFolder.mkdir();
            masterSettingFile = new File(masterFolder, "masterOptions.txt");
            GlobalSettings.LOGGER.info("No linux environment found! Master settings home is: " + masterSettingFile);
        }
    }
    // Getting the master file location as a file.
     File getMasterFile()
    {
        // We should check if the master file exists before returning it.
        if (!masterSettingFile.exists())
        {
            GlobalSettings.LOGGER.error("Master is missing! Can't return master!");
        }
        // If our file exists return it.
        GlobalSettings.LOGGER.info("Got Master!");
        return masterSettingFile;
    }

    // Does our file exist?
    boolean checkMasterFile()
    {
        if (!masterSettingFile.exists())
        {
            GlobalSettings.LOGGER.error("Master is missing!");
            return false;
        }
        // If our file exists return it.
        GlobalSettings.LOGGER.info("Master exists!");
        return true;
    }

    // We need to make the master file so we have something to write to.
    void makeMaster()
    {
            try
            {
                masterSettingFile.createNewFile();
                GlobalSettings.LOGGER.info("Creating master file in: " + masterSettingFile);
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
                GlobalSettings.LOGGER.info("Adding option: " + option.getKey());
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
            GlobalSettings.LOGGER.info("Saving file!");
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
                        GlobalSettings.LOGGER.warn("Skipping bad master option: {}", s);
                    }
                }
            }

            // Take the vanilla options and do the same, Split and store.
            for (String s : optionsRaw)
            {
                try
                {
                    Iterator<String> iterator = OPTION_SPLITTER.omitEmptyStrings().limit(2).split(s).iterator();
                    options.put(iterator.next(), iterator.next());
                }
                catch (Exception e)
                {
                    GlobalSettings.LOGGER.warn("Skipping bad vanilla option: {}", s);
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
        GlobalSettings.LOGGER.info("Checking for auto load value...");
        if (masterOptions.containsKey("autoLoad"))
        {
            GlobalSettings.LOGGER.info("Auto loading option exists!");
            if (masterOptions.get("autoLoad").equals("true"))
            {
                GlobalSettings.LOGGER.info("Auto loading true!");
                return true;
            }
            else
            {
                GlobalSettings.LOGGER.info("Auto loading is disabled!");
                return false;
            }
        }
        else
        {
            GlobalSettings.LOGGER.warn("Auto loading option is missing! Auto loading disabled. Please enable if you would like auto load to function!");
            return false;
        }
    }

    void updateAutoLoad()
    {
        if (!masterOptions.containsKey("autoLoad"))
        {
            masterOptions.put("autoLoad", "true");
            GlobalSettings.LOGGER.warn("Auto Load key is missing! Adding.");
        }
        else if (masterOptions.get("autoLoad").equals("true"))
        {
            masterOptions.replace("autoLoad", "false");
            GlobalSettings.LOGGER.info("Auto Load key exists! Setting to false.");
        }
        else
        {
            masterOptions.replace("autoLoad", "true");
            GlobalSettings.LOGGER.info("Auto Load key exists! Setting to true.");
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
            GlobalSettings.LOGGER.info("Replaced vanilla file!");
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
        Minecraft.getInstance().options.load();
        // Then we need to apply the sound changes to the game else they never update.
        for (SoundSource sound : SoundSource.values())
        {
            Minecraft.getInstance().options.setSoundCategoryVolume(sound, Minecraft.getInstance().options.getSoundSourceVolume(sound));
        }
    }

    TranslatableComponent getAutoloadState()
    {
        if (masterOptions.getOrDefault("autoLoad", "false").equals("false"))
            return autoLoadFalse;
        else
        {
            return autoLoadTrue;
        }
    }
}
