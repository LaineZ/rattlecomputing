package ru.bpm140.rattlecomputing.menus;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import ru.bpm140.rattlecomputing.Rattlecomputing;
import ru.bpm140.rattlecomputing.blockentities.McuBlockEntity;

public class McuBlockMenu extends AbstractContainerMenu {

    public final McuBlockEntity be;
    private static final int HOTBAR_START = 27;
    private static final int HOTBAR_END = 36;
    private static final int INV_START = 0;
    private static final int INV_END = 27;

    public McuBlockMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        super(Rattlecomputing.MCU_BLOCK_MENU.get(), id);


        int startX = 8;
        int startY = 84;

        // PLAYER INVENTORY
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(
                        inv,
                        col + row * 9 + 9,
                        startX + col * 18,
                        startY + row * 18
                ));
            }
        }

        // HOTBAR
        int hotbarY = 142;

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(
                    inv,
                    i,
                    startX + i * 18,
                    hotbarY
            ));
        }

        BlockPos pos = buf.readBlockPos();
        Level level = inv.player.level();

        BlockEntity entity = level.getBlockEntity(pos);

        if (!(entity instanceof McuBlockEntity mcu)) {
            throw new IllegalStateException("Invalid MCU at " + pos);
        }

        this.be = mcu;
    }

    public McuBlockMenu(int id, Inventory inv, BlockEntity mcuEntity) {
        super(Rattlecomputing.MCU_BLOCK_MENU.get(), id);

        if (!(mcuEntity instanceof McuBlockEntity mcu)) {
            throw new IllegalStateException("Invalid MCU at " + mcuEntity.getBlockPos());
        }

        this.be = mcu;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        if (clickType == ClickType.THROW) {
            return;
        }
        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);

        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        // HOTBAR → INVENTORY
        if (index >= HOTBAR_START) {
            if (!this.moveItemStackTo(stack, INV_START, INV_END, false)) {
                return ItemStack.EMPTY;
            }
        }
        // INVENTORY → HOTBAR
        else {
            if (!this.moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}