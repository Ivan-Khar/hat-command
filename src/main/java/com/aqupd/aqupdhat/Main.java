package com.aqupd.aqupdhat;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import static com.aqupd.aqupdhat.utils.AqLogger.*;
import static net.minecraft.server.command.CommandManager.*;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;

public class Main implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            dispatcher.register(literal("hat").executes(ctx -> {
                ServerPlayerEntity user = ctx.getSource().getPlayer();
                ItemStack hatStack = user.getMainHandStack();
                ItemStack currentHat = user.getEquippedStack(EquipmentSlot.HEAD).copy();
                //logError(String.valueOf(currentHat.getEnchantments()));
                if (Permissions.check(user, "aqupdhat.hat.usage", 0)) {
                    if (!currentHat.getEnchantments().toString().contains("minecraft:binding_curse") || Permissions.check(user, "aqupdhat.hat.bypassbinding") || user.isCreative()) {
                        if (hatStack.getItem() == Items.AIR) {
                            if (!currentHat.isEmpty()) {
                                user.equipStack(EquipmentSlot.HEAD, hatStack);
                                user.setStackInHand(Hand.MAIN_HAND, currentHat);
                            } else {
                                ctx.getSource().sendError(new TranslatableText("aqupdhat.noitem"));
                                return -1;
                            }
                        }
                        if (currentHat.isEmpty()) {
                            user.equipStack(EquipmentSlot.HEAD, hatStack.copy());
                            hatStack.setCount(0);
                        } else {
                            user.equipStack(EquipmentSlot.HEAD, hatStack);
                            user.setStackInHand(Hand.MAIN_HAND, currentHat);
                        }
                        return 1;
                    } else {
                        ctx.getSource().sendError(new TranslatableText("aqupdhat.curse"));
                        return -1;
                    }
                } else {
                    ctx.getSource().sendError(new TranslatableText("aqupdhat.permission"));
                    return -1;
                }
            }));
        }));
    }
}
