package ru.bpm140.rattlecomputing.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import ru.bpm140.rattlecomputing.blockentities.McuBlockEntity;
import ru.bpm140.rattlecomputing.menus.McuBlockMenu;

public class McuBlock extends Block implements EntityBlock {
    public McuBlock(Properties properties) {
        super(properties);
    }

    public class McuMenuProvider implements MenuProvider {

        private final BlockPos pos;

        public McuMenuProvider(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public Component getDisplayName() {
            return Component.literal("");
        }

        @Override
        public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
            BlockEntity be = player.level().getBlockEntity(pos);

            if (be instanceof McuBlockEntity mcu) {
                return new McuBlockMenu(id, inv, mcu);
            }

            throw new IllegalStateException("Invalid MCU at " + pos);
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                            Player player, BlockHitResult hit) {

        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            sp.openMenu(new McuMenuProvider(pos), buf -> buf.writeBlockPos(pos));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof McuBlockEntity mcu) {
                mcu.tick();
            }
        };
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {

            BlockEntity be = level.getBlockEntity(pos);

            if (be instanceof McuBlockEntity mcu) {
                if (level instanceof ServerLevel serverLevel) {

                    ItemStackHandler inv = mcu.inventory;

                    for (int i = 0; i < inv.getSlots(); i++) {
                        ItemStack stack = inv.getStackInSlot(i);

                        if (!stack.isEmpty()) {
                            Containers.dropItemStack(level,
                                    pos.getX(), pos.getY(), pos.getZ(),
                                    stack);
                        }
                    }
                }
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new McuBlockEntity(pos, state);
    }
}
