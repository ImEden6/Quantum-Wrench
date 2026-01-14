package com.mervyn.quantum_wrench.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mervyn.quantum_wrench.QuantumWrench;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("quantum_wrench.json");

    private static ModConfig INSTANCE;

    public List<String> wrenchIds = new ArrayList<>();

    private transient Set<String> wrenchIdSet;

    public static ModConfig get() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                INSTANCE = GSON.fromJson(json, ModConfig.class);
                INSTANCE.buildCache();
                QuantumWrench.LOGGER.info("Loaded config with {} wrench IDs", INSTANCE.wrenchIds.size());
            } catch (IOException e) {
                QuantumWrench.LOGGER.error("Failed to load config", e);
                INSTANCE = createDefault();
            }
        } else {
            INSTANCE = createDefault();
            save();
        }
    }

    public static void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(INSTANCE));
        } catch (IOException e) {
            QuantumWrench.LOGGER.error("Failed to save config", e);
        }
    }

    private static ModConfig createDefault() {
        ModConfig config = new ModConfig();
        config.wrenchIds.add("techreborn:wrench");
        config.wrenchIds.add("ae2:certus_quartz_wrench");
        config.wrenchIds.add("ae2:nether_quartz_wrench");
        config.buildCache();
        return config;
    }

    private void buildCache() {
        wrenchIdSet = new HashSet<>(wrenchIds);
    }

    public boolean isWrench(String itemId) {
        if (wrenchIdSet == null) {
            buildCache();
        }
        return wrenchIdSet.contains(itemId);
    }
}
