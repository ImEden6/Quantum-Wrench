package com.mervyn.quantum_wrench.network;

import com.mervyn.quantum_wrench.QuantumWrench;
import com.mervyn.quantum_wrench.item.WrenchInventory;
import com.mervyn.quantum_wrench.registry.ModItems;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ModNetworking {
    public static final Identifier SELECT_WRENCH_PACKET = new Identifier(QuantumWrench.MOD_ID, "select_wrench");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(SELECT_WRENCH_PACKET,
                (server, player, handler, buf, responseSender) -> {
                    int selectedIndex = buf.readInt();
                    server.execute(() -> {
                        handleSelectWrench(player, selectedIndex);
                    });
                });
    }

    private static void handleSelectWrench(ServerPlayerEntity player, int selectedIndex) {
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();

        ItemStack quantumWrench = null;
        boolean isMainHand = false;

        if (mainHand.getItem() == ModItems.QUANTUM_WRENCH) {
            quantumWrench = mainHand;
            isMainHand = true;
        } else if (offHand.getItem() == ModItems.QUANTUM_WRENCH) {
            quantumWrench = offHand;
        }

        if (quantumWrench != null) {
            WrenchInventory inventory = new WrenchInventory(quantumWrench);
            inventory.setSelectedIndex(selectedIndex);

            // Sync updated stack back to client
            if (isMainHand) {
                player.getInventory().markDirty();
            }
        }
    }
}
