package mc.duzo.mobedit.client.screen.editor;

import mc.duzo.mobedit.MobEditMod;
import mc.duzo.mobedit.client.screen.ScreenHelper;
import mc.duzo.mobedit.client.screen.widget.NumericalEditBoxWidget;
import mc.duzo.mobedit.client.screen.widget.ScrollableButton;
import mc.duzo.mobedit.client.screen.widget.ScrollableButtonsWidget;
import mc.duzo.mobedit.common.edits.EditedEntity;
import mc.duzo.mobedit.common.edits.attribute.applier.ApplierRegistry;
import mc.duzo.mobedit.common.edits.attribute.applier.AttributeApplier;
import mc.duzo.mobedit.common.edits.attribute.holder.AttributeHolder;
import mc.duzo.mobedit.network.MobEditNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.impl.client.screen.ButtonList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;

public class MobEditorScreen extends Screen {
	private static final Identifier TEXTURE = new Identifier(MobEditMod.MOD_ID, "textures/gui/editor.png");

	private EditedEntity editor;
	private boolean wasPreviousNext;
	private HashMap<String, NumericalEditBoxWidget> editBoxes;
	private EditBoxWidget nameBox;
	private ScrollableButtonsWidget entityButtons;

	public MobEditorScreen() {
		super(Text.translatable("screen." + MobEditMod.MOD_ID + ".mob_editor"));
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		context.drawTexture(TEXTURE, ScreenHelper.getCentreX() - (256 / 2), ScreenHelper.getCentreY() - (166 / 2), 0, 0, 256, 256); // Background texture

		super.render(context, mouseX, mouseY, delta);

		this.renderEntity(context);

		// this might cause lag
		for (String name : this.editBoxes.keySet()) {
			int y = this.editBoxes.get(name).getY();

			ScreenHelper.renderWidthScaledText(
					name,
					context,
					ScreenHelper.getCentreX() - 64 - 4 - this.textRenderer.getWidth(name),
					y + 5,
					0xFFFFFF,
					this.textRenderer.getWidth(name),
					false
			);
		}
	}

	@Override
	public void renderBackgroundTexture(DrawContext context) {
		super.renderBackgroundTexture(context);
	}

	@Override
	protected void init() {
		super.init();

		this.editor = new EditedEntity(0);

		/*
		this.addDrawableChild(
				ScreenHelper.createTextButton(this.textRenderer, Text.of("→"), (widget) -> this.selectNextEntity(), ScreenHelper.getCentreX() + 64 + 16, ScreenHelper.getCentreY(), false)
		);

		this.addDrawableChild(
				ScreenHelper.createTextButton(this.textRenderer, Text.of("←"), (widget) -> this.selectPreviousEntity(), ScreenHelper.getCentreX() + 64 - this.textRenderer.getWidth("←") - 16, ScreenHelper.getCentreY(), false)
		);
		 */

		this.addDrawableChild(
				ScreenHelper.createTextButton(this.textRenderer, Text.of("CREATE"), (widget) -> this.pressComplete(), ScreenHelper.getCentreX() - 64, ScreenHelper.getCentreY() + 48, true)
		);

		this.nameBox = new EditBoxWidget(this.textRenderer, ScreenHelper.getCentreX() + 32, ScreenHelper.getCentreY() - 40, 64, 18, Text.of(""), Text.of("NAME"));
		this.addDrawableChild(this.nameBox);

		this.editBoxes = new HashMap<>();

		this.entityButtons = this.createButtonList(ScreenHelper.getCentreX() - 12, ScreenHelper.getCentreY(), 128, 64);
		this.addDrawableChild(this.entityButtons);

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
				ScreenHelper.getCentreX() - 64,
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
		LivingEntity found = editor.getSelectedEntity(MinecraftClient.getInstance().world).orElse(null);

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

	private void renderEntity(DrawContext context) {
		InventoryScreen.drawEntity(context, ScreenHelper.getCentreX() + 64, ScreenHelper.getCentreY() - 48, 24, (float) 0f, (float) 0f, this.getSelectedEntity());

		ScreenHelper.renderWidthScaledText(
				this.getSelectedEntity().getName().getString(),
				context,
				ScreenHelper.getCentreX() + 64,
				ScreenHelper.getCentreY() - 16,
				0xFFFFFF,
				this.textRenderer.getWidth(this.getSelectedEntity().getName()),
				true
		);

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

	private ScrollableButtonsWidget createButtonList(int x, int y, int width, int height) {
		ButtonList list = new ButtonList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

		int index = 0;
		for (EntityType<?> type : Registries.ENTITY_TYPE) {
			list.add(createEntityButton(type, index, x, y, width, height / 4));
			index++;
		}

		ScrollableButtonsWidget created = new ScrollableButtonsWidget(x, y, width, height, Text.of("Entities"), list);

		for (ClickableWidget i : list) { // code bad
			if (!(i instanceof ScrollableButton button)) continue;

			button.setParent(created);
		}

		return created;
	}
	private ScrollableButton createEntityButton(EntityType<?> type, int index, int x, int y, int width, int height) {
		return ScrollableButton.builder(
				type.getName(),
				button -> {
					System.out.println("PRESSED " + button.getMessage().getString());
					this.editor = new EditedEntity(index);
					this.onChangeEntity();
				},
				null
		).dimensions(x, y, width, height).build();
	}

	@Override
	public boolean shouldPause() {
		return true;
	}
}
