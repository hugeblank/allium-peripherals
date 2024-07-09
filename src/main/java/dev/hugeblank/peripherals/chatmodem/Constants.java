package dev.hugeblank.peripherals.chatmodem;

import dan200.computercraft.api.lua.MethodResult;
import dev.hugeblank.peripherals.chatmodem.state.CalibratedChatModemState;
import dev.hugeblank.peripherals.chatmodem.state.RangedChatModemState;

import java.util.function.Supplier;

import static dev.hugeblank.peripherals.chatmodem.PeripheralMethod.create;

public interface Constants {
    interface PeripheralMethods{

        PeripheralMethod<?>[] rangedChatMethods ={
            //(string)
            createRanged("say", (state, arguments) -> {
                state.say(arguments.getString(0));
                return MethodResult.of();
            }),
            //(int,int,int)
            createRanged("setSendRange", (state, arguments) ->
                MethodResult.of(state.setSendRange(
                    arguments.getInt(0),
                    arguments.getInt(1),
                    arguments.getInt(2)
                ))),
            createRanged("getSendRange", (state, arguments) -> state.getSendRange()),
            createRanged("getMaxSendRange", (state, arguments) -> state.getMaxSendRange()),
            //(int,int,int)
            createRanged("setListenRange", (state, arguments) ->
                MethodResult.of(state.setListenRange(
                    arguments.getInt(0),
                    arguments.getInt(1),
                    arguments.getInt(2)
                ))),
            createRanged("getListenRange", (state, arguments) -> state.getListenRange()),
            createRanged("getMaxListenRange", (state, arguments) -> state.getMaxListenRange()),
        };
        PeripheralMethod<?>[] calibratedChatMethdos = {
            //(string)
            createBoundable("capture", (state, arguments) ->
                MethodResult.of(state.capture(arguments.getString(0)))
            ),
            //( [string] )
            createBoundable("uncapture", (state, arguments) ->
                MethodResult.of(state.uncapture(arguments.optString(0, null)))
            ),
            //(string[])
            createBoundable("getCaptures", (state, arguments) ->
                MethodResult.of((Object[]) state.getCaptures())
            ),
            createBoundable("getBoundPlayer", (state, arguments) -> {
                return state.getBoundPlayer();

            }),
            createBoundable("say", (state, arguments) -> {
                state.say(arguments.getString(0));
                return MethodResult.of(true);
            }),
        };
        PeripheralMethod<?>[] creativeMethods = staticInit(() -> {
            PeripheralMethod<?>[] peripheralMethods = new PeripheralMethod[calibratedChatMethdos.length - 1];
            System.arraycopy(calibratedChatMethdos, 0, peripheralMethods, 0, peripheralMethods.length);
            return peripheralMethods;
        });

        static PeripheralMethod<RangedChatModemState> createRanged(String name, PeripheralMethod.Body<RangedChatModemState> body) {
            return create(name,body);
        }

        static PeripheralMethod<CalibratedChatModemState> createBoundable(String name, PeripheralMethod.Body<CalibratedChatModemState> body) {
            return create(name,body);
        }
    }

    ;

    private static <T> T[] staticInit(Supplier<T[]> supplier) {
        return supplier.get();
    }
    ;


    interface Events {
        String CHAT_MESSAGE = "chat_message";
        String CHAT_CAPTURE = "chat_capture";
    }
}
