package dev.elexi.hugeblank.api.player;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralTile;
import dev.elexi.hugeblank.api.base.BaseModemBlockEntity;
import dev.elexi.hugeblank.peripherals.chatmodem.ChatModemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class PlayerModemBlockEntity extends BaseModemBlockEntity implements IPeripheralTile {

    private final PlayerPeripheral modem;
    private GameProfile profile; // Temporary

    public PlayerModemBlockEntity(BlockEntityType<? extends BaseModemBlockEntity> type, PlayerPeripheral modem) {
        super(type, modem);
        this.modem = modem;
    }

    boolean onBlockInteraction(PlayerEntity player) {
        return getModemPeripheral().bind(player);
    }

    public PlayerPeripheral getModemPeripheral() {
        return modem;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (modem.isBound()) {
            tag.putUuid("boundUUID", modem.profile.getId());
            tag.putString("boundName", modem.profile.getName());
        }
        return tag;
    }

    @Override
    public void setLocation(World world, BlockPos pos) {
        super.setLocation(world, pos);
        if (profile != null && world instanceof ServerWorld) modem.bind((ServerWorld) world, profile);

    }


    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if (tag.contains("boundUUID") && tag.contains("boundName")) {
            UUID playerID = tag.getUuid("boundUUID");
            String playerName = tag.getString("boundName");
            profile = new GameProfile(playerID, playerName);
            // modem.bind(tempWorld, new GameProfile(playerID, playerName));
        }
    }

    @Nullable
    @Override
    public IPeripheral getPeripheral(@Nonnull Direction side )
    {
        return side == updateDirection() ? modem : null;
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        if( this.hasWorld() )
        {
            updateBlockState();
        }
    }

    private void updateBlockState()
    {
        boolean paired = modem.isBound();
        BlockState state = getCachedState();
        if ( state.get( ChatModemBlock.PAIRED ) != paired) {
            getWorld().setBlockState(getPos(), state.with(ChatModemBlock.PAIRED, paired));
        }
    }
}
