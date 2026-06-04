package ru.bpm140.rattlecomputing.network;

import net.minecraft.core.BlockPos;
import ru.bpm140.rottenmangal.CPUStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CPUClientStore {
    private static final Map<BlockPos, CPUStatus.CPUState> SNAPSHOTS = new HashMap<>();
    private static final List<CpuListener> LISTENERS = new ArrayList<>();

    public static void apply(BlockPos pos, CPUStatus.CPUState snapshot) {
        SNAPSHOTS.put(pos, snapshot);

        for (var l : LISTENERS) {
            l.onCpuUpdate(pos, snapshot);
        }
    }

    public static void remove(BlockPos pos) {
        SNAPSHOTS.remove(pos);
    }

    public static CPUStatus.CPUState get(BlockPos pos) {
        return SNAPSHOTS.get(pos);
    }

    public static void subscribe(CpuListener listener) {
        LISTENERS.add(listener);
    }

    public static void unsubscribe(CpuListener listener) {
        LISTENERS.remove(listener);
    }

    public interface CpuListener {
        void onCpuUpdate(BlockPos pos, CPUStatus.CPUState snapshot);
    }
}
