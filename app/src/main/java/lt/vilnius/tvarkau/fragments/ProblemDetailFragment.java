package lt.vilnius.tvarkau.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import autodagger.AutoComponent;
import autodagger.AutoInjector;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.vilnius.tvarkau.API.ApiMethod;
import lt.vilnius.tvarkau.API.ApiRequest;
import lt.vilnius.tvarkau.API.ApiResponse;
import lt.vilnius.tvarkau.API.GetProblemParams;
import lt.vilnius.tvarkau.API.LegacyApiModule;
import lt.vilnius.tvarkau.API.LegacyApiService;
import lt.vilnius.tvarkau.MainActivity;
import lt.vilnius.tvarkau.ProblemDetailActivity;
import lt.vilnius.tvarkau.ProblemsListActivity;
import lt.vilnius.tvarkau.ProblemsMapActivity;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Problem;
import lt.vilnius.tvarkau.utils.GlobalConsts;
import lt.vilnius.tvarkau.utils.PermissionUtils;
import lt.vilnius.tvarkau.views.adapters.ProblemImagesPagerAdapter;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * A fragment representing a single Problem detail screen.
 * This fragment is either contained in a {@link ProblemsListActivity}
 * in two-pane mode (on tablets) or a {@link ProblemDetailActivity}
 * on handsets.
 */
@AutoComponent(modules = LegacyApiModule.class)
@AutoInjector
@Singleton
public class ProblemDetailFragment extends Fragment {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String KEY_PROBLEM = "problem";

    @Inject LegacyApiService legacyApiService;

    @BindView(R.id.problem_title)
    TextView problemTitle;
    @BindView(R.id.problem_description)
    TextView problemDescription;
    @BindView(R.id.problem_entry_date)
    TextView problemEntryDate;
    @BindView(R.id.problem_status)
    TextView problemStatus;
    @BindView(R.id.problem_address)
    TextView problemAddress;
    @BindView(R.id.problem_answer_block)
    View problemAnswerBlock;
    @BindView(R.id.problem_answer)
    TextView problemAnswer;
    @BindView(R.id.problem_answer_date)
    TextView problemAnswerDate;
    @BindView(R.id.problem_images_view_pager)
    ViewPager problemImagesViewPager;
    @BindView(R.id.problem_images_view_pager_indicator)
    CirclePageIndicator problemImagesViewPagerIndicator;
    @BindView(R.id.problem_image_pager_layout)
    View problemImagePagerLayout;

    String issueId;
    Problem problem;

    public static ProblemDetailFragment getInstance(String problemId) {
        ProblemDetailFragment problemDetailFragment = new ProblemDetailFragment();

        Bundle arguments = new Bundle();
        arguments.putString(ProblemDetailFragment.ARG_ITEM_ID, problemId);

        problemDetailFragment.setArguments(arguments);

        return problemDetailFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (getArguments().containsKey(ARG_ITEM_ID)) {
            issueId = getArguments().getString(ARG_ITEM_ID);
            getData();
        }
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState
    ) {
        View rootView = inflater.inflate(R.layout.problem_detail, container, false);

        DaggerProblemDetailFragmentComponent.create().inject(this);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    private void getData() {
        GetProblemParams params = new GetProblemParams(issueId);
        ApiRequest<GetProblemParams> request = new ApiRequest<>(ApiMethod.GET_REPORT, params);

        Action1<ApiResponse<Problem>> onSuccess = apiResponse -> {
            if (apiResponse.getResult() != null) {
                problem = apiResponse.getResult();
                if (problem.getType() != null) {
                    problemTitle.setText(problem.getType());
                }
                if (problem.getDescription() != null) {
                    problemDescription.setText(problem.getDescription());
                }
                if (problem.getAddress() != null) {
                    problemAddress.setText(problem.getAddress());
                }
                if (problem.getEntryDate() != null) {
                    problemEntryDate.setText(problem.getEntryDate());
                }
                if (problem.getStatus() != null) {
                    problem.applyReportStatusLabel(problem.getStatus(), problemStatus);
                }
                if (problem.getAnswer() != null) {
                    problemAnswerBlock.setVisibility(View.VISIBLE);
                    problemAnswer.setText(problem.getAnswer());
                    problemAnswerDate.setText(problem.getAnswerDate());
                } else {
                    problemAnswerBlock.setVisibility(View.GONE);
                }
                if (problem.getPhotos() != null) {
                    if (problem.getPhotos().length == 1) {
                        problemImagesViewPagerIndicator.setVisibility(View.GONE);
                    }
                    initProblemImagesPager(problem);
                } else {
                    problemImagePagerLayout.setVisibility(View.GONE);
                }

            } else {
                Toast.makeText(getContext(), R.string.error_no_problem, Toast.LENGTH_SHORT).show();
            }
        };

        Action1<Throwable> onError = Throwable::printStackTrace;

        legacyApiService.getProblem(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                onSuccess,
                onError
            );
    }

    private void initProblemImagesPager(Problem problem) {
        String [] photos = problem.getPhotos();

        problemImagesViewPager.setAdapter(new ProblemImagesPagerAdapter<String>(getContext(), photos) {
            @Override
            public void loadImage(String imageId, Context context, ImageView imageView) {
                Glide.with(context).load(imageId).centerCrop().into(imageView);
            }
        });

        problemImagesViewPager.setOffscreenPageLimit(3);
        problemImagesViewPagerIndicator.setViewPager(problemImagesViewPager);
    }

    @OnClick(R.id.problem_address)
    public void onProblemAddressClick() {

        if ((PermissionUtils.isAllPermissionsGranted(getActivity(), MainActivity.MAP_PERMISSIONS))) {
            startProblemActivity();
        } else {
            requestPermissions(MainActivity.MAP_PERMISSIONS, MainActivity.GPS_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MainActivity.GPS_PERMISSION_REQUEST_CODE
            && PermissionUtils.isAllPermissionsGranted(getActivity(), MainActivity.MAP_PERMISSIONS)) {
            startProblemActivity();
        }
    }

    private void startProblemActivity() {
        // TODO investigate photo sizes when app crashes for TransactionTooLargeException: data parcel size
        Intent intent = new Intent(getActivity(), ProblemsMapActivity.class);

        Bundle data = new Bundle();
        data.putString(GlobalConsts.KEY_MAP_FRAGMENT, GlobalConsts.TAG_SINGLE_PROBLEM_MAP_FRAGMENT);
        data.putParcelable(KEY_PROBLEM, Parcels.wrap(problem));

        intent.putExtras(data);

        startActivity(intent);
    }
}
