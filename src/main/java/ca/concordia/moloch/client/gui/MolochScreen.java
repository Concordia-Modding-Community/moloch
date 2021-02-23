package ca.concordia.moloch.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import ca.concordia.moloch.Resources;
import ca.concordia.moloch.container.MolochContainer;
import ca.concordia.moloch.tileentity.MolochTileEntity;
import ca.concordia.moloch.tileentity.moloch.Progression;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        MolochTileEntity tileEntity = this.container.getTitleEntity();

        this.font.func_243248_b(matrixStack, tileEntity.getDisplayName(), (float)this.titleX, (float)this.titleY, 4210752);
        this.font.func_243248_b(matrixStack, this.playerInventory.getDisplayName(), (float)this.playerInventoryTitleX, (float)this.playerInventoryTitleY, 4210752);
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

        MolochTileEntity tileEntity = this.container.getTitleEntity();
        Progression progression = tileEntity.getProgression();

        float flameOffset = progression.getCompletionFraction();

        // Render flame.
        this.blit(matrixStack, x + 83, y + 57 + (int)(13 * (1 - flameOffset)), 176, (int) (13 * (1 - flameOffset)), 13, (int) (13 * flameOffset));

        int arrowProgress = (int) (23 * progression.getCompletionFraction());

        // Render arrow.
        this.blit(matrixStack, x + 79, y + 34, 176, 14, arrowProgress, 16);
    }
}
