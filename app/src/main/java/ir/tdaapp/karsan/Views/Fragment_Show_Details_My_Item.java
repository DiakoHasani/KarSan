package ir.tdaapp.karsan.Views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ir.tdaapp.karsan.DataBase.Tbl_FavoritesItem;
import ir.tdaapp.karsan.DataBase.Tbl_Job;
import ir.tdaapp.karsan.DataBase.Tbl_Madrak;
import ir.tdaapp.karsan.DataBase.Tbl_Major;
import ir.tdaapp.karsan.DataBase.Tbl_User;
import ir.tdaapp.karsan.MainActivity;
import ir.tdaapp.karsan.R;
import ir.tdaapp.karsan.Utility.AppController;
import ir.tdaapp.karsan.Utility.BaseFragment;

public class Fragment_Show_Details_My_Item extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "Fragment_Show_Details_My_Item";

    ShimmerFrameLayout Loading;
    TextView lbl_Title, lbl_DateInsert, lbl_JobType, lbl_Gender, lbl_Insurance, lbl_Age_Range, lbl_JobTime;
    TextView lbl_HoursOfWork, lbl_Description, lbl_Price;

    RelativeLayout R_Madrak_1, R_Madrak_2, R_Madrak_3, R_Major_1, R_Major_2, R_Major_3, btn_Edit;
    TextView L_Madrak_1, L_Madrak_2, L_Madrak_3, L_Major_1, L_Major_2, L_Major_3;

    Tbl_Job tbl_job;
    Tbl_Madrak tbl_madrak;
    Tbl_Major tbl_major;
    Tbl_User tbl_user;
    ImageView img;

    JsonObjectRequest request, DeleteRequst, UndoDeleteRequest,PutTypeItem;
    RequestQueue requestQueue;

    LinearLayout Back;
    Button btn_Delete, btn_Undo, btn_instantaneous, btn_Special;
    int ItemId = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_details_my_item, container, false);

        FindItem(view);
        SetItem();
        OnClick();
        return view;
    }

    void FindItem(View view) {
        img = view.findViewById(R.id.img);
        Back = view.findViewById(R.id.Back);
        L_Major_3 = view.findViewById(R.id.L_Major_3);
        L_Major_2 = view.findViewById(R.id.L_Major_2);
        L_Major_1 = view.findViewById(R.id.L_Major_1);
        R_Major_3 = view.findViewById(R.id.R_Major_3);
        R_Major_2 = view.findViewById(R.id.R_Major_2);
        R_Major_1 = view.findViewById(R.id.R_Major_1);
        R_Madrak_1 = view.findViewById(R.id.R_Madrak_1);
        L_Madrak_3 = view.findViewById(R.id.L_Madrak_3);
        R_Madrak_3 = view.findViewById(R.id.R_Madrak_3);
        R_Madrak_2 = view.findViewById(R.id.R_Madrak_2);
        L_Madrak_2 = view.findViewById(R.id.L_Madrak_2);
        L_Madrak_1 = view.findViewById(R.id.L_Madrak_1);
        lbl_Price = view.findViewById(R.id.lbl_Price);
        lbl_Description = view.findViewById(R.id.lbl_Description);
        lbl_HoursOfWork = view.findViewById(R.id.lbl_HoursOfWork);
        lbl_JobTime = view.findViewById(R.id.lbl_JobTime);
        lbl_Age_Range = view.findViewById(R.id.lbl_Age_Range);
        lbl_Insurance = view.findViewById(R.id.lbl_Insurance);
        lbl_Gender = view.findViewById(R.id.lbl_Gender);
        lbl_JobType = view.findViewById(R.id.lbl_JobType);
        Loading = view.findViewById(R.id.Loading);
        tbl_job = new Tbl_Job(((MainActivity) getActivity()).dbAdapter);
        tbl_madrak = new Tbl_Madrak(((MainActivity) getActivity()).dbAdapter);
        tbl_major = new Tbl_Major(((MainActivity) getActivity()).dbAdapter);
        lbl_Title = view.findViewById(R.id.lbl_Title);
        lbl_DateInsert = view.findViewById(R.id.lbl_DateInsert);
        btn_Delete = view.findViewById(R.id.btn_Delete);
        tbl_user = ((MainActivity) getActivity()).tbl_user;
        btn_Edit = view.findViewById(R.id.btn_Edit);
        btn_Undo = view.findViewById(R.id.btn_Undo);
        btn_instantaneous = view.findViewById(R.id.btn_instantaneous);
        btn_Special = view.findViewById(R.id.btn_Special);
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

        request.setTag(TAG);

        requestQueue.add(request);

        if (DeleteRequst != null) {
            DeleteRequst.setTag(TAG);
            requestQueue.add(DeleteRequst);
        }

        if (UndoDeleteRequest != null) {
            UndoDeleteRequest.setTag(TAG);
            requestQueue.add(UndoDeleteRequest);
        }

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    void SetItem() {
        Loading.startShimmerAnimation();

        try {
            Bundle bundle = getArguments();
            ItemId = bundle.getInt("Id");
            String Url = Api + "Item/" + bundle.getInt("Id");
            if (tbl_user.HasAccount()) {
                Url += "?UniCode=" + tbl_user.GetUniCode();
            }else{
                Url += "?UniCode=";
            }
            request = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        //در اینجا عکس آیتم ست می شود
                        JSONArray images = response.getJSONArray("Images");

                        if (images.length() > 0) {
                            String imageUrl = images.getJSONObject(0).getString("Url");
                            Glide.with(getActivity())
                                    .load(ApiImage_Item + imageUrl)
                                    .placeholder(R.drawable.error_slider)
                                    .into(img);
                        }

                        if (response.getInt("TypeItem")==0){
                            SetEnabled_btn_TypeItem(true);
                        }else{
                            SetEnabled_btn_TypeItem(false);
                        }

                        //در اینجا تایتل ست می شود
                        lbl_Title.setText(response.getString("Title"));
                        lbl_Title.setGravity(Gravity.CENTER);

                        String MinPrice = "0";
                        String MaxPrice = response.getString("MaxPrice");

                        if (response.getString("MinPrice") != null) {
                            MinPrice = response.getString("MinPrice");
                        }

                        //در اینجا حقوق ست می شود
                        if (!MinPrice.equalsIgnoreCase("0")) {
                            //در اینجا اگر کاربر حداقل حقوق را وارد کرده باشد مقدار زیر ست می شود
                            lbl_Price.setText(getResources().getString(R.string.from) + " " + MinPrice + getResources().getString(R.string.Toman) + " " + getResources().getString(R.string.until) + " " + MaxPrice + getResources().getString(R.string.Toman));
                        } else {
                            //در اینجا اگر کاربر حداقل حقوق را وارد نکرده باشد مقدار زیر ست می شود
                            lbl_Price.setText(MaxPrice + " " + getResources().getString(R.string.Toman));
                        }

                        //در اینجا تاریخ درج آگهی ست می شود
                        lbl_DateInsert.setText(response.getString("DateInsert"));

                        //در اینجا تایتل کار ست می شود
                        lbl_JobType.setText(tbl_job.GetTitleById(response.getInt("JobId")));

                        //در اینجا جنسیت ست می شود
                        int Gender = response.getInt("Gender");
                        if (Gender == 0) {
                            lbl_Gender.setText(getResources().getString(R.string.Man));
                        } else if (Gender == 1) {
                            lbl_Gender.setText(getResources().getString(R.string.Women));
                        } else {
                            lbl_Gender.setText(getResources().getString(R.string.ManAndWomen));
                        }

                        //در اینجا محدوده سنی ست می شود
                        lbl_Age_Range.setText(response.getString("Age"));

                        //در اینجا زمان کاری ست می شود
                        lbl_JobTime.setText(response.getString("WorkingTime"));

                        //در اینجا ساعت کاری ست می شود
                        lbl_HoursOfWork.setText(response.getString("HoursOfWork"));

                        //در اینجا بیمه ست می شود
                        if (response.getBoolean("HasInsurance") == true) {
                            lbl_Insurance.setText(getResources().getString(R.string.Yes));
                        } else {
                            lbl_Insurance.setText(getResources().getString(R.string.No));
                        }

                        lbl_Description.setText(response.getString("Description"));

                        //در اینجا مدرک های تحصیلی ست می شوند
                        JSONArray madraks = response.getJSONArray("Madraks");
                        for (int i = 0; i < madraks.length(); i++) {

                            if (i == 0) {
                                R_Madrak_1.setVisibility(View.VISIBLE);
                                L_Madrak_1.setText(tbl_madrak.GetTitleById(madraks.getInt(i)));
                            } else if (i == 1) {
                                R_Madrak_2.setVisibility(View.VISIBLE);
                                L_Madrak_2.setText(tbl_madrak.GetTitleById(madraks.getInt(i)));
                            } else if (i == 2) {
                                R_Madrak_3.setVisibility(View.VISIBLE);
                                L_Madrak_3.setText(tbl_madrak.GetTitleById(madraks.getInt(i)));
                            }
                        }

                        //در اینجا رشته های تحصیلی ست می شود
                        JSONArray majors = response.getJSONArray("Majors");
                        for (int i = 0; i < majors.length(); i++) {

                            if (i == 0) {
                                R_Major_1.setVisibility(View.VISIBLE);
                                L_Major_1.setText(tbl_major.GetTitleById(majors.getInt(i)));
                            } else if (i == 1) {
                                R_Major_2.setVisibility(View.VISIBLE);
                                L_Major_2.setText(tbl_major.GetTitleById(majors.getInt(i)));
                            } else if (i == 2) {
                                R_Major_3.setVisibility(View.VISIBLE);
                                L_Major_3.setText(tbl_major.GetTitleById(majors.getInt(i)));
                            }
                        }

                        if (response.getInt("Status") != 0) {
                            btn_Edit.setEnabled(false);
                            btn_Delete.setVisibility(View.GONE);
                            btn_Undo.setVisibility(View.VISIBLE);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    Loading.stopShimmerAnimation();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    try {

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
                                        SetItem();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setCancelable(true)
                                .show();
                    } catch (Exception e) {
                    }
                }
            });
            AppController.getInstance().addToRequestQueue(SetTimeOut(request));
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Back:
                getActivity().onBackPressed();
                break;
            case R.id.btn_Delete:
                DeleteItem();
                break;
            case R.id.btn_Edit:
                Bundle bundle = new Bundle();
                bundle.putInt("Id", ItemId);
                Fragment_Edit_Item fragment_edit_item = new Fragment_Edit_Item();
                fragment_edit_item.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.Frame_Main, fragment_edit_item).addToBackStack(null).commit();
                break;
            case R.id.btn_Undo:
                UndoDelete();
                break;
            case R.id.btn_instantaneous:
                SetTypeItem(2);
                break;
            case R.id.btn_Special:
                SetTypeItem(1);
                break;
        }
    }

    void OnClick() {
        Back.setOnClickListener(this);
        btn_Delete.setOnClickListener(this);
        btn_Edit.setOnClickListener(this);
        btn_Undo.setOnClickListener(this);
        btn_instantaneous.setOnClickListener(this);
        btn_Special.setOnClickListener(this);
    }

    //اگر کاربر بخواهد آیتم را حذف کند متد زیر را فراخوانی می کند
    void DeleteItem() {

        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Sending) + "</font>")));
        progress.setMessage((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.PleaseWait) + "</font>")));
        progress.show();

        final String Unique = tbl_user.GetUniCode();
        String Url = Api + "Item/" + ItemId + "?UniqueCode=" + Unique;

        DeleteRequst = new JsonObjectRequest(Request.Method.DELETE, Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progress.dismiss();
                try {
                    boolean Resault = response.getBoolean("Resalt");

                    if (Resault) {
                        btn_Delete.setVisibility(View.GONE);
                        btn_Edit.setEnabled(false);
                        btn_Undo.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                boolean hasInternet = ((MainActivity) getActivity()).internet.HaveNetworkConnection();

                if (hasInternet == false) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.CheckYourInternet), Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Error) + "</font>")))
                            .setMessage((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.YourInternetIsVerySlow) + "</font>")))
                            .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Reload) + "</font>")), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    DeleteItem();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(true)
                            .show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(SetTimeOuteToPost(DeleteRequst));
    }

    //در اینجا زمانی که کاربر پشیمان شود که آیتم حذف شده را آندو کند متد زیر را فراخوانی میکند
    void UndoDelete() {
        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Sending) + "</font>")));
        progress.setMessage((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.PleaseWait) + "</font>")));
        progress.show();

        final String Unique = tbl_user.GetUniCode();

        String Url = Api + "Item/GetUndoDeleteItem?Id=" + ItemId + "&CodeUniCode=" + Unique;

        UndoDeleteRequest = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                btn_Delete.setVisibility(View.VISIBLE);
                btn_Edit.setEnabled(true);
                btn_Undo.setVisibility(View.GONE);
                progress.dismiss();
                try {
                    Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                boolean hasInternet = ((MainActivity) getActivity()).internet.HaveNetworkConnection();

                if (hasInternet == false) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.CheckYourInternet), Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Error) + "</font>")))
                            .setMessage((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.YourInternetIsVerySlow) + "</font>")))
                            .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Reload) + "</font>")), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    UndoDelete();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(true)
                            .show();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(SetTimeOuteToPost(UndoDeleteRequest));
    }

    void SetTypeItem(int Type) {
        try {

            Loading.startShimmerAnimation();
            SetEnabledAllButton(false);

            String UniCode = ((MainActivity) getActivity()).tbl_user.GetUniCode();

            String Url = Api + "Item?ItemId=" + ItemId + "&UniCode=" + UniCode + "&Type=" + Type;

            PutTypeItem=new JsonObjectRequest(Request.Method.PUT,Url,null,response -> {

                try {
                    Loading.stopShimmerAnimation();
                    SetEnabledAllButton(true);

                    if (response.getBoolean("Resalt")){
                        SetEnabled_btn_TypeItem(false);
                        Toast.makeText(getContext(), response.getString("Message"), Toast.LENGTH_SHORT).show();
                    }else{
                        SetEnabled_btn_TypeItem(true);
                        Toast.makeText(getContext(), response.getString("Message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Loading.stopShimmerAnimation();
                    e.printStackTrace();
                }

            },error -> {

                SetEnabledAllButton(true);
                Loading.stopShimmerAnimation();

                boolean hasInternet = ((MainActivity) getActivity()).internet.HaveNetworkConnection();

                if (hasInternet == false) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.CheckYourInternet), Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Error) + "</font>")))
                            .setMessage((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.YourInternetIsVerySlow) + "</font>")))
                            .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Reload) + "</font>")), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    SetTypeItem(Type);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(true)
                            .show();
                }
            });

            AppController.getInstance().addToRequestQueue(SetTimeOuteToPost(PutTypeItem));

        } catch (Exception e) {
        }
    }

    //در اینجا فعال شدن یا نشده دکمه های ویژه و فوری ست می شود
    void SetEnabled_btn_TypeItem(boolean t){
        btn_Special.setEnabled(t);
        btn_instantaneous.setEnabled(t);

        if (t){
            btn_Special.setBackground(getResources().getDrawable(R.drawable.background_btn_special));
            btn_instantaneous.setBackground(getResources().getDrawable(R.drawable.background_btn_central));
        }else{
            btn_Special.setBackgroundColor(getResources().getColor(R.color.colorLowBlack2));
            btn_instantaneous.setBackgroundColor(getResources().getColor(R.color.colorLowBlack2));
        }
    }

    //در اینجا وضعیت فعال کردن یا غیر فعال کردن دکمه ها ست می شود
    void SetEnabledAllButton(boolean res){
        btn_Special.setEnabled(res);
        btn_instantaneous.setEnabled(res);
        btn_Delete.setEnabled(res);
        btn_Edit.setEnabled(res);
        btn_Undo.setEnabled(res);
        Back.setEnabled(res);
    }

}
