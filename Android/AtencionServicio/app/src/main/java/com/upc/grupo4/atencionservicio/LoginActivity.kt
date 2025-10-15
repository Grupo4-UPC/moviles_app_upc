package com.upc.grupo4.atencionservicio

import android.content.Context
import android.content.Intent
import android.media.ToneGenerator
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.upc.grupo4.atencionservicio.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private var countDownTimer: CountDownTimer? = null
    private var alertDialog: AlertDialog? = null

    // ToneGenerator para el sonido de error
    private var toneGenerator: ToneGenerator? = null

    companion object {
        private const val PREFS_NAME = "LoginPrefs"
        private const val KEY_FAILED_ATTEMPTS = "failed_attempts"
        private const val KEY_LOCK_TIME = "lock_time"
        private const val KEY_LOCK_COUNT = "lock_count"
        private const val MAX_ATTEMPTS = 3
        private const val BASE_LOCK_DURATION_SECONDS = 30
        private const val LOCK_INCREMENT_SECONDS = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el generador de tonos con volumen máximo
        initializeToneGenerator()

        checkAccountLock()

        binding.btnEnter.setOnClickListener {
            val email = binding.etUser.text?.toString()?.trim().orEmpty()
            val password = binding.etPassword.text?.toString()?.trim().orEmpty()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
            } else {
                // Mostrar loading y validar
                showLoading(true)
                validateCredentials(email, password)
            }
        }
    }

    private fun initializeToneGenerator() {
        try {
            // Volumen máximo (100)
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Función para reproducir el sonido de error con vibración
    private fun playErrorSoundWithVibration() {
        // Reproducir el sonido
        playErrorSound()

        // Agregar vibración
        try {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Para Android 8.0 (API 26) en adelante
                val vibrationEffect = VibrationEffect.createWaveform(
                    longArrayOf(0, 100, 50, 100), // Patrón: espera, vibra, pausa, vibra
                    -1 // No repetir
                )
                vibrator.vibrate(vibrationEffect)
            } else {
                // Para versiones anteriores
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 100, 50, 100), -1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Función para reproducir el sonido de error (tonos descendentes)
    private fun playErrorSound() {
        try {
            // Primer tono (más agudo) - E4
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 150)

            // Segundo tono (más grave) después de una breve pausa - C4
            Handler(Looper.getMainLooper()).postDelayed({
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 150)
            }, 150)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Función para reproducir el sonido de éxito (corto, armonioso y alegre)
    private fun playSuccessSound() {
        try {
            // Dos tonos armoniosos y rápidos - suena natural y alegre
            // Primera nota - tono medio dulce
            toneGenerator?.startTone(ToneGenerator.TONE_DTMF_5, 120)

            // Segunda nota - tono agudo armonioso (quinta perfecta)
            Handler(Looper.getMainLooper()).postDelayed({
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_9, 150)
            }, 100)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validateCredentials(email: String, password: String) {
        // Simular verificación con servidor (1.5 segundos)
        Handler(Looper.getMainLooper()).postDelayed({
            showLoading(false)

            if (email == "admin" && password == "admin") {
                loginSuccess()
            } else {
                loginFailed()
            }
        }, 1500)
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            // Ocultar formulario y mostrar loading
            binding.root.findViewById<View>(R.id.login_container)?.visibility = View.GONE
            binding.root.findViewById<View>(R.id.loading_container)?.visibility = View.VISIBLE
        } else {
            // Mostrar formulario y ocultar loading
            binding.root.findViewById<View>(R.id.login_container)?.visibility = View.VISIBLE
            binding.root.findViewById<View>(R.id.loading_container)?.visibility = View.GONE
        }
    }

    private fun loginSuccess() {
        // ¡REPRODUCIR SONIDO DE ÉXITO!
        playSuccessSound()

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putInt(KEY_FAILED_ATTEMPTS, 0)
            putLong(KEY_LOCK_TIME, 0)
            putInt(KEY_LOCK_COUNT, 0)
            apply()
        }

        // Mostrar diálogo de éxito
        showSuccessDialog()
    }

    private fun showSuccessDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_login_success, null)
        val btnAccept = dialogView.findViewById<Button>(R.id.btn_success_accept)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false)

        val successDialog = builder.create()
        successDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        successDialog.show()

        // Ocultar el botón o hacerlo opcional
        btnAccept?.visibility = View.GONE

        // Después de 2 segundos, navegar a OnboardingActivity
        Handler(Looper.getMainLooper()).postDelayed({
            successDialog.dismiss()
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
        }, 2000)
    }

    private fun loginFailed() {
        // ¡REPRODUCIR SONIDO DE ERROR CON VIBRACIÓN!
        playErrorSoundWithVibration()

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentAttempts = prefs.getInt(KEY_FAILED_ATTEMPTS, 0)
        val newAttempts = currentAttempts + 1

        prefs.edit().putInt(KEY_FAILED_ATTEMPTS, newAttempts).apply()

        if (newAttempts >= MAX_ATTEMPTS) {
            lockAccount()
        } else {
            val remaining = MAX_ATTEMPTS - newAttempts
            showLoginErrorDialog(remaining)
        }
    }

    private fun showLoginErrorDialog(intentosRestantes: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_login_error, null)
        val tvErrorMessage = dialogView.findViewById<TextView>(R.id.tv_error_message)
        val tvAttemptsRemaining = dialogView.findViewById<TextView>(R.id.tv_attempts_remaining)
        val btnAccept = dialogView.findViewById<Button>(R.id.btn_accept)

        tvErrorMessage.text = "Usuario o contraseña incorrectos"
        tvAttemptsRemaining.text = "Intentos restantes: $intentosRestantes"

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false)

        val errorDialog = builder.create()
        errorDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnAccept.setOnClickListener {
            errorDialog.dismiss()
        }

        errorDialog.show()
    }

    private fun lockAccount() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lockCount = prefs.getInt(KEY_LOCK_COUNT, 0)

        val lockDurationSeconds = BASE_LOCK_DURATION_SECONDS + (lockCount * LOCK_INCREMENT_SECONDS)
        val lockUntil = System.currentTimeMillis() + (lockDurationSeconds * 1000L)

        prefs.edit().apply {
            putLong(KEY_LOCK_TIME, lockUntil)
            putInt(KEY_LOCK_COUNT, lockCount + 1)
            putInt(KEY_FAILED_ATTEMPTS, 0)
            apply()
        }

        showLockDialog(lockUntil)
    }

    private fun checkAccountLock() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lockUntil = prefs.getLong(KEY_LOCK_TIME, 0)

        if (lockUntil > System.currentTimeMillis()) {
            showLockDialog(lockUntil)
        } else if (lockUntil > 0) {
            prefs.edit().apply {
                putInt(KEY_FAILED_ATTEMPTS, 0)
                putLong(KEY_LOCK_TIME, 0)
                apply()
            }
        }
    }

    private fun showLockDialog(lockUntil: Long) {
        binding.btnEnter.isEnabled = false
        binding.etUser.isEnabled = false
        binding.etPassword.isEnabled = false

        val remainingTime = lockUntil - System.currentTimeMillis()

        val dialogView = layoutInflater.inflate(R.layout.dialog_security_alert, null)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)
        val btnAccept = dialogView.findViewById<Button>(R.id.btnDialogAccept)

        val remainingSeconds = (remainingTime / 1000).toInt()
        tvMessage.text = "3 Intentos de login fallidos,\nespera $remainingSeconds segundos para que se desbloquee."

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false)

        alertDialog = builder.create()
        alertDialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_rounded_background)
        alertDialog?.show()

        btnAccept.isEnabled = false
        btnAccept.alpha = 0.5f

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                tvMessage.text = "3 Intentos de login fallidos,\nespera $seconds segundos para que se desbloquee."
            }

            override fun onFinish() {
                btnAccept.isEnabled = true
                btnAccept.alpha = 1.0f

                val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val lockCount = prefs.getInt(KEY_LOCK_COUNT, 0)
                val nextLockDuration = BASE_LOCK_DURATION_SECONDS + (lockCount * LOCK_INCREMENT_SECONDS)

                tvMessage.text = "Ya puedes intentar iniciar sesión nuevamente.\n\n⚠️ Siguiente bloqueo: $nextLockDuration segundos"

                btnAccept.setOnClickListener {
                    alertDialog?.dismiss()
                    enableLoginButton()

                    prefs.edit().apply {
                        putInt(KEY_FAILED_ATTEMPTS, 0)
                        putLong(KEY_LOCK_TIME, 0)
                        apply()
                    }
                }
            }
        }.start()
    }

    private fun enableLoginButton() {
        binding.btnEnter.isEnabled = true
        binding.etUser.isEnabled = true
        binding.etPassword.isEnabled = true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_faq, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_faq -> {
                startActivity(Intent(this, FAQActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        alertDialog?.dismiss()

        // Liberar recursos del ToneGenerator
        toneGenerator?.release()
        toneGenerator = null
    }
}