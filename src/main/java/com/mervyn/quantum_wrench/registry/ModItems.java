package com.mervyn.quantum_wrench.registry;

import com.mervyn.quantum_wrench.QuantumWrench;
import com.mervyn.quantum_wrench.item.QuantumWrenchItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final QuantumWrenchItem QUANTUM_WRENCH = new QuantumWrenchItem(
            new Item.Settings().maxCount(1));

    public static void register() {
        Registry.register(Registries.ITEM, new Identifier(QuantumWrench.MOD_ID, "quantum_wrench"), QUANTUM_WRENCH);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(QUANTUM_WRENCH);
        });
    }
}
