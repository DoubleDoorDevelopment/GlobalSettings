package net.doubledoordev.globalsettings;

import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static net.doubledoordev.globalsettings.GlobalSettings.util;

public class EventHandlers
{

    // Fires after button press but before the button acts.
    @SubscribeEvent
    public void onGuiActionPerformed(GuiScreenEvent.ActionPerformedEvent.Pre event)
    {
        // Auto Load Toggle button.
        // Toggles the text on the
        if (event.getGui() instanceof GuiOptions && event.getButton().id == 1568125)
        {
            GlobalSettings.LOGGER.info("Getting correct value for auto load button.");
            if (util.shouldAutoLoad())
            {
                event.getButton().displayString = "Auto-Load Options: True";
                GlobalSettings.LOGGER.info("Changing auto load value to " + util.shouldAutoLoad());
            }
            else
            {
                event.getButton().displayString = "Auto-Load Options: False";
                GlobalSettings.LOGGER.info("Changing auto load value to " + util.shouldAutoLoad());
            }
        }

        // Replaces and loads settings only if auto load is enabled. Otherwise acts like vanilla.
        if (event.getGui() instanceof GuiMainMenu && event.getButton().id == 0 || event.getGui() instanceof GuiIngameMenu && event.getButton().id == 0)
        {
            GlobalSettings.LOGGER.info("Checking auto load options for replacing options in options screen.");
            if (util.shouldAutoLoad())
            {
                GlobalSettings.LOGGER.info("Loading master options for options screen!");
                util.replaceVanillaOptions();
                GlobalSettings.LOGGER.info("Loaded Global options!");
            }
            GlobalSettings.LOGGER.warn("Auto loading disabled, Manually load master options with load button!");
        }
    }
}
