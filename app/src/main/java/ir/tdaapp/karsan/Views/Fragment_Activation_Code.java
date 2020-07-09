package ir.tdaapp.karsan.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import ir.tdaapp.karsan.DataBase.Tbl_User;
import ir.tdaapp.karsan.Enum.BottomBarItems;
import ir.tdaapp.karsan.MainActivity;
import ir.tdaapp.karsan.R;
import ir.tdaapp.karsan.Utility.AppController;
import ir.tdaapp.karsan.Utility.BaseFragment;
import ir.tdaapp.karsan.Utility.Replace;
import ir.tdaapp.karsan.Utility.Validation;
import pl.droidsonroids.gif.GifImageView;

///این صفحه هنگامی که کاربر لاگین می کند وارد این صفحه می شود تا کد فعالسازی را وارد کند
public class Fragment_Activation_Code extends BaseFragment {

    EditText txtS9;
    EditText txt_ActivationCode;
    TextView lbl_btn_login, lbl_RetrySendSMS;
    RelativeLayout btn_Login, btn_RetrySendSMS;
    GifImageView img_btn_login;
    JsonObjectRequest request, requestResend;
    StringRequest requestGetUserInfo;
    CountDownTimer timer;
    Tbl_User tbl_user;
    ImageView Back;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activation_code, container, false);

        FindItem(view);

        txtS9.requestFocus();

        OnClick();

        return view;
    }

    void FindItem(View view) {
        txtS9 = view.findViewById(R.id.txtS9);
        txt_ActivationCode = view.findViewById(R.id.txt_ActivationCode);
        lbl_btn_login = view.findViewById(R.id.lbl_btn_login);
        btn_Login = view.findViewById(R.id.btn_Login);
        img_btn_login = view.findViewById(R.id.img_btn_login);
        btn_RetrySendSMS = view.findViewById(R.id.btn_RetrySendSMS);
        lbl_RetrySendSMS = view.findViewById(R.id.lbl_RetrySendSMS);
        tbl_user = ((MainActivity) getActivity()).tbl_user;
        Back = view.findViewById(R.id.Back);
    }

    void OnClick() {

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        txt_ActivationCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    txt_ActivationCode.setHint("");
                    txt_ActivationCode.setTextDirection(View.TEXT_DIRECTION_LTR);
                    txt_ActivationCode.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                } else {
                    if (!txt_ActivationCode.getText().toString().equalsIgnoreCase("")) {
                        txt_ActivationCode.setHint("");
                        txt_ActivationCode.setTextDirection(View.TEXT_DIRECTION_LTR);
                        txt_ActivationCode.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    } else {
                        txt_ActivationCode.setHint(getResources().getString(R.string.InputYourActivationCode));
                        txt_ActivationCode.setTextDirection(View.TEXT_DIRECTION_RTL);
                        txt_ActivationCode.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    }
                }
            }
        });

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (!Validation.Required(txt_ActivationCode, getResources().getString(R.string.PleaseInputYourReferenceCode))) {

                        btn_Login.setEnabled(false);
                        lbl_btn_login.setVisibility(View.INVISIBLE);
                        img_btn_login.setVisibility(View.VISIBLE);

                        //در اینجا چک می شود که کد وارد شده معتبر است
                        String CellPhone = getArguments().getString("CellPhone");
                        final String Url = Api + "User?CellPhone=" + Replace.Number_fn_To_en(CellPhone) + "&Code=" + Replace.Number_fn_To_en(txt_ActivationCode.getText().toString());

                        request = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    boolean Resault = response.getBoolean("Resalt");

                                    if (Resault) {

                                        //در اینجا اگر کد معتبر باشد اطلاعات کاربر دریافت می شود
                                        String Url2 = Api + "User/GetUserInfo?CellPhone=" + Replace.Number_fn_To_en(getArguments().getString("CellPhone")) + "_@";

                                        requestGetUserInfo = new StringRequest(Request.Method.GET, Url2, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                if (!response.equalsIgnoreCase("")) {
                                                    tbl_user.RemoveAll();
                                                    tbl_user.Add(response);

                                                    //در اینجا کاربر به صفحه ای که دکمه لاگین را زده است بر می گردد
                                                    FragmentManager fragmentManager = ((AppCompatActivity) getActivity()).getSupportFragmentManager();

                                                    List<Fragment> fragments = fragmentManager.getFragments();

                                                    for (Fragment fragment : fragments) {

                                                        if (fragment instanceof Fragment_Login_Home) {
                                                            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                                                        } else if (fragment instanceof Fragment_Add_Account) {
                                                            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                                                        }
                                                    }

                                                    btn_Login.setEnabled(true);
                                                    lbl_btn_login.setVisibility(View.VISIBLE);
                                                    img_btn_login.setVisibility(View.INVISIBLE);

                                                    ((MainActivity) getActivity()).onBackPressed();
                                                } else {
                                                    new AlertDialog.Builder(getActivity())
                                                            .setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Error) + "</font>")))
                                                            .setMessage((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Please_Re_register) + "</font>")))
                                                            .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Ok) + "</font>")),
                                                                    new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                        }
                                                                    })
                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                            .setCancelable(true)
                                                            .show();
                                                }

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        });

                                        AppController.getInstance().addToRequestQueue(SetTimeOut(requestGetUserInfo));

                                    } else {

                                        btn_Login.setEnabled(true);
                                        lbl_btn_login.setVisibility(View.VISIBLE);
                                        img_btn_login.setVisibility(View.INVISIBLE);

                                        String Message = response.getString("Message");

                                        new AlertDialog.Builder(getActivity())
                                                .setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Error) + "</font>")))
                                                .setMessage((Html.fromHtml("<font color='#FF7F27'>" + Message + "</font>")))
                                                .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Ok) + "</font>")),
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                            }
                                                        })
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setCancelable(true)
                                                .show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                btn_Login.setEnabled(true);
                                lbl_btn_login.setVisibility(View.VISIBLE);
                                img_btn_login.setVisibility(View.INVISIBLE);

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
                                        .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Ok) + "</font>")),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setCancelable(true)
                                        .show();
                            }
                        });

                        AppController.getInstance().addToRequestQueue(SetTimeOut(request));
                    }

                } catch (Exception e) {
                    btn_Login.setEnabled(true);
                    lbl_btn_login.setVisibility(View.VISIBLE);
                    img_btn_login.setVisibility(View.INVISIBLE);
                }
            }
        });

        ///مربوط به دکمه ارسال مجدد پیامک
        btn_RetrySendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //در اینجا دکمه ارسال مجدد غیر فعال می شود

                btn_RetrySendSMS.setEnabled(false);

                //در اینجا زمان دکمه مجدد ست می شود
                timer = new CountDownTimer(150000, 1000) {
                    @Override
                    public void onTick(long l) {
                        lbl_RetrySendSMS.setText(String.valueOf(l / 1000));
                    }

                    @Override
                    public void onFinish() {
                        lbl_RetrySendSMS.setText(getResources().getString(R.string.RetrySendSMS));
                        btn_RetrySendSMS.setEnabled(true);
                    }
                }.start();

                String Url = Api + "User/ResendSMS?CellPhone=" + Replace.Number_fn_To_en(getArguments().getString("CellPhone"));

                requestResend = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            boolean Resalt = response.getBoolean("Resalt");

                            //هنگامی که عملیات به درستی انجام شود شرط زیر اجرا می شود
                            if (Resalt) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.Text_Sent_Successfully), Toast.LENGTH_SHORT).show();
                            }
                            //اگر عملیات به درستی انجام نشود کد زیر اجرا می شود
                            else {
                                String Message = response.getString("Message");

                                new AlertDialog.Builder(getActivity())
                                        .setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Error) + "</font>")))
                                        .setMessage((Html.fromHtml("<font color='#FF7F27'>" + Message + "</font>")))
                                        .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Ok) + "</font>")),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setCancelable(true)
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Error) + "</font>")))
                                .setMessage((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.YourInternetIsVerySlow) + "</font>")))
                                .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Ok) + "</font>")),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setCancelable(true)
                                .show();
                    }
                });

                AppController.getInstance().addToRequestQueue(SetTimeOut(requestResend));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowBottomBar(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }

    }
}
