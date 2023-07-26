package com.example.androidieca

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.androidieca.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding:ActivityRegisterBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //inicializa firebase auth

        firebaseAuth= FirebaseAuth.getInstance()

        //Dialogo de progreso de inicio, se mostrara mientras se crea una cuenta | registro usuario
        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Por favor espera")
        progressDialog.setCanceledOnTouchOutside(false)

        //Regresa a la activity previa al hacer click,
        binding.backBtn.setOnClickListener {
            onBackPressed() //goto previous screen
        }

        //hacer click, empieza el registro
        binding.registerBtn.setOnClickListener {
            /* Pasos
            * 1)ingresar datos
            * 2)validar datos
            * 3)crear cuenta- Firebase Auth
            * 4)Guardar informacion de usuario - Realtime Firebase Database */
            validateData()
        }

    }
    private var name = ""
    private var email = ""
    private var password = ""

    private fun validateData() {
        //1)ingresar datos
        name=binding.nameEt.text.toString().trim()
        email=binding.emailEt.text.toString().trim()
        password=binding.passwordEt.text.toString().trim()
        val cPassword=binding.cPasswordEt.text.toString().trim()

        // 2)validar datos
        if(name.isEmpty()){
            //campo nombre vacio
            Toast.makeText(this,"Ingresa tu nombre...", Toast.LENGTH_LONG).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //Patron de correo electronico incorrecto
            Toast.makeText(this,"Correo electronico incorrecto...", Toast.LENGTH_LONG).show()
        }
        else if (password.isEmpty()){
            //campo password vacio
            Toast.makeText(this,"Ingresa tu passoword...", Toast.LENGTH_LONG).show()
        }
        else if (cPassword.isEmpty()){
            //campo cPassword vacio
            Toast.makeText(this,"Confirma tu passoword...", Toast.LENGTH_LONG).show()
        }
        else if (password != cPassword){
            //campo password y cpassword diferentes
            Toast.makeText(this,"Tu passoword y la confirmacion son diferentes...", Toast.LENGTH_LONG).show()
        }
        else{
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        // 3) crear cuenta- Firebase Auth

        //mostrar progreso

        progressDialog.setMessage("Creando cuenta...")
        progressDialog.show()

        //creando usuario en firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
             //Cuenta creada, y agregar la inf de usuario a la bd
              updateUserInfo()

        }
            .addOnFailureListener { e->
                // Cuenta no creada
                progressDialog.dismiss()
                Toast.makeText(this,"Fallo al crear la cuenta debido a ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun updateUserInfo() {
        // 4)Guardar informacion de usuario - Realtime Firebase Database
        progressDialog.setMessage("Guardando informacion de usuario")

        //tiempo estimado
        val timestamp =System.currentTimeMillis()

        //obtener el id del ultimo usuario
        val uid = firebaseAuth.uid

        // configurar datos para guardar en bd
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["profileImage"] = "" // se agrega nulo para que se edite despues
        hashMap["userType"] = "User" // se agrega el tipo user/admin,
        hashMap["timestamp"] = timestamp

        // se manda a la bd
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                //informacion del usario guardada y abre el dashboard de usuario
                progressDialog.dismiss()
                Toast.makeText(this,"Cuenta creada", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@RegisterActivity, DashboardUserActivity::class.java))
                finish()
            }

            .addOnFailureListener {e->
                //Fallo al agregar los datos en bd
                progressDialog.dismiss()
                Toast.makeText(this,"Fallo al guardar la informacion del usuario debido a ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}