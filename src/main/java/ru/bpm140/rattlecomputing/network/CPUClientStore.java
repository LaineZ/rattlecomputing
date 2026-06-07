package ru.bpm140.rattlecomputing.network;

import net.minecraft.core.BlockPos;
import ru.bpm140.rottenmangal.CPUStatus;

import java.util.*;

public class CPUClientStore {
    private static final int MAX_SIZE = 512;

    private static final Map<BlockPos, CPUStatus.CPUState> SNAPSHOTS = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<BlockPos, CPUStatus.CPUState> eldest) {
            return size() > MAX_SIZE;
        }
    };
    private static final List<CpuListener> LISTENERS = new ArrayList<>();

    public static void apply(BlockPos pos, CPUStatus.CPUState snapshot) {
        SNAPSHOTS.put(pos, snapshot);

        for (var l : LISTENERS) {
            l.onCpuUpdate(pos, snapshot);
        }
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
