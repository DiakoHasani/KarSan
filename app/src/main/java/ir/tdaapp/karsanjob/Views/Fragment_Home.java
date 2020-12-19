package ir.tdaapp.karsanjob.Views;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import ir.tdaapp.karsanjob.Adapter.ItemAdapter;
import ir.tdaapp.karsanjob.Adapter.SlidersHomeAdapter;
import ir.tdaapp.karsanjob.DataBase.Tbl_Job;
import ir.tdaapp.karsanjob.Enum.BottomBarItems;
import ir.tdaapp.karsanjob.Enum.InsuranceHistory;
import ir.tdaapp.karsanjob.Enum.JobHistory;
import ir.tdaapp.karsanjob.Enum.Job_Time;
import ir.tdaapp.karsanjob.MainActivity;
import ir.tdaapp.karsanjob.R;
import ir.tdaapp.karsanjob.Utility.AppController;
import ir.tdaapp.karsanjob.Utility.BaseFragment;
import ir.tdaapp.karsanjob.Utility.KeyBoard;
import ir.tdaapp.karsanjob.Utility.Replace;
import ir.tdaapp.karsanjob.ViewModel.VM_SearchFilter;
import ir.tdaapp.karsanjob.ViewModel.VM_Short_Item;
import ir.tdaapp.karsanjob.ViewModel.VM_SliderHome;
import ir.tdaapp.karsanjob.Views.Dialog.Dialog_Filter_Search;

/**
 * Created by Diako on 8/26/2019.
 */

public class Fragment_Home extends BaseFragment {

    public static final String TAG = "Fragment_Home";

    SlidersHomeAdapter SliderAdapter;
    ViewPager SliderPager;
    AutoCompleteTextView txt_Search;
    ImageView Slider_Right, Slider_Left;
    List<VM_SliderHome> imgs;
    int CountImageSlider;
    RecyclerView RecyclerItem;
    ItemAdapter itemAdapter;
    LinearLayoutManager Layout_Item;
    ProgressBar progressbar_items;
    boolean loading, isWorking, showToastError;
    int Page;
    NestedScrollView NestedScroll;
    int CountItem;
    boolean SliderNext;

    private boolean started;
    private Handler handler;


    private ShimmerFrameLayout mShimmerViewContainer;

    Tbl_Job tbl_job;
    ShimmerFrameLayout NoData, Shimmer_No_Internet;
    RelativeLayout NoInternet;
    TextView lbl_TryingAgain;

    ImageView img_Filter_Search;

    FloatingActionButton floatButton, btn_RemoveFilter;
    SwipeRefreshLayout SwipeRefresh;
    TextView lbl_Error;
    Animation ani_FadeIn,ani_FadeOut;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ((MainActivity) getActivity()).GetPage(BottomBarItems.Home);

        FindItem(view);

        OnClick();

        Set_txt_Search();
        SetImagesSlider();
        SetItems();

        start();

        Scroll();

        RefreshPage();

        return view;
    }

    void FindItem(View view) {
        imgs = new ArrayList<>();
        SliderPager = view.findViewById(R.id.SliderPager);
        txt_Search = view.findViewById(R.id.txt_Search);
        Slider_Right = view.findViewById(R.id.Slider_Right);
        Slider_Left = view.findViewById(R.id.Slider_Left);
        RecyclerItem = view.findViewById(R.id.RecyclerItem);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        progressbar_items = view.findViewById(R.id.progressbar_items);
        loading = true;
        isWorking = false;
        showToastError = true;
        Page = 0;
        NestedScroll = view.findViewById(R.id.NestedScroll);
        CountItem = -1;
        SliderNext = true;
        started = true;
        handler = new Handler();
        CountImageSlider = 0;
        tbl_job = new Tbl_Job(((MainActivity) getActivity()).dbAdapter);
        NoData = view.findViewById(R.id.NoData);
        Shimmer_No_Internet = view.findViewById(R.id.Shimmer_No_Internet);
        NoInternet = view.findViewById(R.id.NoInternet);
        lbl_TryingAgain = view.findViewById(R.id.lbl_TryingAgain);
        img_Filter_Search = view.findViewById(R.id.img_Filter_Search);
        floatButton = view.findViewById(R.id.floatButton);
        SwipeRefresh = view.findViewById(R.id.SwipeRefresh);
        lbl_Error = view.findViewById(R.id.lbl_Error);
        btn_RemoveFilter = view.findViewById(R.id.btn_RemoveFilter);

        ani_FadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);
        ani_FadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);
    }

    @SuppressLint("ClickableViewAccessibility")
    void OnClick() {

        floatButton.hide();

        //در اینجا کاربر هنگامی که دکمه سمت راست اسلایدر را فشار می دهد عکس بعدی اسلایدر نمایش داده می شود
        Slider_Right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SliderPager.setCurrentItem(getItem(+1), true);
            }
        });

        //در اینجا کاربر هنگامی که دکمه سمت چپ اسلایدر را فشار می دهد عکس قبلی اسلایدر نمایش داده می شود
        Slider_Left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SliderPager.setCurrentItem(getItem(-1), true);
            }
        });

        //رخداد دکمه سرچ در کیبورد تکست باکس سرچ
        txt_Search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    txt_Search.dismissDropDown();
                    KeyBoard.hideKeyboard(getActivity());
                    SearchFilters();
                }
                return false;
            }
        });

        //مربوط به تکست ویو تلاش دوباره
        lbl_TryingAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //در اینجا پیجینگ صفر می شود تا داده ها دوباره از سرور خوانده شوند
                Page = 0;

                //در اینجا آیتم مربوط به نبود اینترنت مخفی می شود
                NoInternet.setVisibility(View.GONE);

                //در اینجا لودینگ نمایش داده می شود
                mShimmerViewContainer.startShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.VISIBLE);

                //در اینجا مقادیر اسلایدر و آیتم ها دوباره از سرور گرفته می شوند
                SetItems();
                SetImagesSlider();
            }
        });

        //این رخداد مربوط به زمانی است که کاربر یکی از آیتم های سرچ را انتخاب کند
        txt_Search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchFilters();
            }
        });

        img_Filter_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(Dialog_Filter_Search.TAG);

                if (prev == null) {
                    ft.addToBackStack(null);

                    Dialog_Filter_Search dialog_filter_search = new Dialog_Filter_Search();

                    dialog_filter_search.show(ft, Dialog_Filter_Search.TAG);

                    dialog_filter_search.setClickDialogFilterSearh(() -> {
                        SearchFilters();
                    });
                }
            }
        });

        SliderPager.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    handler.removeCallbacks(runnable);
                    start();
                    break;
            }
            return false;
        });

        Slider_Left.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handler.removeCallbacks(runnable);
                    break;
                case MotionEvent.ACTION_UP:
                    start();
                    break;
            }
            return false;
        });

        Slider_Right.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handler.removeCallbacks(runnable);
                    break;
                case MotionEvent.ACTION_UP:
                    start();
                    break;
            }
            return false;
        });

        floatButton.setOnClickListener(view -> {
            NestedScroll.smoothScrollTo(0, 0);
            floatButton.hide();
        });

        btn_RemoveFilter.setOnClickListener(view -> {
            btn_RemoveFilter.hide();
            ((MainActivity) getActivity()).SearchFilter = new VM_SearchFilter();
            txt_Search.setText("");
            SearchFilters();
        });

    }

    // <editor-fold desc=" در اینجا اسلایدر تبلیغات به صورت اتوماتیک تغییر صفحه می کند ">

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            //در اینجا معلوم می شود که اسلایدر در کدام پیج است
            int PagingSliderPosition = SliderPager.getCurrentItem();

            //در اینجا چک می شود که اسلایدر در صفحه اول است اگر شرط درست باشد به صفحه بعد می رود
            if (PagingSliderPosition == 0) {
                SliderPager.setCurrentItem(getItem(+1), true);
                SliderNext = true;
            }
            //در اینجا چک می شود که اگر اسلایدر در صفحه آخر است به صفحه قبل بر می گردد
            else if (PagingSliderPosition == CountImageSlider - 1) {
                SliderPager.setCurrentItem(getItem(-1), true);
                SliderNext = false;
            } else {
                //در اینجا اسلایدر به صفحه بعد می رود
                if (SliderNext == true) {
                    SliderPager.setCurrentItem(getItem(+1), true);
                }
                //در اینجا اسلایدر به صفحه قبل می رود
                else {
                    SliderPager.setCurrentItem(getItem(-1), true);
                }
            }
            if (started) {
                start();
            }
        }
    };

// </editor-fold>


    // <editor-fold desc="این متد در زمان تعیین شده متد تغییر صفحات اسلایدر فراخوانی می شود">

    public void start() {
        started = true;
        handler.postDelayed(runnable, TimePageSlider);
    }

    // </editor-fold>


    // <editor-fold desc="در اینجا آداپتر تکست باکس جستجو ست می شود">
    void Set_txt_Search() {
        String[] txt_SearchData = tbl_job.GetAll_Title();

        ArrayAdapter<String> adapter_txt_Search = new ArrayAdapter<String>(getContext()
                , android.R.layout.select_dialog_item
                , txt_SearchData);
        txt_Search.setAdapter(adapter_txt_Search);

    }
    // </editor-fold>


    // <editor-fold desc="در اینجا آیتم ها ست می شوند">
    public void SetItems() {

        isWorking = true;

        NoData.setVisibility(View.GONE);
        NoInternet.setVisibility(View.GONE);

        if (Page == 0) {
            mShimmerViewContainer.startShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.VISIBLE);
            RecyclerItem.setVisibility(View.GONE);
        }

        String Url_Item = Api + "Item/Item?Page=" + Page;

        //در اینجا فیلتر های جستجو ست می شوند
        JSONArray params = new JSONArray();
        JSONObject param = new JSONObject();
        VM_SearchFilter filter = ((MainActivity) getActivity()).SearchFilter;

        try {

            //در اینجا سرتیتر ست می شود
            param.put("Title", txt_Search.getText());

            //در اینجا حداقل قیمت ست می شود
            param.put("MinRights", filter.getMinRights());

            //در اینجا حداکثر قیمت ست می شود
            param.put("MaxRights", filter.getMaxRights());

            //در اینجا زمان کاری ست می شود
            JSONArray job_Time = new JSONArray();
            for (int i = 0; i < filter.getJob_time().size(); i++) {
                if (filter.getJob_time().get(i) == Job_Time.FullTime) {
                    job_Time.put(0);
                } else if (filter.getJob_time().get(i) == Job_Time.PartTime) {
                    job_Time.put(1);
                } else if (filter.getJob_time().get(i) == Job_Time.TornTime) {
                    job_Time.put(2);
                }
            }
            param.put("Job_Time", job_Time);

            //در اینجا سابقه بیمه ست می شود
            if (filter.getInsuranceHistory() == InsuranceHistory.Yes) {
                param.put("InsuranceHistory", 0);
            } else if (filter.getInsuranceHistory() == InsuranceHistory.No) {
                param.put("InsuranceHistory", 1);
            } else if (filter.getInsuranceHistory() == InsuranceHistory.Ignored) {
                param.put("InsuranceHistory", 2);
            }

            //در اینجا سابقه کار ست می شود
            if (filter.getJobHistory() == JobHistory.Yes) {
                param.put("JobHistory", 0);
            } else if (filter.getJobHistory() == JobHistory.No) {
                param.put("JobHistory", 1);
            } else if (filter.getJobHistory() == JobHistory.Ignored) {
                param.put("JobHistory", 2);
            }

            //در اینجا مدرک ست می شود
            param.put("Madrak", filter.getMadrak());

            param.put("Major", filter.getMajor());

            params.put(param);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequest RequestItem = new JsonArrayRequest(Request.Method.POST, Url_Item, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {

                    if (((MainActivity) getActivity()).SearchFilter.IsEnabledSearched()) {
                        btn_RemoveFilter.show();
                    } else {
                        btn_RemoveFilter.hide();
                    }

                    if (!txt_Search.getText().toString().equalsIgnoreCase("")) {
                        if (!btn_RemoveFilter.isShown())
                            btn_RemoveFilter.show();
                    }

                    isWorking = false;
                    SwipeRefresh.setRefreshing(false);
                    progressbar_items.setAnimation(ani_FadeOut);
                    progressbar_items.setVisibility(View.GONE);

                    NoData.setVisibility(View.GONE);
                    NoData.stopShimmerAnimation();

                    NoInternet.setVisibility(View.GONE);
                    Shimmer_No_Internet.stopShimmerAnimation();

                    //در اینجا اگر آیتمی موجود نباشد آیتم زیر نمایش داده می شود
                    if (response.length() == 0 && Page == 0) {

                        //در اینجا لودینگ مخفی می شود
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);

                        //در اینجا نبودن آیتم نمایش داده می شود
                        NoData.setVisibility(View.VISIBLE);
                        NoData.startShimmerAnimation();

                        //در اینجا اگر کاربر داده ای جدید جستجو کند که در دیتابیس وجود نداشته باشد آیتم های قبلی در رسایکر ویو حذف می شوند
                        if (itemAdapter != null) {
                            itemAdapter.RemoveAll();
                            itemAdapter.notifyDataSetChanged();
                        }

                    } else {
                        RecyclerItem.setVisibility(View.VISIBLE);
                        List<VM_Short_Item> vals = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {

                            try {
                                JSONObject json = response.getJSONObject(i);

                                VM_Short_Item item = new VM_Short_Item();

                                //در اینجا ای دی دریافت می شود
                                item.setId(json.getInt("Id"));

                                //در اینجا حقوق در یافت می شود
                                item.setPrice(Replace.Number_en_To_fa(json.getString("MaxPrice")));

                                //در اینجا تاریخ ایتم دریافت می شود
                                item.setTime(Replace.Number_en_To_fa(json.getString("Date")));

                                //در اینجا تایتل آیتم دریافت می شود
                                item.setTitle(json.getString("Title"));

                                //در اینجا نوع ایتم در یافت می شود
                                item.setType(json.getInt("TypeItem"));

                                //در اینجا عکس آیتم دریافت می شود
                                item.setImage(ApiImage_Item + json.getString("Image"));

                                if (CountItem == -1) {
                                    CountItem = json.getInt("CountItem");
                                }

                                vals.add(item);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (Page == 0) {
                            itemAdapter = new ItemAdapter(getContext(), vals);
                            Layout_Item = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

                            RecyclerItem.setAdapter(itemAdapter);
                            RecyclerItem.setLayoutManager(Layout_Item);

                            mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);
                        } else {
                            progressbar_items.setAnimation(ani_FadeOut);
                            progressbar_items.setVisibility(View.GONE);
                            itemAdapter.AddItems(vals);
                            loading = true;

                            mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);
                        }
                    }

                } catch (Exception e) {
                    showError(new VolleyError(e.toString()));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (((MainActivity) getActivity()).SearchFilter.IsEnabledSearched()) {
                    btn_RemoveFilter.show();
                } else {
                    btn_RemoveFilter.hide();
                }

                if (!txt_Search.getText().toString().equalsIgnoreCase("")) {
                    if (!btn_RemoveFilter.isShown())
                        btn_RemoveFilter.show();
                }

                showError(error);
            }
        });

        AppController.getInstance().addToRequestQueue(SetTimeOut(RequestItem));
    }
    // </editor-fold>


    // <editor-fold desc="این متد برای به دست آوردن صفحه بعد اسلایدر می باشد">
    private int getItem(int i) {
        return SliderPager.getCurrentItem() + i;
    }
    // </editor-fold>


    // <editor-fold desc="در اینجا عکس های اسلایدر تبلیغات در سرور خوانده می شوند">
    void SetImagesSlider() {

        String url = Api + "Slider/Sliders";

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {

                    VM_SliderHome sliderHome = new VM_SliderHome();

                    try {
                        JSONObject object = response.getJSONObject(i);

                        sliderHome.setId(Integer.parseInt(object.getString("Id")));
                        sliderHome.setImageUrl(ApiImage_Slider + object.getString("UrlImage"));
                        sliderHome.setDescription(object.getString("Title"));
                        sliderHome.setLink(object.getString("Link"));
                        sliderHome.setTypeLink(object.getInt("TypeLink"));

                        imgs.add(sliderHome);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                CountImageSlider = response.length();

                SliderAdapter = new SlidersHomeAdapter(imgs, getActivity(), (view, vm_SliderHome) -> {

                    handler.removeCallbacks(runnable);
                    start();

                    //در اینجا کاربر با کلیک روی عکس اینستاگرامش باز می شود
                    if (vm_SliderHome.getTypeLink() == 1) {
                        Uri uri = Uri.parse(vm_SliderHome.getLink());
                        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                        likeIng.setPackage("com.instagram.android");
                        try {
                            startActivity(likeIng);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(vm_SliderHome.getLink())));
                        }
                    }
                    //در اینجا مرورگرش باز می شود
                    else if (vm_SliderHome.getTypeLink() == 2) {
                        try {
                            Uri uri = Uri.parse(vm_SliderHome.getLink());
                            startActivity(new Intent(Intent.ACTION_VIEW, uri));
                        } catch (Exception e) {
                        }
                    }
                });

                SliderPager.setAdapter(SliderAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SetImagesSlider();
            }
        });

        AppController.getInstance().addToRequestQueue(SetTimeOut(arrayRequest));
    }
    // </editor-fold>

    //در اینجا اگر مقدار ترو برگشت داده شود یعنی زمان پیجینگ رسیده است و نیاز به خواندن داده از سرور می باشد
    boolean isLastItemDisplaying() {

        View view = NestedScroll.getChildAt(NestedScroll.getChildCount() - 1);

        int diff = (view.getBottom() - (NestedScroll.getHeight() + NestedScroll
                .getScrollY()));


        if (diff <= 1000)
            return true;
        return false;
    }

    // <editor-fold desc="در اینجا پیجینگ آیتم ها انجام می شود">
    void Scroll() {
        NestedScroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            if (RecyclerItem.getAdapter() != null) {

                if (loading) {

                    if (isLastItemDisplaying()) {
                        //اگر برنامه در حال دریافت اطلاعات از سرور باشد اجازه در یافت آیتم بعدی را نمی دهد
                        if (!isWorking) {
                            loading = false;

                            progressbar_items.setAnimation(ani_FadeIn);
                            progressbar_items.setVisibility(View.VISIBLE);

                            ++Page;
                            SetItems();
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
        });
    }
    // </editor-fold>


    // <editor-fold desc="این متد زمانی که کاربر در صفحه فیلتر جستجو دکمه جستجو را فشار دهد فراخوانی می شود">
    public void SearchFilters() {
        Page = 0;
        CountItem = -1;
        if (itemAdapter != null) {
            itemAdapter.RemoveAll();
            itemAdapter.notifyDataSetChanged();
        }
        SetItems();
    }
    // </editor-fold>

    //در اینجا هنگامی بخواهد صفحه از اول لود شود کد زیر فراخوانی می شود
    void RefreshPage() {
        SwipeRefresh.setOnRefreshListener(() -> {
            progressbar_items.setAnimation(ani_FadeOut);
            progressbar_items.setVisibility(View.GONE);
            itemAdapter = new ItemAdapter(getActivity(), new ArrayList<>());
            RecyclerItem.setAdapter(itemAdapter);
            RecyclerItem.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
            Page = 0;
            SetItems();
        });
    }

    //در اینجا چک می شود که ادیت تکست جستجو خالی است یا خیر
    public boolean Check_txt_search() {
        if (txt_Search.getText().toString().equalsIgnoreCase(""))
            return false;
        return true;
    }

    public void Set_txt_Search(String val) {
        txt_Search.setText(val);
    }

    void showError(VolleyError error) {

        if (!loading) {
            loading = true;
            Page--;
        }

        isWorking = false;
        SwipeRefresh.setRefreshing(false);

        mShimmerViewContainer.stopShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.GONE);
        progressbar_items.setAnimation(ani_FadeOut);
        progressbar_items.setVisibility(View.GONE);

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
            if (itemAdapter != null) {
                if (itemAdapter.getItemCount() > 0) {
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
