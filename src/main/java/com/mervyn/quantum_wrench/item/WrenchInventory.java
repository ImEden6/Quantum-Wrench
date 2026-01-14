package com.mervyn.quantum_wrench.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for managing stored wrenches in NBT.
 * Caches parsed wrench list and invalidates on write.
 */
public class WrenchInventory {
    private static final String STORED_WRENCHES_KEY = "StoredWrenches";
    private static final String SELECTED_INDEX_KEY = "SelectedIndex";
    public static final int MAX_WRENCHES = 16;

    private final ItemStack quantumWrench;
    private List<ItemStack> cachedWrenches;

    public WrenchInventory(ItemStack quantumWrench) {
        this.quantumWrench = quantumWrench;
    }

    public List<ItemStack> getAllWrenches() {
        if (cachedWrenches != null) {
            return cachedWrenches;
        }

        cachedWrenches = new ArrayList<>();
        NbtCompound nbt = quantumWrench.getNbt();
        if (nbt != null && nbt.contains(STORED_WRENCHES_KEY, NbtElement.LIST_TYPE)) {
            NbtList list = nbt.getList(STORED_WRENCHES_KEY, NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); i++) {
                ItemStack stack = ItemStack.fromNbt(list.getCompound(i));
                if (!stack.isEmpty()) {
                    cachedWrenches.add(stack);
                }
            }
        }
        return cachedWrenches;
    }

    public int getSelectedIndex() {
        NbtCompound nbt = quantumWrench.getNbt();
        if (nbt != null && nbt.contains(SELECTED_INDEX_KEY)) {
            int index = nbt.getInt(SELECTED_INDEX_KEY);
            if (index == -1)
                return -1;
            int size = getAllWrenches().size();
            if (size == 0)
                return -1;
            return Math.max(0, Math.min(index, size - 1));
        }
        return -1;
    }

    public void setSelectedIndex(int index) {
        NbtCompound nbt = quantumWrench.getOrCreateNbt();
        if (index == -1) {
            nbt.putInt(SELECTED_INDEX_KEY, -1);
            return;
        }
        int size = getAllWrenches().size();
        int clampedIndex = size == 0 ? -1 : Math.max(0, Math.min(index, size - 1));
        nbt.putInt(SELECTED_INDEX_KEY, clampedIndex);
    }

    public ItemStack getSelectedWrench() {
        int index = getSelectedIndex();
        if (index == -1) {
            return ItemStack.EMPTY;
        }
        List<ItemStack> wrenches = getAllWrenches();
        if (wrenches.isEmpty() || index >= wrenches.size()) {
            return ItemStack.EMPTY;
        }
        return wrenches.get(index).copy();
    }

    public boolean addWrench(ItemStack wrench) {
        if (wrench.isEmpty())
            return false;

        // Check for duplicate by Item ID
        Identifier wrenchId = Registries.ITEM.getId(wrench.getItem());
        for (ItemStack stored : getAllWrenches()) {
            Identifier storedId = Registries.ITEM.getId(stored.getItem());
            if (wrenchId.equals(storedId)) {
                return false; // Already stored
            }
        }

        NbtCompound nbt = quantumWrench.getOrCreateNbt();
        NbtList list = nbt.getList(STORED_WRENCHES_KEY, NbtElement.COMPOUND_TYPE);
        if (list == null) {
            list = new NbtList();
        }

        NbtCompound wrenchNbt = new NbtCompound();
        wrench.writeNbt(wrenchNbt);
        list.add(wrenchNbt);
        nbt.put(STORED_WRENCHES_KEY, list);

        markDirty();
        return true;
    }

    public List<ItemStack> extractAll() {
        List<ItemStack> result = new ArrayList<>(getAllWrenches());

        NbtCompound nbt = quantumWrench.getNbt();
        if (nbt != null) {
            nbt.remove(STORED_WRENCHES_KEY);
            nbt.remove(SELECTED_INDEX_KEY);
        }

        markDirty();
        return result;
    }

    public boolean isEmpty() {
        return getAllWrenches().isEmpty();
    }

    public int size() {
        return getAllWrenches().size();
    }

    public boolean isFull() {
        return size() >= MAX_WRENCHES;
    }

    private void markDirty() {
        cachedWrenches = null;
    }
}
