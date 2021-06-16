package dev.elexi.hugeblank.peripherals.entangledmodem;

import dev.elexi.hugeblank.api.player.PlayerModemBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class EntangledModemBlock extends PlayerModemBlock implements Waterloggable, BlockEntityProvider {
    private final boolean creative;

    public EntangledModemBlock(FabricBlockSettings settings, boolean creative) {
        super(settings);
        this.creative = creative;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        BlockEntityType<EntangledModemBlockEntity> use;
        if (creative) {
            use = EntangledModemBlockEntity.TYPE_CREATIVE;
        } else {
            use = EntangledModemBlockEntity.TYPE;
        }
        return new EntangledModemBlockEntity(use, this.creative);
    }
}
