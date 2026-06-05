package ru.bpm140.rattlecomputing.items;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import ru.bpm140.rattlecomputing.blockentities.McuBlockEntity;

public class DebuggerItem extends Item {
    public DebuggerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var pos = context.getClickedPos();
        var level = context.getLevel();


        if (level instanceof ServerLevel serverLevel) {
            BlockEntity be = serverLevel.getBlockEntity(pos);
            var player = context.getPlayer();

            if (player == null) {
                return InteractionResult.FAIL;
            }

            if (be instanceof McuBlockEntity mcu) {
                var snapshot = mcu.soc.cpu.takeSnapshot();
                player.sendSystemMessage(Component.literal(String.format("MCAUSE=0x%08X MTVAL=0x%08X MSTATUS=0x%06X",
                                        snapshot.mcause, snapshot.mtval, snapshot.mstatus)));
                var registersString = new StringBuilder();
                for (int x = 0; x < 32; x++) {
                    registersString.append(String.format("x%02d=0x%08X ", x, snapshot.registers[x]));
                }
                player.sendSystemMessage(Component.literal(registersString.toString()));
                player.sendSystemMessage(Component.literal(String.format("State: %s PC: 0x%08X MEPC: 0x%08X MTVEC: 0x%08X",
                        snapshot.state, snapshot.pc, snapshot.mepc, snapshot.mtvec)));

            }
        }

        return InteractionResult.SUCCESS;
    }
}
