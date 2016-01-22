package lt.vilnius.tvarkau;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.vilnius.tvarkau.entity.Problem;
import lt.vilnius.tvarkau.factory.DummyProblems;

/**
 * A fragment representing a single Problem detail screen.
 * This fragment is either contained in a {@link ProblemsListActivity}
 * in two-pane mode (on tablets) or a {@link ProblemDetailActivity}
 * on handsets.
 */
public class ProblemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Problem mItem;


    @Bind(R.id.problem_title)
    TextView mProblemTitle;
    @Bind(R.id.problem_description)
    TextView mProblemDesc;

    public static ProblemDetailFragment getInstance(int problemId) {
        ProblemDetailFragment problemDetailFragment = new ProblemDetailFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(ProblemDetailFragment.ARG_ITEM_ID, problemId);

        problemDetailFragment.setArguments(arguments);

        return problemDetailFragment;
    }

    public ProblemDetailFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            int problemIndex = getArguments().getInt(ARG_ITEM_ID);

            mItem = DummyProblems.getProblems().get(problemIndex);

            mProblemTitle.setText(mItem.getTitle());
            mProblemDesc.setText(mItem.getDescription());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.problem_detail, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }
}
