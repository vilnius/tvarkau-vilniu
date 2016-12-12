package lt.vilnius.tvarkau.mvp.interactors

import lt.vilnius.tvarkau.entity.Profile

/**
 * @author Martynas Jurkus
 */
interface PersonalDataInteractor {

    fun getPersonalData(): Profile?

    fun storePersonalData(profile: Profile)

    fun isUserAnonymous(): Boolean
}