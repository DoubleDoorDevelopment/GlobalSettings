package net.doubledoordev.globalsettings;

import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;
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

            int width = e.getGui().field_230708_k_;
            int height = e.getGui().field_230709_l_;

            e.addWidget(new Button(width / 2 - 195, height / 6 + 168, 90, 20, new TranslationTextComponent("globalsettings.load"), (button) -> {
                GlobalSettings.LOGGER.info("Attempting to load master file!");
                util.replaceVanillaOptions();
                GlobalSettings.LOGGER.info("Loaded Global options!");
            }));
            e.addWidget(new Button(width / 2 + 105, height / 6 + 168, 90, 20, new TranslationTextComponent("globalsettings.update"), (button) -> {
                GlobalSettings.LOGGER.info("Updating master file!");
                util.getAllOptions();
                util.updateMaster();
                util.saveMaster();
            }));
            TranslationTextComponent autoLoadMessage = new TranslationTextComponent("globalsettings.autoload", util.getAutoloadState().toUpperCase());
            e.addWidget(new Button(width / 2 - 70, height / 6 + 144, 144, 20, autoLoadMessage, (button) -> {
                GlobalSettings.LOGGER.info("Changing auto-load option!");
                util.updateAutoLoad();
                util.saveMaster();
                button.func_238482_a_(new TranslationTextComponent("globalsettings.autoload", util.getAutoloadState().toUpperCase()));
            }));
        }

    }
}
