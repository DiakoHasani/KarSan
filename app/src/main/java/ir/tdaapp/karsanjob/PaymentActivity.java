package ir.tdaapp.karsanjob;

import androidx.appcompat.app.AppCompatActivity;
import ir.tdaapp.karsanjob.Adapter.DBAdapter;
import ir.tdaapp.karsanjob.DataBase.Tbl_User;
import ir.tdaapp.karsanjob.Statics.Price;
import ir.tdaapp.karsanjob.Utility.AppController;
import ir.tdaapp.karsanjob.Utility.Internet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.zarinpal.ewallets.purchase.ZarinPal;

import org.json.JSONException;

public class PaymentActivity extends AppCompatActivity {

    public final static String TAG = "PaymentActivity";

    JsonObjectRequest PutInventoryRequest;
    DBAdapter dbAdapter;
    Tbl_User tbl_user;
    Internet internet;
    ShimmerFrameLayout ShowPleaseWait, Shimmer_No_Internet;
    RelativeLayout Error, Loading, ErrorWallet, NoInternet, ErrorPayment, btnCall;
    TextView lbl_TryingAgain;
    Button btn_SMS;
    RequestQueue requestQueue;

    //در اینجا زمانی که کاربر پرداخت را انجام دهد ولی زمانی که رکوست مربوط به تغییرات عملیات در سمت سرور با تایم اوت مواجه شود متغیر زیر چک می شود که دوباره دوباره رکوست ارسال شود و تا دوبار مجاز به ارسال رکوست می باشد
    int ErrorForPutDataBaseServer = 0;

    boolean CanUserToExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        FindItem();
        GetResaultPayment();
        ShowPleaseWait.startShimmerAnimation();
        Shimmer_No_Internet.startShimmerAnimation();

        OnClick();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        requestQueue = Volley.newRequestQueue(this);

        if (PutInventoryRequest != null) {
            PutInventoryRequest.setTag(TAG);

            requestQueue.add(PutInventoryRequest);
        }

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }

    }

    @Override
    public void onBackPressed() {

        if (CanUserToExit) {
            super.onBackPressed();
            finish();
        }
    }

    void FindItem() {
        dbAdapter = new DBAdapter(this);
        tbl_user = new Tbl_User(dbAdapter);
        internet = new Internet(this);
        ShowPleaseWait = findViewById(R.id.ShowPleaseWait);
        Error = findViewById(R.id.Error);
        Loading = findViewById(R.id.Loading);
        ErrorWallet = findViewById(R.id.ErrorWallet);
        Shimmer_No_Internet = findViewById(R.id.Shimmer_No_Internet);
        lbl_TryingAgain = findViewById(R.id.lbl_TryingAgain);
        NoInternet = findViewById(R.id.NoInternet);
        ErrorPayment = findViewById(R.id.ErrorPayment);
        btnCall = findViewById(R.id.btnCall);
        btn_SMS = findViewById(R.id.btn_SMS);
    }

    void GetResaultPayment() {
        final Uri data = getIntent().getData();
        ZarinPal.getPurchase(this).verificationPayment(data, (isPaymentSuccess, refID, paymentRequest) -> {
            try {
                String price = String.valueOf(paymentRequest.getAmount());
                String desc = String.valueOf(paymentRequest.getDescription());

                if (isPaymentSuccess) {

                    if (price.equalsIgnoreCase(String.valueOf(Price.P5000))) {
                        AddInventory();
                    } else {
                        CanUserToExit = true;
                        Error.setVisibility(View.GONE);
                        Loading.setVisibility(View.GONE);
                        ErrorWallet.setVisibility(View.GONE);
                        NoInternet.setVisibility(View.GONE);
                        ErrorPayment.setVisibility(View.VISIBLE);
                    }

                } else {
                    CanUserToExit = true;
                    Error.setVisibility(View.GONE);
                    Loading.setVisibility(View.GONE);
                    ErrorWallet.setVisibility(View.VISIBLE);
                    NoInternet.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                CanUserToExit = true;
                Toast.makeText(this, getResources().getString(R.string.ErrorDuringOperationPleaseTryAgain), Toast.LENGTH_LONG).show();
            }
        });
    }

    void AddInventory() {

        Error.setVisibility(View.GONE);
        Loading.setVisibility(View.VISIBLE);
        ErrorWallet.setVisibility(View.GONE);
        NoInternet.setVisibility(View.GONE);

        String UniqueCode = tbl_user.GetUniCode();
        String Url = "http://karsanjob.ir/api/User/PutInventoryUser?UniCode=" + UniqueCode;

        PutInventoryRequest = new JsonObjectRequest(Request.Method.PUT, Url, null, response -> {

            try {

                CanUserToExit = true;

                if (response.getBoolean("Resalt")) {
                    Toast.makeText(this, response.getString("Message"), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Error.setVisibility(View.VISIBLE);
                    Loading.setVisibility(View.GONE);
                    ErrorWallet.setVisibility(View.GONE);
                    NoInternet.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            CanUserToExit = true;
            boolean HasInternet = internet.HaveNetworkConnection();
            if (HasInternet) {
                if (ErrorForPutDataBaseServer <= 1) {
                    ErrorForPutDataBaseServer++;
                    AddInventory();
                } else {
                    Error.setVisibility(View.VISIBLE);
                    Loading.setVisibility(View.GONE);
                    ErrorWallet.setVisibility(View.GONE);
                    NoInternet.setVisibility(View.GONE);
                }
            } else {
                if (ErrorForPutDataBaseServer != 0) {
                    ErrorForPutDataBaseServer--;
                }
                Error.setVisibility(View.GONE);
                Loading.setVisibility(View.GONE);
                ErrorWallet.setVisibility(View.GONE);
                NoInternet.setVisibility(View.VISIBLE);
            }
        });

        RetryPolicy policy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        PutInventoryRequest.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(PutInventoryRequest);
    }

    void OnClick() {
        lbl_TryingAgain.setOnClickListener(view -> AddInventory());

        btnCall.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + "09186960528"));
            startActivity(intent);
        });

        btn_SMS.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", "09186960528", null))));

    }

}
