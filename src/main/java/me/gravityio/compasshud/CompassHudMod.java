package me.gravityio.compasshud;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompassHudMod implements ModInitializer {
    public static CompassHudMod INSTANCE;
    public static MinecraftClient CLIENT;
    public static String MOD_ID = "compasshud";
    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final KeyBinding KEYBIND_OPEN = new KeyBinding("key." + MOD_ID + ".open", GLFW.GLFW_KEY_J, "key.categories.compasshud");
    private ItemStack current = ItemStack.EMPTY;

    @Override
    public void onInitialize() {
        INSTANCE = this;

        KeyBindingHelper.registerKeyBinding(KEYBIND_OPEN);
        CLIENT = MinecraftClient.getInstance();
        ClientTickEvents.START_WORLD_TICK.register(this::onWorldTick);
        HudRenderCallback.EVENT.register(this::onRenderHud);
    }

    public ItemStack getCurrentItem() {
        return current;
    }

    private void onWorldTick(ClientWorld world) {
        while (KEYBIND_OPEN.wasPressed()) {
            CLIENT.setScreen(new CompassHudScreen());
        }
    }

    private void onRenderHud(MatrixStack matrices, float tickDelta) {
        if (CLIENT.player == null) return;
        if (this.current.isEmpty()) return;
        this.onRenderCompass(matrices);
    }


    private void onRenderCompass(MatrixStack matrices) {
        int scaledWidth = CLIENT.getWindow().getScaledWidth();
        int scaledHeight = CLIENT.getWindow().getScaledHeight();
        ClientPlayerEntity player = CLIENT.player;

        boolean hasOffhand = !player.getOffHandStack().isEmpty();
        if (this.current == null) return;

        int x = scaledWidth / 2 + 91;
        int y = scaledHeight - 23;
        if (hasOffhand) {
            var hand = player.getMainArm().getOpposite();
            if (hand == Arm.RIGHT) {
                x += 23;
            }
        }
        RenderHelper.renderSlottedItem(matrices, CLIENT.getItemRenderer(), this.current, x,  y);
    }

    public void setCurrentItem(ItemStack stack) {
        this.current = stack;
    }
}
