package cope.nebula.client.feature.module.render.hud.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import cope.nebula.client.feature.module.render.HUD;
import cope.nebula.client.feature.module.render.hud.IHUDComponent;
import cope.nebula.util.renderer.FontUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;

public class Info implements IHUDComponent {
    @Override
    public void render(ScaledResolution resolution) {
        int posY = resolution.getScaledHeight() - FontUtil.getHeight() - 1;

        if (mc.currentScreen instanceof GuiChat) {
            posY -= 13;
        }

        if (HUD.speed.getValue()) {
            String text = ChatFormatting.GRAY + "Speed: " + ChatFormatting.RESET + getSpeedInKmh() + " km/h";
            FontUtil.drawString(text, resolution.getScaledWidth() - FontUtil.getWidth(text) - 2.0f, posY, -1);
        }
    }

    private String getSpeedInKmh() {
        return String.format("%.2f", Math.sqrt(
                Math.pow(mc.player.posX - mc.player.prevPosX, 2) +
                        Math.pow(mc.player.posZ - mc.player.prevPosZ, 2)) * 71.2729367892);
    }

    @Override
    public String getName() {
        return "Info";
    }

    @Override
    public boolean isAlwaysActive() {
        return false;
    }
}