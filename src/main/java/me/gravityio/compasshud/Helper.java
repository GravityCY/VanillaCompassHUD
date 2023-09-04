package me.gravityio.compasshud;

import com.mojang.blaze3d.systems.RenderSystem;
import me.gravityio.goodlib.helper.GoodItemHelper;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.gui.widget.ClickableWidget.WIDGETS_TEXTURE;

public class Helper {
    public static List<ItemStack> getCompasses(ClientPlayerEntity player) {
        List<ItemStack> compasses = new ArrayList<>();
        compasses.addAll(getCompassesFromInventory(player, player.getInventory().main));
        return compasses;
    }
    /**
     * Get compasses from an inventory
     */
    public static List<ItemStack> getCompassesFromInventory(ClientPlayerEntity player, AbstractCollection<ItemStack> inventory) {
        return getCompassesFromInventory(player, inventory, true);
    }
    /**
     * Get all compasses from an inventory
     */
    public static List<ItemStack> getCompassesFromInventory(ClientPlayerEntity player, AbstractCollection<ItemStack> inventory, boolean allowEnder) {
        List<ItemStack> compasses = new ArrayList<>();
        for (ItemStack stack : inventory) {
            if (stack.isOf(Items.COMPASS)) {
                compasses.add(stack);
            } else if (allowEnder && stack.isOf(Items.ENDER_CHEST)) {
                CompassHudMod.LOGGER.info("Getting all compasses from ender chest");
                player.getEnderChestInventory().
                var a = getCompassesFromInventory(player, player.getEnderChestInventory().stacks, false);
                for (ItemStack itemStack : a) {
                    CompassHudMod.LOGGER.info("Found compass: " + itemStack + " in ender chest");
                }
                compasses.addAll(a);
            } else if (stack.getItem() instanceof BlockItem blockItem) {
                if (blockItem.getBlock() instanceof ShulkerBoxBlock) {
                    var inShulker = getCompassesFromShulker(stack);
                    if (inShulker == null) continue;
                    compasses.addAll(inShulker);
                }
            }
        }
        return compasses;
    }
    /**
     * Get compasses from a shulker item
     */
    public static List<ItemStack> getCompassesFromShulker(ItemStack shulker) {
        return List.of(GoodItemHelper.NbtInventory.getUnorderedInventory(shulker));
    }
}

