package ir.tdaapp.karsanjob.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.zarinpal.ewallets.purchase.PaymentRequest;
import com.zarinpal.ewallets.purchase.ZarinPal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ir.tdaapp.karsanjob.MainActivity;
import ir.tdaapp.karsanjob.R;
import ir.tdaapp.karsanjob.Statics.Price;
import ir.tdaapp.karsanjob.Utility.AppController;
import ir.tdaapp.karsanjob.Utility.BaseFragment;

public class Fragment_Wallet extends BaseFragment implements View.OnClickListener {

    public static final String TAG="Fragment_Wallet";

    RelativeLayout btn_Charge;
    StringRequest GetInventoryRequest;
    TextView lbl_Inventory;
    ShimmerFrameLayout Loading;
    CountDownTimer timer;
    LinearLayout Back;
    RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_wallet,container,false);

        FindItem(view);
        OnClick();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowBottomBar(false);
        GetInventory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        timer.cancel();

        requestQueue= Volley.newRequestQueue(getContext());

        GetInventoryRequest.setTag(TAG);

        requestQueue.add(GetInventoryRequest);

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    void FindItem(View view){
        btn_Charge=view.findViewById(R.id.btn_Charge);
        lbl_Inventory=view.findViewById(R.id.lbl_Inventory);
        Loading=view.findViewById(R.id.Loading);
        Back=view.findViewById(R.id.Back);
    }

    //در اینجا کیف پول شارژ می شود
    void ChargeWallet(){
        PaymentRequest paymentRequest = ZarinPal.getPaymentRequest();
        ZarinPal zarinPal = ZarinPal.getPurchase(getContext());

        paymentRequest.setMerchantID("b8dcfc0c-8d51-11ea-a2bf-000c295eb8fc");
        paymentRequest.setAmount(Price.P5000);

        paymentRequest.setDescription(getResources().getString(R.string.ChargeWalletKarsan));
        paymentRequest.setMobile("09186960528");
        paymentRequest.setEmail("ireach@gmail.com");

        //--> CallBack URL After ZArin Pal Payment Returns
        paymentRequest.setCallbackURL("zarpayment://karsan");

        zarinPal.startPayment(paymentRequest, (status, authority, paymentGatewayUri, intent) -> {
            if (status == 100) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            } else {
                //TODO Failure Authority !
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_Charge:
                ChargeWallet();
                break;
            case R.id.Back:
                getActivity().onBackPressed();
                break;
        }
    }

    void OnClick(){
        btn_Charge.setOnClickListener(this);
        Back.setOnClickListener(this);
    }

    void GetInventory(){

        Loading.startShimmerAnimation();

        String UniCode=((MainActivity)getActivity()).tbl_user.GetUniCode();

        String Url=Api+"User/GetInventoryUser?Unicode="+UniCode;

        GetInventoryRequest=new StringRequest(Request.Method.GET,Url,response -> {

            Loading.stopShimmerAnimation();

            String Price;

            if (response.replace("\"","").equalsIgnoreCase(""))
                Price="0";
            else
                Price=response.replace("\"","");

            SetInventory(Price);
        }, error -> {
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
                            GetInventory();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(true)
                    .show();
        });

        AppController.getInstance().addToRequestQueue(SetTimeOut(GetInventoryRequest));
    }

    //در اینجا موجودی به صورت انیمیشن نمایش داده می شود
    void SetInventory(String P){
        long price=Long.valueOf(P);

        if (price<50){

            lbl_Inventory.setText("0");

            timer=new CountDownTimer(2000,1) {
                @Override
                public void onTick(long l) {
                    if (Long.valueOf(lbl_Inventory.getText().toString())<price){
                        lbl_Inventory.setText(String.valueOf(Long.valueOf(lbl_Inventory.getText().toString())+1));
                    }
                }

                @Override
                public void onFinish() {

                }
            }.start();
        }else{
            long price2=price-50;

            lbl_Inventory.setText(String.valueOf(price2));

            timer=new CountDownTimer(2000,1) {
                @Override
                public void onTick(long l) {
                    if (Long.valueOf(lbl_Inventory.getText().toString())<price){
                        lbl_Inventory.setText(String.valueOf(Long.valueOf(lbl_Inventory.getText().toString())+1));
                    }
                }

                @Override
                public void onFinish() {

                }
            }.start();

        }

    }

}
