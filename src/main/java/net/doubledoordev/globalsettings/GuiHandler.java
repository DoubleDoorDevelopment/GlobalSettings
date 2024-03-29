package net.doubledoordev.globalsettings;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.network.chat.TranslatableComponent;
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
        // If we have a GUI that is GUIOptions we will add our load button.
        if (e.getGui() instanceof OptionsScreen)
        {
            //Check to see if we need to replace the options now.
            GlobalSettings.LOGGER.info("Checking auto load options for replacing options in options screen.");
            if (util.shouldAutoLoad())
            {
                GlobalSettings.LOGGER.info("Loading master options for options screen!");
                util.replaceVanillaOptions();
                GlobalSettings.LOGGER.info("Loaded Global options!");
            }
            GlobalSettings.LOGGER.warn("Auto loading disabled, Manually load master options with load button!");

            e.addWidget(new Button(e.getGui().width / 2 - 195, e.getGui().height / 6 + 168, 90, 20, new TranslatableComponent("globalsettings.master.load.button"), (button) -> {
                GlobalSettings.LOGGER.info("Attempting to load master file!");
                util.replaceVanillaOptions();
                GlobalSettings.LOGGER.info("Loaded Global options!");
            }));
            e.addWidget(new Button(e.getGui().width / 2 + 105, e.getGui().height / 6 + 168, 90, 20, new TranslatableComponent("globalsettings.master.update.button"), (button) -> {
                GlobalSettings.LOGGER.info("Updating master file!");
                util.getAllOptions();
                util.updateMaster();
                util.saveMaster();
            }));
            e.addWidget(new Button(e.getGui().width / 2 - 70, e.getGui().height / 6 + 144, 144, 20, util.getAutoloadState(), (button) -> {
                GlobalSettings.LOGGER.info(util.getAutoloadState().getString());
                util.updateAutoLoad();
                util.saveMaster();
                button.setMessage(util.getAutoloadState());
            }));
        }
    }
}
