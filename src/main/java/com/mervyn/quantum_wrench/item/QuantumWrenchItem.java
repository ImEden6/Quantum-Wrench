package com.mervyn.quantum_wrench.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuantumWrenchItem extends Item {
    private static final int MAX_TOOLTIP_WRENCHES = 5;

    public QuantumWrenchItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null)
            return ActionResult.PASS;

        Hand hand = context.getHand();
        ItemStack originalStack = player.getStackInHand(hand);

        WrenchInventory inventory = new WrenchInventory(originalStack);
        ItemStack wrenchCopy = inventory.getSelectedWrench();

        if (wrenchCopy.isEmpty()) {
            return ActionResult.PASS;
        }

        // Temporarily swap stack in player's hand
        player.setStackInHand(hand, wrenchCopy);
        try {
            ActionResult result = wrenchCopy.useOnBlock(context);
            return result;
        } finally {
            // Always restore original stack
            player.setStackInHand(hand, originalStack);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        WrenchInventory inventory = new WrenchInventory(stack);
        List<ItemStack> wrenches = inventory.getAllWrenches();
        int selectedIndex = inventory.getSelectedIndex();

        if (wrenches.isEmpty()) {
            tooltip.add(Text.translatable("tooltip.quantum_wrench.empty").formatted(Formatting.GRAY));
            return;
        }

        if (selectedIndex == -1) {
            tooltip.add(Text.translatable("tooltip.quantum_wrench.selected")
                    .append(Text.literal("Quantum Wrench"))
                    .formatted(Formatting.AQUA));
        } else {
            ItemStack selected = wrenches.get(selectedIndex);
            tooltip.add(Text.translatable("tooltip.quantum_wrench.selected")
                    .append(selected.getName())
                    .formatted(Formatting.GOLD));
        }

        // Show stored count
        tooltip.add(Text.translatable("tooltip.quantum_wrench.stored", wrenches.size())
                .formatted(Formatting.GRAY));

        // List wrenches (truncated)
        int displayCount = Math.min(wrenches.size(), MAX_TOOLTIP_WRENCHES);
        for (int i = 0; i < displayCount; i++) {
            ItemStack wrench = wrenches.get(i);
            Text name = wrench.getName().copy();
            if (i == selectedIndex) {
                tooltip.add(Text.literal("  • ").append(name).append(" ←").formatted(Formatting.YELLOW));
            } else {
                tooltip.add(Text.literal("  • ").append(name).formatted(Formatting.DARK_GRAY));
            }
        }

        if (wrenches.size() > MAX_TOOLTIP_WRENCHES) {
            int remaining = wrenches.size() - MAX_TOOLTIP_WRENCHES;
            tooltip.add(Text.translatable("tooltip.quantum_wrench.more", remaining).formatted(Formatting.DARK_GRAY));
        }
    }

    public void playSelectSound(World world, PlayerEntity player) {
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(),
                SoundCategory.PLAYERS, 0.5f, 1.5f);
    }
}
