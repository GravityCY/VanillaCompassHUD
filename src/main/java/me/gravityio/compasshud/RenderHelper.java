package me.gravityio.compasshud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;

import static net.minecraft.client.gui.widget.ClickableWidget.WIDGETS_TEXTURE;

/**
 * This is not within my lane at all
 */
public class RenderHelper {

    public static void renderSlottedItem(MatrixStack matrices, ItemRenderer itemRenderer, ItemStack stack, int x, int y) {
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        matrices.push();
        matrices.translate(0.0f, 0.0f, -90.0f);
        InGameHud.drawTexture(matrices, x, y, 53, 22, 29, 24);
        matrices.pop();
        itemRenderer.renderInGuiWithOverrides(matrices, stack, x + 10, y + 4, 0);
    }

    public static void fillBoxWithGradientOutline(MatrixStack matrices, int sx, int sy, int ex, int ey, int s_color, int e_color, int pad) {
        fillGradient(matrices, sx, sy, ex, ey, s_color, s_color, GradientType.LEFT_TO_RIGHT);

        fillGradient(matrices, sx - pad, sy, sx, ey, s_color, e_color, GradientType.RIGHT_TO_LEFT);
        fillGradient(matrices, ex , sy, ex + pad, ey, s_color, e_color, GradientType.LEFT_TO_RIGHT);

        fillGradient(matrices, sx, ey, ex, ey + pad, s_color, e_color, GradientType.TOP_TO_BOTTOM);
        fillGradient(matrices, sx, sy - pad, ex, sy, s_color, e_color, GradientType.BOTTOM_TO_TOP);

        fillGradient(matrices, sx - pad, sy - pad, sx, sy, s_color, e_color, GradientType.BOTTOM_RIGHT_TO_TOP_LEFT);
        fillGradient(matrices, ex, sy - pad, ex + pad, sy, s_color, e_color, GradientType.BOTTOM_LEFT_TO_TOP_RIGHT);

        fillGradient(matrices, sx - pad, ey, sx, ey + pad, s_color, e_color, GradientType.TOP_RIGHT_TO_BOTTOM_LEFT);
        fillGradient(matrices, ex, ey, ex + pad, ey + pad, s_color, e_color, GradientType.TOP_LEFT_TO_BOTTOM_RIGHT);

    }
    public static void fillGradient(MatrixStack matrices, int startX, int startY, int endX, int endY, int colorStart, int colorEnd, GradientType type) {
        fillGradient(matrices, startX, startY, endX, endY, colorStart, colorEnd, 0, type);
    }
    public static void fillGradient(MatrixStack matrices, int startX, int startY, int endX, int endY, int colorStart, int colorEnd, int z, GradientType type) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        fillGradient(matrices.peek().getPositionMatrix(), bufferBuilder, startX, startY, endX, endY, z, colorStart, colorEnd, type);
        tessellator.draw();
        RenderSystem.disableBlend();
    }
    private static void fillGradient(Matrix4f matrix, BufferBuilder builder, int sx, int sy, int ex, int ey, int z, int colorStart, int colorEnd, GradientType type) {
        float r1 = (float) ColorHelper.Argb.getRed(colorStart)   / 255.0f;
        float g1 = (float) ColorHelper.Argb.getGreen(colorStart) / 255.0f;
        float b1 = (float) ColorHelper.Argb.getBlue(colorStart)  / 255.0f;
        float a1 = (float) ColorHelper.Argb.getAlpha(colorStart) / 255.0f;

        float r2 = (float) ColorHelper.Argb.getRed(colorEnd)     / 255.0f;
        float g2 = (float) ColorHelper.Argb.getGreen(colorEnd)   / 255.0f;
        float b2 = (float) ColorHelper.Argb.getBlue(colorEnd)    / 255.0f;
        float a2 = (float) ColorHelper.Argb.getAlpha(colorEnd)   / 255.0f;

        var gradient = new Gradient(matrix, builder, sx, sy, ex, ey, z, r1, g1, b1, a1, r2, g2, b2, a2);
        if (type == GradientType.LEFT_TO_RIGHT) {
            fillGradientLeftToRight(gradient);

        } else if (type == GradientType.RIGHT_TO_LEFT) {
            gradient.reverse = true;
            fillGradientLeftToRight(gradient);

        } else if (type == GradientType.TOP_TO_BOTTOM) {
            fillGradientTopToBottom(gradient);

        } else if (type == GradientType.BOTTOM_TO_TOP) {
            gradient.reverse = true;
            fillGradientTopToBottom(gradient);

        } else if (type == GradientType.TOP_LEFT_TO_BOTTOM_RIGHT) {
            fillGradientTopLeftToBottomRight(gradient);

        } else if (type == GradientType.BOTTOM_RIGHT_TO_TOP_LEFT) {
            gradient.reverse = true;
            fillGradientTopLeftToBottomRight(gradient);

        } else if (type == GradientType.BOTTOM_LEFT_TO_TOP_RIGHT) {
            fillGradientBottomLeftToTopRight(gradient);

        } else if (type == GradientType.TOP_RIGHT_TO_BOTTOM_LEFT) {
            gradient.reverse = true;
            fillGradientBottomLeftToTopRight(gradient);

        }
    }
    private static void fillGradientBottomLeftToTopRight(Gradient g) {
        g.bb().vertex(g.m(), g.sx(), g.sy(), g.z())   .color(g.r2(), g.g2(), g.b2(), g.a2()).next();
        g.bb().vertex(g.m(), g.sx(), g.ey(), g.z())     .color(g.r1(), g.g1(), g.b1(), g.a1()).next();
        g.bb().vertex(g.m(), g.ex(), g.ey(), g.z())     .color(g.r2(), g.g2(), g.b2(), g.a2()).next();
        g.bb().vertex(g.m(), g.ex(), g.sy(), g.z())       .color(g.r2(), g.g2(), g.b2(), g.a2()).next();
    }
    private static void fillGradientTopLeftToBottomRight(Gradient g) {
        g.bb().vertex(g.m(), g.sx(), g.sy(), g.z())   .color(g.r1(), g.g1(), g.b1(), g.a1()).next();
        g.bb().vertex(g.m(), g.sx(), g.ey(), g.z())     .color(g.r2(), g.g2(), g.b2(), g.a2()).next();
        g.bb().vertex(g.m(), g.ex(), g.ey(), g.z())     .color(g.r2(), g.g2(), g.b2(), g.a2()).next();
        g.bb().vertex(g.m(), g.ex(), g.sy(), g.z())       .color(g.r2(), g.g2(), g.b2(), g.a2()).next();
    }
    private static void fillGradientTopToBottom(Gradient g) {
        g.bb().vertex(g.m(), g.sx(), g.sy(), g.z())   .color(g.r1(), g.g1(), g.b1(), g.a1()).next();
        g.bb().vertex(g.m(), g.sx(), g.ey(), g.z())     .color(g.r2(), g.g2(), g.b2(), g.a2()).next();
        g.bb().vertex(g.m(), g.ex(), g.ey(), g.z())     .color(g.r2(), g.g2(), g.b2(), g.a2()).next();
        g.bb().vertex(g.m(), g.ex(), g.sy(), g.z())       .color(g.r1(), g.g1(), g.b1(), g.a1()).next();
    }
    private static void fillGradientLeftToRight(Gradient g) {
        g.bb().vertex(g.m(), g.sx(), g.sy(), g.z())   .color(g.r1(), g.g1(), g.b1(), g.a1()).next();
        g.bb().vertex(g.m(), g.sx(), g.ey(), g.z())     .color(g.r1(), g.g1(), g.b1(), g.a1()).next();
        g.bb().vertex(g.m(), g.ex(), g.ey(), g.z())       .color(g.r2(), g.g2(), g.b2(), g.a2()).next();
        g.bb().vertex(g.m(), g.ex(), g.sy(), g.z())     .color(g.r2(), g.g2(), g.b2(), g.a2()).next();
    }
    public enum GradientType {
        TOP_LEFT_TO_BOTTOM_RIGHT,
        TOP_RIGHT_TO_BOTTOM_LEFT,
        BOTTOM_RIGHT_TO_TOP_LEFT,
        BOTTOM_LEFT_TO_TOP_RIGHT,
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        TOP_TO_BOTTOM,
        BOTTOM_TO_TOP
    }
    private static class Gradient {

        private final Matrix4f m;
        private final BufferBuilder bb;
        private final int sx, sy, ex, ey, z;
        private final float r1, g1, b1, a1;
        private final float r2, g2, b2, a2;
        public boolean reverse = false;

        public Gradient(
                Matrix4f m, BufferBuilder bb,
                int sx, int sy, int ex, int ey, int z,
                float r1, float g1, float b1, float a1,
                float r2, float g2, float b2, float a2) {
            this.m = m;
            this.bb = bb;
            this.sx = sx;
            this.sy = sy;
            this.ex = ex;
            this.ey = ey;
            this.z = z;
            this.r1 = r1;
            this.g1 = g1;
            this.b1 = b1;
            this.a1 = a1;
            this.r2 = r2;
            this.g2 = g2;
            this.b2 = b2;
            this.a2 = a2;
        }


        public Matrix4f m() {
            return m;
        }

        public BufferBuilder bb() {
            return bb;
        }

        public int z() {
            return z;
        }

        public float r1() {
            return r1;
        }

        public float g1() {
            return g1;
        }

        public float b1() {
            return b1;
        }

        public float a1() {
            return a1;
        }

        public float r2() {
            return r2;
        }

        public float g2() {
            return g2;
        }

        public float b2() {
            return b2;
        }

        public float a2() {
            return a2;
        }

        public boolean isReverse() {
            return reverse;
        }

        public int sx() {
            return reverse ? ex : sx;
        }

        public int sy() {
            return reverse ? ey : sy;
        }

        public int ex() {
            return reverse ? sx : ex;
        }

        public int ey() {
            return reverse ? sy : ey;
        }

    }

}
