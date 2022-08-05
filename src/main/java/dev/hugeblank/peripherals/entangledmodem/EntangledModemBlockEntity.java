package dev.hugeblank.peripherals.entangledmodem;

import dev.hugeblank.Allium;
import dev.hugeblank.api.player.PlayerModemBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class EntangledModemBlockEntity extends PlayerModemBlockEntity<EntangledPeripheral> {
    public static BlockEntityType<EntangledModemBlockEntity> TYPE = FabricBlockEntityTypeBuilder
            .create((pos, state) ->
                    new EntangledModemBlockEntity(EntangledModemBlockEntity.TYPE, pos, state),
                    Allium.Blocks.ENTANGLED_MODEM,
                    Allium.Blocks.ENTANGLED_MODEM_CREATIVE
            ).build(null);

    public EntangledModemBlockEntity(BlockEntityType<? extends EntangledModemBlockEntity> blockEntityType, BlockPos pos, BlockState state) {
        super(
                blockEntityType,
                pos,
                state,
                new EntangledPeripheral( // Determine whether this should be a creative or default modem
                        state.getBlock().equals(Allium.Blocks.ENTANGLED_MODEM)
                )
        );
    }
}
