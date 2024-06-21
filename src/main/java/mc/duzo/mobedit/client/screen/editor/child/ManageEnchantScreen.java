package mc.duzo.mobedit.client.screen.editor.child;

import mc.duzo.mobedit.MobEditMod;
import mc.duzo.mobedit.client.screen.ScreenHelper;
import mc.duzo.mobedit.client.screen.editor.MobEditorScreen;
import mc.duzo.mobedit.client.screen.widget.NumericalEditBoxWidget;
import mc.duzo.mobedit.client.screen.widget.ScrollableButton;
import mc.duzo.mobedit.client.screen.widget.ScrollableButtonsWidget;
import mc.duzo.mobedit.common.edits.attribute.enchants.EnchantmentAttribute;
import net.fabricmc.fabric.impl.client.screen.ButtonList;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class ManageEnchantScreen extends Screen {
	private MobEditorScreen parent;
	private StatusEffect status;
	private NumericalEditBoxWidget timeBox;

	public ManageEnchantScreen(MobEditorScreen parent) {
		super(Text.translatable("screen." + MobEditMod.MOD_ID + ".manage_enchant"));
		this.parent = parent;
		this.status = Registries.STATUS_EFFECT.get(0);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		MobEditorScreen.renderBGTexture(context);

		super.render(context, mouseX, mouseY, delta);

		if (status != null) {
			ScreenHelper.renderWidthScaledText(
					this.status.getName().getString(),
					context,
					ScreenHelper.getCentreX(),
					ScreenHelper.getCentreY() - 32,
					0xFFFFFF,
					this.textRenderer.getWidth(this.status.getName()),
					true
			);
		}

		ScreenHelper.renderWidthScaledText(
				"secs",
				context,
				ScreenHelper.getCentreX() + 2,
				ScreenHelper.getCentreY() - 16,
				0xFFFFFF,
				this.textRenderer.getWidth("secs"),
				false
		);

		ScreenHelper.renderWidthScaledText(
				"REMOVE ENCHANTMENTS",
				context,
				ScreenHelper.getCentreX() + 120,
				ScreenHelper.getCentreY() - 42,
				0xFFFFFF,
				this.textRenderer.getWidth("REMOVE ENCHANTMENTS"),
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
				this.createEffectButtonList(ScreenHelper.getCentreX() - 184, ScreenHelper.getCentreY() - 32, 128, 64)
		);

		this.addDrawableChild(
				ScreenHelper.createTextButton(this.textRenderer, Text.of("ADD TO ENTITY"), (widget) -> this.pressComplete(true), ScreenHelper.getCentreX(), ScreenHelper.getCentreY(), true)
		);
		this.status = Registries.STATUS_EFFECT.get(0);
		this.timeBox = new NumericalEditBoxWidget(this.textRenderer, ScreenHelper.getCentreX() - 32, ScreenHelper.getCentreY() - 20, 32, 18, 0, Text.of("Time (seconds)"));
		this.addDrawableChild(this.timeBox);

		this.addDrawableChild(
				this.createExistingButtonList(ScreenHelper.getCentreX() + 56, ScreenHelper.getCentreY() - 32, 128, 64)
		);
	}

	private void pressComplete(boolean apply) {
		if (apply && this.status != null)
			this.parent.getEditor().addEnchantment(this.createAttribute());

		ScreenHelper.setScreen(this.parent);
	}
	private StatusEffectInstance createInstance() {
		return new StatusEffectInstance(this.status, (int) this.timeBox.getValue() * 20);
	}
	private EnchantmentAttribute createAttribute() {
		return new EnchantmentAttribute(this.createInstance());
	}

	private ScrollableButtonsWidget createEffectButtonList(int x, int y, int width, int height) {
		ButtonList list = new ButtonList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

		int index = 0;
		for (StatusEffect effect : Registries.STATUS_EFFECT) {
			list.add(createEffectButton(effect, index, x, y, width, height / 4));
			index++;
		}

		ScrollableButtonsWidget created = new ScrollableButtonsWidget(x, y, width, height, Text.of("Effects"), list);

		for (ClickableWidget i : list) { // code bad
			if (!(i instanceof ScrollableButton button)) continue;

			button.setParent(created);
		}

		return created;
	}
	private ScrollableButton createEffectButton(StatusEffect effect, int index, int x, int y, int width, int height) {
		return ScrollableButton.builder(
				effect.getName(),
				button -> {
					System.out.println("PRESSED " + button.getMessage().getString());
					this.status = effect;
				},
				null
		).dimensions(x, y, width, height).build();
	}

	private ScrollableButtonsWidget createExistingButtonList(int x, int y, int width, int height) {
		ButtonList list = new ButtonList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

		int index = 0;
		for (EnchantmentAttribute attr : this.parent.getEditor().getEnchants()) {
			list.add(createExistingButton(attr, index, x, y, width, height / 4));
			index++;
		}

		ScrollableButtonsWidget created = new ScrollableButtonsWidget(x, y, width, height, Text.of("Existing"), list);

		for (ClickableWidget i : list) { // code bad
			if (!(i instanceof ScrollableButton button)) continue;

			button.setParent(created);
		}

		return created;
	}
	private ScrollableButton createExistingButton(EnchantmentAttribute attr, int index, int x, int y, int width, int height) {
		return ScrollableButton.builder(
				Text.of(attr.getName() + " (" + attr.getDuration() / 20 + " secs)"),
				button -> {
					System.out.println("PRESSED " + button.getMessage().getString());
					this.parent.getEditor().removeEnchantment(attr);
					this.pressComplete(false);
				},
				null
		).dimensions(x, y, width, height).build();
	}
}
