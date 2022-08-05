package dev.hugeblank.api.base;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class BasePeripheral implements IDynamicPeripheral {
    // Note to self - Don't try merging this with BaseModemBlockEntity.
    // BlockEntity.getType and IPeripheral.getType collide.

    protected final Set<IComputerAccess> computers = new HashSet<>();
    protected final ArrayList<Method> methods = new ArrayList<>();
    protected final ArrayList<String> names = new ArrayList<>();
    protected BaseModemBlockEntity<?> entity;

    public BasePeripheral() {}

    public void setBlockEntity(BaseModemBlockEntity<?> entity) {
        this.entity = entity;
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
        methods.add(method);
        names.add(name);
    }

    @Override
    public String @NotNull [] getMethodNames() {
        return names.toArray(new String[0]);
    }

    @Override
    public @NotNull MethodResult callMethod(@Nonnull IComputerAccess computer, @Nonnull ILuaContext context, int method, @Nonnull IArguments arguments) throws LuaException {
        return methods.get(method).run(computer, context, arguments);
    }
}
