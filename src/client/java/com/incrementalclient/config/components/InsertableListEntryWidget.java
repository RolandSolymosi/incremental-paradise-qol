package com.incrementalclient.config.components;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.ListOptionEntry;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.TooltipButtonWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class InsertableListEntryWidget extends AbstractWidget implements ParentElement {
    private final TooltipButtonWidget removeButton, moveUpButton, moveDownButton;
    private final TextFieldWidget indexField;
    private final AbstractWidget entryWidget;

    private final ListOption<?> listOption;
    private final ListOptionEntry<?> listOptionEntry;

    private final String optionNameString;

    private Element focused;
    private boolean dragging;

    public InsertableListEntryWidget(YACLScreen screen, ListOptionEntry<?> listOptionEntry, AbstractWidget entryWidget) {
        super(entryWidget.getDimension().withHeight(Math.min(entryWidget.getDimension().height(), 20) - ((listOptionEntry.parentGroup().indexOf(listOptionEntry) == listOptionEntry.parentGroup().options().size() - 1) ? 0 : 2))); // -2 to remove the padding
        this.listOptionEntry = listOptionEntry;
        this.listOption = listOptionEntry.parentGroup();
        this.optionNameString = listOptionEntry.name().getString().toLowerCase();
        this.entryWidget = entryWidget;

        Dimension<Integer> dim = entryWidget.getDimension();
        entryWidget.setDimension(dim.clone().move(20 * 3, 0).expand(-20 * 4, 0));

        removeButton = new TooltipButtonWidget(screen, dim.xLimit() - 20, dim.y(), 20, 20, Text.literal("\u274c"), Text.translatable("yacl.list.remove"), btn -> {
            listOption.removeEntry(listOptionEntry);
            updateButtonStates();
        });

        indexField = new TextFieldWidget(screen.getTextRenderer(), dim.x(), dim.y(), 20, 20, Text.literal(""));
        indexField.setText(String.valueOf(listOption.indexOf(listOptionEntry)));
        indexField.setChangedListener(s -> {});
        indexField.setRenderTextProvider((text, first) -> Text.of(text).asOrderedText());

        moveUpButton = new TooltipButtonWidget(screen, dim.x() + 20, dim.y(), 20, 20, Text.literal("\u2191"), Text.translatable("yacl.list.move_up"), btn -> {
            int index = listOption.indexOf(listOptionEntry) - 1;
            if (index >= 0) {
                listOption.removeEntry(listOptionEntry);
                listOption.insertEntry(index, listOptionEntry);
                updateButtonStates();
            }
        });

        moveDownButton = new TooltipButtonWidget(screen, dim.x() + 2 * 20, dim.y(), 20, 20, Text.literal("\u2193"), Text.translatable("yacl.list.move_down"), btn -> {
            int index = listOption.indexOf(listOptionEntry) + 1;
            if (index < listOption.options().size()) {
                listOption.removeEntry(listOptionEntry);
                listOption.insertEntry(index, listOptionEntry);
                updateButtonStates();
            }
        });

        updateButtonStates();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (indexField.isFocused() && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            try {
                int newIndex = Integer.parseInt(indexField.getText());
                int currentIndex = listOption.indexOf(listOptionEntry);

                if (newIndex != currentIndex) {
                    listOption.removeEntry(listOptionEntry);

                    if (newIndex < 0) newIndex = 0;
                    if (newIndex > listOption.options().size()) newIndex = listOption.options().size();

                    listOption.insertEntry(newIndex, listOptionEntry);

                    indexField.setFocused(false);
                    setFocused(null);
                    updateButtonStates();
                    return true;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers) || indexField.keyPressed(keyCode, scanCode, modifiers) || entryWidget.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (indexField.isFocused()) {
            return indexField.charTyped(chr, modifiers);
        }
        return entryWidget.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (indexField.mouseClicked(mouseX, mouseY, button)) {
            indexField.setText("");
            setFocused(indexField);
            return true;
        }
        if (moveUpButton.mouseClicked(mouseX, mouseY, button)) {
            setFocused(moveUpButton);
            return true;
        }
        if (moveDownButton.mouseClicked(mouseX, mouseY, button)) {
            setFocused(moveDownButton);
            return true;
        }
        if (removeButton.mouseClicked(mouseX, mouseY, button)) {
            setFocused(removeButton);
            return true;
        }

        setFocused(null);
        return entryWidget.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        updateButtonStates();

        if (!indexField.isFocused()) {
            indexField.setText(String.valueOf(listOption.indexOf(listOptionEntry)));
        }

        indexField.setY(getDimension().y());
        removeButton.setY(getDimension().y());
        moveUpButton.setY(getDimension().y());
        moveDownButton.setY(getDimension().y());
        entryWidget.setDimension(entryWidget.getDimension().withY(getDimension().y()));

        indexField.renderWidget(graphics, mouseX, mouseY, delta);
        removeButton.render(graphics, mouseX, mouseY, delta);
        moveUpButton.render(graphics, mouseX, mouseY, delta);
        moveDownButton.render(graphics, mouseX, mouseY, delta);
        entryWidget.render(graphics, mouseX, mouseY, delta);
    }

    protected void updateButtonStates() {
        removeButton.active = listOption.available() && listOption.numberOfEntries() > listOption.minimumNumberOfEntries();
        moveUpButton.active = listOption.indexOf(listOptionEntry) > 0 && listOption.available();
        moveDownButton.active = listOption.indexOf(listOptionEntry) < listOption.options().size() - 1 && listOption.available();
        indexField.setEditable(listOption.available());
    }

    @Override
    public void unfocus() {
        setFocused(null);
        entryWidget.unfocus();
    }

    @Override
    public boolean matchesSearch(String query) {
        return optionNameString.contains(query.toLowerCase());
    }

    @Override
    public List<? extends Element> children() {
        return ImmutableList.of(indexField, moveUpButton, moveDownButton, entryWidget, removeButton);
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Nullable
    @Override
    public Element getFocused() {
        return focused;
    }

    @Override
    public void setFocused(@Nullable Element focused) {
        for (var child : children()) {
            child.setFocused(child == focused);
        }
        this.focused = focused;
    }
}
