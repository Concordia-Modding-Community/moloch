package ca.concordia.moloch.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import ca.concordia.moloch.Resources;
import ca.concordia.moloch.container.MolochContainer;
import ca.concordia.moloch.tileentity.MolochTileEntity;
import ca.concordia.moloch.tileentity.moloch.desire.Desire;
import ca.concordia.moloch.tileentity.moloch.progression.Progression;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

@OnlyIn(Dist.CLIENT)
public class MolochScreen extends ContainerScreen<MolochContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Resources.MOD_ID,
            "textures/gui/container/moloch.png");

    public MolochScreen(MolochContainer screenContainer, PlayerInventory playerInventory, ITextComponent titleIn) {
        super(screenContainer, playerInventory, titleIn);
        this.guiLeft = 0;
        this.guiTop = 0;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    private void drawText(String text, MatrixStack matrixStack, int x, int y, int color) {
        this.font.func_243248_b(matrixStack, new StringTextComponent(text), (float) x,
                (float) y, color);
    }

    private void drawText(ITextComponent text, MatrixStack matrixStack, int x, int y) {
        this.font.func_243248_b(matrixStack, text, (float) x,
                (float) y, 0x404040);
    }

    private void drawText(String text, MatrixStack matrixStack, int x, int y) {
        this.drawText(new StringTextComponent(text), matrixStack, x, y);
    }

    private void drawHoveringText(MatrixStack matrixStack, List<? extends ITextComponent> lines, int mouseX, int mouseY) {
        GuiUtils.drawHoveringText(matrixStack, lines, mouseX - guiLeft, mouseY - guiTop, width, height, 100, font);
    }

    private void drawTimeRemainingTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        MolochTileEntity tileEntity = this.container.getTitleEntity();
        Progression progression = tileEntity.getCurrentProgression();

        if(progression == null) return;

        if(!(guiLeft + 83 < mouseX && mouseX < guiLeft + 96 && guiTop + 55 < mouseY && mouseY < guiTop + 70)) return;

        List<ITextComponent> lines = new ArrayList<ITextComponent>();

        lines.add(new StringTextComponent("Time Remaining").setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.RED))));

        double deltaTime = tileEntity.getCurrentProgression().getEnd() - System.currentTimeMillis();

        lines.add(new StringTextComponent("Day(s): " + (int)(deltaTime / (1000d * 60d * 24d * 365d))));
        lines.add(new StringTextComponent("Hour(s): " + ((int)(deltaTime / (1000d * 60d * 24d)) % 365)));
        lines.add(new StringTextComponent("Minute(s): " + ((int)(deltaTime / (1000d * 60d)) % 60)));
        lines.add(new StringTextComponent("Second(s): " + ((int)(deltaTime / (1000d)) % 60)));

        drawHoveringText(matrixStack, lines, mouseX, mouseY);
    }

    private void drawItems(MatrixStack matrixStack, int mouseX, int mouseY) {
        MolochTileEntity tileEntity = this.container.getTitleEntity();
        Progression progression = tileEntity.getCurrentProgression();

        if(progression == null) return;

        int yOffset = 22;
        for(Desire desire : progression.getDesires()) {
            if(desire.getAmountRemaining() == 0) continue;

            itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(() -> desire.getItem(), 1), 113, yOffset-5);
            itemRenderer.renderItemOverlays(font, new ItemStack(() -> Items.BEDROCK, desire.getAmountRemaining()), 113, yOffset-5);

            if(guiLeft + 110 < mouseX  && mouseX < guiLeft + 131 && guiTop + yOffset - 7 < mouseY && mouseY < guiTop + yOffset + 12) {
                renderTooltip(matrixStack, new ItemStack(() -> desire.getItem(), 1), mouseX - guiLeft, mouseY - guiTop);
            }

            yOffset += 18;
        }
    }

    private void drawTitles(MatrixStack matrixStack, int mouseX, int mouseY) {
        MolochTileEntity tileEntity = this.container.getTitleEntity();

        drawText(tileEntity.getMolochName(), matrixStack, titleX, titleY);
        drawText(playerInventory.getDisplayName(), matrixStack, playerInventoryTitleX, playerInventoryTitleY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        drawTitles(matrixStack, mouseX, mouseY);
        drawTimeRemainingTooltip(matrixStack, mouseX, mouseY);
        drawItems(matrixStack, mouseX, mouseY);
    }

    private void drawFlameProgress(MatrixStack matrixStack, int x, int y) {
        MolochTileEntity tileEntity = this.container.getTitleEntity();
        Progression progression = tileEntity.getCurrentProgression();

        if(progression == null) return;

        float flameOffset = (float)(1.0f - (double)(System.currentTimeMillis() - progression.getStart()) / (double)(progression.getEnd() - progression.getStart()));

        flameOffset = Math.min(1, Math.max(0, flameOffset));

        // Render flame.
        this.blit(matrixStack, x + 83, y + 57 + (int)(13 * (1 - flameOffset)), 176, (int) (13 * (1 - flameOffset)), 13, (int) (13 * flameOffset));
    } 

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTricks, int mouseX,
            int mouseY) {
        // Render background.
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.blit(matrixStack, x, y, 0, 0, this.xSize, this.ySize);

        drawFlameProgress(matrixStack, x, y);
    }
}
