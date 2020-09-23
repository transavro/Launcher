package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.HashSet;
import java.util.Set;

import utils.NetworkUtils;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private Set<NetworkConnectivityInterface> connectivityInterfaceSet;
    private Boolean connected = false;

    public NetworkChangeReceiver() {
        connectivityInterfaceSet = new HashSet<>();
    }

    public void addListener(NetworkConnectivityInterface networkConnectivityInterface) {
        connectivityInterfaceSet.add(networkConnectivityInterface);
    }

    public void removeListener(NetworkConnectivityInterface networkConnectivityInterface) {
        connectivityInterfaceSet.remove(networkConnectivityInterface);
    }

    private void notifyAllListener() {
        for (NetworkConnectivityInterface networkConnectivityInterface : connectivityInterfaceSet) {
            notifyListener(networkConnectivityInterface);
        }
    }

    private void notifyListener(NetworkConnectivityInterface networkConnectivityInterface) {
        if (connected) {
            networkConnectivityInterface.networkConnected();
        } else {
            networkConnectivityInterface.networkDisconnected();
        }
    }


    @Override
    public void onReceive(final Context context, final Intent intent) {
        connected = NetworkUtils.getConnectivityStatus(context) != NetworkUtils.TYPE_NOT_CONNECTED;
        notifyAllListener();
    }

    public interface NetworkConnectivityInterface {
        void networkConnected();

        void networkDisconnected();
    }
}
