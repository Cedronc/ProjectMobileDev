package com.example.projectmobiledev

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {
  var emailPattern: String? = null

  var uid: String? = null

  private lateinit var auth: FirebaseAuth
  private var mAuthListener: AuthStateListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      setContentView(R.layout.activity_login)

      val login_button = findViewById<View>(R.id.login_button) as Button
      val email = findViewById<View>(R.id.email_login) as EditText
      val password = findViewById<View>(R.id.password_login) as EditText


      auth = FirebaseAuth.getInstance()
      mAuthListener = AuthStateListener { firebaseAuth: FirebaseAuth ->
        if (firebaseAuth.currentUser != null) {
        }
      }

      val create_new_account = findViewById<View>(R.id.create_new_account) as Button
      create_new_account.setOnClickListener { v: View? ->
        val mainActivity = Intent(this@Login, Register::class.java)
        mainActivity.putExtra("UID", uid)
        startActivity(mainActivity)
      }

      emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

      //Zet een listener op de button
      login_button.setOnClickListener(View.OnClickListener { v: View? ->
        if (password.text.toString().trim { it <= ' ' } == "") {
          Toast.makeText(applicationContext, "Please enter a password", Toast.LENGTH_SHORT).show()
        } else if (email.text.toString().trim { it <= ' ' } == "") {
          Toast.makeText(applicationContext, "Please enter an e-mail", Toast.LENGTH_SHORT).show()
        } else {
          logIn(
            email.text.toString().trim { it <= ' ' },
            password.text.toString().trim { it <= ' ' })
        }
      })

      auth = Firebase.auth

    }

  private fun logIn(email: String, password: String) {
    auth.signInWithEmailAndPassword(email, password)
      .addOnCompleteListener(OnCompleteListener { task: Task<AuthResult?> ->
        if (task.isSuccessful) {
          val user: FirebaseUser = auth.getCurrentUser()!!
          if (user.isEmailVerified) {
            uid = auth.getCurrentUser()!!.getUid()
            val mainActivity = Intent(
              this@Login,
              MainActivity::class.java
            ) //We maken een nieuwe intent dat van de register pagina terug naar de login pagina laat gaan
            mainActivity.putExtra("UID", uid)
            startActivity(mainActivity) //We starten de intent
          } else {
            Toast.makeText(applicationContext, "Please verify your email.", Toast.LENGTH_SHORT)
              .show()
          }
        } else {
          val temp = findViewById<EditText>(R.id.password_login)
          temp.error = "Details doesn't match."
          temp.requestFocus()
        }
      })
  }

  public override fun onStart() {
    super.onStart()
    auth.addAuthStateListener(mAuthListener!!)

    val currentUser = auth.currentUser
    if(currentUser != null){
    }
  }
}
