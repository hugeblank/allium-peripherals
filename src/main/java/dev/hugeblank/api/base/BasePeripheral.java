package dev.hugeblank.api.base;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class BasePeripheral implements IDynamicPeripheral {
    // Note to self - Don't try merging this with BaseModemBlockEntity.
    // BlockEntity.getType and IPeripheral.getType collide.

    protected final Set<IComputerAccess> computers = new HashSet<>();
    protected final Map<String, Method> methods = new HashMap<>();
    protected final ArrayList<String> names = new ArrayList<>();
    protected BaseModemBlockEntity<?> entity;

    public BasePeripheral() {}

    public void setBlockEntity(BaseModemBlockEntity<?> entity) {
        this.entity = entity;
    }

    public void scheduleTick() {
        if (entity.hasWorld())
            //noinspection ConstantConditions
            entity.getWorld().createAndScheduleBlockTick( entity.getPos(), entity.getCachedState().getBlock(), 0 );
    }

    public Set<IComputerAccess> getComputers() {
        return computers;
    }

    @Override
    public synchronized void attach(@Nonnull IComputerAccess computer) {
        synchronized (computers) {
            computers.add(computer);
        }
    }

    @Override
    public synchronized void detach(@Nonnull IComputerAccess computer) {
        synchronized (computers) {
            computers.remove(computer);
        }
    }

    public void addMethod(String name, Method method) {
        methods.put(name, method);
        names.add(name);
    }

    public boolean removeMethod(String name) {
        if (!names.contains(name)) return false;
        methods.remove(name);
        names.remove(name);
        return true;
    }

    @Override
    public String @NotNull [] getMethodNames() {
        return names.toArray(new String[0]);
    }

    @Override
    public @NotNull MethodResult callMethod(@Nonnull IComputerAccess computer, @Nonnull ILuaContext context, int method, @Nonnull IArguments arguments) throws LuaException {
        return methods.get(names.get(method)).run(computer, context, arguments);
    }
}
