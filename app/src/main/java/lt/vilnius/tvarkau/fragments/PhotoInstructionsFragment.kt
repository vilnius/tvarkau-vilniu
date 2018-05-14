package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_photo_instructions.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.activity.ActivityConstants
import lt.vilnius.tvarkau.dagger.component.ActivityComponent

/**
 * @author Martynas Jurkus
 */
@Screen(navigationMode = NavigationMode.CLOSE,
        trackingScreenName = ActivityConstants.SCREEN_PHOTO_INSTRUCTIONS)
class PhotoInstructionsFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo_instructions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photo_instructions_btn_ok.setOnClickListener {
            activity!!.onBackPressed()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appPreferences.photoInstructionsLastSeen.set(System.currentTimeMillis())
    }

    override fun onInject(component: ActivityComponent) {
        component.inject(this)
    }

    companion object {
        fun newInstance() = PhotoInstructionsFragment()
    }
}