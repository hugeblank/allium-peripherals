package dev.hugeblank.peripherals.entangledmodem;

import dev.hugeblank.Allium;
import dev.hugeblank.api.base.BaseModemBlockEntity;
import dev.hugeblank.api.player.PlayerModemBlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class EntangledModemBlockEntity extends PlayerModemBlockEntity {
    public static BlockEntityType<EntangledModemBlockEntity> TYPE = BlockEntityType.Builder
            .create(() -> new EntangledModemBlockEntity(EntangledModemBlockEntity.TYPE, false), Allium.Blocks.ENTANGLED_MODEM)
            .build(null);

    public static BlockEntityType<EntangledModemBlockEntity> TYPE_CREATIVE = BlockEntityType.Builder
            .create(() -> new EntangledModemBlockEntity(EntangledModemBlockEntity.TYPE, true), Allium.Blocks.ENTANGLED_MODEM_CREATIVE)
            .build(null);


    private final EntangledPeripheral peripheral;

    public EntangledModemBlockEntity(BlockEntityType<? extends BaseModemBlockEntity> blockEntityType, boolean creative) {
        super(blockEntityType, new EntangledPeripheral(creative));
        this.peripheral = (EntangledPeripheral) super.getModemPeripheral();
        peripheral.setBlockEntity(this);
    }
}
