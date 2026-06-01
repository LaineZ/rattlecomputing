package ru.bpm140.rattlecomputing.soc;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import ru.bpm140.rottenmangal.*;
import ru.bpm140.rottenmangal.devices.FramebufferDevice;

import java.io.IOException;

public class SystemOnChip implements INBTSerializable<CompoundTag> {
    public final CPU cpu = new CPU();
    public final Bus bus = new Bus();
    public final FramebufferDevice internalFramebuffer = new FramebufferDevice(40, 34);

    public SystemOnChip() {
        var memory = new MemoryRegion(0x80000000, 16*1024*1024, true, true);
        cpu.memory.add(memory);
        cpu.bus.attach(internalFramebuffer);
    }

    public void tick() {
        if (cpu.getState() == CPUStatus.CPUState.RUNNING) {
            cpu.step();
        }
    }

    public void power() {
        cpu.setRunning();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        try {
            tag.putByteArray("cpu", cpu.takeSnapshot().toBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        try {
            if (compoundTag.contains("cpu")) {
                byte[] data = compoundTag.getByteArray("cpu");
                cpu.restoreFromSnapshot(new CPUSnapshot(data));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
