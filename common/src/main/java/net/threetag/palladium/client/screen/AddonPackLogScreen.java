package net.threetag.palladium.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.threetag.palladium.addonpack.log.AddonPackLogEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddonPackLogScreen extends Screen {

    public DevLogList list;
    public EditBox textFieldWidget;
    @Nullable
    public Screen parent;
    public List<AddonPackLogEntry> logs;
    public static boolean INFO_FILTER = false;
    public static boolean WARNINGS_FILTER = true;
    public static boolean ERRORS_FILTER = true;

    public AddonPackLogScreen(List<AddonPackLogEntry> logs, @Nullable Screen parent) {
        super(Component.translatable("gui.palladium.addon_pack_log"));
        this.logs = logs;
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        this.addWidget(this.textFieldWidget = new EditBox(this.font, this.width / 2 - 310, this.height - 27, 620 - (this.parent != null ? 90 : 0), 17, Component.translatable("gui.palladium.addon_pack_log.search")));
        this.textFieldWidget.setFocused(false);
        this.textFieldWidget.setCanLoseFocus(true);
        this.textFieldWidget.setResponder(search -> this.list.refreshList());

        this.addWidget(this.list = new DevLogList(this.minecraft, this.width, this.height, 48, this.height - 64, 36));

        if (this.parent != null)
            this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> Objects.requireNonNull(this.minecraft).setScreen(this.parent))
                    .bounds(this.width / 2 + 310 - 70, this.height - 28, 75, 20).build());

        this.addRenderableWidget(Checkbox.builder(Component.literal("INFO").withStyle(AddonPackLogEntry.Type.INFO.getColor()), this.font)
                .pos(this.width / 2 - 310, this.height - 52).maxWidth(70).selected(INFO_FILTER)
                .onValueChange((checkbox, selected) -> {
                    INFO_FILTER = selected;
                    this.list.refreshList();
                }).build());
        this.addRenderableWidget(Checkbox.builder(Component.literal("WARNING").withStyle(AddonPackLogEntry.Type.WARNING.getColor()), this.font)
                .pos(this.width / 2 - 35, this.height - 52).maxWidth(70).selected(WARNINGS_FILTER)
                .onValueChange((checkbox, selected) -> {
                    WARNINGS_FILTER = selected;
                    this.list.refreshList();
                }).build());
        this.addRenderableWidget(Checkbox.builder(Component.literal("ERROR").withStyle(AddonPackLogEntry.Type.ERROR.getColor()), this.font)
                .pos(this.width / 2 + 310 - 50, this.height - 52).maxWidth(70).selected(ERRORS_FILTER)
                .onValueChange((checkbox, selected) -> {
                    ERRORS_FILTER = selected;
                    this.list.refreshList();
                }).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);

        if (this.list != null)
            this.list.render(guiGraphics, mouseX, mouseY, partialTicks);
        if (this.textFieldWidget != null)
            this.textFieldWidget.render(guiGraphics, mouseX, mouseY, partialTicks);

        guiGraphics.drawCenteredString(font, this.title, this.width / 2, 20, 16777215);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    public class DevLogList extends AbstractSelectionList<DevLogEntry> {

        private final int listWidth;

        public DevLogList(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
            super(mcIn, widthIn, bottomIn - topIn, topIn, slotHeightIn);
            this.listWidth = 620;
            this.refreshList();
        }

        public void refreshList() {
            this.clearEntries();

            for (AddonPackLogEntry info : AddonPackLogScreen.this.logs) {
                if ((AddonPackLogScreen.this.textFieldWidget.getValue() != null && !AddonPackLogScreen.this.textFieldWidget.getValue().isEmpty() && !info.getText().toLowerCase(Locale.ROOT).contains(AddonPackLogScreen.this.textFieldWidget.getValue()))
                        || (info.getType() == AddonPackLogEntry.Type.INFO && !INFO_FILTER)
                        || (info.getType() == AddonPackLogEntry.Type.WARNING && !WARNINGS_FILTER)
                        || (info.getType() == AddonPackLogEntry.Type.ERROR && !ERRORS_FILTER)) {
                    continue;
                }
                this.addEntry(new DevLogEntry(info));
            }
        }

        @Override
        public int getRowWidth() {
            return this.listWidth;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.width / 2 + this.getRowWidth() / 2 + 4;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }
    }

    public class DevLogEntry extends AbstractSelectionList.Entry<DevLogEntry> {

        private final AddonPackLogEntry info;

        public DevLogEntry(AddonPackLogEntry info) {
            this.info = info;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
            guiGraphics.fill(left, top + 33, left + entryWidth, top + 34, -1601138544);
            guiGraphics.drawString(font, Component.literal(this.info.getType().toString()).withStyle(this.info.getType().getColor()), left, top + 12, isMouseOver ? 16777120 : 0xfefefe, true);

            MutableComponent msg = Component.literal(this.info.getText()).withStyle(this.info.getType().getColor());
            List<FormattedCharSequence> lines = font.split(msg, 560);
            int maxLines = Math.min(lines.size(), 3);
            for (int i = 0; i < lines.size(); i++) {
                FormattedCharSequence processor = lines.get(i);
                if (i < 3) {
                    guiGraphics.drawString(font, processor, left + 50, (int) (top + 2 + (10 - (maxLines - 1) * 10F / 2F) + i * 10), isMouseOver ? 16777120 : 0xfefefe, true);
                }
            }
            if (lines.size() > 3) {
                guiGraphics.drawString(font, "[...]", left + 602, top + 2 + (maxLines - 1) * 10, isMouseOver ? 16777120 : 0xfefefe, true);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int type) {
            Objects.requireNonNull(AddonPackLogScreen.this.minecraft).getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            AddonPackLogScreen.this.minecraft.setScreen(new AddonPackLogEntryScreen(AddonPackLogScreen.this, this.info));
            return true;
        }
    }

}
