package com.mervyn.quantum_wrench.recipe;

import com.mervyn.quantum_wrench.item.WrenchInventory;
import com.mervyn.quantum_wrench.registry.ModItems;
import com.mervyn.quantum_wrench.registry.ModRecipes;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class QuantumWrenchExtractRecipe extends SpecialCraftingRecipe {

    public QuantumWrenchExtractRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        ItemStack quantumWrench = ItemStack.EMPTY;
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
            } else {
                return false; // Only quantum wrench allowed
            }
        }

        // Must be exactly one quantum wrench with stored wrenches
        if (quantumWrench.isEmpty() || itemCount != 1) {
            return false;
        }

        WrenchInventory inv = new WrenchInventory(quantumWrench);
        return !inv.isEmpty(); // Only match if there are wrenches to extract
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        // Returns an empty quantum wrench
        // The stored wrenches are handled in getRemainder
        return new ItemStack(ModItems.QUANTUM_WRENCH);
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
        DefaultedList<ItemStack> remainder = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() == ModItems.QUANTUM_WRENCH) {
                // Note: We can't return multiple items as remainder in a single slot
                // The wrenches will need to be dropped manually or use events
                // For now, we'll handle this via a world event or player drops
                break;
            }
        }

        return remainder;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.QUANTUM_WRENCH_EXTRACT_SERIALIZER;
    }
}
