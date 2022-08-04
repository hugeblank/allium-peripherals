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

    protected Set<IComputerAccess> computers;
    protected BaseModemBlockEntity entity;
    protected ArrayList<Method> methods;
    protected ArrayList<String> names;

    public BasePeripheral() {
        computers = new HashSet<>();
        methods = new ArrayList<>();
        names = new ArrayList<>();
        setComputers(computers);
    }

    public void setBlockEntity(BaseModemBlockEntity entity) {
        this.entity = entity;
    }

    public void setComputers(Set<IComputerAccess> computers) {
        this.computers = computers;
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
        String[] out = new String[names.size()];
        int i = 0;
        for (String name : names) {
            out[i++] = name;
        }
        return out;
    }

    @Override
    public MethodResult callMethod(@Nonnull IComputerAccess computer, @Nonnull ILuaContext context, int method, @Nonnull IArguments arguments) throws LuaException {
        return methods.get(method).run(computer, context, arguments);
    }
}
