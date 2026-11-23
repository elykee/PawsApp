package com.ek.pawsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.fragment.app.DialogFragment


class FloatingNotificationFragment : DialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Estilo sin marco
        setStyle(STYLE_NO_FRAME, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragment
        return inflater.inflate(R.layout.fragment_floating_notification, container, false)
    }

    override fun onStart() {
        super.onStart()
        // Asegura que el Dialog sea transparente y solo se vea nuestro layout
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setLayout(
            // Ajustar la ventana del Dialog al contenedor padre
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Referencias a los elementos
        val notificationCard = view.findViewById<View>(R.id.floating_notification)
        val closeButton = view.findViewById<ImageView>(R.id.close_button)

        // Cargar animaciones
        val slideIn = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in)
        val slideOut = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out)

        // Postergar la animación hasta que el layout esté listo
        notificationCard.post {
            // Lanzamos el deslizamiento
            notificationCard.startAnimation(slideIn)
            // La hacemos visible en el mismo instante
            notificationCard.visibility = View.VISIBLE
        }

        // Cerrar al clicar la X
        closeButton.setOnClickListener {
            notificationCard.startAnimation(slideOut)

            // Esconder al terminar la animación
            slideOut.setAnimationListener(object :
                android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation) {}
                override fun onAnimationEnd(animation: android.view.animation.Animation) {
                    dismiss()
                }

                override fun onAnimationRepeat(animation: android.view.animation.Animation) {
                }
            })
        }

    }
}