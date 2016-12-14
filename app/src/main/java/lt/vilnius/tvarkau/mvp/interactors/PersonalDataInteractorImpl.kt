package lt.vilnius.tvarkau.mvp.interactors

import lt.vilnius.tvarkau.entity.Profile
import lt.vilnius.tvarkau.utils.SharedPrefsManager

/**
 * @author Martynas Jurkus
 */
class PersonalDataInteractorImpl(
        private val sharedPrefsManager: SharedPrefsManager
) : PersonalDataInteractor {

    override fun getPersonalData(): Profile? {
        return sharedPrefsManager.userProfile
    }

    override fun storePersonalData(profile: Profile) {
        sharedPrefsManager.saveUserDetails(profile)
    }

    override fun isUserAnonymous(): Boolean {
        return sharedPrefsManager.isUserAnonymous
    }
}