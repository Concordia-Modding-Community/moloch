package ca.concordia.moloch.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import ca.concordia.moloch.Resources;
import ca.concordia.moloch.container.MolochOPContainer;
import ca.concordia.moloch.tileentity.MolochTileEntity;
import ca.concordia.moloch.tileentity.moloch.Action;
import ca.concordia.moloch.tileentity.moloch.Desire;
import ca.concordia.moloch.tileentity.moloch.Progression;
import ca.concordia.moloch.tileentity.moloch.Punishment;
import ca.concordia.moloch.tileentity.moloch.Reward;
import ca.concordia.moloch.tileentity.moloch.State;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MolochOPScreen extends ContainerScreen<MolochOPContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Resources.MOD_ID,
            "textures/gui/container/moloch_op.png");

    private TextFieldWidget molochNameField;
    private List<ArrayList<TextFieldWidget>> desireFields;
    private Button desireAddButton;
    private Button desireRemoveButton;
    private List<TextFieldWidget> rewardFields;
    private Button rewardAddButton;
    private Button rewardRemoveButton;
    private List<TextFieldWidget> punishmentFields; 
    private Button punishmentAddButton;
    private Button punishmentRemoveButton;

    public MolochOPScreen(MolochOPContainer screenContainer, PlayerInventory playerInventory, ITextComponent titleIn) {
        super(screenContainer, playerInventory, titleIn);
        this.guiLeft = 0;
        this.guiTop = 0;
        this.xSize = 176;
        this.ySize = 166;
        this.desireFields = new ArrayList<ArrayList<TextFieldWidget>>();
        this.rewardFields = new ArrayList<TextFieldWidget>();
        this.punishmentFields = new ArrayList<TextFieldWidget>();
    }

    private <T, U> void updateFieldCount(
        List<T> source,
        List<U> target,
        Function<T, U> create,
        Consumer<U> destroy
    ) {
        int sourceCount = source.size();
        int targetCount = target.size();
        int index = targetCount;

        if(sourceCount > targetCount) {
            index--;
            
            while(sourceCount > ++index) {
                target.add(create.apply(source.get(index)));
            }
        } else if(sourceCount < targetCount) {
            while(sourceCount <= --index) {
                destroy.accept(target.remove(index));
            }
        }
    }

    private <T extends Action> void updateActionFieldCount(
        MolochTileEntity tileEntity,
        List<T> actions, 
        List<TextFieldWidget> target
    ) {
        updateFieldCount(actions, target, 
            action -> {
                TextFieldWidget textField = createTextField(action.getCommand(), 
                    text -> {
                        action.setCommand(text);
            
                        tileEntity.markDirtyClient();
                    }
                );

                return textField;
            }, 
            textField -> {
                this.children.remove(textField);
            }
        );
    }

    private void updateDesireFieldCount(
        MolochTileEntity tileEntity,
        List<Desire> desires
    ) {
        updateFieldCount(desires, this.desireFields, 
            desire -> {
                ArrayList<TextFieldWidget> fields = new ArrayList<TextFieldWidget>();

                fields.add(createTextField(
                    desire.getItemString(),
                    text -> {
                        desire.setItem(text);
            
                        tileEntity.markDirtyClient();
                    }
                ));

                fields.add(createTextField(
                    String.valueOf(desire.getCount()), 
                    text -> {
                        try {
                            desire.setCount(Integer.parseInt(text));
            
                            tileEntity.markDirtyClient();
                        } catch(Exception e) {}
                    }
                ));

                return fields;
            },
            listField -> {
                for(TextFieldWidget textField : listField) {
                    this.children.remove(textField);
                }
            } 
        );
    }

    private void updateTextFieldCounts() {
        MolochTileEntity tileEntity = this.container.getTitleEntity();
        Progression progression = tileEntity.getProgression();
        Optional<State> oState = progression.getCurrentState();

        List<Desire> desires;
        List<Reward> rewards;
        List<Punishment> punishments;

        if(oState.isPresent()) {
            State state = oState.get();

            desires = state.getDesires();
            rewards = state.getRewards();
            punishments = state.getPunishments();
        } else {
            desires = new ArrayList<Desire>();
            rewards = new ArrayList<Reward>();
            punishments = new ArrayList<Punishment>();
        }

        updateDesireFieldCount(tileEntity, desires);
        updateActionFieldCount(tileEntity, rewards, this.rewardFields);
        updateActionFieldCount(tileEntity, punishments, this.punishmentFields);
    }

    private int updateLayoutTitle(int x, int y, int width, int yOffset) {
        this.molochNameField.x = x + 10;
        this.molochNameField.setWidth(width - 20);
        this.molochNameField.y = yOffset;

        return yOffset + 25;
    }

    private int updateLayoutDesires(int x, int y, int width, int yOffset) {
        for(ArrayList<TextFieldWidget> listField : this.desireFields) {
            int itemFieldWidth = (int)(2.0 * (width - 20) / 3.0);
            listField.get(0).setWidth(itemFieldWidth);
            listField.get(0).x = x + 10;
            listField.get(1).setWidth((int)(1.0 * (width - 20) / 3.0));
            listField.get(1).x = x + 10 + itemFieldWidth;

            for(TextFieldWidget textField : listField) {
                textField.y = yOffset;
            }
            
            yOffset += 15;
        }

        this.desireAddButton.x = x + width / 2;
        this.desireAddButton.y = yOffset;

        this.desireRemoveButton.x = x + width / 2 - 20;
        this.desireRemoveButton.y = yOffset;

        return yOffset + 25;
    }

    private int updateLayoutRewards(int x, int y, int width, int yOffset) {
        for(TextFieldWidget textField : this.rewardFields) {
            textField.x = x + 10;
            textField.setWidth(width - 20);
            textField.y = yOffset;

            yOffset += 15;
        }

        this.rewardAddButton.x = x + width / 2;
        this.rewardAddButton.y = yOffset;

        this.rewardRemoveButton.x = x + width / 2 - 20;
        this.rewardRemoveButton.y = yOffset;

        return yOffset + 25;
    }

    private int updateLayoutPunishments(int x, int y, int width, int yOffset) {
        for(TextFieldWidget textField : this.punishmentFields) {
            textField.x = x + 10;
            textField.setWidth(width - 20);
            textField.y = yOffset;

            yOffset += 15;
        }

        this.punishmentAddButton.x = x + width / 2;
        this.punishmentAddButton.y = yOffset;

        this.punishmentRemoveButton.x = x + width / 2 - 20;
        this.punishmentRemoveButton.y = yOffset;

        return yOffset + 25;
    }

    private void updateLayouts() {
        int x = this.guiLeft;
        int y = this.guiTop;
        int width = this.xSize;
        int yOffset = y + 10;

        yOffset = updateLayoutTitle(x, y, width, yOffset);
        yOffset = updateLayoutDesires(x, y, width, yOffset);
        yOffset = updateLayoutRewards(x, y, width, yOffset);
        yOffset = updateLayoutPunishments(x, y, width, yOffset);
    }

    private TextFieldWidget createTextField(String text, Consumer<String> consumer) {
        TextFieldWidget textField = new TextFieldWidget(this.font, 0, 0, 0, 10, null);

        textField.setMaxStringLength(100);
        textField.setEnableBackgroundDrawing(true);
        textField.setVisible(true);
        textField.setTextColor(0xFFFFFF);
        textField.setText(text);
        textField.setResponder(consumer);

        this.children.add(textField);

        return textField;
    }

    private Button createButton(String text, int x, int y, IPressable iPressable) {
        return this.addButton(new Button(x, y, 20, 20, new StringTextComponent(text), iPressable));
    }

    private Button createButton(String text, IPressable iPressable) {
        return this.createButton(text, 0, 0, iPressable);
    }

    private Button createAddFieldButton(
        Consumer<State> consumer
    ) {
        MolochTileEntity tileEntity = this.container.getTitleEntity();
        Progression progression = tileEntity.getProgression();

        return this.createButton("+", button -> {
            Optional<State> oState = progression.getCurrentState();
            
            if(!oState.isPresent()) return;

            State state = oState.get();

            consumer.accept(state);

            tileEntity.markDirtyClient();
        });
    }

    private <T> Button createRemoveFieldButton(
        Function<State, List<T>> provider
    ) {
        MolochTileEntity tileEntity = this.container.getTitleEntity();
        Progression progression = tileEntity.getProgression();

        return this.createButton("-", button -> {
            Optional<State> oState = progression.getCurrentState();
            
            if(!oState.isPresent()) return;

            State state = oState.get();

            List<T> list = provider.apply(state);

            if(list.size() == 0) return;

            list.remove(list.size() - 1);

            tileEntity.markDirtyClient();
        });
    }

    private void initTitle() {
        MolochTileEntity tileEntity = this.container.getTitleEntity();

        this.molochNameField = createTextField(
            tileEntity.getDisplayName().getString(),
            text -> {
                tileEntity.setCustomName(new StringTextComponent(text));
            
                tileEntity.markDirtyClient();
            }
        );
    }

    private void initButtons() {
        MolochTileEntity tileEntity = this.container.getTitleEntity();
        Progression progression = tileEntity.getProgression();

        createButton("<", 0, 0, button -> { 
            progression.previous();

            tileEntity.markDirtyClient();
        });
        createButton(">", 20, 0, button -> {
            progression.next();

            tileEntity.markDirtyClient();
        });
        createButton("+", 40, 0, button -> {
            progression.add(new State());

            tileEntity.markDirtyClient();
        });
        createButton(progression.getActive() ? "O" : "X", 60, 0, button -> {
            progression.setActive(!progression.getActive());

            button.setMessage(new StringTextComponent(progression.getActive() ? "O" : "X"));

            tileEntity.markDirtyClient();
        });

        this.desireAddButton = createAddFieldButton(state -> state.add(new Desire()));
        this.desireRemoveButton = createRemoveFieldButton(state -> state.getDesires());

        this.rewardAddButton = createAddFieldButton(state -> state.add(new Reward()));
        this.rewardRemoveButton = createRemoveFieldButton(state -> state.getRewards());

        this.punishmentAddButton = createAddFieldButton(state -> state.add(new Punishment()));
        this.punishmentRemoveButton = createRemoveFieldButton(state -> state.getPunishments());
    }

    private void setButtonVisibility() {
        MolochTileEntity tileEntity = this.container.getTitleEntity();
        Progression progression = tileEntity.getProgression();
        Optional<State> oState = progression.getCurrentState();

        boolean visible = oState.isPresent();

        this.desireAddButton.visible = visible;
        this.desireRemoveButton.visible = visible;
        this.rewardAddButton.visible = visible;
        this.rewardRemoveButton.visible = visible;
        this.punishmentAddButton.visible = visible;
        this.punishmentRemoveButton.visible = visible;
    }

    @Override
    protected void init() {
        super.init();

        this.initTitle();
        this.initButtons();

        this.updateTextFieldCounts();
        this.updateLayouts();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        switch(keyCode) {
            case 262:
            case 263:
            case 264:
            case 265:
            case 256:
            case 257:
            case 258:
            case 259:
            case 340:
            case 341:
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
    public void tick() {
        this.molochNameField.tick();

        for(ArrayList<TextFieldWidget> listField : this.desireFields) {
            for(TextFieldWidget textField : listField) {
                textField.tick();
            }
        }
        
        for(TextFieldWidget textField : this.rewardFields) {
            textField.tick();
        }

        for(TextFieldWidget textField : this.punishmentFields) {
            textField.tick();
        }
    }

    private void drawText(String text, MatrixStack matrixStack, int x, int y, int color) {
        this.font.func_243248_b(matrixStack, new StringTextComponent(text), (float) x,
                (float) y, color);
    }

    private void drawText(String text, MatrixStack matrixStack, int x, int y) {
        this.drawText(text, matrixStack, x, y, 0x404040);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.updateTextFieldCounts();
        this.setButtonVisibility();
        this.updateLayouts();

        MolochTileEntity tileEntity = this.container.getTitleEntity();
        Progression progression = tileEntity.getProgression();

        drawText(String.format("%d/%d", progression.getIndex(), progression.getNumberOfStates()), matrixStack, this.xSize / 2 - this.xSize / 16, -20, 0xFFFFFF);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX,
            int mouseY) {
        /**
         * Background
         */
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.blit(matrixStack, x, y, 0, 0, this.xSize, this.ySize);

        /**
         * Text Fields
         */
        this.molochNameField.render(matrixStack, mouseX, mouseY, partialTicks);

        for(ArrayList<TextFieldWidget> listField : this.desireFields) {
            for(TextFieldWidget textField : listField) {
                textField.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }
        
        for(TextFieldWidget textField : this.rewardFields) {
            textField.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        for(TextFieldWidget textField : this.punishmentFields) {
            textField.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }
}