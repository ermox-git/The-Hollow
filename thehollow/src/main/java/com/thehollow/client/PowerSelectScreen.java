package com.thehollow.client;

import com.thehollow.PowerNetworking.SelectPowerPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Small menu shown when you right-click the Hollow Relic. */
public class PowerSelectScreen extends Screen {
    private static final String[] NAMES = {"Reaper's Edge", "Hollow Wall", "Stillness Orb"};
    private static final String[] TIPS = {
            "Gives you a sword that instantly kills anything it hits. Then 100 seconds of cooldown.",
            "Raises a wall in front of you for 50 seconds. 10 second cooldown.",
            "Freezes every creature within 40 blocks for 10 seconds. 60 second cooldown."
    };

    public PowerSelectScreen() {
        super(Text.literal("Hollow Relic"));
    }

    @Override
    protected void init() {
        int top = this.height / 2 - 30;
        for (int i = 0; i < NAMES.length; i++) {
            final int index = i;
            this.addDrawableChild(ButtonWidget.builder(Text.literal(NAMES[i]), button -> {
                        ClientPlayNetworking.send(new SelectPowerPayload(index));
                        this.close();
                    })
                    .dimensions(this.width / 2 - 110, top + i * 28, 220, 20)
                    .tooltip(Tooltip.of(Text.literal(TIPS[i])))
                    .build());
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 66, 0xFF4455);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Choose a power"), this.width / 2, this.height / 2 - 50, 0xBBBBBB);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
