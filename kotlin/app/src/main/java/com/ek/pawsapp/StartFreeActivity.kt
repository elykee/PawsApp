package com.ek.pawsapp


import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment


class StartFreeActivity : AppCompatActivity() {

    //Declaramos aquí para que toda la clase la vea
    private lateinit var allNavs: List<LinearLayout>

    // Parte Eva 2
    //---------------------------------------------------------------------------------------------------
    // Guardamos los extras del intent en una variable para poder reutilizarlos
    private var bundleMain: Bundle? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start_free)

        // Recuperamos los datos del Intent
        bundleMain = intent.extras

        //----------------------------------------------------------------------------------------------------

        // Referencias a la interfaz (bottomNav)
        val navHome = findViewById<LinearLayout>(R.id.nav_home)
        val navPets = findViewById<LinearLayout>(R.id.nav_pets)
        val navEvents = findViewById<LinearLayout>(R.id.nav_events)
        val navYou = findViewById<LinearLayout>(R.id.nav_you)

        //Asignamos lista global
        allNavs = listOf(navHome, navPets, navEvents, navYou)

        val frameContainer = findViewById<FrameLayout>(R.id.frame_container)
        val customBottomNav = findViewById<LinearLayout>(R.id.custom_bottom_nav)

        val initialContainerTopPadding= frameContainer.paddingTop
        val initialNavHeight =customBottomNav.layoutParams.height
        val initialNavBottomPadding =customBottomNav.paddingBottom

        // Parte superior: bajamos el contenido de los fragments lo que mida la barra de estado
        ViewCompat.setOnApplyWindowInsetsListener(frameContainer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = initialContainerTopPadding + systemBars.top)
            insets
        }

        // Parte inferior: subimos los botones del menú y extendemos el fondo hasta el borde sin aplastarlos
        ViewCompat.setOnApplyWindowInsetsListener(customBottomNav) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams {
                height = if (initialNavHeight > 0) initialNavHeight + systemBars.bottom else initialNavHeight
            }
            v.updatePadding(bottom = initialNavBottomPadding + systemBars.bottom)
            insets
        }
        // Parte 2 Eva
        //-----------------------------------------------------------------------------------------------------------
        // Carga Inicial: HomeFragment + Marcar botón Home

        val homeFrag = HomeFragment()

        // Preparamos el fragmento con datos
        homeFrag.arguments = bundleMain

        // Establecemos el fragmento instanciado dentro de StartFreeActivity
        replaceFragment(homeFrag)

        selectNav(navHome)

        // Botón Home
        navHome.setOnClickListener {
            val fragment = HomeFragment()

            // Creamos un sub-bundle basado en el original para mantener los datos del usuario
            val updateBundle = Bundle(bundleMain)

            // Activamos el flag de navegación para evitar que se repitan la notificación y el sonido
            updateBundle.putBoolean("IS_NAVIGATING", true)

            fragment.arguments = updateBundle
            replaceFragment(fragment)
            selectNav(navHome)
        }


        navPets.setOnClickListener {
            val fragment = PetsFragment()
            // Le pasamaos el bundle también a PetsFragment para que al volver sepa quién es el usuario
            fragment.arguments = bundleMain
            replaceFragment(fragment)
            selectNav(navPets)
        }
    }
    //-------------------------------------------------------------------------------------------------------------


    // Función para reemplazar el fragmento que se muestra dentro del contenedor
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }

    //Función para marcar cuál botón está seleccionado
    private fun selectNav(selected: LinearLayout) {
        allNavs.forEach { it.isSelected = it == selected }
    }
}