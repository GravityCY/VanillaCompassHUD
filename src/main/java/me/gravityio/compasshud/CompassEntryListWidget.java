package me.gravityio.compasshud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 *  I DO NOT KNOW WHAT I'M DOING
 */
public class CompassEntryListWidget extends EntryListWidget<CompassEntryListWidget.CompassEntryWidget> {
    private final Text empty;
    private final int emptyWidth;
    public CompassEntryListWidget(MinecraftClient client, int sx, int sy, int mx, int my, int itemHeight) {
        super(client, mx - sx, my - sy, sy, my, itemHeight);
        super.setLeftPos(sx);
        super.setRenderBackground(false);
        super.setRenderHorizontalShadows(false);
        Helper.getCompasses(client.player).forEach(stack -> {
            super.addEntry(new CompassEntryWidget(stack, client.getItemRenderer(), client.textRenderer));
        });
        this.empty = Text.translatable("compasshud.empty");
        this.emptyWidth = this.client.textRenderer.getWidth(this.empty);
    }

    @Override
    public int getRowWidth() {
        return super.width;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderHelper.fillBoxWithGradientOutline(matrices, super.left, super.top, super.right, super.bottom, 0x88101010, 0x00101010, 20);
        if (super.getEntryCount() == 0) {
            matrices.push();
            matrices.scale(0.6f, 0.6f, 1f);
            matrices.translate(340, 35, 0);
            this.client.textRenderer.draw(matrices, this.empty, super.left + (float) super.width / 2 - (float) this.emptyWidth / 2 , super.top + 4, 0xffffffff);
            matrices.pop();
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.right - 20;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    public static class CompassEntryWidget extends Entry<CompassEntryWidget> {
        private final ItemStack stack;
        private final ItemRenderer itemRenderer;
        private final TextRenderer textRenderer;

        public CompassEntryWidget(ItemStack stack, ItemRenderer itemRenderer, TextRenderer textRenderer) {
            this.stack = stack;
            this.itemRenderer = itemRenderer;
            this.textRenderer = textRenderer;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            x += 16;
            MatrixStack matrices2 = new MatrixStack();
            this.itemRenderer.renderInGuiWithOverrides(matrices2, this.stack, x, y);
            this.textRenderer.draw(matrices2, this.stack.getName(), x + 20, y + 4, 0xffffffff);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            CompassHudMod.INSTANCE.setCurrentItem(this.stack);
            return true;
        }
    }
}
