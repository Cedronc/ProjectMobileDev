package com.example.projectmobiledev

class Gebruiker {
  private var firstName: String? = null
  private var lastName: String? = null
  private var email: String? = null
  private var admin = false

  fun getFirstName(): String? {
    return firstName
  }

  fun setFirstName(firstName: String?) {
    this.firstName = firstName
  }

  fun getLastName(): String? {
    return lastName
  }

  fun setLastName(lastName: String?) {
    this.lastName = lastName
  }

  fun getEmail(): String? {
    return email
  }

  fun setEmail(email: String?) {
    this.email = email
  }

  fun isAdmin(): Boolean {
    return admin
  }

  fun setAdmin(admin: Boolean) {
    this.admin = admin
  }
}
