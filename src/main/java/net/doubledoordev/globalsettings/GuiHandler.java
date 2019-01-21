package net.doubledoordev.globalsettings;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = GlobalSettings.MOD_ID, value = Side.CLIENT)
public class GuiHandler
{
    @SubscribeEvent
    public static void open(GuiScreenEvent.InitGuiEvent.Post e)
    {
        String autoLoadText;

        // If we have a GUI that is GUIOptions we will add our load button.
        if (e.getGui() instanceof GuiOptions)
        {
            GlobalSettings.log.info("Checking auto load for toggle button text.");
            if (GlobalSettings.util.shouldAutoLoad())
                autoLoadText = "Auto-Load Options: True";
            else autoLoadText = "Auto-Load Options: False";

            e.getButtonList().add(new GuiButton(1568123, e.getGui().width / 2 - 195, e.getGui().height / 6 + 168, 90, 20, "Load Master"));
            e.getButtonList().add(new GuiButton(1568124, e.getGui().width / 2 + 105, e.getGui().height / 6 + 168, 90, 20, "Update Master"));
            e.getButtonList().add(new GuiButton(1568125, e.getGui().width / 2 - 70, e.getGui().height / 6 + 144, 140, 20, autoLoadText));
        }

    }
}
