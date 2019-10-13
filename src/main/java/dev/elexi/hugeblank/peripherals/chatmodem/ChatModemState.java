package dev.elexi.hugeblank.peripherals.chatmodem;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


public class ChatModemState {

    private ArrayList<String> captures = new ArrayList<>();
    private boolean changed;
    private boolean open;

    public ChatModemState() {
        this.changed = false;
    }

    public boolean isOpen() { return open;}

    private void setOpen( boolean state) {
        if(state == this.open) return;
        this.open = state;
        if( !changed) changed = true;
    }

    public boolean pollChanged()
    {
        if (changed) {
            changed = false;
            return true;
        }
        return false;
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
        setOpen(true);
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
            if (captures.isEmpty()) setOpen(false);
        }
    }
}
