package pl.venixpll.plugin;

import com.darkmagician6.eventapi.EventManager;

public abstract class ProxyPlugin {

    public void onLoad(){
        EventManager.register(this);
    }

}
