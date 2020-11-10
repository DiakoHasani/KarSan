package ir.tdaapp.karsan.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;

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
import ir.tdaapp.karsan.Adapter.FavoritItemAdapter;
import ir.tdaapp.karsan.Adapter.MyItemsAdapter;
import ir.tdaapp.karsan.MainActivity;
import ir.tdaapp.karsan.R;
import ir.tdaapp.karsan.Services.IRecyclerMyItemClick;
import ir.tdaapp.karsan.Services.IRefreshPage;
import ir.tdaapp.karsan.Utility.AppController;
import ir.tdaapp.karsan.Utility.BaseFragment;
import ir.tdaapp.karsan.Utility.Replace;
import ir.tdaapp.karsan.ViewModel.VM_Short_Item;

public class Fragment_My_Items extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "Fragment_My_Items";

    LinearLayout Back;
    NestedScrollView NestedScroll;
    ShimmerFrameLayout Loading, NoData;
    RecyclerView RecyclerItem;
    ProgressBar progressbar_items;
    JsonArrayRequest jsonArrayRequest;
    RequestQueue requestQueue;
    boolean loading = true;
    int Page = 0;
    MyItemsAdapter myItemsAdapter;
    LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_items, container, false);

        FindItem(view);
        GetVals();
        OnScroll();
        OnClick();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowBottomBar(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requestQueue = Volley.newRequestQueue(getContext());

        jsonArrayRequest.setTag(TAG);

        requestQueue.add(jsonArrayRequest);

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    void FindItem(View view) {
        Back = view.findViewById(R.id.Back);
        NestedScroll = view.findViewById(R.id.NestedScroll);
        Loading = view.findViewById(R.id.Loading);
        NoData = view.findViewById(R.id.NoData);
        RecyclerItem = view.findViewById(R.id.RecyclerItem);
        progressbar_items = view.findViewById(R.id.progressbar_items);
    }

    void OnScroll() {
        NestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!NestedScroll.canScrollVertically(1) && scrollY != 0) {

                    if (loading) {
                        progressbar_items.setVisibility(View.VISIBLE);

                        boolean HasInternet = ((MainActivity) getActivity()).internet.HaveNetworkConnection();

                        if (HasInternet) {
                            Page += 1;
                            loading = false;
                            GetVals();
                        }
                        //Do pagination.. i.e. fetch new data
                    }
                }
            }
        });
    }

    void GetVals() {

        if (Page == 0) {
            RecyclerItem.setVisibility(View.GONE);
            Loading.startShimmerAnimation();
            Loading.setVisibility(View.VISIBLE);
        }

        NoData.stopShimmerAnimation();
        NoData.setVisibility(View.GONE);

        String UniCode = ((MainActivity) getActivity()).tbl_user.GetUniCode();
        String Url = Api + "Item/GetItemsUser?Page=" + Page + "&UniCode=" + UniCode;

        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Loading.stopShimmerAnimation();
                Loading.setVisibility(View.GONE);
                RecyclerItem.setVisibility(View.VISIBLE);

                List<VM_Short_Item> vals = new ArrayList<>();

                if (Page == 0) {
                    if (response.length() == 0) {
                        NoData.startShimmerAnimation();
                        NoData.setVisibility(View.VISIBLE);
                    }
                }

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

                        vals.add(item);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                if (Page == 0) {
                    myItemsAdapter = new MyItemsAdapter(getContext(), vals, (view, item) -> {
                        Bundle bundle = new Bundle();
                        bundle.putInt("Id", item.getId());
                        Fragment_Show_Details_My_Item fragment_show_details_my_item = new Fragment_Show_Details_My_Item();

                        fragment_show_details_my_item.setArguments(bundle);
                        fragment_show_details_my_item.setiRefreshPage(() -> {
                            Page = 0;
                            GetVals();
                        });

                        getActivity().getSupportFragmentManager()
                                .beginTransaction().setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                                .add(R.id.Frame_Main, fragment_show_details_my_item)
                                .addToBackStack(null).commit();
                    });
                    linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                    RecyclerItem.setLayoutManager(linearLayoutManager);
                    RecyclerItem.setAdapter(myItemsAdapter);
                } else {
                    progressbar_items.setVisibility(View.GONE);
                    myItemsAdapter.AddItems(vals);
                    loading = true;
                    Loading.stopShimmerAnimation();
                    Loading.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                RecyclerItem.setVisibility(View.VISIBLE);
                Loading.stopShimmerAnimation();
                Loading.setVisibility(View.GONE);

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

                new AlertDialog.Builder(getActivity())
                        .setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Error) + "</font>")))
                        .setMessage((Html.fromHtml("<font color='#FF7F27'>" + errorText + "</font>")))
                        .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Reload) + "</font>")), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                GetVals();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(true)
                        .show();
            }
        });
        AppController.getInstance().addToRequestQueue(SetTimeOut(jsonArrayRequest));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Back:
                getActivity().onBackPressed();
                break;
        }
    }

    void OnClick() {
        Back.setOnClickListener(this);
    }
}
