package com.mervyn.quantum_wrench.registry;

import com.mervyn.quantum_wrench.client.gui.WrenchSelectionScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {
    public static final String CATEGORY = "key.categories.quantum_wrench";

    public static KeyBinding OPEN_WRENCH_MENU;

    public static void register() {
        OPEN_WRENCH_MENU = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.quantum_wrench.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_WRENCH_MENU.wasPressed()) {
                if (client.player != null && client.currentScreen == null) {
                    client.setScreen(new WrenchSelectionScreen());
                }
            }
        });
    }
}
