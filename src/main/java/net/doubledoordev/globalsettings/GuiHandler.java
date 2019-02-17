package net.doubledoordev.globalsettings;

import net.minecraft.client.gui.GuiOptions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.doubledoordev.globalsettings.GlobalSettings.util;


@Mod.EventBusSubscriber(modid = GlobalSettings.MOD_ID, value = Dist.CLIENT)
public class GuiHandler
{
    @SubscribeEvent
    public static void open(GuiScreenEvent.InitGuiEvent.Post e)
    {
        String autoLoadText;

        // If we have a GUI that is GUIOptions we will add our load button.
        if (e.getGui() instanceof GuiOptions)
        {
            //GlobalSettings.LOGGER.info("Checking auto load for toggle button text.");
//            if (GlobalSettings.util.shouldAutoLoad())
//                autoLoadText = "Auto-Load Options: True";
//            else autoLoadText = "Auto-Load Options: False";

            e.addButton(new LoadButton(1568123, e.getGui().width / 2 - 195, e.getGui().height / 6 + 168, 90, 20, "Load Master"));
            e.addButton(new UpdateButton(1568124, e.getGui().width / 2 + 105, e.getGui().height / 6 + 168, 90, 20, "Update Master"));
            e.addButton(new ToggleAutoLoadButton(1568125, e.getGui().width / 2 - 70, e.getGui().height / 6 + 144, 140, 20, "Auto-Load Options: " + util.getAutoloadState()));
        }

    }
}
