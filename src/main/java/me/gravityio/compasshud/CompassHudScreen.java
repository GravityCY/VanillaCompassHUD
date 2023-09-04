package me.gravityio.compasshud;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class CompassHudScreen extends Screen {
    private CompassEntryListWidget entryList;
    protected CompassHudScreen() {
        super(Text.translatable("compasshud.title"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void init() {
        this.entryList = new CompassEntryListWidget(this.client, this.width / 2 + 100, 20, this.width - 25, this.height - 20, 20);
        this.addSelectableChild(this.entryList);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.entryList.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (!CompassHudMod.KEYBIND_OPEN.matchesKey(keyCode, scanCode)) {
            return super.keyReleased(keyCode, scanCode, modifiers);
        }
        this.close();
        return true;
    }
}
