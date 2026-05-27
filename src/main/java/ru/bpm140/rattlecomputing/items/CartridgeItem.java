package ru.bpm140.rattlecomputing.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import ru.bpm140.rattlecomputing.packets.FirmwareUploadPacket;
import ru.bpm140.rattlecomputing.packets.SelfDestructPacket;
import screens.firmware.FirmwarePickerScreen;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CartridgeItem extends Item {
    public CartridgeItem(Properties properties) {
        super(properties);
    }

    public static void setFirmware(ItemStack stack, String path, String name) {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        tag.putString("path", path);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static String getFirmwarePath(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        CompoundTag tag = data.copyTag();

        return tag.getString("path");
    }

    public static String getFirmwareName(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        CompoundTag tag = data.copyTag();

        return tag.getString("name");
    }

    private void openFile(ItemStack stack) {
        Path dir = Path.of(System.getProperty("user.home"));

        Minecraft.getInstance().setScreen(
                new FirmwarePickerScreen(dir)
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            openFile(stack);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> components, TooltipFlag tooltipFlag) {
        String path = getFirmwarePath(stack);
        String name = getFirmwareName(stack);

        if (path == null && name == null) {
            components.add(Component.literal("< Empty cartridge >")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            if (name != null) {
                components.add(Component.literal(name)
                        .withStyle(ChatFormatting.WHITE));
            }

            if (path != null) {
                components.add(Component.literal(path)
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }
}
