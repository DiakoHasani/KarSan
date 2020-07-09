package ir.tdaapp.karsan.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import ir.tdaapp.karsan.MainActivity;
import ir.tdaapp.karsan.R;
import ir.tdaapp.karsan.Utility.AppController;
import ir.tdaapp.karsan.Utility.BaseFragment;

public class Fragment_Show_National_Code extends BaseFragment implements View.OnClickListener {


    public final static String TAG="Fragment_Show_National_Code";

    StringRequest request;
    TextView lbl_RefrenceCode;
    ShimmerFrameLayout Loading;
    CardView Share;
    String RefrenceCode="";
    RequestQueue requestQueue;
    LinearLayout Back;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_national_code, container, false);

        FindItem(view);

        GetRefrenceCode();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowBottomBar(false);
    }

    void FindItem(View view) {
        lbl_RefrenceCode = view.findViewById(R.id.lbl_RefrenceCode);
        Loading = view.findViewById(R.id.Loading);
        Share = view.findViewById(R.id.Share);
        Back = view.findViewById(R.id.Back);

        Share.setOnClickListener(this);
        Back.setOnClickListener(this);
    }

    void GetRefrenceCode(){

        Share.setEnabled(false);
        Loading.startShimmerAnimation();

        String UniCode=((MainActivity)getActivity()).tbl_user.GetUniCode();

        String Url=Api+"User/GetNationalCodeUser?UniCode="+UniCode;

        request=new StringRequest(Request.Method.GET,Url,response -> {

            RefrenceCode=response.replace("\"","");
            Share.setEnabled(true);
            Loading.stopShimmerAnimation();
            lbl_RefrenceCode.setText(RefrenceCode);

        },error -> {
            Loading.stopShimmerAnimation();
            boolean hasInternet=((MainActivity)getActivity()).internet.HaveNetworkConnection();

            if (hasInternet==false){
                Toast.makeText(getActivity(), getResources().getString(R.string.CheckYourInternet), Toast.LENGTH_SHORT).show();
            }
            else{
                new AlertDialog.Builder(getActivity())
                        .setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Error) + "</font>")))
                        .setMessage((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.YourInternetIsVerySlow) + "</font>")))
                        .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Reload) + "</font>")), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                GetRefrenceCode();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(true)
                        .show();
            }
        });

        AppController.getInstance().addToRequestQueue(SetTimeOut(request));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Share:
                ShareRefrenceCode();
                break;
            case R.id.Back:
                getActivity().onBackPressed();
                break;
        }
    }

    void ShareRefrenceCode(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, RefrenceCode);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.Share)));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        requestQueue= Volley.newRequestQueue(getContext());

        request.setTag(TAG);

        requestQueue.add(request);

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}
