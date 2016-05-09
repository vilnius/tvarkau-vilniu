package lt.vilnius.tvarkau.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.viewpagerindicator.CirclePageIndicator;

import javax.inject.Inject;
import javax.inject.Singleton;

import autodagger.AutoComponent;
import autodagger.AutoInjector;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.vilnius.tvarkau.ProblemDetailActivity;
import lt.vilnius.tvarkau.ProblemsListActivity;
import lt.vilnius.tvarkau.ProblemsMapActivity;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Problem;
import lt.vilnius.tvarkau.network.APIModule;
import lt.vilnius.tvarkau.network.service.IssueService;
import lt.vilnius.tvarkau.utils.GlobalConsts;
import lt.vilnius.tvarkau.utils.PermissionUtils;
import lt.vilnius.tvarkau.views.adapters.ProblemImagesPagerAdapter;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a single Problem detail screen.
 * This fragment is either contained in a {@link ProblemsListActivity}
 * in two-pane mode (on tablets) or a {@link ProblemDetailActivity}
 * on handsets.
 */
@AutoComponent(modules = APIModule.class)
@AutoInjector
@Singleton
public class ProblemDetailFragment extends Fragment implements Callback<Problem> {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    @Inject
    IssueService issueService;

    @Bind(R.id.problem_title)
    TextView mProblemTitle;
    @Bind(R.id.problem_description)
    TextView mProblemDesc;
    @Bind(R.id.problem_images_view_pager)
    ViewPager mProblemImagesViewPager;
    @Bind(R.id.problem_images_view_pager_indicator)
    CirclePageIndicator mProblemImagesViewPagerIndicator;

    public static ProblemDetailFragment getInstance(int problemId) {
        ProblemDetailFragment problemDetailFragment = new ProblemDetailFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(ProblemDetailFragment.ARG_ITEM_ID, problemId);

        problemDetailFragment.setArguments(arguments);

        return problemDetailFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DaggerProblemDetailFragmentComponent.create().inject(this);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            int issueId = getArguments().getInt(ARG_ITEM_ID);


            issueService.getIssue(issueId).enqueue(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.problem_detail, container, false);

        ButterKnife.bind(this, rootView);

        initProblemImagesPager();

        return rootView;
    }

    private void initProblemImagesPager() {
        // TODO: change to real images
        Integer[] imagesIds = {
                R.drawable.report1,
                R.drawable.report2,
                R.drawable.report3,
                R.drawable.report4
        };

        mProblemImagesViewPager.setAdapter(new ProblemImagesPagerAdapter<Integer>(getContext(), imagesIds) {
            @Override
            public void loadImage(Integer imageId, Context context, ImageView imageView) {
                Glide.with(context).load(imageId).centerCrop().into(imageView);
            }
        });
        mProblemImagesViewPager.setOffscreenPageLimit(3);
        mProblemImagesViewPagerIndicator.setViewPager(mProblemImagesViewPager);
    }

    @Override
    public void onResponse(Response<Problem> response) {
        Problem problem = response.body();

        mProblemDesc.setText(problem.getDescription());
    }

    @Override
    public void onFailure(Throwable t) {
        Toast.makeText(getActivity(), "Can't load issue: " + t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.problem_address)
    public void onProblemAddressClick() {
        Intent intent = new Intent(getActivity(), ProblemsMapActivity.class);

        Bundle data = new Bundle();
        data.putString(GlobalConsts.KEY_MAP_FRAGMENT, GlobalConsts.TAG_SINGLE_PROBLEM_MAP_FRAGMENT);

        intent.putExtras(data);

        startActivity(intent);
    }
}
