package ru.bpm140.rattlecomputing.soc;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import ru.bpm140.rottenmangal.*;
import ru.bpm140.rottenmangal.devices.FramebufferDevice;
import java.nio.file.Files;
import java.nio.file.Path;


public class SystemOnChip implements INBTSerializable<CompoundTag> {
    public final CPU cpu = new CPU();
    public final Bus bus = new Bus();
    public final FramebufferDevice internalFramebuffer = new FramebufferDevice(40, 34);
    boolean changed = true;

    public SystemOnChip() {
        var memory = new MemoryRegion(0x80000000, 16 * 1024 * 1024, true, true);
        cpu.memory.add(memory);
        cpu.bus.attach(internalFramebuffer);
    }

    public void tick() {
        if (cpu.getState() == CPUStatus.CPUState.RUNNING) {
            for (int i = 0; i < 1000; i++) {
                cpu.step();
            }
            changed = true;
        }
    }

    public void toggle(Path path) throws Exception {
        var programBinary = Files.readAllBytes(path);
        cpu.loadELF(programBinary);
        cpu.reset(cpu.getState() == CPUStatus.CPUState.RUNNING);
        changed = true;
    }

    public void reset() {
        if (cpu.getState() == CPUStatus.CPUState.RUNNING) {
            cpu.reset(false);
        }
    }

    public boolean isChanged() {
        if (changed) {
         changed = false;
         return true;
        } else {
            return false;
        }
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        var snapshot = cpu.takeSnapshot();
        tag.putByte("status", (byte) snapshot.state.ordinal()); // FIXME: нормальную сериализацию хуйнуть, а то чё это за хрень
        tag.putInt("pc", snapshot.pc);
        tag.putIntArray("registers", snapshot.registers);
        tag.putInt("mip", snapshot.mip);
        tag.putInt("mie", snapshot.mie);
        tag.putInt("mtvec", snapshot.mtvec);
        tag.putInt("mepc", snapshot.mepc);
        tag.putInt("mcause", snapshot.mcause);
        tag.putInt("mtval", snapshot.mtval);
        tag.putInt("mstatus", snapshot.mstatus);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        var snapshot = new CPUSnapshot();
        snapshot.state = CPUStatus.CPUState.values()[compoundTag.getByte("state")];
        snapshot.pc = compoundTag.getInt("pc");
        snapshot.registers = compoundTag.getIntArray("registers");
        snapshot.mip = compoundTag.getInt("mip");
        snapshot.mie = compoundTag.getInt("mie");
        snapshot.mtvec = compoundTag.getInt("mtvec");
        snapshot.mepc = compoundTag.getInt("mepc");
        snapshot.mcause = compoundTag.getInt("mcause");
        snapshot.mtval = compoundTag.getInt("mtval");
        snapshot.mstatus = compoundTag.getInt("mstatus");


        cpu.restoreFromSnapshot(snapshot);
    }
}