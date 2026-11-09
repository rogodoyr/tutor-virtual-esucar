package cl.esucar.tutor.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

/**
 * Programa recordatorios locales con AlarmManager para las fechas
 * limite de los entregables. El Receiver muestra la notificacion.
 */
object RecordatorioScheduler {

    fun programar(context: Context, id: Int, titulo: String, mensaje: String, cuandoMillis: Long) {
        val intent = Intent(context, RecordatorioReceiver::class.java).apply {
            putExtra(NotificacionHelper.EXTRA_ID, id)
            putExtra(NotificacionHelper.EXTRA_TITULO, titulo)
            putExtra(NotificacionHelper.EXTRA_MENSAJE, mensaje)
        }
        val pending = PendingIntent.getBroadcast(
            context, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cuandoMillis, pending)
    }

    fun cancelar(context: Context, id: Int) {
        val intent = Intent(context, RecordatorioReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pending)
    }
}
