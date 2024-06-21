package mc.duzo.mobedit.client.screen.editor.child;

import mc.duzo.mobedit.MobEditMod;
import mc.duzo.mobedit.client.screen.ScreenHelper;
import mc.duzo.mobedit.client.screen.editor.MobEditorScreen;
import mc.duzo.mobedit.client.screen.widget.ScrollableButton;
import mc.duzo.mobedit.client.screen.widget.ScrollableButtonsWidget;
import net.fabricmc.fabric.impl.client.screen.ButtonList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class ManageDropScreen extends Screen {
	private MobEditorScreen parent;
	private ItemStack stack;
	private PressableTextWidget addButton;

	public ManageDropScreen(MobEditorScreen parent) {
		super(Text.translatable("screen." + MobEditMod.MOD_ID + ".manage_drops"));
		this.parent = parent;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		MobEditorScreen.renderBGTexture(context);

		super.render(context, mouseX, mouseY, delta);

		if (stack != null) {
			ScreenHelper.renderWidthScaledText(
					this.stack.getName().getString(),
					context,
					ScreenHelper.getCentreX(),
					ScreenHelper.getCentreY() - 32,
					0xFFFFFF,
					this.textRenderer.getWidth(this.stack.getName()),
					true
			);
			context.drawItem(stack, ScreenHelper.getCentreX() - 8, ScreenHelper.getCentreY() - 24);
			context.drawItemInSlot(this.textRenderer, stack, ScreenHelper.getCentreX() - 8, ScreenHelper.getCentreY() - 24);
		}

		ScreenHelper.renderWidthScaledText(
				"REMOVE DROP",
				context,
				ScreenHelper.getCentreX() + 120,
				ScreenHelper.getCentreY() - 42,
				0xFFFFFF,
				this.textRenderer.getWidth("REMOVE DROP"),
				true
		);

		ScreenHelper.renderWidthScaledText(
				"INVENTORY",
				context,
				ScreenHelper.getCentreX() - 120,
				ScreenHelper.getCentreY() - 42,
				0xFFFFFF,
				this.textRenderer.getWidth("INVENTORY"),
				true
		);
	}

	@Override
	protected void init() {
		super.init();

		this.addDrawableChild(
				ScreenHelper.createTextButton(this.textRenderer, Text.of("BACK"), (widget) -> this.pressComplete(false), ScreenHelper.getCentreX() - 184, ScreenHelper.getCentreY() - 72, false)
		);

		this.addDrawableChild(
				this.createItemButtonList(ScreenHelper.getCentreX() - 184, ScreenHelper.getCentreY() - 32, 128, 64)
		);

		this.addButton = ScreenHelper.createTextButton(this.textRenderer, Text.of("ADD TO ENTITY"), (widget) -> this.pressComplete(true), ScreenHelper.getCentreX(), ScreenHelper.getCentreY(), true);
		this.addDrawableChild(this.addButton);
		this.addButton.visible = false;

		this.addDrawableChild(
				this.createExistingButtonList(ScreenHelper.getCentreX() + 56, ScreenHelper.getCentreY() - 32, 128, 64)
		);
	}

	private void pressComplete(boolean apply) {
		if (apply && this.stack != null)
			this.parent.getEditor().getDrops().addDrop(stack);

		ScreenHelper.setScreen(this.parent);
	}

	private ScrollableButtonsWidget createItemButtonList(int x, int y, int width, int height) {
		ButtonList list = new ButtonList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

		int index = 0;
		for (ItemStack stack : MinecraftClient.getInstance().player.getInventory().main) {
			if (stack.isEmpty()) continue;

			list.add(createItemButton(stack, index, x, y, width, height / 4));
			index++;
		}

		ScrollableButtonsWidget created = new ScrollableButtonsWidget(x, y, width, height, Text.of("Items"), list);

		for (ClickableWidget i : list) { // code bad
			if (!(i instanceof ScrollableButton button)) continue;

			button.setParent(created);
		}

		return created;
	}

	private ScrollableButton createItemButton(ItemStack stack, int index, int x, int y, int width, int height) {
		return ScrollableButton.builder(
				stack.getName(),
				button -> {
					System.out.println("PRESSED " + button.getMessage().getString());
					this.stack = stack;
					this.addButton.visible = true;
				},
				null
		).dimensions(x, y, width, height).build();
	}

	private ScrollableButtonsWidget createExistingButtonList(int x, int y, int width, int height) {
		ButtonList list = new ButtonList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

		int index = 0;
		for (ItemStack stack : this.parent.getEditor().getDrops().getDrops()) {
			list.add(createExistingButton(stack, index, x, y, width, height / 4));
			index++;
		}

		ScrollableButtonsWidget created = new ScrollableButtonsWidget(x, y, width, height, Text.of("Existing"), list);

		for (ClickableWidget i : list) { // code bad
			if (!(i instanceof ScrollableButton button)) continue;

			button.setParent(created);
		}

		return created;
	}

	private ScrollableButton createExistingButton(ItemStack stack, int index, int x, int y, int width, int height) {
		return ScrollableButton.builder(
				Text.of(stack.getName().getString() + " (" + stack.getCount() + ")"),
				button -> {
					System.out.println("PRESSED " + button.getMessage().getString());
					this.parent.getEditor().getDrops().removeDrop(stack);
					this.pressComplete(false);
				},
				null
		).dimensions(x, y, width, height).build();
	}
}