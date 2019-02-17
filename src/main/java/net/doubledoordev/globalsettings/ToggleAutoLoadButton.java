package net.doubledoordev.globalsettings;

import net.minecraft.client.gui.GuiButton;

import static net.doubledoordev.globalsettings.GlobalSettings.util;

public class ToggleAutoLoadButton extends GuiButton
{
    public ToggleAutoLoadButton(int p_i46323_1_, int p_i46323_2_, int p_i46323_3_, int p_i46323_4_, int p_i46323_5_, String p_i46323_6_)
    {
        super(p_i46323_1_, p_i46323_2_, p_i46323_3_, p_i46323_4_, p_i46323_5_, p_i46323_6_);
    }

    @Override
    public void onClick(double p_194829_1_, double p_194829_3_)
    {
        GlobalSettings.LOGGER.info("Changing auto-load option!");
        util.updateAutoLoad();
        util.saveMaster();
        this.displayString = "Auto-Load Options: " + util.getAutoloadState();
    }
}
