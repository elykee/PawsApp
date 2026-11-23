package com.ek.pawsapp

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ek.pawsapp.PreferenceHelper.customPreference
import com.ek.pawsapp.PreferenceHelper.password
import com.ek.pawsapp.PreferenceHelper.userId
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {


    // Parte Eva 2 (Variables Globales)
    //-------------------------------------------------------------------------------------------------------------------------------
    val customPrefName = "User_data"

    // Variable para controlar la visibilidad del pwd
    private var isPasswordVisible = false

    // Declaramos las variables
    private lateinit var etEmail: EditText
    private lateinit var etPwd: EditText
    private lateinit var btnEye: ImageButton
    private lateinit var btnStartFree: MaterialButton
    private lateinit var btnLogin: MaterialButton

    //-----------------------------------------------------------------------------------------------------------------------------------


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Instalar systema splash screen (Android 12+)
        installSplashScreen()

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Referencias a las vistas de la animacíon
        val background = findViewById<View>(R.id.background_view)
        val smalllogo = findViewById<ImageView>(R.id.small_logo)
        val bigLogo = findViewById<ImageView>(R.id.big_logo)
        val loginContent = findViewById<View>(R.id.login_content)


        // Parte Eva 2
        //------------------------------------------------------------------------------------------------------------------------------------------
        // Referencias a las vistas del LoginContent
        etEmail = findViewById(R.id.etEmail)
        etPwd = findViewById(R.id.etPwd)
        btnEye = findViewById(R.id.btnEye)
        btnStartFree = findViewById(R.id.startFreeButton)
        btnLogin = findViewById(R.id.loginButton)
        //------------------------------------------------------------------------------------------------------------------------------------------------

        //Primer paso: animar el logo pequeño (fade out)
        smalllogo.animate()
            .alpha(0f)
            .setDuration(400)
            .withEndAction {
                //Segundo paso: transicionar al logo grande (fade in)
                bigLogo.scaleX = 0.8f
                bigLogo.scaleY = 0.8f
                bigLogo.alpha = 0f
                bigLogo.visibility = View.VISIBLE
                bigLogo.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(400)
                    .setInterpolator(AccelerateDecelerateInterpolator()) //curva de velocidad suave
                    .start()

                //Tercer paso: animar el color del fondo (amarillo a gris)
                val colorFrom = ContextCompat.getColor(this, R.color.yellow)
                val colorTo = ContextCompat.getColor(this, R.color.bg_color)
                //Calcula todos los colores intermedios para que el fondo cambie gradualmente gracias al ArgbEvaluator()
                val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
                colorAnimation.duration = 400
                //Actualiza el color de fondo de la vista en tiempo real durante la animación
                colorAnimation.addUpdateListener { animator ->
                    background.setBackgroundColor(
                        animator.animatedValue as Int
                    )
                }
                colorAnimation.start()
            }
            .start()

        //Cuarto paso: muestra la pantalla del sign-up/login después de la animación del splash
        Handler(Looper.getMainLooper()).postDelayed({  //Asegura que el código se ejecute en el hilo principal de la UI
            loginContent.visibility = View.VISIBLE  //Mostrar login
            smalllogo.visibility = View.GONE   //Ocultar logo pequeño
            bigLogo.visibility = View.GONE    //Ocultar logo grande
        }, 1500) //Espera 1200 ms (1.2 segundos) antes de ejecutar este bloque


        // Parte Eva 2
        //----------------------------------------------------------------------------------------------------------------------------
        // Lógica de Persistencia de Datos
        val prefs = customPreference(this, customPrefName)

        // Función para guardar
        fun saveAndClean(mensaje: String) {

            // Guardar en SharedPreferences
            prefs.userId = etEmail.text.toString()
            prefs.password = etPwd.text.toString()

            // Limpiar los datos
            etEmail.setText("")
            etPwd.setText("")

            isPasswordVisible = false
            etPwd.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            // Usamos el mensaje que le pasemos al llamar a la función
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }


        // BOTONES MODIFICADOS

        // 1 - Botón Start Free (Registro)

        btnStartFree.setOnClickListener {
            // Para validar email
            val emailEscrito = etEmail.text.toString()

            if (emailEscrito.isEmpty() || !emailEscrito.contains("@")) {
                // Mensaje que aclara que el registro requiere un correo
                Toast.makeText(
                    this,
                    "Please enter an email to create your account.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Extraemos el nombre
                val userName = emailEscrito.substringBefore("@")

                // Limpiamos y guardamos en SharedPreferences, pasándole el mensaje correspondiente
                saveAndClean("Account created succesfully!")

                // Crea el Bundle explícito
                val bundle = Bundle()
                bundle.putString("USER_NAME", userName)
                // Por ser una cuenta nueva
                bundle.putBoolean("IS_LOGIN", false)

                // Navega a StartFreeActivity
                val intent = Intent(this, StartFreeActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }

        //  2 - Botón Login (Inicio de sesión)
        btnLogin.setOnClickListener {
            // Para validar email
            val emailEscrito = etEmail.text.toString()

            if (emailEscrito.isEmpty() || !emailEscrito.contains("@")) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            } else {
                // Extraer el nombre (lo que esté antes del @) y lo guardamos en una variable local
                val userName = emailEscrito.substringBefore("@")

                // Limpiamos y guardamos en SharedPreferences, pasándole el mensaje correspondiente
                saveAndClean("Access granted! Welcome Back!.")

                // Crear el Bundle explícito
                val bundle = Bundle()
                bundle.putString("USER_NAME", userName)
                // Señal de que ya es usuario existente
                bundle.putBoolean("IS_LOGIN", true)

                // Navega a StartFreeActivity
                val intent = Intent(this, StartFreeActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
        // 3 - Botón ojo (Lógica visual)
        btnEye.setOnClickListener {
            // Invertimos el estado
            isPasswordVisible = !isPasswordVisible

            // Aplicamos los cambios según el nuevo estado
            if (isPasswordVisible) {
                // Modo ver: Texto visible + Icono tachado
                etPwd.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btnEye.setImageResource(R.drawable.ic_eye_closed)
            } else {
                // Modo ocultar: Puntitos + Icono normal
                etPwd.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnEye.setImageResource(R.drawable.ic_eye)
            }
            // Mantenemos el cursor al final del texto
            etPwd.setSelection(etPwd.text.length)
        }
    }

    // Cargar último estado al abrir la app
    override fun onResume() {
        super.onResume()
        val prefs = customPreference(this, customPrefName)
        val savedEmail = prefs.userId

        // Si hay email guardado, lo mostramos. Si está vacío, sale el Hint.
        etEmail.setText(savedEmail)


        // Password siempre vacía al inicio (Hint visible)
        etPwd.setText("")
        isPasswordVisible = false
        etPwd.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    }
}

// --- HELPER OBJECT ---
object PreferenceHelper {
    const val USER_ID = "USER_ID"
    const val USER_PASSWORD = "PASSWORD"

    fun customPreference(context: Context, name: String): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)

    inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    var SharedPreferences.userId
        get() = getString(USER_ID, "") ?: ""
        set(value) {
            editMe { it.putString(USER_ID, value) }
        }

    var SharedPreferences.password
        get() = getString(USER_PASSWORD, "") ?: ""
        set(value) {
            editMe { it.putString(USER_PASSWORD, value) }
        }

    // En nuestro caso esta parte no hace falta ya que no tenemos un botón de borrar
    // Pero por convención lo he dejado en el código
    var SharedPreferences.clearValues
        get() = false
        set(_) {
            editMe { it.clear() }
        }
}

//------------------------------------------------------------------------------------------------------------------------------------