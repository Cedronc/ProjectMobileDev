package com.example.projectmobiledev

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class Register : AppCompatActivity() {
  var reff: DatabaseReference? = null
  private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_register)

      mAuth = FirebaseAuth.getInstance()

      val already_have_an_account = findViewById<View>(R.id.already_have_account) as Button

      already_have_an_account.setOnClickListener {
        val registerActivity = Intent(
          this@Register,
          Login::class.java
        )
        startActivity(registerActivity)
      }

      val register_button = findViewById<View>(R.id.register_button) as Button
      val firstName = findViewById<View>(R.id.firstName_registration) as EditText
      val lastName = findViewById<View>(R.id.lastName_registration) as EditText
      val email = findViewById<View>(R.id.email_register) as EditText
      val password = findViewById<View>(R.id.password_registration) as EditText
      val c_password = findViewById<View>(R.id.confirm_password_registration) as EditText

    }
}
