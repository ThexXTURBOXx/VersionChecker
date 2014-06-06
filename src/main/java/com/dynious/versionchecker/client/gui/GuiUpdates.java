package com.dynious.versionchecker.client.gui;

import com.dynious.versionchecker.handler.DownloadThread;
import com.dynious.versionchecker.handler.Update;
import com.dynious.versionchecker.helper.WebHelper;
import com.dynious.versionchecker.lib.Resources;
import com.dynious.versionchecker.lib.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiUpdates extends GuiScreen
{
    private GuiUpdateList updateList;
    private GuiButton updateButton;
    private GuiButton closeButton;

    private Update openUpdate = null;

    private int windowStartX, windowStartY, windowEndX, windowEndY;

    private static final int listShift = 50;

    @Override
    @SuppressWarnings("unchecked")
    public void initGui()
    {
        super.initGui();

        windowStartX = width / 2 - 110 + listShift;
        windowStartY = height / 2 - 90;
        windowEndX = width / 2 + 110 + listShift;
        windowEndY = height / 2 + 70;

        buttonList.add(new GuiButton(0, width / 2 - 75, height - 30, 150, 20, StatCollector.translateToLocal("gui.done")));
        buttonList.add(updateButton = new GuiButton(1, width / 2 - 100 + listShift, height / 2 + 40, 96, 20, StatCollector.translateToLocal(Strings.UPDATE)));
        updateButton.visible = false;
        buttonList.add(closeButton = new GuiButton(2, width / 2 + 4 + listShift, height / 2 + 40, 96, 20, StatCollector.translateToLocal("gui.done")));
        closeButton.visible = false;
        updateList = new GuiUpdateList(this, 300, 180, height / 2 - 100, height / 2 + 80, width / 2 - 150 + listShift);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float par3)
    {
        updateList.drawScreen(mouseX, mouseY, par3);
        this.fontRendererObj.drawSplitString(StatCollector.translateToLocal(Strings.INFO).replace(";", "\n"), 10, height / 2 - 60, width / 2 - 150 + listShift - 20, 0xCCCCCC);
        if (openUpdate != null)
        {
            drawUpdateWindow();
            drawCenteredString(fontRendererObj, openUpdate.displayName, width / 2 + listShift, height / 2 - 80, 0xFFFFFF);
            if (openUpdate.changeLog != null)
            {
                this.fontRendererObj.drawSplitString(openUpdate.changeLog, width / 2 - 100 + listShift, height / 2 - 60, 200, 0xCCCCCC);
            }
            else
            {
                drawCenteredString(fontRendererObj, StatCollector.translateToLocal(Strings.NO_CHANGE_LOG), width / 2 + listShift, height / 2 - 60, 0xCCCCCC);
            }
        }
        if (DownloadThread.isUpdating())
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getMinecraft().renderEngine.bindTexture(Resources.GUI_ICONS);
            Gui.func_146110_a(width - 20, 4, 0, 0, 16, 16, 32, 32);
        }

        super.drawScreen(mouseX, mouseY, par3);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        switch (button.id)
        {
            case 0:
                Minecraft.getMinecraft().displayGuiScreen(null);
                break;
            case 1:
                if (!openUpdate.isDirectLink)
                {
                    WebHelper.openWebpage(openUpdate.updateURL);
                }
                else
                {
                    DownloadThread.downloadUpdate(openUpdate);
                }
                closeInfoScreen();
                break;
            case 2:
                closeInfoScreen();
                break;
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int par3)
    {
        if (openUpdate == null)
        {
            super.mouseClicked(x, y, par3);
        }
        else
        {
            if (x > windowStartX && x < windowEndX && y > windowStartY && y < windowEndY)
            {
                super.mouseClicked(x, y, par3);
            }
            else
            {
                closeInfoScreen();
            }
        }
    }

    public FontRenderer getFontRenderer()
    {
        return fontRendererObj;
    }

    public void openInfoScreen(Update update)
    {
        openUpdate = update;
        updateButton.visible = true;
        closeButton.visible = true;
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
        if (update.isDirectLink)
        {
            updateButton.displayString = StatCollector.translateToLocal(Strings.UPDATE);
            updateButton.enabled = !update.isDownloaded();
        }
        else
        {
            updateButton.displayString = StatCollector.translateToLocal(Strings.OPEN_WEBPAGE);
            updateButton.enabled = update.updateURL != null;
        }
    }

    public void closeInfoScreen()
    {
        openUpdate = null;
        updateButton.visible = false;
        closeButton.visible = false;
    }

    public void drawUpdateWindow()
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        Tessellator tessellator = Tessellator.instance;
        this.mc.getTextureManager().bindTexture(optionsBackground);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(4210752);
        tessellator.addVertexWithUV(windowStartX, windowEndY, 0.0D, 0.0D,  (windowEndY - windowStartY) / f);
        tessellator.addVertexWithUV(windowEndX, windowEndY, 0.0D, (windowEndX - windowStartX) / f, (windowEndY - windowStartY) / f);
        tessellator.addVertexWithUV(windowEndX, windowStartY, 0.0D, (windowEndX - windowStartX) / f, 0);
        tessellator.addVertexWithUV(windowStartX, windowStartY, 0.0D, 0.0D, 0);
        tessellator.draw();
    }
}
