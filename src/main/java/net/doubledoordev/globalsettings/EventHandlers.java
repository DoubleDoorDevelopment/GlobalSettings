package net.doubledoordev.globalsettings;

import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.doubledoordev.globalsettings.GlobalSettings.util;

public class EventHandlers
{
    // This event handles what the buttons should do.
    @SubscribeEvent
    public void onGuiActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event)
    {
        // Load button
        // Replaces and loads from master file.
        if(event.getGui() instanceof GuiOptions && event.getButton().id == 1568123)
        {
            GlobalSettings.log.warn("Attempting to load master file!");
            util.replaceVanillaOptions();
            GlobalSettings.log.warn("Loaded Global options!");
        }

        // Update Master button
        // Updates master file with the current set of options being used.
        if(event.getGui() instanceof GuiOptions && event.getButton().id == 1568124)
        {
            GlobalSettings.log.warn("Updating master file!");
                util.getAllOptions();
                util.updateMaster();
                util.saveMaster();
        }

        // Auto Load Toggle button.
        // Toggles the text on the
        if(event.getGui() instanceof GuiOptions && event.getButton().id == 1568125)
        {
            GlobalSettings.log.warn("Getting correct value for auto load button.");
            if (util.shouldAutoLoad())
            {
                event.getButton().displayString = "Auto-Load Options: True";
                GlobalSettings.log.warn("Changing auto load value to " + util.shouldAutoLoad());
            }
            else
            {
                event.getButton().displayString = "Auto-Load Options: False";
                GlobalSettings.log.warn("Changing auto load value to " + util.shouldAutoLoad());
            }
        }
    }

    // Fires after button press but before the button acts.
    @SubscribeEvent
    public void onGuiActionPerformed(GuiScreenEvent.ActionPerformedEvent.Pre event)
    {
        // Auto Update button
        if (event.getGui() instanceof GuiOptions && event.getButton().id == 1568125)
        {
            GlobalSettings.log.warn("Changing auto-load option!");
            util.updateAutoLoad();
            util.saveMaster();
        }

        // Replaces and loads settings only if auto load is enabled. Otherwise acts like vanilla.
        if (event.getGui() instanceof GuiMainMenu && event.getButton().id == 0 || event.getGui() instanceof GuiIngameMenu && event.getButton().id == 0)
        {
            GlobalSettings.log.warn("Checking auto load options for replacing options in options screen.");
            if (util.shouldAutoLoad())
            {
                GlobalSettings.log.warn("Loading master options for options screen!");
                util.replaceVanillaOptions();
                GlobalSettings.log.warn("Loaded Global options!");
            }
            GlobalSettings.log.warn("Auto loading disabled, Manually load master options with load button!");
        }
    }
}
