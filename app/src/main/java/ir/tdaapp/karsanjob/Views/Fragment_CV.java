package ir.tdaapp.karsanjob.Views;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ir.tdaapp.karsanjob.Adapter.CVAdapter;
import ir.tdaapp.karsanjob.DataBase.Tbl_Madrak;
import ir.tdaapp.karsanjob.DataBase.Tbl_Major;
import ir.tdaapp.karsanjob.MainActivity;
import ir.tdaapp.karsanjob.R;
import ir.tdaapp.karsanjob.Utility.AppController;
import ir.tdaapp.karsanjob.Utility.BaseFragment;
import ir.tdaapp.karsanjob.Utility.CustomRequest;
import ir.tdaapp.karsanjob.ViewModel.VM_CV_Short;
import ir.tdaapp.karsanjob.ViewModel.VM_FilterCV;
import ir.tdaapp.karsanjob.Views.Dialog.Dialog_Filter_CV;
import ir.tdaapp.karsanjob.Views.Dialog.Dialog_Filter_Search;

public class Fragment_CV extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "Fragment_CV";

    ShimmerFrameLayout Loading, Shimmer_No_Internet;
    RecyclerView RecyclerCV;
    CVAdapter cvAdapter;
    LinearLayoutManager linearLayoutManager;
    CustomRequest request;
    RequestQueue requestQueue;
    int Page = 0;
    Tbl_Madrak tbl_madrak;
    Tbl_Major tbl_major;
    NestedScrollView NestedScroll;
    boolean loading = true, isWorking, showToastError;
    ProgressBar progressBar;
    TextView lbl_TryingAgain;
    RelativeLayout NoInternet;
    SwipeRefreshLayout SwipeRefresh;
    FloatingActionButton floatButton, btn_RemoveFilter;
    AlphaAnimation Anim_FadeIn;
    ImageView img_Filter;
    TextView lbl_Error;

    //مربوط به فیلتر جستجو
    VM_FilterCV filterCV;
    ShimmerFrameLayout NoData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cv, container, false);

        FindItem(view);
        filterCV = new VM_FilterCV();
        SetCVs();
        Paging();
        OnClick();
        RefreshPage();

        return view;
    }

    void FindItem(View view) {
        Loading = view.findViewById(R.id.Loading);
        RecyclerCV = view.findViewById(R.id.RecyclerCV);
        progressBar = view.findViewById(R.id.progressBar);
        NestedScroll = view.findViewById(R.id.NestedScroll);
        NoInternet = view.findViewById(R.id.NoInternet);
        Shimmer_No_Internet = view.findViewById(R.id.Shimmer_No_Internet);
        lbl_TryingAgain = view.findViewById(R.id.lbl_TryingAgain);
        SwipeRefresh = view.findViewById(R.id.SwipeRefresh);
        img_Filter = view.findViewById(R.id.img_Filter);
        lbl_Error = view.findViewById(R.id.lbl_Error);
        tbl_madrak = new Tbl_Madrak(((MainActivity) getActivity()).dbAdapter);
        tbl_major = new Tbl_Major(((MainActivity) getActivity()).dbAdapter);
        tbl_major = new Tbl_Major(((MainActivity) getActivity()).dbAdapter);
        floatButton = view.findViewById(R.id.floatButton);
        NoData = view.findViewById(R.id.NoData);
        btn_RemoveFilter = view.findViewById(R.id.btn_RemoveFilter);
        Anim_FadeIn = new AlphaAnimation(0.0f, 1.0f);
        Anim_FadeIn.setDuration(500);
        isWorking = false;
        showToastError = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        requestQueue = Volley.newRequestQueue(getContext());

        request.setTag(TAG);

        requestQueue.add(request);

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    void SetCVs() {

        NoData.stopShimmerAnimation();
        NoData.setVisibility(View.GONE);

        isWorking = true;
        NoInternet.setVisibility(View.GONE);
        Shimmer_No_Internet.stopShimmerAnimation();

        if (Page == 0) {
            Loading.setVisibility(View.VISIBLE);
            Loading.startShimmerAnimation();
            RecyclerCV.setVisibility(View.GONE);
        }

        JSONObject input = new JSONObject();

        try {
            input.put("MadrakId", filterCV.getMadrakId());
            input.put("MajorId", filterCV.getMajorId());
            input.put("InsuranceId", filterCV.getInsuranceId());
            input.put("GenderId", filterCV.getGenderId());
            input.put("base1", filterCV.isBase1());
            input.put("base2", filterCV.isBase2());
            input.put("base3", filterCV.isBase3());
            input.put("Special", filterCV.isSpecial());
            input.put("Motorcycle", filterCV.isMotorcycle());
            input.put("Page", Page);
        } catch (Exception e) {
        }

        String Url = Api + "CV/PostCVS";

        request = new CustomRequest(Request.Method.POST, Url, input, response -> {

            if (filterCV.isEnableSearch()) {
                btn_RemoveFilter.show();
            } else {
                btn_RemoveFilter.hide();
            }

            isWorking = false;
            SwipeRefresh.setRefreshing(false);

            Loading.setVisibility(View.GONE);
            Loading.stopShimmerAnimation();
            progressBar.setVisibility(View.GONE);

            if (response.length() == 0 && Page == 0) {
                NoData.setVisibility(View.VISIBLE);
                NoData.startShimmerAnimation();
            } else {

                RecyclerCV.setVisibility(View.VISIBLE);
                List<VM_CV_Short> cvs = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject object = response.getJSONObject(i);
                        VM_CV_Short cv = new VM_CV_Short();

                        cv.setId(object.getInt("Id"));
                        cv.setDateInsert(object.getString("DateInsert"));
                        cv.setName(object.getString("Name"));
                        cv.setMadrak(tbl_madrak.GetTitleById(object.getInt("MadrakId")));
                        cv.setMajor(tbl_major.GetTitleById(object.getInt("MajorId")));
                        cv.setImage(ApiImage_CV + object.getString("ImageUrl"));

                        cvs.add(cv);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (Page == 0) {
                    cvAdapter = new CVAdapter(getContext(), cvs);
                    linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                    RecyclerCV.setAdapter(cvAdapter);
                    RecyclerCV.setLayoutManager(linearLayoutManager);
                } else {
                    cvAdapter.Add(cvs);
                    loading = true;
                }
            }

        }, error -> {

            if (filterCV.isEnableSearch()) {
                btn_RemoveFilter.show();
            } else {
                btn_RemoveFilter.hide();
            }

            showError(error);
        });

        AppController.getInstance().addToRequestQueue(SetTimeOut(request));
    }

    //در اینجا اگر مقدار ترو برگشت داده شود یعنی زمان پیجینگ رسیده است و نیاز به خواندن داده از سرور می باشد
    boolean isLastItemDisplaying() {

        View view = NestedScroll.getChildAt(NestedScroll.getChildCount() - 1);

        int diff = (view.getBottom() - (NestedScroll.getHeight() + NestedScroll
                .getScrollY()));

        if (diff <= 2000)
            return true;
        return false;
    }

    void Paging() {
        NestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (RecyclerCV.getAdapter() != null) {

                    if (loading) {

                        if (isLastItemDisplaying()) {
                            //اگر برنامه در حال دریافت اطلاعات از سرور باشد اجازه در یافت آیتم بعدی را نمی دهد
                            if (!isWorking) {
                                loading = false;

                                progressBar.setVisibility(View.VISIBLE);

                                ++Page;
                                SetCVs();
                            }
                        }

                    }

                }

                if (scrollY == 0) {
                    if (floatButton.isShown()) {
                        floatButton.hide();
                    }
                }

                if (NestedScroll.canScrollVertically(1)) {

                    if (!floatButton.isShown()) {

                        if (scrollY > oldScrollY) {
                            if (scrollY > 200) {
                                floatButton.show();
                            }
                        }
                    }
                }

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lbl_TryingAgain:
                SetCVs();
                break;
            case R.id.floatButton:
                NestedScroll.smoothScrollTo(0, 0);
                floatButton.hide();
                break;
            case R.id.img_Filter:
                openFilterDialog();
                break;
            case R.id.btn_RemoveFilter:
                btn_RemoveFilter.hide();
                filterCV = new VM_FilterCV();
                Page = 0;
                SetCVs();
                break;
        }
    }

    void openFilterDialog() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(Dialog_Filter_Search.TAG);

        if (prev == null) {
            ft.addToBackStack(null);

            Dialog_Filter_CV dialog_filter_cv = new Dialog_Filter_CV(filterCV);

            dialog_filter_cv.show(ft, Dialog_Filter_CV.TAG);

            dialog_filter_cv.setClickDialogFilterSearh(() -> {
                Page = 0;
                SetCVs();
            });
        }
    }

    void OnClick() {
        floatButton.hide();
        lbl_TryingAgain.setOnClickListener(this);
        floatButton.setOnClickListener(this);
        img_Filter.setOnClickListener(this);
        btn_RemoveFilter.setOnClickListener(this);
    }

    //در اینجا هنگامی بخواهد صفحه از اول لود شود کد زیر فراخوانی می شود
    void RefreshPage() {
        SwipeRefresh.setOnRefreshListener(() -> {
            progressBar.setVisibility(View.GONE);
            cvAdapter = new CVAdapter(getActivity(), new ArrayList<>());
            RecyclerCV.setAdapter(cvAdapter);
            RecyclerCV.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

            Page = 0;
            floatButton.hide();
            SetCVs();
        });
    }

    void showError(VolleyError error) {

        if (!loading) {
            loading = true;
            Page--;
        }

        isWorking = false;
        SwipeRefresh.setRefreshing(false);

        Loading.stopShimmerAnimation();
        Loading.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        String errorText;

        if (error instanceof TimeoutError) {
            errorText = getString(R.string.YourInternetIsVerySlow);
        } else if (error instanceof ServerError) {
            errorText = getString(R.string.ErrorInServer);
        } else if (error instanceof NetworkError) {
            errorText = getString(R.string.PleaseCheckedYourConnectionInternet);
        } else if (error instanceof ParseError) {
            errorText = getString(R.string.ErrorInApplication);
        } else {
            errorText = getString(R.string.ErrorInApplication);
        }

        if (Page == 0) {
            if (cvAdapter != null) {
                if (cvAdapter.getItemCount() > 0) {
                    if (showToastError) {
                        Toast.makeText(getActivity(), errorText, Toast.LENGTH_SHORT).show();
                        showToastError = false;
                        isWorking = true;
                    }
                } else {
                    NoInternet.setVisibility(View.VISIBLE);
                    lbl_Error.setText(errorText);
                }
            } else {
                NoInternet.setVisibility(View.VISIBLE);
                lbl_Error.setText(errorText);
            }
        } else {
            if (showToastError) {
                Toast.makeText(getActivity(), errorText, Toast.LENGTH_SHORT).show();
                showToastError = false;
                isWorking = true;
            }
        }

        new Handler().postDelayed(() -> {
            showToastError = true;
            isWorking = false;
        }, 5000);
    }
}
