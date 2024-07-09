package dev.hugeblank.peripherals.chatmodem.state;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dev.hugeblank.peripherals.chatmodem.IChatCatcher;
import dev.hugeblank.peripherals.chatmodem.PeripheralMethod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;

public interface IModemState extends IChatCatcher {
    PeripheralMethod<?>[] peripheralMethods();


    void setComputers(Iterable<IComputerAccess> mComputers);

    void readNbt(NbtCompound nbt, BlockEntity entity);

    void writeNbt(NbtCompound nbt);

    default BlockState updateBlockState(BlockState state){
        return state;
    }

    default void onDestroy() {
        detach();
    }

    default void detach() {

    }

    default void attach() {

    }

   default void onBlockInteraction(PlayerEntity player){

   }

    boolean matches(ServerPlayerEntity player);
}
