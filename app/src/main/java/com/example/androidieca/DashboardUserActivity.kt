package com.example.androidieca

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.drawerlayout.widget.DrawerLayout
import com.example.androidieca.databinding.ActivityDashboardUserBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardUserActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityDashboardUserBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val drawerListView: ListView = findViewById(R.id.drawerListView)
        val navigationDrawer: View = findViewById(R.id.navigationDrawer)

        val drawerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.drawer_items, // R.array.drawer_items apunta al archivo drawer_menu.xml
            android.R.layout.simple_list_item_1
        )

        drawerListView.adapter = drawerAdapter
        drawerListView.setOnItemClickListener { _, _, position, _ ->
            // Manejar eventos de click en los items del Navigation Drawer aquí
            //
            when (position) {
                0 -> {
                    // Item 1 seleccionado
                }
                1 -> {
                    // Item 2 seleccionado
                }
                // Agrega el resto de las opciones aquí
            }
            drawerLayout.closeDrawer(navigationDrawer) // Cerrar el Navigation Drawer después de seleccionar un item
        }

        // Configurar el evento de click en el botón de hamburguesa para abrir el Navigation Drawer

        binding.menuBtn.setOnClickListener {
            drawerLayout.openDrawer(navigationDrawer)
        }


        //Haciendo click en logout
        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun checkUser() {

        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            //Si no esta logueado el usuario puede navegar en el panel de usuario sin loguearse
            binding.subTitleTv.text = "Invitado"
        }
        else{
            // logueado correctamente y mostrando informacion
            val email = firebaseUser.email
            // mandar a textview de toolbar
            binding.subTitleTv.text = email

        }
    }
}