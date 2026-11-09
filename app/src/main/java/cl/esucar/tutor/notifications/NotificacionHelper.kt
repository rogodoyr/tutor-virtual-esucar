package cl.esucar.tutor.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import cl.esucar.tutor.R

/**
 * Recurso nativo 1: notificaciones locales.
 * Gestiona el canal y la construccion de las alertas de recordatorio.
 */
object NotificacionHelper {

    const val CANAL_ID = "recordatorios_entregas"
    const val EXTRA_ID = "extra_id"
    const val EXTRA_TITULO = "extra_titulo"
    const val EXTRA_MENSAJE = "extra_mensaje"

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                context.getString(R.string.canal_recordatorios),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.canal_recordatorios_desc)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }
    }

    fun mostrar(context: Context, id: Int, titulo: String, mensaje: String) {
        val permitido = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        if (!permitido) return

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(id, notificacion)
    }
}
