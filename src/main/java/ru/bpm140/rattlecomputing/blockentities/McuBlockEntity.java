package ru.bpm140.rattlecomputing.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import ru.bpm140.rattlecomputing.Rattlecomputing;
import ru.bpm140.rattlecomputing.items.CartridgeItem;
import ru.bpm140.rattlecomputing.menus.McuBlockMenu;
import ru.bpm140.rattlecomputing.network.packets.CPUStatePacket;
import ru.bpm140.rattlecomputing.soc.SystemOnChip;
import ru.bpm140.rottenmangal.CPUSnapshot;

import java.nio.file.Files;
import java.nio.file.Path;

public class McuBlockEntity extends BaseContainerBlockEntity {
    public static final int INVENTORY_SIZE = 1;
    private NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
    public SystemOnChip soc = new SystemOnChip();

    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };


    public McuBlockEntity(BlockPos pos, BlockState blockState) {
        super(Rattlecomputing.MCU_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.rattlecomputing.mcu");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return new McuBlockMenu(id, playerInventory, this);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;
        soc.tick();
        if (soc.isChanged()) {
            setChanged();
            PacketDistributor.sendToPlayersTrackingChunk(
                    (ServerLevel) getLevel(),
                    new ChunkPos(getBlockPos()),
                    new CPUStatePacket(getBlockPos(), soc.cpu.getState())
            );
        }
    }

    public void toggleSoc(ServerPlayer interactionPlayer) {
        ItemStack cartridgeStack = inventory.getStackInSlot(0);
        var firmware = CartridgeItem.getFirmwarePath(cartridgeStack);
        if (firmware != null) {
            var path = Path.of(firmware);

            if (!Files.exists(path)) {
                interactionPlayer.sendSystemMessage(
                        Component.literal("Unable to start CPU: Firmware path is not found"));
                return;
            }

            try {
                soc.toggle(path);
            } catch (Exception e ) {
                interactionPlayer.sendSystemMessage(Component.literal("Failed to start CPU: " + e.getMessage()));
            }
        } else {
            interactionPlayer.sendSystemMessage(
                    Component.literal("Unable to start CPU: No firmware cartridge inserted"));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("cpustate")) {
            soc.deserializeNBT(provider, tag.getCompound("cpustate"));
        }

        if (tag.contains("inv")) {
            inventory.deserializeNBT(provider, tag.getCompound("inv"));
        }
    }

    public CPUSnapshot getSnapshot() {
        return soc.cpu.takeSnapshot();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("inv", inventory.serializeNBT(provider));
        tag.put("cpustate", soc.serializeNBT(provider));
    }

//    @Override
//    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
//        CompoundTag tag = new CompoundTag();
//        tag.putByte("state", (byte)soc.cpu.getState().ordinal());
//        return tag;
//    }
//
//    @Override
//    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
//        if (tag.contains("state")) {
//            var state = tag.getInt("state");
//        }
//    }
//
//    @Override
//    public ClientboundBlockEntityDataPacket getUpdatePacket() {
//        return ClientboundBlockEntityDataPacket.create(this);
//    }



    @Override
    public void setChanged() {
        super.setChanged();

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
