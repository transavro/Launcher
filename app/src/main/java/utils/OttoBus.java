package utils;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class OttoBus {
    private static Bus mBus;

    public static Bus getBus(){
        if(mBus == null){
            mBus = new Bus(ThreadEnforcer.ANY);
        }
        return mBus;
    }
}