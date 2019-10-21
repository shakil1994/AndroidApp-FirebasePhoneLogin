package com.example.shakil.kotlinfirebasephonelogin

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import java.util.*

class MainActivity : AppCompatActivity() {

    private val APP_REQUEST_CODE = 1000

    /*@BindView(R.id.btn_login)
    internal var btn_login: Button? = null*/

    @OnClick(R.id.btn_login)
    internal fun loginUser() {
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers!!).build(), APP_REQUEST_CODE
        )
    }

    lateinit var providers: List<AuthUI.IdpConfig>
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var listener: FirebaseAuth.AuthStateListener

    internal var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this@MainActivity)

        init()
    }

    private fun init() {
        providers = Arrays.asList<AuthUI.IdpConfig>(AuthUI.IdpConfig.PhoneBuilder().build())
        firebaseAuth = FirebaseAuth.getInstance().addAuthStateListener(listener)

        listener = { firebaseAuth1 ->
            var user = firebaseAuth1.getCurrentUser()
            if (user != null) {
                FirebaseInstanceId.getInstance()
                    .instanceId
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("BARBERTOKEN", task.result!!.token)

                            val intent = Intent(this@MainActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }.addOnFailureListener { e ->
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
            } else {
                loginUser()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
            } else {
                Toast.makeText(this, "Failed to sign in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (listener != null && firebaseAuth != null) {
            firebaseAuth.addAuthStateListener(listener)
        }
    }

    override fun onStop() {
        if (listener != null && firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(listener)
        }
        super.onStop()
    }
}
