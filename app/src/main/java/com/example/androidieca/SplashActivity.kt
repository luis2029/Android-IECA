package com.example.androidieca

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        firebaseAuth = FirebaseAuth.getInstance()

        Handler().postDelayed(Runnable {
          checkUser()
        },1000)
    }

    private fun checkUser() {
        // get current user, if logged in or not
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            //usuario no logueado a main
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else{

            //usuario logueado revisar el tipo
            val firebaseUser = firebaseAuth.currentUser!!

            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        //get user type e.g. user | admin
                        val userType = snapshot.child("userType").value
                        if (userType == "User"){
                            //usuario simple abre dashboard user
                            startActivity(Intent(this@SplashActivity, DashboardUserActivity::class.java))
                            finish()

                        }else if (userType == "Admin"){
                            //usuario admin abre dashboard admin
                            startActivity(Intent(this@SplashActivity, DashboardAdminActivity::class.java))
                            finish()

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

        }
    }
}