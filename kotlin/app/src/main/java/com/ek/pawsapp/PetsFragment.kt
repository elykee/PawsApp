package com.ek.pawsapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

class PetsFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout para este Fragment
        return inflater.inflate(R.layout.fragment_pets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Parte Eva 2---------------------------------------------------------------------------------------------------------

        //Referencia al elemento botón
        val addPetBtn = view.findViewById<FrameLayout>(R.id.pet_add)

        addPetBtn.setOnClickListener { replaceFragment(PetProfileCreationFragment()) }


    }

    //Función para reemplazar el fragmento que se muestra dentro del contenedor
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction().replace(R.id.frame_container, fragment)
            // Para poder volver atrás
            .addToBackStack(null).commit()
    }
}

//--------------------------------------------------------------------------------------------------------------------------------
