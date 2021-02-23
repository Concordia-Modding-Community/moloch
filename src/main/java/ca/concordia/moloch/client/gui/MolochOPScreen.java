package ca.concordia.moloch.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import ca.concordia.moloch.Resources;
import ca.concordia.moloch.container.MolochOPContainer;
import ca.concordia.moloch.tileentity.MolochInventory;
import ca.concordia.moloch.tileentity.MolochTileEntity;
import ca.concordia.moloch.tileentity.moloch.Desire;
import ca.concordia.moloch.tileentity.moloch.Reward;
import ca.concordia.moloch.tileentity.moloch.State;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MolochOPScreen extends ContainerScreen<MolochOPContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Resources.MOD_ID,
            "textures/gui/container/moloch_op.png");

    private TextFieldWidget searchField;

    public MolochOPScreen(MolochOPContainer screenContainer, PlayerInventory playerInventory, ITextComponent titleIn) {
        super(screenContainer, playerInventory, titleIn);
        this.guiLeft = 0;
        this.guiTop = 0;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void init() {
        super.init();

        MolochTileEntity molochTileEntity = this.container.getTitleEntity();

        this.searchField = new TextFieldWidget(this.font, this.guiLeft + 8, this.guiTop + 6, 160, 9,
                new TranslationTextComponent("itemGroup.search"));
        this.searchField.setText(molochTileEntity.getDisplayName().getString());
        this.searchField.setMaxStringLength(100);
        this.searchField.setEnableBackgroundDrawing(true);
        this.searchField.setVisible(true);
        this.searchField.setTextColor(16777215);

        this.children.add(this.searchField);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        switch(keyCode) {
            case 340:
            case 341:
            case 259:
            case 256:
                return super.keyPressed(keyCode, scanCode, modifiers);
            default:
                return false;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        MolochTileEntity molochTileEntity = this.container.getTitleEntity();

        StringTextComponent name = new StringTextComponent(this.searchField.getText());

        if(!molochTileEntity.getCustomName().equals(name)) {
            molochTileEntity.setCustomName(name);
            molochTileEntity.markDirtyNetwork();
        }

        this.font.func_243248_b(matrixStack, this.playerInventory.getDisplayName(), (float) this.playerInventoryTitleX,
                (float) this.playerInventoryTitleY, 4210752);

        this.addButton(new Button(
            10, 10, 20, 20, new StringTextComponent("+"), button -> {
                molochTileEntity.getProgression().add(
                    new State()
                        .add(new Desire(new ItemStack(Item.getItemById(1)), 64))
                        .add(new Reward("Carrots", "give @p minecraft:carrots"))
                );

                molochTileEntity.markDirtyNetwork();
            })
        );
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX,
            int mouseY) {
        // Render background.
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.blit(matrixStack, x, y, 0, 0, this.xSize, this.ySize);

        this.searchField.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}