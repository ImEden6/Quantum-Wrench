package com.mervyn.quantum_wrench.registry;

import com.mervyn.quantum_wrench.QuantumWrench;
import com.mervyn.quantum_wrench.recipe.QuantumWrenchAddRecipe;
import com.mervyn.quantum_wrench.recipe.QuantumWrenchExtractRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static final SpecialRecipeSerializer<QuantumWrenchAddRecipe> QUANTUM_WRENCH_ADD_SERIALIZER = new SpecialRecipeSerializer<>(
            QuantumWrenchAddRecipe::new);

    public static final SpecialRecipeSerializer<QuantumWrenchExtractRecipe> QUANTUM_WRENCH_EXTRACT_SERIALIZER = new SpecialRecipeSerializer<>(
            QuantumWrenchExtractRecipe::new);

    public static void register() {
        Registry.register(Registries.RECIPE_SERIALIZER,
                new Identifier(QuantumWrench.MOD_ID, "quantum_wrench_add"),
                QUANTUM_WRENCH_ADD_SERIALIZER);
        Registry.register(Registries.RECIPE_SERIALIZER,
                new Identifier(QuantumWrench.MOD_ID, "quantum_wrench_extract"),
                QUANTUM_WRENCH_EXTRACT_SERIALIZER);
    }
}
