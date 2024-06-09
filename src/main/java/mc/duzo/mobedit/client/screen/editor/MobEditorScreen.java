package mc.duzo.mobedit.client.screen.editor;

import mc.duzo.mobedit.MobEditMod;
import mc.duzo.mobedit.client.screen.ScreenHelper;
import mc.duzo.mobedit.client.screen.widget.NumericalEditBoxWidget;
import mc.duzo.mobedit.common.edits.EditedEntity;
import mc.duzo.mobedit.common.edits.attribute.applier.ApplierRegistry;
import mc.duzo.mobedit.common.edits.attribute.applier.AttributeApplier;
import mc.duzo.mobedit.common.edits.attribute.holder.AttributeHolder;
import mc.duzo.mobedit.network.MobEditNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.HashMap;

public class MobEditorScreen extends Screen {
	private EditedEntity editor;
	private boolean wasPreviousNext;
	private HashMap<String, NumericalEditBoxWidget> editBoxes;
	private EditBoxWidget nameBox;

	public MobEditorScreen() {
		super(Text.translatable("screen." + MobEditMod.MOD_ID + ".mob_editor"));
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

		InventoryScreen.drawEntity(context, ScreenHelper.getCentreX(), (int) (ScreenHelper.getScreenHeight() * 0.48), ScreenHelper.getScreenWidth() / 16, (float) 0f, (float) 0f, this.getSelectedEntity());

		ScreenHelper.renderWidthScaledText(
				this.getSelectedEntity().getName().getString(),
				context,
				ScreenHelper.getCentreX(),
				ScreenHelper.getCentreY() + 16,
				0xFFFFFF,
				this.textRenderer.getWidth(this.getSelectedEntity().getName()),
				true
		);

		// this might cause lag
		for (String name : this.editBoxes.keySet()) {
			int y = this.editBoxes.get(name).getY();

			ScreenHelper.renderWidthScaledText(
					name,
					context,
					ScreenHelper.getCentreX() - 128 - 4 - this.textRenderer.getWidth(name),
					y + 5,
					0xFFFFFF,
					this.textRenderer.getWidth(name),
					false
			);
		}
	}

	@Override
	protected void init() {
		super.init();

		this.editor = new EditedEntity(0);

		this.addDrawableChild(
				ScreenHelper.createTextButton(this.textRenderer, Text.of("→"), (widget) -> this.selectNextEntity(), ScreenHelper.getCentreX() + 16, ScreenHelper.getCentreY(), false)
		);

		this.addDrawableChild(
				ScreenHelper.createTextButton(this.textRenderer, Text.of("←"), (widget) -> this.selectPreviousEntity(), ScreenHelper.getCentreX() - this.textRenderer.getWidth("←") - 16, ScreenHelper.getCentreY(), false)
		);

		this.addDrawableChild(
				ScreenHelper.createTextButton(this.textRenderer, Text.of("CREATE"), (widget) -> this.pressComplete(), ScreenHelper.getCentreX(), ScreenHelper.getCentreY() + 64, true)
		);

		this.nameBox = new EditBoxWidget(this.textRenderer, ScreenHelper.getCentreX() - (ScreenHelper.getScreenWidth() / 32), ScreenHelper.getCentreY() + 32, ScreenHelper.getScreenWidth() / 16, 18, Text.of(""), Text.of("NAME"));
		this.addDrawableChild(this.nameBox);

		this.editBoxes = new HashMap<>();

		int count = 1;
		for (AttributeApplier applier : ApplierRegistry.REGISTRY) {
			createEditBoxForApplier(applier, count);
			count++;
		}

		this.onChangeEntity();
	}

	private NumericalEditBoxWidget createEditBoxForApplier(AttributeApplier applier, int count) {
		boolean even = (count % 2) == 0;
		int y = ScreenHelper.getCentreY();
		if (even) {
			y = y - (20 * (count / 2));
		}
		else {
			y = y + (20 * (count / 2));
		}

		NumericalEditBoxWidget widget = new NumericalEditBoxWidget(
				this.textRenderer,
				ScreenHelper.getCentreX() - 128,
				y,
				48,
				18,
				0,
				Text.of("")
		);

		this.addDrawableChild(widget);

		this.editBoxes.put(applier.getName(), widget);

		return widget;
	}
	private LivingEntity getSelectedEntity() {
		LivingEntity found = editor.getSelectedEntity().orElse(null);

		if (found == null) {
			if (this.wasPreviousNext) this.selectNextEntity();
			else this.selectPreviousEntity();

			return this.getSelectedEntity();
		}

		return found;
	}
	private void selectNextEntity() {
		int index = this.editor.getEntityIndex();

		index++;

		if (index > getRegistrySize()) {
			index = 0;
		}

		this.editor = new EditedEntity(index);
		this.wasPreviousNext = true;
		this.onChangeEntity();
	}
	private void selectPreviousEntity() {
		int index = this.editor.getEntityIndex();

		index--;

		if (index < 0) {
			index = getRegistrySize();
		}

		this.editor = new EditedEntity(index);
		this.wasPreviousNext = false;
		this.onChangeEntity();
	}
	private void onChangeEntity() {
		for (String name : this.editBoxes.keySet()) {
			NumericalEditBoxWidget widget = this.editBoxes.get(name);
			AttributeApplier applier = applierFromName(name);
			widget.setText("" + MobEditMod.round(applier.getDefault(this.getSelectedEntity()).orElse(0d), 2));
		}
	}

	private static int getRegistrySize() {
		return Registries.ENTITY_TYPE.size();
	}

	private void pressComplete() {
		for (String name : this.editBoxes.keySet()) {
			NumericalEditBoxWidget widget = this.editBoxes.get(name);
			AttributeApplier applier = applierFromName(name);
			if (widget.getValue() == applier.getDefault(this.getSelectedEntity()).orElse(-1d)) continue;

			this.editor.addAttribute(new AttributeHolder(applier, widget.getValue()));
		}

		if (!this.nameBox.getText().isBlank()) {
			this.editor.setName(this.nameBox.getText());
		}

		NbtCompound data = this.editor.serialize();

		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeNbt(data);

		ClientPlayNetworking.send(MobEditNetworking.REQUEST_EGG, buf);

		this.close();
	}
	private AttributeApplier applierFromName(String name) {
		return ApplierRegistry.REGISTRY.stream().filter(applier -> applier.getName().equals(name)).findFirst().orElse(null);
	}

	@Override
	public boolean shouldPause() {
		return true;
	}
}
