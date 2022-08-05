package dev.hugeblank.peripherals.entangledmodem;

import dev.hugeblank.api.player.PlayerModemBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class EntangledModemBlock extends PlayerModemBlock implements Waterloggable, BlockEntityProvider {

    public EntangledModemBlock(FabricBlockSettings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EntangledModemBlockEntity(EntangledModemBlockEntity.TYPE, pos, state);
    }
}
