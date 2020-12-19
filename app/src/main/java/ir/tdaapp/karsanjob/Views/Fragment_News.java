package ir.tdaapp.karsanjob.Views;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ir.tdaapp.karsanjob.Adapter.NewsAdapter;
import ir.tdaapp.karsanjob.R;
import ir.tdaapp.karsanjob.Utility.AppController;
import ir.tdaapp.karsanjob.Utility.BaseFragment;
import ir.tdaapp.karsanjob.ViewModel.VM_ShortNews;

public class Fragment_News extends BaseFragment {

    public static final String TAG = "Fragment_News";

    TextView Employed, CountItem, JobSeeker, lbl_TryingAgain;
    JsonObjectRequest requestSetStatistics;
    JsonArrayRequest requestNews;
    CountDownTimer timer_Statistics;
    ShimmerFrameLayout loading_Statistics, LoadingNews, NoData, Shimmer_No_Internet;
    RecyclerView RecyclerNews;
    int Page = 0;
    NewsAdapter newsAdapter;
    LinearLayoutManager linearLayoutManager;
    NestedScrollView NestedScroll;
    boolean loading = true,isWorking, showToastError;
    ProgressBar progressbar_news;
    RelativeLayout NoInternet;
    RequestQueue requestQueue;
    SwipeRefreshLayout SwipeRefresh;
    FloatingActionButton floatButton;
    AlphaAnimation Anim_FadeIn;
    TextView lbl_Error;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        FindItem(view);

        GetStatistics();

        SetNews();

        Paging();

        OnClick();

        RefreshPage();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (timer_Statistics != null) {
            timer_Statistics.cancel();
        }


        requestQueue = Volley.newRequestQueue(getContext());

        requestNews.setTag(TAG);

        requestQueue.add(requestNews);

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    void FindItem(View view) {
        Employed = view.findViewById(R.id.Employed);
        CountItem = view.findViewById(R.id.CountItem);
        JobSeeker = view.findViewById(R.id.JobSeeker);
        loading_Statistics = view.findViewById(R.id.loading_Statistics);
        LoadingNews = view.findViewById(R.id.LoadingNews);
        RecyclerNews = view.findViewById(R.id.RecyclerNews);
        NestedScroll = view.findViewById(R.id.NestedScroll);
        progressbar_news = view.findViewById(R.id.progressbar_news);
        NoData = view.findViewById(R.id.NoData);
        NoInternet = view.findViewById(R.id.NoInternet);
        Shimmer_No_Internet = view.findViewById(R.id.Shimmer_No_Internet);
        lbl_TryingAgain = view.findViewById(R.id.lbl_TryingAgain);
        SwipeRefresh = view.findViewById(R.id.SwipeRefresh);
        floatButton = view.findViewById(R.id.floatButton);
        lbl_Error = view.findViewById(R.id.lbl_Error);
        Anim_FadeIn = new AlphaAnimation(0.0f, 1.0f);
        Anim_FadeIn.setDuration(500);
        isWorking = false;
        showToastError = true;
    }

    void SetNews() {

        isWorking = true;

        try {

            NoData.setVisibility(View.GONE);
            NoData.stopShimmerAnimation();
            NoInternet.setVisibility(View.GONE);
            Shimmer_No_Internet.stopShimmerAnimation();

            if (Page == 0) {
                LoadingNews.startShimmerAnimation();
                LoadingNews.setVisibility(View.VISIBLE);
                RecyclerNews.setVisibility(View.GONE);
            }

            String Url = Api + "News?Page=" + Page;

            requestNews = new JsonArrayRequest(Request.Method.GET, Url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    RecyclerNews.setVisibility(View.VISIBLE);

                    isWorking = false;
                    SwipeRefresh.setRefreshing(false);
                    progressbar_news.setVisibility(View.GONE);

                    NoInternet.setVisibility(View.GONE);
                    Shimmer_No_Internet.stopShimmerAnimation();

                    if (response.length() == 0 && Page == 0) {
                        NoData.setVisibility(View.VISIBLE);
                        NoData.startShimmerAnimation();
                    } else {
                        NoData.setVisibility(View.GONE);
                        NoData.stopShimmerAnimation();
                    }

                    LoadingNews.stopShimmerAnimation();
                    LoadingNews.setVisibility(View.GONE);
                    progressbar_news.setVisibility(View.GONE);

                    List<VM_ShortNews> news = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        VM_ShortNews n = new VM_ShortNews();
                        try {
                            JSONObject object = response.getJSONObject(i);

                            n.setId(object.getInt("Id"));
                            n.setAbstractNews(object.getString("AbstractNews"));
                            n.setTitle(object.getString("Title"));
                            n.setDateInsert(object.getString("DateInsert"));
                            n.setImage(ApiImage_News + object.getString("Image"));

                            news.add(n);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (Page == 0) {
                        newsAdapter = new NewsAdapter(getContext(), news);
                        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                        RecyclerNews.setAdapter(newsAdapter);
                        RecyclerNews.setLayoutManager(linearLayoutManager);
                    } else {
                        newsAdapter.Add(news);
                        loading = true;
                    }
                }
            }, error -> showError(error));

            AppController.getInstance().addToRequestQueue(SetTimeOut(requestNews));

        } catch (Exception e) {
            LoadingNews.stopShimmerAnimation();
            LoadingNews.setVisibility(View.GONE);
        }
    }

    void OnClick() {

        floatButton.hide();
        lbl_TryingAgain.setOnClickListener(view -> {
            SetNews();
            GetStatistics();
        });
        floatButton.setOnClickListener(view -> {

            NestedScroll.smoothScrollTo(0, 0);
            floatButton.hide();
        });
    }

    //در اینجا اگر مقدار ترو برگشت داده شود یعنی زمان پیجینگ رسیده است و نیاز به خواندن داده از سرور می باشد
    boolean isLastItemDisplaying() {

        View view = NestedScroll.getChildAt(NestedScroll.getChildCount() - 1);

        int diff = (view.getBottom() - (NestedScroll.getHeight() + NestedScroll
                .getScrollY()));

        if (diff <= 5000)
            return true;
        return false;
    }

    //در اینجا آمار های برنامه در سرور گرفته می شود
    void GetStatistics() {
        try {

            loading_Statistics.startShimmerAnimation();

            String Url = Api + "Statistics";

            requestSetStatistics = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        loading_Statistics.stopShimmerAnimation();
                        //تعداد آگهی ها
                        int CountItem = response.getInt("CountItems");

                        //استخدام شده ها
                        int Employed = response.getInt("Employed");

                        //جویای کار
                        int JobSeeker = response.getInt("JobSeeker");

                        GetGetStatistics(CountItem, Employed, JobSeeker);

                    } catch (JSONException e) {
                        loading_Statistics.stopShimmerAnimation();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading_Statistics.stopShimmerAnimation();
                }
            });

            AppController.getInstance().addToRequestQueue(SetTimeOut(requestSetStatistics));

        } catch (Exception e) {
            loading_Statistics.stopShimmerAnimation();
        }
    }

    //اینجا مربوط ب تایمر آمار برنامه می باشد که در 2 ثانیه آن را پر می کند
    void GetGetStatistics(final int CountItems, final int Employeds, final int JobSeekers) {

        if (CountItems > 60) {
            CountItem.setText(String.valueOf(CountItems - 60));
        }
        if (Employeds > 60) {
            Employed.setText(String.valueOf(Employeds - 60));
        }
        if (JobSeekers > 60) {
            JobSeeker.setText(String.valueOf(JobSeekers - 60));
        }

        try {

            timer_Statistics = new CountDownTimer(2000, 1) {
                @Override
                public void onTick(long l) {

                    if (Integer.parseInt(CountItem.getText().toString()) < CountItems) {
                        CountItem.setText(String.valueOf(Integer.parseInt(CountItem.getText().toString()) + 1));
                    }

                    if (Integer.parseInt(Employed.getText().toString()) < Employeds) {
                        Employed.setText(String.valueOf(Integer.parseInt(Employed.getText().toString()) + 1));
                    }

                    if (Integer.parseInt(JobSeeker.getText().toString()) < JobSeekers) {
                        JobSeeker.setText(String.valueOf(Integer.parseInt(JobSeeker.getText().toString()) + 1));
                    }
                }

                @Override
                public void onFinish() {

                }
            }.start();

        } catch (Exception e) {

        }
    }

    void Paging() {
        NestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (RecyclerNews.getAdapter() != null) {

                    if (loading) {

                        if (isLastItemDisplaying()) {
                            //اگر برنامه در حال دریافت اطلاعات از سرور باشد اجازه در یافت آیتم بعدی را نمی دهد
                            if (!isWorking) {
                                loading = false;

                                progressbar_news.setVisibility(View.VISIBLE);

                                ++Page;
                                SetNews();
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

    //در اینجا هنگامی بخواهد صفحه از اول لود شود کد زیر فراخوانی می شود
    void RefreshPage() {
        SwipeRefresh.setOnRefreshListener(() -> {
            progressbar_news.setVisibility(View.GONE);
            newsAdapter=new NewsAdapter(getActivity(),new ArrayList<>());
            RecyclerNews.setAdapter(newsAdapter);
            RecyclerNews.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));

            Page = 0;
            floatButton.hide();
            SetNews();
        });
    }

    void showError(VolleyError error) {

        if (!loading) {
            loading = true;
            Page--;
        }

        isWorking = false;
        SwipeRefresh.setRefreshing(false);

        LoadingNews.stopShimmerAnimation();
        LoadingNews.setVisibility(View.GONE);
        progressbar_news.setVisibility(View.GONE);

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
            if (newsAdapter != null) {
                if (newsAdapter.getItemCount() > 0) {
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
