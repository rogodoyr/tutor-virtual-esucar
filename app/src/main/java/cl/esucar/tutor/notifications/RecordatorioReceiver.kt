package cl.esucar.tutor.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Receptor que muestra la notificacion local cuando se cumple
 * la fecha programada del recordatorio.
 */
class RecordatorioReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(NotificacionHelper.EXTRA_ID, 0)
        val titulo = intent.getStringExtra(NotificacionHelper.EXTRA_TITULO) ?: "Recordatorio de entrega"
        val mensaje = intent.getStringExtra(NotificacionHelper.EXTRA_MENSAJE) ?: "Tienes un entregable proximo"

        NotificacionHelper.mostrar(context, id, titulo, mensaje)
    }
}
