package ir.tdaapp.karsanjob.Views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import ir.tdaapp.karsanjob.R;
import ir.tdaapp.karsanjob.Utility.BaseFragment;

public class Fragment_Contact_US extends BaseFragment implements View.OnClickListener {

    LinearLayout Back;
    CardView telegram,instagram,whatsapp;
    RelativeLayout btnCall;
    Button btn_SMS;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_contact_us,container,false);

        FindItem(view);

        return view;
    }

    void FindItem(View view){
        Back=view.findViewById(R.id.Back);
        telegram=view.findViewById(R.id.telegram);
        instagram=view.findViewById(R.id.instagram);
        whatsapp=view.findViewById(R.id.whatsapp);
        btnCall=view.findViewById(R.id.btnCall);
        btn_SMS=view.findViewById(R.id.btn_SMS);

        Back.setOnClickListener(this);
        telegram.setOnClickListener(this);
        instagram.setOnClickListener(this);
        whatsapp.setOnClickListener(this);
        btn_SMS.setOnClickListener(this);
        btnCall.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowBottomBar(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Back:
                getActivity().onBackPressed();
                break;
            case R.id.telegram:
                GetTelegram();
                break;
            case R.id.instagram:
                GetInstagram();
                break;
            case R.id.whatsapp:
                GetWhatsApp();
                break;
            case R.id.btnCall:
                Call();
                break;
            case R.id.btn_SMS:
                SMS();
                break;
        }
    }

    void GetTelegram(){

        try {
            Intent telegramIntent = new Intent(Intent.ACTION_VIEW);
            telegramIntent.setData(Uri.parse("http://telegram.me/karsanjob"));
            startActivity(telegramIntent);
        } catch (Exception e) {
            // show error message
        }
    }

    void GetInstagram(){
        try {
            Intent instagramIntent = new Intent(Intent.ACTION_VIEW);
            instagramIntent.setData(Uri.parse("https://www.instagram.com/karsanjob"));
            startActivity(instagramIntent);
        } catch (Exception e) {
            // show error message
        }
    }

    void GetWhatsApp(){
        try{

            String trimToNumner = "+989371527989"; //10 digit number
            Intent intent = new Intent ( Intent.ACTION_VIEW );
            intent.setData ( Uri.parse ( "https://wa.me/" + trimToNumner + "/?text=" + "" ) );
            startActivity ( intent );

        }catch (Exception e){

        }
    }

    void Call(){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:08733568920"));
        startActivity(intent);
    }

    void SMS(){
        String number = "+989371527989";  // The number on which you want to send SMS
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
    }
}
