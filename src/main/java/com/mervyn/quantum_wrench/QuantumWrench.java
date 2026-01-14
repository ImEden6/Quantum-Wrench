package com.mervyn.quantum_wrench;

import com.mervyn.quantum_wrench.registry.ModItems;
import com.mervyn.quantum_wrench.registry.ModRecipes;
import com.mervyn.quantum_wrench.network.ModNetworking;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuantumWrench implements ModInitializer {
    public static final String MOD_ID = "quantum_wrench";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Quantum Wrench");
        ModItems.register();
        ModRecipes.register();
        ModNetworking.registerC2SPackets();
    }
}
