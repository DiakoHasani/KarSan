package ir.tdaapp.karsan.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import ir.tdaapp.karsan.DataBase.Tbl_Madrak;
import ir.tdaapp.karsan.DataBase.Tbl_Major;
import ir.tdaapp.karsan.MainActivity;
import ir.tdaapp.karsan.R;
import ir.tdaapp.karsan.Utility.AppController;
import ir.tdaapp.karsan.Utility.BaseFragment;

public class Fragment_My_CV extends BaseFragment implements View.OnClickListener {

    public static final String TAG="Fragment_My_CV";

    JsonObjectRequest GetDetailsRequest,DeleteRequest;
    TextView lbl_DateInsert,lbl_FullName,lbl_Major,lbl_Madrak,lbl_Certificate,lbl_CellPhone,lbl_Gender;
    ShimmerFrameLayout Loading;
    Tbl_Major tbl_major;
    Tbl_Madrak tbl_madrak;
    String CvUrl="";
    CardView ShowCv,DeleteCv;
    int CvId=0;
    RequestQueue requestQueue;
    LinearLayout Back;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_my_cv,container,false);

        FindItem(view);
        SetDetails();
        OnClick();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        requestQueue= Volley.newRequestQueue(getContext());

        GetDetailsRequest.setTag(TAG);

        requestQueue.add(GetDetailsRequest);

        if (DeleteRequest!=null){
            DeleteRequest.setTag(TAG);
            requestQueue.add(DeleteRequest);
        }

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowBottomBar(false);
    }

    void FindItem(View view){
        lbl_DateInsert=view.findViewById(R.id.lbl_DateInsert);
        lbl_FullName=view.findViewById(R.id.lbl_FullName);
        lbl_Major=view.findViewById(R.id.lbl_Major);
        lbl_Madrak=view.findViewById(R.id.lbl_Madrak);
        lbl_Certificate=view.findViewById(R.id.lbl_Certificate);
        lbl_CellPhone=view.findViewById(R.id.lbl_CellPhone);
        lbl_Gender=view.findViewById(R.id.lbl_Gender);
        Loading=view.findViewById(R.id.Loading);
        ShowCv=view.findViewById(R.id.ShowCv);
        DeleteCv=view.findViewById(R.id.DeleteCv);
        Back=view.findViewById(R.id.Back);

        tbl_major=new Tbl_Major(((MainActivity)getActivity()).dbAdapter);
        tbl_madrak=new Tbl_Madrak(((MainActivity)getActivity()).dbAdapter);
    }

    void SetDetails(){

        Loading.startShimmerAnimation();

        String UniCode=((MainActivity)getActivity()).tbl_user.GetUniCode();

        String Url=Api+"CV?UniCode="+UniCode;

        GetDetailsRequest=new JsonObjectRequest(Request.Method.GET,Url,null,response ->{
            Loading.stopShimmerAnimation();

            try {

                CvId=response.getInt("CvId");

                if (CvId!=0){

                    ShowCv.setEnabled(true);
                    DeleteCv.setEnabled(true);

                    lbl_CellPhone.setText(response.getString("CellPhone"));
                    int Certificate=response.getInt("Certificate");

                    //در اینجا گواهینامه ست می شود
                    switch (Certificate){
                        case 0:
                            lbl_Certificate.setText(getResources().getString(R.string.DontHaveCertificate));
                            break;
                        case 1:
                            lbl_Certificate.setText(getResources().getString(R.string.GradeOne));
                            break;
                        case 2:
                            lbl_Certificate.setText(getResources().getString(R.string.GradeTwo));
                            break;
                        case 3:
                            lbl_Certificate.setText(getResources().getString(R.string.GradeThird));
                            break;
                        case 4:
                            lbl_Certificate.setText(getResources().getString(R.string.Special));
                            break;
                    }

                    lbl_DateInsert.setText(response.getString("DateInsert"));
                    lbl_FullName.setText(response.getString("FullName"));

                    int Gender=response.getInt("Gender");
                    //در اینجا جنسیت ست می شود
                    switch (Gender){
                        case 0:
                            lbl_Gender.setText(getResources().getString(R.string.Man));
                            break;
                        case 1:
                            lbl_Gender.setText(getResources().getString(R.string.Women));
                            break;
                    }

                    lbl_Madrak.setText(tbl_madrak.GetTitleById(response.getInt("MadrakId")));
                    lbl_Major.setText(tbl_major.GetTitleById(response.getInt("MajorId")));

                    if (!response.getString("Url").equalsIgnoreCase("")){
                        CvUrl=ApiPdf+response.getString("Url");
                    }
                }else{
                    ShowCv.setEnabled(false);
                    DeleteCv.setEnabled(false);
                    Toast.makeText(getContext(), getResources().getString(R.string.YouDontWriteCV), Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ,error -> {

            ShowCv.setEnabled(false);
            DeleteCv.setEnabled(false);

            Loading.stopShimmerAnimation();
            Loading.stopShimmerAnimation();

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
                            SetDetails();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(true)
                    .show();

        } );
        AppController.getInstance().addToRequestQueue(SetTimeOut(GetDetailsRequest));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ShowCv:
                if (!CvUrl.equalsIgnoreCase("")){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(CvUrl)));
                }
                break;
            case R.id.DeleteCv:
                DeleteCV();
                break;
            case R.id.Back:
                getActivity().onBackPressed();
                break;
        }
    }

    void OnClick(){
        ShowCv.setOnClickListener(this);
        DeleteCv.setOnClickListener(this);
        Back.setOnClickListener(this);
    }

    //در اینجا یک رزومه حذف می شود
    void DeleteCV(){
        if (CvId!=0){

            DeleteCv.setEnabled(false);
            ShowCv.setEnabled(false);
            Loading.startShimmerAnimation();

            String UniCode=((MainActivity)getActivity()).tbl_user.GetUniCode();
            String Url=Api+"CV/"+CvId+"?UniCode="+UniCode;

            DeleteRequest=new JsonObjectRequest(Request.Method.DELETE,Url,null,response -> {
                DeleteCv.setEnabled(true);
                ShowCv.setEnabled(true);
                Loading.stopShimmerAnimation();
                try {

                    Toast.makeText(getContext(), response.getString("Message"), Toast.LENGTH_LONG).show();

                    if (response.getBoolean("Resalt")){
                        getActivity().onBackPressed();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            ,error -> {

                DeleteCv.setEnabled(true);
                ShowCv.setEnabled(true);
                Loading.stopShimmerAnimation();

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
                                DeleteCV();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(true)
                        .show();
            });

            AppController.getInstance().addToRequestQueue(SetTimeOuteToPost(DeleteRequest));
        }
    }
}
