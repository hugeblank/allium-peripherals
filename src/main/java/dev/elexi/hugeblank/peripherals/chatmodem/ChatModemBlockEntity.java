package dev.elexi.hugeblank.peripherals.chatmodem;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralTile;
import dev.elexi.hugeblank.Allium;
import dev.elexi.hugeblank.AlliumRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.math.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChatModemBlockEntity extends BlockEntity implements IPeripheralTile {

    public static BlockEntityType<ChatModemBlockEntity> normalChatModem = BlockEntityType.Builder.create(AlliumRegistry.normalSupplier, Allium.Blocks.chatModem).build(null);
    public static BlockEntityType<ChatModemBlockEntity> creativeChatModem = BlockEntityType.Builder.create(AlliumRegistry.creativeSupplier, Allium.Blocks.chatModemCreative).build(null);


    private static class Peripheral extends ChatPeripheral {
        private final ChatModemBlockEntity entity;

        Peripheral(ChatModemBlockEntity entity, boolean creative)
        {
            super( new ChatModemState(entity, creative) );
            this.entity = entity;
        }

        public void destroy() {
            this.getModemState().uncapture(null);
        }

        @Override
        public boolean equals( IPeripheral other )
        {
            return this == other || (other instanceof Peripheral && entity == ((Peripheral) other).entity);
        }
    }

    private Direction modemDirection = Direction.DOWN;
    private boolean hasModemDirection = false;
    private boolean destroyed = false;
    private Peripheral modem;

    public ChatModemBlockEntity(BlockEntityType<? extends ChatModemBlockEntity> type, boolean creative ) {
        super( type );
        modem = new Peripheral( this, creative);
    }

    public void onBlockInteraction(PlayerEntity player) {
        modem.setPlayer(player);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (modem.creative) return tag;
        String[] playerInfo = modem.getBoundPlayer();
        if (modem.getModemState().isBound()) {
            ListTag boundPlayer = new ListTag();
            boundPlayer.addTag(0, StringTag.of(playerInfo[0]));
            boundPlayer.addTag(1, StringTag.of(playerInfo[1]));

            tag.put("boundPlayer", boundPlayer);
        }
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if (modem.creative) return;
        if (tag.getType("boundPlayer") != 0 && !modem.getModemState().isBound()) {
            ListTag boundPlayer = tag.getList("boundPlayer", 8);
            modem.setBoundPlayer(boundPlayer.getString(0), boundPlayer.getString(1));
        }
    }

    public void destroy()
    {
        if( !destroyed )
        {
            modem.destroy();
            destroyed = true;
        }
    }

    @Override
    public void markDirty()
    {
        if (destroyed) return;
        super.markDirty();
        if( world != null )
        {
            updateDirection();
            updateBlockState();
        }
        else
        {
            hasModemDirection = false;
        }
    }

    @Override
    public void resetBlock()
    {
        super.resetBlock();
        hasModemDirection = false;
        world.getBlockTickScheduler().schedule( getPos(), getCachedState().getBlock(), 0 );
    }

    private void updateDirection()
    {
        if( hasModemDirection ) return;

        hasModemDirection = true;
        modemDirection = getCachedState().get( BlockChatModem.FACING );
    }

    private void updateBlockState()
    {
        boolean on = modem.getModemState().isOpen();
        boolean paired = modem.getModemState().isBound();
        BlockState state = getCachedState();
        if( state.get( BlockChatModem.ON ) != on )
        {
            getWorld().setBlockState( getPos(), state.with( BlockChatModem.ON, on ) );
        }
        if ( state.get( BlockChatModem.PAIRED ) != paired) {
            getWorld().setBlockState(getPos(), state.with(BlockChatModem.PAIRED, paired));
        }
    }

    @Nullable
    @Override
    public IPeripheral getPeripheral( @Nonnull Direction side )
    {
        updateDirection();
        return side == modemDirection ? modem : null;
    }
}
