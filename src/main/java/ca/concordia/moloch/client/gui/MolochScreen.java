package ca.concordia.moloch.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import ca.concordia.moloch.Resources;
import ca.concordia.moloch.container.MolochContainer;
import ca.concordia.moloch.tileentity.MolochTileEntity;
import ca.concordia.moloch.tileentity.moloch.desire.Desire;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        MolochTileEntity tileEntity = this.container.getTitleEntity();

        drawText(tileEntity.getMolochName(), matrixStack, titleX, titleY);
        drawText(playerInventory.getDisplayName(), matrixStack, playerInventoryTitleX, playerInventoryTitleY);

        if(tileEntity.getCurrentProgression() == null) return;

        int yOffset = 22;
        for(Desire desire : tileEntity.getCurrentProgression().getDesires()) {
            itemRenderer.renderItemIntoGUI(new ItemStack(() -> desire.getItem(), 1), 113, yOffset-5);
            
        	if(desire.getItem().equals(Items.BEDROCK)) {
                drawText("err", matrixStack, 135, yOffset - 1, 0xFF4444);
        	} else if(desire.getAmountRemaining() > 0) {
                drawText("" + desire.getAmountRemaining(), matrixStack, 136, yOffset);
                drawText("" + desire.getAmountRemaining(), matrixStack, 135, yOffset - 1, 0xFFFFFF);
            }
            
            yOffset += 18;
        }
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

        if(tileEntity.getCurrentProgression() == null) return;

        float flameOffset = 1 - Math.min(1, ((tileEntity.getCurrentProgression().getEnd()-System.currentTimeMillis())/1000*60*60*24*7));

        // Render flame.
        this.blit(matrixStack, x + 83, y + 57 + (int)(13 * (1 - flameOffset)), 176, (int) (13 * (1 - flameOffset)), 13, (int) (13 * flameOffset));
    }
}
