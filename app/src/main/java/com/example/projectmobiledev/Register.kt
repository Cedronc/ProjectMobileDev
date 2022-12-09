package com.example.projectmobiledev

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Register : AppCompatActivity() {
  var reff: DatabaseReference? = null
  private var mAuth: FirebaseAuth? = null
  var passwordPush: String? = null

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


      var maxGebruikersNr: Long = 0


      //maak object gebruiker
      var gebruiker = Gebruiker()


      //Maak databank connectie
      reff = FirebaseDatabase.getInstance().reference.child("Gebruikers")
      reff!!.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
          if (dataSnapshot.exists()) {
            maxGebruikersNr = dataSnapshot.childrenCount
          }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
      })

      register_button.setOnClickListener(View.OnClickListener { v: View? ->
        if (firstName.text.toString().trim { it <= ' ' } == "") {
          firstName.error = "Please enter your first name"
          firstName.requestFocus()
        } else if (lastName.text.toString().trim { it <= ' ' } == "") {
          lastName.error = "Please enter your last name"
          lastName.requestFocus()
        } else if (email.text.toString().trim { it <= ' ' } == "") {
          email.error = "Please enter an email"
          email.requestFocus()
        } else if (password.text.toString().trim { it <= ' ' } == "") {
          password.error = "Please enter a password"
          password.requestFocus()
        } else if (c_password.text.toString().trim { it <= ' ' } == "") {
          c_password.error = "Please confirm your password"
          c_password.requestFocus()
        } else if (password.text.toString() != c_password.text.toString()) {
          c_password.error = "Please make sure botch passwords are the same"
          c_password.requestFocus()
        } else {
          gebruiker.setEmail(email.text.toString().trim { it <= ' ' })
          gebruiker.setFirstName(firstName.text.toString().trim { it <= ' ' })
          gebruiker.setLastName(lastName.text.toString().trim { it <= ' ' })
          passwordPush = password.text.toString().trim { it <= ' ' }
          registerGebruiker(gebruiker)
          val registerActivity = Intent(this@Register, Login::class.java)
          startActivity(registerActivity)
        }
      })
    }

  private fun registerGebruiker(gebruiker: Gebruiker) {
    val email = gebruiker.getEmail()!!.trim()
    val password: String? = passwordPush
    mAuth!!.createUserWithEmailAndPassword(email, password.toString()).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val user = mAuth!!.currentUser
        user!!.sendEmailVerification().addOnCompleteListener { task ->
          if (task.isSuccessful) {
            Toast.makeText(
              applicationContext,
              "User registered succes, please verify your email.",
              Toast.LENGTH_LONG
            ).show()
          }
        }
        val userID = mAuth!!.currentUser!!.uid
        reff!!.child(userID).setValue(gebruiker)
      }
    }
  }
}
