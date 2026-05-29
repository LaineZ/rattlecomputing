package ru.bpm140.rattlecomputing.menus;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import ru.bpm140.rattlecomputing.Rattlecomputing;
import ru.bpm140.rattlecomputing.blockentities.McuBlockEntity;

public class McuBlockMenu extends AbstractContainerMenu {
    public McuBlockMenu(int id, Inventory playerInventory, McuBlockEntity be) {
        super(Rattlecomputing.MCU_BLOCK_MENU.get(), id);

        addSlot(new SlotItemHandler(
                be.inventory,
                0,
                24,
                14
        ));

        int startX = 8;
        int startY = 84;

        // PLAYER INVENTORY
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, startX + col * 18, startY + row * 18));
            }
        }

        // HOTBAR
        int hotbarY = 142;

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(
                    playerInventory,
                    i,
                    startX + i * 18,
                    hotbarY
            ));
        }
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        if (clickType == ClickType.THROW) {
            return;
        }
        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int quickMovedSlotIndex) {
        ItemStack quickMovedStack = ItemStack.EMPTY;
        Slot quickMovedSlot = this.slots.get(quickMovedSlotIndex);

        if (quickMovedSlot != null && quickMovedSlot.hasItem()) {
            ItemStack rawStack = quickMovedSlot.getItem();
            quickMovedStack = rawStack.copy();

            // SLOT 0 = BLOCK SLOT
            if (quickMovedSlotIndex == 0) {

                // PLAYER INV + HOTBAR
                if (!this.moveItemStackTo(rawStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }

                quickMovedSlot.onQuickCraft(rawStack, quickMovedStack);
            }

            // PLAYER INVENTORY + HOTBAR → BLOCK SLOT
            else if (quickMovedSlotIndex >= 1 && quickMovedSlotIndex < 37) {

                // TRY PUT INTO BLOCK SLOT ONLY
                if (!this.moveItemStackTo(rawStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (rawStack.isEmpty()) {
                quickMovedSlot.setByPlayer(ItemStack.EMPTY);
            } else {
                quickMovedSlot.setChanged();
            }

            quickMovedSlot.onTake(player, rawStack);
        }

        return quickMovedStack;
    }


    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}