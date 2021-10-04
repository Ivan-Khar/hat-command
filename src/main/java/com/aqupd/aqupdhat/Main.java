package com.aqupd.aqupdhat;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import static net.minecraft.server.command.CommandManager.*;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;

public class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            dispatcher.register(literal("hat").executes(context -> {
                ServerPlayerEntity user = context.getSource().getPlayer();
                ItemStack hatStack = user.getMainHandStack();
                ItemStack currentHat = user.getEquippedStack(EquipmentSlot.HEAD).copy();
                if (hatStack.getItem() == Items.AIR) {
                    if (!currentHat.isEmpty()){
                        user.equipStack(EquipmentSlot.HEAD, hatStack);
                        user.setStackInHand(Hand.MAIN_HAND, currentHat);
                    } else {
                        context.getSource().sendError(new TranslatableText("You don't have an item in your hand/on your head."));
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
            }));
        }));
    }
}
