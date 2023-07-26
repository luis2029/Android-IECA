package com.example.androidieca

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.androidieca.databinding.ActivityDashboardUserBinding
import com.example.androidieca.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.ValueEventRegistration

class LoginActivity : AppCompatActivity() {
    // viewbinding
    private lateinit var binding: ActivityLoginBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //inicializa firebase auth

        firebaseAuth= FirebaseAuth.getInstance()

        //Dialogo de progreso de inicio, se mostrara mientras se valida al usuario
        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Por favor espera")
        progressDialog.setCanceledOnTouchOutside(false)

        // click en no tengo cuenta, manda a la activity de registro
        binding.noAccountTv.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        //Click en el boton login
        binding.loginBtn.setOnClickListener {
            /* Pasos
            * 1) Ingresar datos
            * 2) Validar datos
            * 3) login -Firebise Auth
            * 4) revisar el tipo de usuario
            *      if user- ir a user Dashboard
            *      if Admin- ir a admin Dashboard*/
            validateData()
        }

    }
    private var email = ""
    private var password = ""

    private fun validateData() {
        //1) Ingresar datos
        email=binding.emailEt.text.toString().trim()
        password=binding.passwordEt.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Correo electronico incorrecto", Toast.LENGTH_LONG).show()
        }
        else if (password.isEmpty()){
            Toast.makeText(this, "Ingresa el password", Toast.LENGTH_LONG).show()
        }
        else{
            loginUser()
        }
    }

    private fun loginUser() {
        // 3) login -Firebise Auth

        //mostrar progreso
        progressDialog.setMessage("Iniciar sesion")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //login correcto
                checkUser()
            }
            .addOnFailureListener { e->
                //login fallid
                progressDialog.dismiss()
                Toast.makeText(this,"Acceso fallido debido a ${e.message}", Toast.LENGTH_LONG).show()

            }
    }

    private fun checkUser() {
        /* 4) revisar el tipo de usuario
            *      if user- ir a user Dashboard
            *      if Admin- ir a admin Dashboard*/
        progressDialog.setMessage("Validando Usuario")

        val firebaseUser = firebaseAuth.currentUser!!

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()

                    //get user type e.g. user | admin
                    val userType = snapshot.child("userType").value
                    if (userType == "User"){
                        //usuario simple abre dashboard user
                        startActivity(Intent(this@LoginActivity, DashboardUserActivity::class.java))
                        finish()

                    }else if (userType == "Admin"){
                        //usuario admin abre dashboard admin
                        startActivity(Intent(this@LoginActivity, DashboardAdminActivity::class.java))
                        finish()

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}