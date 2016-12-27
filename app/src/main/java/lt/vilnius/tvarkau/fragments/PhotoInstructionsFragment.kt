package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_photo_instructions.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.dagger.component.ApplicationComponent
import lt.vilnius.tvarkau.prefs.BooleanPreference
import lt.vilnius.tvarkau.prefs.Preferences
import javax.inject.Inject
import javax.inject.Named

/**
 * @author Martynas Jurkus
 */
class PhotoInstructionsFragment : BaseFragment() {

    @field:[Inject Named(Preferences.DISPLAY_PHOTO_INSTRUCTIONS)]
    lateinit var displayPhotoInstructions: BooleanPreference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo_instructions, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photo_instructions_dont_show.setOnCheckedChangeListener { button, checked ->
            displayPhotoInstructions.set(!checked)
        }

        photo_instructions_btn_ok.setOnClickListener {
            activity.onBackPressed()
        }
    }

    override fun onInject(component: ApplicationComponent) {
        component.inject(this)
    }

    companion object {
        fun newInstance() = PhotoInstructionsFragment()
    }
}