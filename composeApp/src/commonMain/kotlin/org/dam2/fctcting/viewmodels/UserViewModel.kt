package com.es.appmovil.viewmodel


import com.russhwolf.settings.Settings
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.dam2.fctcting.database.Database
import org.dam2.fctcting.database.Database.supabase

/**
 * Clase viewmodel para el usuario, donde guardaremos los datos del usuario y sus posibles funciones
 */
class UserViewModel {

    // Nombre de usuario del trabajador con el que iniciará sesión.
    private var _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    // Contraseña del usuario con la que iniciará sesión.
    private var _password = MutableStateFlow("")
    val passwordText: StateFlow<String> = _password

    // Contraseña del usuario con la que iniciará sesión.
    private var _visibility = MutableStateFlow(false)
    val visibility: StateFlow<Boolean> = _visibility

    // Contraseña del usuario con la que iniciará sesión.
    private var _login = MutableStateFlow(false)
    val login: StateFlow<Boolean> = _login

    // Contraseña del usuario con la que iniciará sesión.
    private var _checkSess = MutableStateFlow(false)
    val checkSess: StateFlow<Boolean> = _checkSess


    // Guarda el mensaje del error al fallar el login.
    private var _loginErrorMessage = MutableStateFlow("")
    val loginErrorMessage: StateFlow<String> = _loginErrorMessage

    // Indica si ha habido error o no en el login.
    private var _loginError = MutableStateFlow(false)
    val loginError: StateFlow<Boolean> = _loginError

    // Actualiza las variables para que se reflejen en la pantalla.
    fun onChangeValue(name:String, pass:String) {
        _username.value = name
        _password.value = pass
    }

    fun onChangeVisibility() {
        _visibility.value = !_visibility.value
    }

    fun checkLogin() {
        // Comprueba que los datos no estén vacíos
        if (username.value.isNotBlank() && passwordText.value.isNotBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Intenta iniciar sesión en la base de datos
                    supabase.auth.signInWith(Email){
                        email = _username.value
                        password = _password.value
                    }
                    _login.value = true
                    val session = supabase.auth.currentSessionOrNull()
                    if (session != null) {
                        // Guarda en almacenamiento local (por ejemplo, con Settings)
                        val settings = Settings()
                        settings.putString("access_token", session.accessToken)
                        settings.putString("refresh_token", session.refreshToken)
                        settings.putString("email_user", _username.value)
                    }
                } catch (e:AuthRestException) { // Si da error no ha podido iniciar sesión
                    _loginError.value = true
                    _loginErrorMessage.value = "Credenciales incorrectas"
                    _password.value = ""
                } catch (e:Exception) { // Si da error no ha podido iniciar sesión
                    _loginError.value = true
                    _loginErrorMessage.value = "No se ha podido conectar con la base de datos: ${e.message}, ${e.cause}"
                    _password.value = ""
                }

            }
        }
    }

    suspend fun checkSession() {
        _checkSess.value = true
        val settings = Settings()
        val emailUser = settings.getStringOrNull("email_user")
        val accessToken = settings.getStringOrNull("access_token")
        val refreshToken = settings.getStringOrNull("refresh_token")
        try {
            if (accessToken != null &&refreshToken != null) {

                val user = supabase.auth.retrieveUser(accessToken)

                val session = UserSession(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    expiresIn = 3600, // o cualquier valor razonable si no lo tienes exacto
                    tokenType = "Bearer",
                    user = user // opcional, Supabase puede refrescarlo luego
                )
                supabase.auth.importSession(session)
                withContext(Dispatchers.IO) {
                    Database.getEmployee(user.email ?: "")
                }
                _login.value = true
            } else {
                if (emailUser != null) _username.value = emailUser
            }
        } catch (e:Exception) {
            println(e)
        }
    }

//    fun signOut() {
//        supabase.auth.signOut()
//        settings.remove("access_token")
//        settings.remove("refresh_token")
//    }
    fun resetVar() {
        _visibility.value = false
        _login.value = false
        //_username.value = ""
        _password.value = ""
    }

    fun resetError() {
        _loginError.value = false
        _loginErrorMessage.value = ""
    }
}