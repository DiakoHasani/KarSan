package ir.tdaapp.karsanjob.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ir.tdaapp.karsanjob.MainActivity;
import ir.tdaapp.karsanjob.R;
import ir.tdaapp.karsanjob.Utility.AppController;
import ir.tdaapp.karsanjob.Utility.BaseFragment;
import ir.tdaapp.karsanjob.Utility.Replace;
import ir.tdaapp.karsanjob.Utility.Validation;
import pl.droidsonroids.gif.GifImageView;

public class Fragment_Login_Home extends BaseFragment {

    EditText txt_PhoneNumber,txt98;
    TextView lbl_btn_login;
    RelativeLayout btn_Login,btn_NewAccount;
    GifImageView img_btn_login;
    JsonObjectRequest request;
    ImageView Back;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_login_home,container,false);

        FindItem(view);

        OnClick();

        //در اینجا روی ادیت تکست +98 فوکوس می کند
        txt98.requestFocus();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowBottomBar(false);
    }

    void FindItem(View view){
        txt_PhoneNumber=view.findViewById(R.id.txt_PhoneNumber);
        txt98=view.findViewById(R.id.txt98);
        btn_Login=view.findViewById(R.id.btn_Login);
        lbl_btn_login=view.findViewById(R.id.lbl_btn_login);
        img_btn_login=view.findViewById(R.id.img_btn_login);
        btn_NewAccount=view.findViewById(R.id.btn_NewAccount);
        Back=view.findViewById(R.id.Back);
    }

    void OnClick(){

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).onBackPressed();
            }
        });

        btn_NewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.Frame_Main,new Fragment_Add_Account()).addToBackStack(null).commit();
            }
        });

        txt_PhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    txt_PhoneNumber.setHint("");
                    txt_PhoneNumber.setTextDirection(View.TEXT_DIRECTION_LTR);
                    txt_PhoneNumber.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                }else{
                    if (!txt_PhoneNumber.getText().toString().equalsIgnoreCase("")){
                        txt_PhoneNumber.setHint("");
                        txt_PhoneNumber.setTextDirection(View.TEXT_DIRECTION_LTR);
                        txt_PhoneNumber.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    }else{
                        txt_PhoneNumber.setHint(getResources().getString(R.string.InputYourPhoneNumber));
                        txt_PhoneNumber.setTextDirection(View.TEXT_DIRECTION_RTL);
                        txt_PhoneNumber.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    }
                }
            }
        });

        //زمانی که کاربر دکمه ورود را فشار می دهد
        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    //در اینجا اگر ادیت تکست شماره موبایل خالی نباشد شرط زیر اجرا می شود
                    if (!Validation.Required(txt_PhoneNumber,getResources().getString(R.string.PleaseInputYourNumberPhone))){

                        btn_Login.setEnabled(false);
                        btn_NewAccount.setEnabled(false);
                        lbl_btn_login.setVisibility(View.INVISIBLE);
                        img_btn_login.setVisibility(View.VISIBLE);

                        String Url=Api+"User?CellPhone="+txt_PhoneNumber.getText().toString();

                        request=new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    //در اینجا نتیجه عملیات گرفت می شود
                                    boolean Resalt=response.getBoolean("Resalt");

                                    //اگر عملیات به درستی انجام شده باشد شرط زیر اجرا می شود
                                    if (Resalt){

                                        btn_Login.setEnabled(true);
                                        btn_NewAccount.setEnabled(true);
                                        lbl_btn_login.setVisibility(View.VISIBLE);
                                        img_btn_login.setVisibility(View.INVISIBLE);

                                        Bundle bundle=new Bundle();
                                        bundle.putString("CellPhone", Replace.Number_fn_To_en(txt_PhoneNumber.getText().toString()));
                                        Fragment_Activation_Code fragmentActivationCode=new Fragment_Activation_Code();
                                        fragmentActivationCode.setArguments(bundle);

                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_left)
                                                .add(R.id.Frame_Main,fragmentActivationCode).addToBackStack(null).commit();

                                    }
                                    //اگر عملیات به درستی انجام نشده باشد شرط زیر اجرا می شود
                                    else{

                                        btn_Login.setEnabled(true);
                                        btn_NewAccount.setEnabled(true);
                                        lbl_btn_login.setVisibility(View.VISIBLE);
                                        img_btn_login.setVisibility(View.INVISIBLE);

                                        //در اینجا متن خطا گرفته می شود
                                        String Message=response.getString("Message");

                                        new AlertDialog.Builder(getActivity())
                                                .setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Error) + "</font>")))
                                                .setMessage((Html.fromHtml("<font color='#FF7F27'>" + Message + "</font>")))
                                                .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Ok) + "</font>")),
                                                        new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) { }
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
                                btn_NewAccount.setEnabled(true);
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
                                                    public void onClick(DialogInterface dialog, int which) { }
                                                })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setCancelable(true)
                                        .show();
                            }
                        });

                        AppController.getInstance().addToRequestQueue(SetTimeOut(request));
                    }

                }catch (Exception e){
                    btn_Login.setEnabled(true);
                    btn_NewAccount.setEnabled(true);
                    lbl_btn_login.setVisibility(View.VISIBLE);
                    img_btn_login.setVisibility(View.INVISIBLE);

                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
