package com.mervyn.quantum_wrench.recipe;

import com.mervyn.quantum_wrench.QuantumWrench;
import com.mervyn.quantum_wrench.config.ModConfig;
import com.mervyn.quantum_wrench.item.WrenchInventory;
import com.mervyn.quantum_wrench.registry.ModItems;
import com.mervyn.quantum_wrench.registry.ModRecipes;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class QuantumWrenchAddRecipe extends SpecialCraftingRecipe {
    public static final TagKey<net.minecraft.item.Item> WRENCHES_TAG = TagKey.of(RegistryKeys.ITEM,
            new Identifier(QuantumWrench.MOD_ID, "wrenches"));

    public QuantumWrenchAddRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    private boolean isValidWrench(ItemStack stack) {
        if (stack.isIn(WRENCHES_TAG))
            return true;
        String itemId = Registries.ITEM.getId(stack.getItem()).toString();
        return ModConfig.get().isWrench(itemId);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        ItemStack quantumWrench = ItemStack.EMPTY;
        ItemStack wrenchToAdd = ItemStack.EMPTY;
        int itemCount = 0;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty())
                continue;

            itemCount++;
            if (stack.getItem() == ModItems.QUANTUM_WRENCH) {
                if (!quantumWrench.isEmpty())
                    return false;
                quantumWrench = stack;
            } else if (isValidWrench(stack)) {
                if (!wrenchToAdd.isEmpty())
                    return false;
                wrenchToAdd = stack;
            } else {
                return false;
            }
        }

        if (quantumWrench.isEmpty() || wrenchToAdd.isEmpty() || itemCount != 2) {
            return false;
        }

        WrenchInventory inv = new WrenchInventory(quantumWrench);
        if (inv.isFull()) {
            return false;
        }

        Identifier wrenchId = Registries.ITEM.getId(wrenchToAdd.getItem());
        for (ItemStack stored : inv.getAllWrenches()) {
            if (Registries.ITEM.getId(stored.getItem()).equals(wrenchId)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack quantumWrench = ItemStack.EMPTY;
        ItemStack wrenchToAdd = ItemStack.EMPTY;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty())
                continue;

            if (stack.getItem() == ModItems.QUANTUM_WRENCH) {
                quantumWrench = stack.copy();
            } else if (isValidWrench(stack)) {
                wrenchToAdd = stack.copy();
            }
        }

        if (quantumWrench.isEmpty() || wrenchToAdd.isEmpty()) {
            return ItemStack.EMPTY;
        }

        WrenchInventory inv = new WrenchInventory(quantumWrench);
        inv.addWrench(wrenchToAdd);

        return quantumWrench;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.QUANTUM_WRENCH_ADD_SERIALIZER;
    }
}
