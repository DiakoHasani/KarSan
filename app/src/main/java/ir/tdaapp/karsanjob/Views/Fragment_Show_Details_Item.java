package ir.tdaapp.karsanjob.Views;

import android.app.AlertDialog;
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

import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ir.tdaapp.karsanjob.DataBase.Tbl_FavoritesItem;
import ir.tdaapp.karsanjob.DataBase.Tbl_Job;
import ir.tdaapp.karsanjob.DataBase.Tbl_Madrak;
import ir.tdaapp.karsanjob.DataBase.Tbl_Major;
import ir.tdaapp.karsanjob.DataBase.Tbl_User;
import ir.tdaapp.karsanjob.MainActivity;
import ir.tdaapp.karsanjob.R;
import ir.tdaapp.karsanjob.Utility.AppController;
import ir.tdaapp.karsanjob.Utility.BaseFragment;

public class Fragment_Show_Details_Item extends BaseFragment {

    public static final String TAG = "Fragment_Show_Details_Item";

    ShimmerFrameLayout Loading;
    TextView lbl_Title, lbl_DateInsert, lbl_JobType, lbl_Gender, lbl_Insurance, lbl_Age_Range, lbl_JobTime;
    TextView lbl_HoursOfWork, lbl_Description, lbl_Price;

    RelativeLayout R_Madrak_1, R_Madrak_2, R_Madrak_3, R_Major_1, R_Major_2, R_Major_3, btnCall;
    TextView L_Madrak_1, L_Madrak_2, L_Madrak_3, L_Major_1, L_Major_2, L_Major_3;

    Tbl_Job tbl_job;
    Tbl_Madrak tbl_madrak;
    Tbl_Major tbl_major;
    Tbl_User tbl_user;
    ImageView img, Like, Share;

    JsonObjectRequest request;
    RequestQueue requestQueue;

    LinearLayout Back;
    Button btn_infringement;
    String CellPhone = "";
    int ItemId = 0;
    Tbl_FavoritesItem tbl_favoritesItem;
    boolean IsLike;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_details_item, container, false);

        FindItem(view);
        SetItem();
        OnClick();
        return view;
    }

    void FindItem(View view) {
        img = view.findViewById(R.id.img);
        Back = view.findViewById(R.id.Back);
        L_Major_3 = view.findViewById(R.id.L_Major_3);
        Like = view.findViewById(R.id.Like);
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
        btn_infringement = view.findViewById(R.id.btn_infringement);
        tbl_user = ((MainActivity) getActivity()).tbl_user;
        btnCall = view.findViewById(R.id.btnCall);
        Share = view.findViewById(R.id.Share);
        tbl_favoritesItem = new Tbl_FavoritesItem(((MainActivity) getActivity()).dbAdapter);
    }

    void SetItem() {
        Loading.startShimmerAnimation();

        try {
            Like.setEnabled(false);
            Share.setEnabled(false);

            Bundle bundle = getArguments();
            ItemId = bundle.getInt("Id");
            String Url = Api + "Item/" + bundle.getInt("Id");

            if (tbl_user.HasAccount()) {
                Url += "?UniCode=" + tbl_user.GetUniCode();
            } else {
                Url += "?UniCode=";
            }

            request = new JsonObjectRequest(Request.Method.GET, Url, null, response -> {
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

                    //در اینجا تایتل ست می شود
                    lbl_Title.setText(response.getString("Title"));
                    lbl_Title.setGravity(Gravity.CENTER);

                    String MinPrice = "0";
                    String MaxPrice = response.getString("MaxPrice");

                    CellPhone = response.getString("CellPhone");

                    if (response.getString("MinPrice") != null) {
                        MinPrice = response.getString("MinPrice");
                    }

                    //در اینجا حقوق ست می شود
                    if (!MinPrice.equalsIgnoreCase("-1") && !MaxPrice.equalsIgnoreCase("-1")) {
                        if (!MinPrice.equalsIgnoreCase("0")) {
                            //در اینجا اگر کاربر حداقل حقوق را وارد کرده باشد مقدار زیر ست می شود
                            lbl_Price.setText(getResources().getString(R.string.from) + " " + showPrice(MinPrice) + getResources().getString(R.string.Toman) + " " + getResources().getString(R.string.until) + " " + showPrice(MaxPrice) + getResources().getString(R.string.Toman));
                        } else {
                            //در اینجا اگر کاربر حداقل حقوق را وارد نکرده باشد مقدار زیر ست می شود
                            lbl_Price.setText(showPrice(MaxPrice) + " " + getResources().getString(R.string.Toman));
                        }
                    } else {
                        lbl_Price.setText(getString(R.string.Agreement));
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


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Like.setEnabled(true);
                Share.setEnabled(true);

                ///در اینجا چک می شود که آیا کاربر قبلا این آیتم را در لیست مورد علاقه قرار داده است
                IsLike = tbl_favoritesItem.IsLike(ItemId);
                Like.setBackground(null);
                if (IsLike) {
                    Like.setImageDrawable(getResources().getDrawable(R.drawable.like));
                } else {
                    Like.setImageDrawable(getResources().getDrawable(R.drawable.dont_like));
                }


                Loading.stopShimmerAnimation();
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    try {

                        Like.setEnabled(false);
                        Share.setEnabled(false);

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

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    void OnClick() {

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + CellPhone));
                startActivity(intent);
            }
        });
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        btn_infringement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean hasAccount = tbl_user.HasAccount();

                //در اینجا اگر کاربر حساب کاربری نداشته باشد وارد صفحه لاگین می شود
                if (!hasAccount) {

                    Toast.makeText(getContext(), getResources().getString(R.string.Create_An_Account_First), Toast.LENGTH_LONG).show();

                    Fragment_Login_Home fragment_login_home = new Fragment_Login_Home();

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.Frame_Main, fragment_login_home).addToBackStack(null).commit();
                } else {

                    Fragment_Infringement fragment_infringement = new Fragment_Infringement();

                    Bundle bundle = new Bundle();
                    bundle.putInt("Id", ItemId);
                    fragment_infringement.setArguments(bundle);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.Frame_Main, fragment_infringement).addToBackStack(null).commit();
                }
            }
        });

        Like.setOnClickListener(view -> {
            boolean HasAccount = tbl_user.HasAccount();
            if (HasAccount) {
                if (!IsLike) {
                    tbl_favoritesItem.Add(ItemId);
                    Like.setBackground(null);
                    Like.setImageDrawable(getResources().getDrawable(R.drawable.like));
                    IsLike = true;

                    Snackbar snackbar = Snackbar.make(view, getResources().getString(R.string.ThisAdWasAddedToTheFavoritesList), Snackbar.LENGTH_LONG);
                    snackbar.show();

                } else {
                    tbl_favoritesItem.Delete(ItemId);
                    Like.setBackground(null);
                    Like.setImageDrawable(getResources().getDrawable(R.drawable.dont_like));
                    IsLike = false;


                    Snackbar snackbar = Snackbar.make(view, getResources().getString(R.string.ThisItemHasBeenRemovedFromTheFavoritesList), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.Create_An_Account_First), Toast.LENGTH_LONG).show();

                Fragment_Login_Home fragment_login_home = new Fragment_Login_Home();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.Frame_Main, fragment_login_home).addToBackStack(null).commit();
            }
        });

        Share.setOnClickListener(view -> {
            String Url = ApiStyles + "Items/GetItemForShare/" + ItemId;

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Url);
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.Share)));
        });
    }

    //در اینجا مبلغ سه رقم جدا خواهد شد
    String showPrice(String price) {
        price = price.replace(",", "");

        if (price.length() > 0) {
            DecimalFormat sdd = new DecimalFormat("#,###");
            Double doubleNumber = Double.parseDouble(price);

            String format = sdd.format(doubleNumber);
            return format;
        }
        return price;
    }
}
