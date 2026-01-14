package com.mervyn.quantum_wrench;

import com.mervyn.quantum_wrench.item.WrenchInventory;
import com.mervyn.quantum_wrench.network.ModNetworking;
import com.mervyn.quantum_wrench.registry.ModItems;
import com.mervyn.quantum_wrench.registry.ModKeybinds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class QuantumWrenchClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModKeybinds.register();

        ModelPredicateProviderRegistry.register(
                ModItems.QUANTUM_WRENCH,
                new Identifier(QuantumWrench.MOD_ID, "stored_count"),
                (stack, world, entity, seed) -> {
                    WrenchInventory inv = new WrenchInventory(stack);
                    return Math.max(0, inv.size() - 1);
                });

        ClientPreAttackCallback.EVENT.register((client, player, clickCount) -> {
            ItemStack mainHand = player.getMainHandStack();
            ItemStack offHand = player.getOffHandStack();

            ItemStack quantumWrench = null;
            if (mainHand.getItem() == ModItems.QUANTUM_WRENCH) {
                quantumWrench = mainHand;
            } else if (offHand.getItem() == ModItems.QUANTUM_WRENCH) {
                quantumWrench = offHand;
            }

            if (quantumWrench != null) {
                WrenchInventory inv = new WrenchInventory(quantumWrench);
                if (!inv.isEmpty()) {
                    inv.setSelectedIndex(-1);

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeInt(-1);
                    ClientPlayNetworking.send(ModNetworking.SELECT_WRENCH_PACKET, buf);

                    return true;
                }
            }
            return false;
        });
    }
}
