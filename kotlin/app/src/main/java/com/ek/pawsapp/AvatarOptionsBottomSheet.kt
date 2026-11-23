package com.ek.pawsapp

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File


class AvatarOptionsBottomSheet : BottomSheetDialogFragment() {

    // Atributos del fragmento: Referencia al avatar y URI de la foto seleccionada

    private var photoUri: Uri? = null


    // Lanzador para elegir imagen desde galería/archivos
    private val pickImageLauncher = this.registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUri = uri
            sendSelectedAvatar(uri)
        }
    }

    // Lanzador para tomar foto con la cámara
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        val uri = photoUri
        if (success && uri != null) {
            sendSelectedAvatar(uri)
        }
    }

    private fun sendSelectedAvatar(uri: Uri) {
        val result = Bundle().apply {
            putString("avatar_uri", uri.toString())
        }
        parentFragmentManager.setFragmentResult("avatar_request", result)
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout de este fragment y lo devolvemos
        // inflater.inflate() convierte el XML en un objeto View que Android puede mostrar
        return inflater.inflate(R.layout.fragment_avatar_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Botón de cerrar en la esquina superior derecha
        view.findViewById<ImageButton>(R.id.close_button).setOnClickListener {
            dismiss()
        }

        //Opción 1: Lanzar la cámara y recoger la foto en photoUri
        view.findViewById<View>(R.id.camera_button).setOnClickListener {
            val uri = createTempImageUri()
            photoUri = uri
            takePictureLauncher.launch(uri)

        }

        //Opctión 2: Escoger imagen de la Galería
        view.findViewById<View>(R.id.gallery_button).setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        //Opción 3: Navegar a otro BottomSheetFragment (Colección de avatares predefinidos)
        view.findViewById<View>(R.id.avatars_button).setOnClickListener {
            AvatarsBottomSheet().show(parentFragmentManager, "fragment_avatars_sheet")
        }

    }

    // Función para guardar la imagen en un archivo temporal y obtener su Uri mediante FileProvider
    private fun createTempImageUri(): Uri {
        val imagesDir = File(requireContext().cacheDir, "images").apply { mkdirs() }
        val imageFile = File.createTempFile("pet_avatar_", ".jpg", imagesDir)
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            imageFile
        )
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