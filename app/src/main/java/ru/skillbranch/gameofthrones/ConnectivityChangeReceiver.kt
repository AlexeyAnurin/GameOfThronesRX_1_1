package ru.skillbranch.gameofthrones

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class ConnectivityChangeReceiver : BroadcastReceiver() {

    private var listener: ((Int) -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {
        listener?.invoke(getConnectionStatus(context))
    }

    fun getConnectionStatus(context: Context): Int {
        val cm: ConnectivityManager? =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm != null) {
            val activeNetwork = cm.activeNetworkInfo
            if (null != activeNetwork && activeNetwork.isConnected) {
                if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) return STATUS_WIFI
                if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return STATUS_MOBILE
            }
        }
        return STATUS_NOT_CONNECTED
    }

    fun setListener(listener: (Int) -> Unit) {
        this.listener = listener
    }

    companion object {
        const val STATUS_WIFI = 1
        const val STATUS_MOBILE = 2
        const val STATUS_NOT_CONNECTED = 0
    }

}