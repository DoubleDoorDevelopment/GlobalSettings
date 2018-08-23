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
        if (GlobalSettings.util.shouldAutoLoad())
            autoLoadText = "Auto-Load Options: True";
        else autoLoadText = "Auto-Load Options: False";
        // If we have a GUI that is GUIOptions we will add our load button.
        if (e.getGui() instanceof GuiOptions)
        {
            e.getButtonList().add(new GuiButton(1568123, 5, 5, 100, 20, "Load Options"));
            e.getButtonList().add(new GuiButton(1568124, 105, 5, 100, 20, "Update Options"));
            e.getButtonList().add(new GuiButton(1568125, 205, 5, 100, 20, autoLoadText));
        }

    }
}
