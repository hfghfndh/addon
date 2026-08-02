package hfghfndh.addon.modules;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.util.InputUtil;

/**
 * Simple Flight module for the Meteor Client addon template.
 *
 * Notes:
 * - Place this file under src/main/java/hfghfndh/addon/modules/Flight.java (done).
 * - Register the module from your addon entry point with:
 *     Modules.get().add(new Flight());
 *
 * This module supports a "creative mode" style flight (uses player abilities)
 * and a simple non-creative flight using upward/downward movement when the
 * jump/sneak keys are pressed.
 */
public class Flight extends Module {
    private final SettingGroup sg = settings.getDefaultGroup();

    private final Setting<Boolean> creative = sg.add(new BoolSetting.Builder()
        .name("creative-mode")
        .description("Use vanilla creative flight (allowsFlying + flying).")
        .defaultValue(true)
        .build()
    );

    private final Setting<Double> speed = sg.add(new DoubleSetting.Builder()
        .name("speed")
        .description("Horizontal flight speed multiplier.")
        .defaultValue(1.0)
        .min(0.1)
        .max(5.0)
        .sliderMin(0.1)
        .sliderMax(5.0)
        .build()
    );

    private final Setting<Double> verticalSpeed = sg.add(new DoubleSetting.Builder()
        .name("vertical-speed")
        .description("Vertical speed used when not in creative mode.")
        .defaultValue(1.0)
        .min(0.1)
        .max(5.0)
        .sliderMin(0.1)
        .sliderMax(5.0)
        .build()
    );

    public Flight() {
        super(Category.Movement, "flight", "Simple flight module (creative or custom).", "flight");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        if (creative.get()) {
            // Enable creative-style flying
            mc.player.getAbilities().allowFlying = true;
            mc.player.getAbilities().flying = true;
            mc.player.getAbilities().setFlySpeed((float) (speed.get() / 10.0));
            mc.player.sendAbilities();
        } else {
            // Simple custom flight: apply vertical motion when jump/sneak pressed
            double hSpeed = speed.get();
            double vSpeed = verticalSpeed.get();

            // Horizontal motion: keep current movement but scale it
            mc.player.setVelocity(mc.player.getVelocity().multiply(hSpeed, 1.0, hSpeed));

            // Vertical controls
            InputUtil.KeyBindingState jump = mc.options.jumpKey;
            InputUtil.KeyBindingState sneak = mc.options.sneakKey;

            if (jump.isPressed()) {
                mc.player.addVelocity(0, vSpeed / 20.0, 0);
            } else if (sneak.isPressed()) {
                mc.player.addVelocity(0, -vSpeed / 20.0, 0);
            }

            mc.player.velocityModified = true; // ensure the client applies the velocity
        }
    }

    @Override
    public void onDeactivate() {
        if (mc.player != null && creative.get()) {
            mc.player.getAbilities().allowFlying = false;
            mc.player.getAbilities().flying = false;
            mc.player.sendAbilities();
        }
    }
}
