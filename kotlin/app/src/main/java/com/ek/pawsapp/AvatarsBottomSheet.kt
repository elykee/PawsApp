package com.ek.pawsapp

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import com.google.android.material.bottomsheet.BottomSheetDialog


class AvatarsBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflamos el layout de este fragment y lo devolvemos
        // inflater.inflate() convierte el XML en un objeto View que Android puede mostrar
        return inflater.inflate(R.layout.fragment_avatars_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Botón de cerrar en la esquina superior derecha
        view.findViewById<ImageButton>(R.id.close_button).setOnClickListener {
            dismiss()
        }
    }

    //Personalizar el Dialog para cambiar el fondo
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Forzamos a que el diálogo sea un BottomSheetDialog, ya que por defecto crea Dialog normal
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {

            // Accedemos al layout raíz del bottom sheet de la libreria Material Design
            val bottomSheet = dialog.findViewById<FrameLayout>(
                com.google.android.material.R.id.design_bottom_sheet
            )

            // Cambiamos el fondo al color que quieras (evita el feo borde blanco)
            bottomSheet?.setBackgroundResource(R.drawable.bottomsheet_bg)
        }

        return dialog
    }

}