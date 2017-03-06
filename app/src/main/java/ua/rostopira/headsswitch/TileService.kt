package ua.rostopira.headsswitch

import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.provider.Settings
import android.service.quicksettings.TileService
import android.widget.Toast

class HeadsUpSwitcher: TileService() {
    private var isEnabled: Boolean
        get() = Settings.Global.getInt(contentResolver, "heads_up_notifications_enabled") != 0
        set(v) {
            try {
                Settings.Global.putInt(contentResolver, "heads_up_notifications_enabled", if (v) 1 else 0)
            } catch (se: SecurityException) {
                Toast.makeText(this, "Security Exception", Toast.LENGTH_LONG).show()
                getPermission()
            }
            setTileState()
        }

    override fun onTileAdded() {
        if (checkSelfPermission(WRITE_SECURE_SETTINGS) != PackageManager.PERMISSION_GRANTED)
            getPermission()
        setTileState()
    }

    override fun onClick() {
        isEnabled = !isEnabled
    }

    private fun getPermission() =
        Runtime.getRuntime().exec("""su -c 'pm grant ${BuildConfig.APPLICATION_ID} $WRITE_SECURE_SETTINGS'""")

    private fun setTileState(state: Boolean = isEnabled) {
        if (state) {
            qsTile.label = "Heads-up enabled"
            qsTile.icon = Icon.createWithResource(this, R.drawable.ic_check_box_black_24dp)
        } else {
            qsTile.label = "Heads-up disabled"
            qsTile.icon = Icon.createWithResource(this, R.drawable.ic_check_box_outline_blank_black_24dp)
        }
        qsTile.updateTile()
    }
}

const val WRITE_SECURE_SETTINGS = "android.permission.WRITE_SECURE_SETTINGS"