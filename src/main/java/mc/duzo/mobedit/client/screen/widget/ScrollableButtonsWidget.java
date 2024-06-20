package mc.duzo.mobedit.client.screen.widget;

import net.fabricmc.fabric.impl.client.screen.ButtonList;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;

public class ScrollableButtonsWidget extends ScrollableWidget {
	private final ButtonList buttons;
	private final double deltaY;
	private final int totalHeight;

	public ScrollableButtonsWidget(int x, int y, int width, int height, Text message, ButtonList buttons) {
		super(x, y, width, height, message);

		this.buttons = buttons;
		this.cleanseButtons();

		double tempDelta;
		try {
			tempDelta = buttons.get(0).getHeight() / 2f;
		} catch (Exception e) {
			tempDelta = 2;
		}
		this.deltaY = tempDelta;

		int tempHeight = 0;
		for (ClickableWidget widget : this.buttons) {
			tempHeight += widget.getHeight();
		}
		this.totalHeight = tempHeight;
	}

	public ButtonList getButtons() {
		return buttons;
	}
	private void cleanseButtons() {
		int index = 0;
		for (ClickableWidget button : this.buttons) {
			if (button instanceof ScrollableButton) continue;

			this.buttons.remove(index);
			index++;
		}

		index = 0;
		int totalHeight = 0;
		for (ClickableWidget button : this.buttons) {
			button.setX(this.getX());
			button.setY(this.getY() + totalHeight);
			totalHeight += button.getHeight();
			index++;
		}
	}

	@Override
	protected int getContentsHeight() {
		return this.totalHeight;
	}

	@Override
	protected double getDeltaYPerScroll() {
		return this.deltaY;
	}

	@Override
	protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
		for (ClickableWidget button : this.buttons) {
			button.render(context, mouseX, mouseY, delta);
		}
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {

	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (ClickableWidget widget : this.buttons) {
			widget.mouseClicked(mouseX, mouseY, button);
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public double getScrollY() {
		return super.getScrollY();
	}
}
