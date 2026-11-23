package com.ek.pawsapp

import android.app.DatePickerDialog
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import java.io.File
import java.util.Calendar

// Parte Eva 2  --------------------------------------------------------------------------------------------------------------------------------------
class PetProfileCreationFragment : Fragment() {

    // Multimedia - Creamos efecto de sonido para el botón al crear el perfil
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pet_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referencia a las Vistas
        val etPetName = view.findViewById<EditText>(R.id.etPetName)
        val etPetBirth = view.findViewById<EditText>(R.id.etPetBirth)
        val btnCalendar = view.findViewById<ImageButton>(R.id.btnCalendar)
        val btnBack = view.findViewById<ImageButton>(R.id.btn_backarrow)
        val etBreed = view.findViewById<AutoCompleteTextView>(R.id.etBreed)
        val rgGender = view.findViewById<RadioGroup>(R.id.rgGender)
        val btnCreateProfile = view.findViewById<MaterialButton>(R.id.createPetProfile)
        val etPetWeight = view.findViewById<EditText>(R.id.etWeight)
        val avatarButtonContainer = view.findViewById<FrameLayout>(R.id.avatar_button_container)
        val petAvatarButton = view.findViewById<ShapeableImageView>(R.id.pet_avatar_button)
        val plusIconImageView = view.findViewById<ImageView>(R.id.plus_icon)


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
                petAvatarButton.layoutParams = petAvatarButton.layoutParams.apply {
                    width = petAvatarButton.width
                    height = petAvatarButton.height
                }

                // Ocultamos el icono plus y reemplazamos la imagen recortada en círculo
                petAvatarButton.setImageURI(selectedImageUri)
                plusIconImageView.isVisible = false
            }
        }

        //  Configuración Autocomplete (Razas)
        val breeds = resources.getStringArray(R.array.Breeds)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, breeds)
        etBreed.setAdapter(adapter)
        etBreed.threshold = 0 // Muestra sugerencias desde la primera letra o al hacer click

        // Un extra para navegación con teclado y poder seleccionar en el despliegue
        etBreed.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) etBreed.showDropDown()
        }

        // Muestra el desplegable al hacer click manual
        etBreed.setOnClickListener {
            etBreed.showDropDown()
        }

        // Configuración DatePickerDialog (Calendario de Nacimiento)
        val showDatePicker = {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd =
                DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                    val dateText = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    etPetBirth.setText(dateText)
                }, year, month, day)

            // Restricción: No se pueden elegir fechas futuras
            dpd.datePicker.maxDate = System.currentTimeMillis()
            dpd.show()
        }

        // Aplicamos el evento a ambos elementos
        etPetBirth.setOnClickListener { showDatePicker() }
        btnCalendar.setOnClickListener { showDatePicker() }

        // Navegación Atrás
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Lógica de peso Interactive Input (SWIPE / DESLIZAR)
        var weight = 0.0
        var xInitial = 0f
        var isDragging = false

        @android.annotation.SuppressLint("ClickableViewAccessibility")
        etPetWeight.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    xInitial = event.x
                    isDragging = false
                    false // Permitimos que el foco siga funcionando para escritura manual
                }

                android.view.MotionEvent.ACTION_MOVE -> {
                    val xDiff = event.x - xInitial

                    // Si el movimiento horizontal supera los 30px, activamos el modo deslizamiento
                    if (Math.abs(xDiff) > 30) {
                        isDragging = true

                        // IMPORTANTE: Bloqueamos el ScrollView para que no se mueva verticalmente mientras deslizamos
                        v.parent.requestDisallowInterceptTouchEvent(true)

                        if (xDiff > 0) {
                            weight += 0.1
                        } else {
                            if (weight > 0.1) weight -= 0.1 else weight = 0.0
                        }

                        if (weight > 0.05) { // Usamos 0.05 por si acaso hay errores de redondeo
                            etPetWeight.setText(String.format("%.2f kg", weight))
                        } else {
                            // Si el peso es 0, dejamos el campo vacío para que Android muestre el HINT
                            etPetWeight.setText("")
                        }
                        xInitial = event.x
                        true // Consumimos el evento: el peso cambia pero no salta el teclado
                    } else {
                        false
                    }
                }

                android.view.MotionEvent.ACTION_UP -> {
                    // Si solo fue un click (no drag), devolvemos false para que se abra el teclado normal
                    isDragging
                }

                else -> false
            }
        }

        // Sincronización: Si el usuario escribe el peso a mano, actualizamos la variable 'weight'
        etPetWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Intentamos convertir el texto a número para que el swipe sepa desde dónde empezar
                val value = s.toString().toDoubleOrNull()
                if (value != null) {
                    weight = value
                }
            }
        })

        // Botón Final: CREATE PROFILE
        btnCreateProfile.setOnClickListener {
            // Recogemos el género seleccionado del RadioGroup
            val selectedId = rgGender.checkedRadioButtonId

            // Validación simple: El nombre es obligatorio
            val name = etPetName.text.toString().trim()

            if (name.isEmpty()) {
                etPetName.error = "Please fill the pet's name to continue."
            } else if (selectedId == -1) {
                // Si no ha elegido género, avisamos al usuario
                Toast.makeText(requireContext(), "Please select a gender", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val radioButton = view.findViewById<RadioButton>(selectedId)
                val gender = radioButton.text.toString()

                // Añadimos un pequeño efecto de sonido al clicar el botón como toque de finalización del formulario
                playNotificationSound()
                // Notificación de éxito
                Toast.makeText(
                    requireContext(),
                    "${name}'s Profile created successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                // Aquí iría la lógica para navegar al siguiente fragment o guardar el nombre y el gender en la base de datos
            }
        }
    }

    // Funciones del play del efecto de sonido en Create Profile Button
    private fun playNotificationSound() {
        try {
            // Si el mediaplayer existe y está sonando, lo paramos de golpe
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            // Liberamos la memoria siempre antes de crear uno nuevo
            mediaPlayer?.release()
            mediaPlayer = null

            // Creamos la instancia nueva
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.bark)

            mediaPlayer?.let {
                it.setVolume(1.0f, 1.0f) // volumen al máximo
                it.start()

                // Cuando termine de sonar, liberamos la memoria automáticamente
                it.setOnCompletionListener { mp ->
                    mp.release()
                }
            }
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
}