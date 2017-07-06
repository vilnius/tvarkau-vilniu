package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_photo_instructions.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.dagger.component.ActivityComponent
import lt.vilnius.tvarkau.prefs.LongPreference
import lt.vilnius.tvarkau.prefs.Preferences.LAST_DISPLAYED_PHOTO_INSTRUCTIONS
import javax.inject.Inject
import javax.inject.Named

/**
 * @author Martynas Jurkus
 */
class PhotoInstructionsFragment : BaseFragment() {

    @field:[Inject Named(LAST_DISPLAYED_PHOTO_INSTRUCTIONS)]
    lateinit var lastDisplayPhotoInstructions: LongPreference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo_instructions, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photo_instructions_btn_ok.setOnClickListener {
            activity.onBackPressed()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lastDisplayPhotoInstructions.set(System.currentTimeMillis())
    }

    override fun onInject(component: ActivityComponent) {
        component.inject(this)
    }

    companion object {
        fun newInstance() = PhotoInstructionsFragment()
    }
}