package com.ek.pawsapp

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.view.isVisible
import com.google.android.material.imageview.ShapeableImageView


class HomeFragment : Fragment() {

    // Parte Eva 2
    //---------------------------------------------------------------------------------------------------------------------------------------------
    // Multimedia - Campanilla de notificación
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout de HomeFragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recuperar los datos del bundle
        val bundleRecibido = arguments
        val userName = bundleRecibido?.getString("USER_NAME") ?: ""
        val esLogin = bundleRecibido?.getBoolean("IS_LOGIN") ?: false
        val isNavigating = bundleRecibido?.getBoolean("IS_NAVIGATING") ?: false

        // Personalizar el mensaje de bienvenida en el layout si aplica y el sonido de notificaciones
        val tvWelcome = view.findViewById<android.widget.TextView>(R.id.welcome_title)

        // Lógica del texto de bienvenida
        if (esLogin && userName.isNotEmpty()) {
            val nameUpper = userName.replaceFirstChar { it.uppercase() }
            tvWelcome.text = "Welcome back, $nameUpper!"
        }

        // Lógica de la campana y la notificación flotante
        if (!isNavigating) {

            if (!esLogin) {
                /// Caso start free: lanzamos la notificación
                FloatingNotificationFragment().show(childFragmentManager, "floating_notification")

                // El sonido suena después de la notificación
                view.postDelayed({
                    if (isAdded) playNotificationSound()
                }, 5000)
            } else {
                // Caso Login: Suena la campana segundos después de entrar
                view.postDelayed({
                    if (isAdded) playNotificationSound()
                }, 3000)
            }
        }

        // ----------------------------------------------------------------------------------------------------------------------------------------

        //Referencia aL botón Avatar
        val avatarButtonContainer = view.findViewById<FrameLayout>(R.id.avatar_button_container)
        val avatarButton = view.findViewById<ShapeableImageView>(R.id.avatar_button)

        val editIconImageView = view.findViewById<ImageView>(R.id.edit_icon)

        //Inicializamos el AvatarOptionsBottomSheet Dialog (con childfragmentmanager porque Dialog no necesita contenedor (flota encima)
        avatarButtonContainer.setOnClickListener {
            val avatarsBottomSheet = AvatarOptionsBottomSheet()
            avatarsBottomSheet.show(childFragmentManager, "avatar_options_sheet")
        }

        childFragmentManager.setFragmentResultListener(
            "avatar_request",
            viewLifecycleOwner
        ) { _, bundle ->
            val uriString = bundle.getString("avatar_uri")
            if (!uriString.isNullOrEmpty()) {
                val selectedImageUri = Uri.parse(uriString)
                // Congelamos el tamaño actual (el que tenía con pet_add) para que no cambie de tamaño ni posición
                avatarButton.layoutParams = avatarButton.layoutParams.apply {
                    width = avatarButton.width
                    height = avatarButton.height
                }

                // Ocultamos el icono de edición y reemplazamos la imagen recortada en círculo
                avatarButton.setImageURI(selectedImageUri)
                editIconImageView.isVisible = false
            }
        }
    }

    // Parte Eva 2 ---------------------------------------------------------------------------------------------------------------------------------
    // Funciones del play de la notificación
    private fun playNotificationSound() {
        try {
            // Liberamos la memoria siempre antes de crear uno nuevo
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.bell)
            mediaPlayer?.start()
        } catch (e: Exception) {
            // Esto evita que la app se cierre si algo falla con el audio
            e.printStackTrace()
        }
    }

    // Liberar recursos al destruir el fragmento definitivamente
    override fun onDestroy() {
        super.onDestroy()
        // Por si acaso el usuario cierra la pantalla mientras suena
        mediaPlayer?.release()
        mediaPlayer = null
    }
// --------------------------------------------------------------------------------------------------------------------------------------------------
}

