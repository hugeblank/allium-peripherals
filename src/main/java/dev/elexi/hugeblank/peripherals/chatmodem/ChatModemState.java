package dev.elexi.hugeblank.peripherals.chatmodem;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


public class ChatModemState {

    private final Runnable onChanged;
    private AtomicBoolean changed;
    private ArrayList<String> captures;
    private boolean open;

    public ChatModemState() {
        this.onChanged = null;
    }

    public ChatModemState(Runnable onChanged) {
        this.onChanged = onChanged;
    }

    public boolean isOpen() { return open;}

    private void setOpen( boolean state) {
        if(state == this.open) return;
        this.open = state;
        if( !changed.getAndSet( true ) && onChanged != null ) onChanged.run();
    }

    public boolean pollChanged()
    {
        return changed.getAndSet( false );
    }

    public void capture(String capture) {
        synchronized (captures) {
            boolean exists = false;
            for (int i = 0; i < captures.size(); i++) {
                if (captures.get(i).equals(capture)) {
                    exists = true;
                }
            }
            if (!exists) {
                captures.add(capture);
            }
        }
    }

    public String[] getCaptures() {
        synchronized (captures) {
            String[] captureList = new String[captures.size()];
            for (int i = 0; i < captures.size(); i++) {
                captureList[i] = captures.get(i);
            }
            return captureList;
        }
    }

    public void uncapture(String capture) {
        synchronized (captures) {
            if (capture != null) {
                for (int i = 0; i < captures.size(); i++) {
                    captures.remove(i);
                }
            } else {
                for (int i = 0; i < captures.size(); i++) {
                    if (captures.get(i).equals(capture)) {
                        captures.remove(i);
                    }
                }
            }
        }
    }
}
