package mc.duzo.mobedit.client.screen;


import mc.duzo.mobedit.util.DeltaTimeManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

/**
 * Helper methods for screens
 * Portions of code from Tardis Refined, modified to work with this mod.
 * @author duzo
 * @author Tardis Refined Team
 */
public class ScreenHelper {

	/**
	 * @param text  - The text you'd like to draw
	 * @param width - The max width of the text, scales to maintain this width if larger than it
	 * @author Tardis Refined
	 */
	public static void renderWidthScaledText(String text, DrawContext context, float x, float y, int color, int width, float scale, boolean centered) {
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		MatrixStack matrix = context.getMatrices();
		matrix.push();
		int textWidth = textRenderer.getWidth(text);
		float inputScale = width / (float) textWidth;
		inputScale = MathHelper.clamp(inputScale, 0.0F, scale);
		matrix.translate(x, y, 0);
		matrix.scale(inputScale, inputScale, inputScale);
		if (centered) {
			context.drawCenteredTextWithShadow(textRenderer, text, 0, 0, color);
		} else {
			context.drawTextWithShadow(textRenderer, text, 0, 0, color);
		}

		matrix.pop();
	}

	/**
	 * @author Tardis Refined
	 */
	public static void renderWidthScaledText(String text, DrawContext context, float x, float y, int color, int width, boolean centered) {
		renderWidthScaledText(text, context, x, y, color, width, 1.0F, centered);
	}

	/**
	 * Sets a new screen after a delay
	 * @param screen the new screen
	 * @param delay how long to wait until displaying this screen (in millis)
	 */
	public static void setScreen(Screen screen, long delay) {
		DeltaTimeManager.enqueueTask(delay, () -> MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(screen)));
	}
	public static void setScreen(Screen screen) {
		setScreen(screen, 0);
	}

	public static int getCentreX() {
		return getScreenWidth()/ 2;
	}
	public static int getCentreY() {
		return getScreenHeight() / 2;
	}
	public static int getScreenWidth() {
		return MinecraftClient.getInstance().getWindow().getScaledWidth();
	}
	public static int getScreenHeight() {
		return MinecraftClient.getInstance().getWindow().getScaledHeight();
	}

	public static PressableTextWidget createTextButton(TextRenderer renderer, Text text, ButtonWidget.PressAction onPress, int x, int y, boolean centered) {
		int width = renderer.getWidth(text);

		if (centered) {
			x -= width / 2;
		}

		return new PressableTextWidget(
				x,
				y,
				width,
				10,
				text,
				onPress,
				renderer
		);
	}
}