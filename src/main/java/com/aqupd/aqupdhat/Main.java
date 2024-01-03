package com.aqupd.aqupdhat;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import static net.minecraft.server.command.CommandManager.literal;

public class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> dispatcher.register(literal("hat").executes(ctx -> {
            if(ctx.getSource().isExecutedByPlayer()) {
                ServerPlayerEntity user = ctx.getSource().getPlayer();
                ItemStack handItem = user.getMainHandStack();
                int handItemCount = handItem.getCount();
                ItemStack hatItem = user.getEquippedStack(EquipmentSlot.HEAD).copy();
                int hatItemCount = hatItem.getCount();
                //logError(String.valueOf(hatItem.getEnchantments()));
                if (Permissions.check(user, "aqupdhat.hat.usage", 0)) {
                    if (!EnchantmentHelper.hasBindingCurse(hatItem) || Permissions.check(user, "aqupdhat.hat.bypassbinding") || user.isCreative()) {
                        if (handItem.isEmpty()) { // If there's no item in hands
                            if (!hatItem.isEmpty()) {
                                user.equipStack(EquipmentSlot.HEAD, handItem);
                                user.setStackInHand(Hand.MAIN_HAND, hatItem);
                            } else {
                                ctx.getSource().sendError(Text.translatable("aqupdhat.noitem"));
                                return -1;
                            }
                        } else if (hatItem.isEmpty()) {    // If player doesn't have hat item
                            ItemStack oneHandItem = handItem.copy();
                            oneHandItem.setCount(1);
                            user.equipStack(EquipmentSlot.HEAD, oneHandItem);
                            handItem.setCount(handItem.getCount() - 1);
                        } else {
                            PlayerInventory inventory = user.getInventory();
                            int slot = inventory.getOccupiedSlotWithRoomForStack(hatItem);

                            user.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                            if(handItem.getCount() == 1) {
                                user.equipStack(EquipmentSlot.HEAD, handItem);
                                user.setStackInHand(Hand.MAIN_HAND, hatItem);
                            } else if(slot != -1) {
                                ItemStack invItem = inventory.getStack(slot);
                                invItem.setCount(invItem.getCount() + hatItemCount);
                            } else if(inventory.getEmptySlot() != -1) {
                                inventory.setStack(inventory.getEmptySlot(), hatItem);
                            } else {
                                user.dropStack(hatItem, 1);
                                ctx.getSource().sendError(Text.translatable("aqupdhat.droppeditem"));
                            }
                        }
                        return 1;
                    } else {
                        ctx.getSource().sendError(Text.translatable("aqupdhat.curse", Text.translatable("enchantment.minecraft.binding_curse")));
                        return -1;
                    }
                } else {
                    ctx.getSource().sendError(Text.translatable("aqupdhat.permission"));
                    return -1;
                }
            } else {
                ctx.getSource().sendError(Text.translatable("aqupdhat.console"));
                return -1;
            }
        }))));
    }
}
