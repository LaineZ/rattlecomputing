package ru.bpm140.rattlecomputing.blockentities;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import ru.bpm140.rattlecomputing.Rattlecomputing;

public class McuBlockEntity extends BlockEntity {
    public final int WIDTH = 128;
    public final int HEIGHT = 64;

    public final byte[] pixels = new byte[WIDTH * HEIGHT];

    @OnlyIn(Dist.CLIENT)
    private boolean dirty = true;
    private int tick = 0;
    public final NativeImage image = new NativeImage(WIDTH, HEIGHT, false);


    public void setPixel(int x, int y, boolean on) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) return;

        int i = x + y * WIDTH;
        pixels[i] = (byte)(on ? 255 : 0);

        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public McuBlockEntity(BlockPos pos, BlockState blockState) {
        super(Rattlecomputing.MCU_BLOCK_ENTITY.get(), pos, blockState);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        int x = tick % WIDTH;
        int y = (tick / WIDTH) % HEIGHT;

        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 0;
        }

        setPixel(x, y, true);
        tick++;
        setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        byte[] data = tag.getByteArray("pixels");
        System.arraycopy(data, 0, pixels, 0, Math.min(data.length, pixels.length));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putByteArray("pixels", pixels);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        if (tag.contains("pixels")) {
            byte[] data = tag.getByteArray("pixels");
            System.arraycopy(data, 0, pixels, 0, Math.min(data.length, pixels.length));
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        if (pkt.getTag() != null && pkt.getTag().contains("pixels")) {
            byte[] data = pkt.getTag().getByteArray("pixels");
            System.arraycopy(data, 0, pixels, 0, Math.min(data.length, pixels.length));
        }
    }
}
