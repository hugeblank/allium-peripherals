package dev.elexi.hugeblank.peripherals.chatmodem;

import dev.elexi.hugeblank.Allium;
import dev.elexi.hugeblank.api.player.PlayerModemBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;

public class ChatModemBlockEntity extends PlayerModemBlockEntity {

    public static BlockEntityType<ChatModemBlockEntity> TYPE = BlockEntityType.Builder
            .create(() -> new ChatModemBlockEntity(ChatModemBlockEntity.TYPE, false), Allium.Blocks.CHAT_MODEM)
            .build(null);
    public static BlockEntityType<ChatModemBlockEntity> TYPE_CREATIVE = BlockEntityType.Builder
            .create(() -> new ChatModemBlockEntity(ChatModemBlockEntity.TYPE_CREATIVE, true), Allium.Blocks.CHAT_MODEM_CREATIVE)
            .build(null);

    private final ChatPeripheral modem;
    public ChatModemBlockEntity(BlockEntityType<? extends ChatModemBlockEntity> type, boolean creative ) {
        super( type, new ChatPeripheral(creative));
        modem = (ChatPeripheral) super.getModemPeripheral();
        modem.setBlockEntity(this);
    }

    public void destroy()
    {
        super.destroy();
        modem.destroy();
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        if( world != null )
        {
            updateBlockState();
        }
    }

    private void updateBlockState()
    {
        boolean on = modem.isOpen();
        BlockState state = getCachedState();
        if( state.get( ChatModemBlock.ON ) != on )
        {
            getWorld().setBlockState( getPos(), state.with( ChatModemBlock.ON, on ) );
        }
    }
}
