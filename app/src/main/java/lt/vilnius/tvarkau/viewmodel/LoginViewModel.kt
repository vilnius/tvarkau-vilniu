package lt.vilnius.tvarkau.viewmodel

import javax.inject.Inject

class LoginViewModel @Inject constructor() : BaseViewModel() {

    fun attemptLogin() {
        _errorEvents.value = RuntimeException("Username/Password authentication not yet implemented")
    }
}
