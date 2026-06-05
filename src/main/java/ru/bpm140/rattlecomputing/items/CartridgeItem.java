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
import net.neoforged.neoforge.network.PacketDistributor;
import ru.bpm140.rattlecomputing.network.packets.FirmwareUploadPacket;
import ru.bpm140.rattlecomputing.screens.Modal;
import ru.bpm140.rattlecomputing.screens.firmware.FirmwarePickerScreen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CartridgeItem extends Item {
    public CartridgeItem(Properties properties) {
        super(properties);
    }

    public static void setFirmware(ItemStack stack, String originalPath, String path, String name) {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        tag.putString("original_path", originalPath);
        tag.putString("path", path);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static String getFirmwarePath(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        CompoundTag tag = data.copyTag();

        return tag.getString("path");
    }

    public static String getFirmwareOriginalPath(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        CompoundTag tag = data.copyTag();

        return tag.getString("original_path");
    }

    public static String getFirmwareName(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        CompoundTag tag = data.copyTag();

        return tag.getString("name");
    }

    private boolean onFileOpened(Path file) {
        try {
            var size = Files.size(file) / 1024;
            if (size > 32) { // TODO: Increase limit with packet splitting or smth
                Modal.alert(Component.literal("File read error"), Component.literal("File is too large. (" + size + " KiB)"));
                return false;
            }

            byte[] elf = Files.readAllBytes(file);
            PacketDistributor.sendToServer(new FirmwareUploadPacket(file.toString(), elf));
            return true;
        } catch (IOException e) {
            Modal.alert(Component.literal("Upload error"), Component.literal(e.toString()));
            return false;
        }
    }

    private void openFilePicker(ItemStack stack) {
        var path = getFirmwareOriginalPath(stack);
        Path dir = Path.of(System.getProperty("user.home"));
        try {
            if (path != null) {
                dir = Path.of(path).getParent();
            }
        } catch (Exception ignored) {}

        Minecraft.getInstance().setScreen(new FirmwarePickerScreen(dir, this::onFileOpened));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            openFilePicker(stack);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> components, TooltipFlag tooltipFlag) {
        String originalPath = getFirmwareOriginalPath(stack);
        String name = getFirmwareName(stack);

        if (originalPath == null && name == null) {
            components.add(Component.literal("< Empty cartridge >")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            if (name != null) {
                components.add(Component.literal(name)
                        .withStyle(ChatFormatting.GRAY));
            }

            if (originalPath != null) {
                components.add(Component.literal(originalPath)
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }
}
