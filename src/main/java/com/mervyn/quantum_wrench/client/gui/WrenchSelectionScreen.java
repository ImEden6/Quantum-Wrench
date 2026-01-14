package com.mervyn.quantum_wrench.client.gui;

import com.mervyn.quantum_wrench.item.WrenchInventory;
import com.mervyn.quantum_wrench.network.ModNetworking;
import com.mervyn.quantum_wrench.registry.ModItems;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.List;

public class WrenchSelectionScreen extends Screen {
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 4;

    private List<ItemStack> wrenches;
    private int selectedIndex;
    private ItemStack quantumWrench;

    public WrenchSelectionScreen() {
        super(Text.translatable("screen.quantum_wrench.selection"));
    }

    @Override
    protected void init() {
        super.init();

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            close();
            return;
        }

        // Find quantum wrench in player's hands
        ItemStack mainHand = client.player.getMainHandStack();
        ItemStack offHand = client.player.getOffHandStack();

        if (mainHand.getItem() == ModItems.QUANTUM_WRENCH) {
            quantumWrench = mainHand;
        } else if (offHand.getItem() == ModItems.QUANTUM_WRENCH) {
            quantumWrench = offHand;
        } else {
            close();
            return;
        }

        WrenchInventory inventory = new WrenchInventory(quantumWrench);
        wrenches = inventory.getAllWrenches();
        selectedIndex = inventory.getSelectedIndex();

        if (wrenches.isEmpty()) {
            close();
            return;
        }

        // Create buttons for each wrench + reset option
        int buttonCount = wrenches.size() + 1;
        int totalHeight = buttonCount * (BUTTON_HEIGHT + BUTTON_SPACING) - BUTTON_SPACING;
        int startY = (height - totalHeight) / 2;

        // Reset button (Quantum Wrench mode)
        Text resetText = Text.literal("Quantum Wrench");
        if (selectedIndex == -1) {
            resetText = Text.literal("→ ").append(resetText).append(" ←");
        }
        ButtonWidget resetButton = ButtonWidget.builder(resetText, btn -> selectWrench(-1))
                .dimensions((width - BUTTON_WIDTH) / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
        addDrawableChild(resetButton);

        // Wrench buttons
        for (int i = 0; i < wrenches.size(); i++) {
            final int index = i;
            ItemStack wrench = wrenches.get(i);

            Text buttonText = wrench.getName();
            if (i == selectedIndex) {
                buttonText = Text.literal("→ ").append(buttonText).append(" ←");
            }

            ButtonWidget button = ButtonWidget.builder(buttonText, btn -> selectWrench(index))
                    .dimensions((width - BUTTON_WIDTH) / 2, startY + (i + 1) * (BUTTON_HEIGHT + BUTTON_SPACING),
                            BUTTON_WIDTH, BUTTON_HEIGHT)
                    .build();

            addDrawableChild(button);
        }
    }

    private void selectWrench(int index) {
        if (index == selectedIndex) {
            close();
            return;
        }

        // Send packet to server
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(index);
        ClientPlayNetworking.send(ModNetworking.SELECT_WRENCH_PACKET, buf);

        // Update local state
        WrenchInventory inventory = new WrenchInventory(quantumWrench);
        inventory.setSelectedIndex(index);

        // Play sound
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 0.5f, 1.5f);
        }

        close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
