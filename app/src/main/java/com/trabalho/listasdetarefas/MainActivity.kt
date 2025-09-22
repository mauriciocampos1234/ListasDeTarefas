package com.trabalho.listasdetarefas

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {

    private lateinit var listView: ListView
    private lateinit var speakButton: Button
    private lateinit var bluetoothButton: Button
    private lateinit var alertButton: Button
    private lateinit var audioManager: AudioManager
    private var tts: TextToSpeech? = null

    private val tasks = listOf(
        "Reunião às 10h",
        "Lembrar de beber água",
        "Enviar relatório até 16h",
        "Pausa para alongamento",
        "Verificar e-mails prioritários"
    )

    private val CHANNEL_ID = "alertas_wear_os"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listView)
        speakButton = findViewById(R.id.speakButton)
        bluetoothButton = findViewById(R.id.bluetoothButton)
        alertButton = findViewById(R.id.alertButton)

        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tasks)

        tts = TextToSpeech(this, this)
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        listView.setOnItemClickListener { _, _, position, _ ->
            val text = tasks[position]
            speak("Item selecionado: $text")
        }

        speakButton.setOnClickListener {
            val joined = tasks.joinToString(separator = "; ")
            speak("Lista de tarefas: $joined")
        }

        bluetoothButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                putExtra("EXTRA_CONNECTION_ONLY", true)
                putExtra("EXTRA_CLOSE_ON_CONNECT", true)
                putExtra("android.bluetooth.devicepicker.extra.FILTER_TYPE", 1)
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Não foi possível abrir as configurações de Bluetooth.", Toast.LENGTH_SHORT).show()
            }
        }

        alertButton.setOnClickListener {
            sendAlertNotification("Alerta de segurança", "Verifique o ambiente: risco detectado.")
            speak("Alerta de segurança: verifique o ambiente.")
        }

        NotificationHelper.createChannel(this, CHANNEL_ID, "Alertas", "Alertas e notificações importantes")

        // (Opcional) Em API 33+, peça permissão para notificação
        // Você já adicionou <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
        // no Manifest. Se quiser pedir em runtime, pode fazer aqui.
        // if (Build.VERSION.SDK_INT >= 33) {
        //     requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        // }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("pt", "BR"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Idioma PT-BR não suportado para TTS", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Falha ao inicializar TTS", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_utterance_id")
    }

    // Consulta única das saídas de áudio (se quiser utilizar em alguma lógica)
    private fun audioOutputAvailable(type: Int): Boolean {
        val outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        return outputs.any { it.type == type }
    }

    private fun sendAlertNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Usa o NotificationManager do framework (sem compat)
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        nm.notify(1001, notification)
    }
}