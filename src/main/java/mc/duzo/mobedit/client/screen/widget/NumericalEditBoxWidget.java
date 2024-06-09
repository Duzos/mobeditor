package mc.duzo.mobedit.client.screen.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.text.Text;

public class NumericalEditBoxWidget extends EditBoxWidget {
	public NumericalEditBoxWidget(TextRenderer textRenderer, int x, int y, int width, int height, int placeholder, Text message) {
		super(textRenderer, x, y, width, height, Text.of(String.valueOf(placeholder)), message);
	}

	@Override
	public void setText(String text) {
		try {
			Float.parseFloat(text);
			super.setText(text);
		} catch (Exception ignored) { // if unable to parse the float, must not be valid and will throw error stopping it from setting
		}
	}

	public float getValue() {
		try {
			return Float.parseFloat(this.getText());
		} catch (Exception e) {
			this.setText("0.0");
			return this.getValue();
		}
	}
}
