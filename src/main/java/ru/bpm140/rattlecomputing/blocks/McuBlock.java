package ru.bpm140.rattlecomputing.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import ru.bpm140.rattlecomputing.blockentities.McuBlockEntity;
import ru.bpm140.rattlecomputing.menus.McuBlockMenu;

public class McuBlock extends Block implements EntityBlock {
    public McuBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                            Player player, BlockHitResult hit) {

        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            sp.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.literal("");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
                    BlockEntity entity = level.getBlockEntity(pos);
                    if (entity instanceof McuBlockEntity be) {
                        return new McuBlockMenu(id, inv, be);
                    }
                    return null;
                }
            }, buf -> buf.writeBlockPos(pos));
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new McuBlockEntity(pos, state);
    }
}
