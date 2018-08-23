package net.doubledoordev.globalsettings;

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.doubledoordev.globalsettings.GlobalSettings.util;

public class EventHandlers
{
    // This event handles what the buttons should do.
    @SubscribeEvent
    public void onGuiActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event)
    {
        // Load button.
        if(event.getButton().id == 1568123)
        {
            GlobalSettings.log.warn("Attempting to load master file!");
            util.replaceVanillaOptions();
        }

        // Update button
        if(event.getButton().id == 1568124)
        {
            GlobalSettings.log.warn("Updating master file!");
                util.getAllOptions();
                util.updateMaster();
                util.saveMaster();
        }

        // Auto Update button
        if(event.getButton().id == 1568125)
        {
            if (GlobalSettings.util.shouldAutoLoad())
                event.getButton().displayString = "Auto-Load Options: True";
            else event.getButton().displayString = "Auto-Load Options: False";
        }
    }

    @SubscribeEvent
    public void onGuiActionPerformed(GuiScreenEvent.ActionPerformedEvent.Pre event)
    {
        // Auto Update button
        if (event.getButton().id == 1568125)
        {
            GlobalSettings.log.warn("Changing auto-load option!");
            util.updateAutoLoad();
            util.saveMaster();
        }
    }
}
