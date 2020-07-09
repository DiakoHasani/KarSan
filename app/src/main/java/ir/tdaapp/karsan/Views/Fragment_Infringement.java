package ir.tdaapp.karsan.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ir.tdaapp.karsan.DataBase.Tbl_User;
import ir.tdaapp.karsan.MainActivity;
import ir.tdaapp.karsan.R;
import ir.tdaapp.karsan.Utility.AppController;
import ir.tdaapp.karsan.Utility.BaseFragment;
import ir.tdaapp.karsan.Utility.Validation;

//مربوط به صفحه گزارش تخلف
public class Fragment_Infringement extends BaseFragment {

    public static final String TAG = "Fragment_Infringement";

    Button btn_Report;
    JsonObjectRequest request;
    EditText txt_Text;
    Tbl_User tbl_user;
    RequestQueue requestQueue;
    LinearLayout Back;
    RelativeLayout loading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_infringement, container, false);

        FindItem(view);
        OnClick();

        return view;
    }

    void FindItem(View view) {
        btn_Report = view.findViewById(R.id.btn_Report);
        txt_Text=view.findViewById(R.id.txt_Text);
        tbl_user=((MainActivity)getActivity()).tbl_user;
        loading=view.findViewById(R.id.loading);
        Back=view.findViewById(R.id.Back);
    }

    void OnClick() {

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).onBackPressed();
            }
        });

        btn_Report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (!Validation.Required(txt_Text,getString(R.string.ThisValueMustBeFilled))){
                        loading.setVisibility(View.VISIBLE);

                        String Url = Api + "Item";

                        JSONObject object=new JSONObject();

                        Bundle bundle=getArguments();

                        int ItemId=bundle.getInt("Id");

                        object.put("ItemId",ItemId);
                        object.put("Text",txt_Text.getText().toString());
                        object.put("UniCode",tbl_user.GetUniCode());

                        request=new JsonObjectRequest(Request.Method.POST, Url, object, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    loading.setVisibility(View.GONE);

                                    boolean resault=response.getBoolean("Resalt");

                                    if (resault){
                                        Toast.makeText(getActivity(), getResources().getString(R.string.Your_Report_Will_Be_Processed_As_Soon_As_Possible), Toast.LENGTH_LONG).show();
                                    }else{

                                        String Message=response.getString("Message");

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
                                    loading.setVisibility(View.GONE);
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                loading.setVisibility(View.GONE);

                                //در اینجا چک می شود که اینترنت کاربر متصل است
                                boolean HasInternet=((MainActivity)getActivity()).internet.HaveNetworkConnection();

                                //اگر اینترنت متصل نباشد شرط زیر اجرا می شود
                                if (!HasInternet){
                                    Toast.makeText(getActivity(), getResources().getString(R.string.CheckYourInternet), Toast.LENGTH_LONG).show();
                                }
                                //اگر اینترنت متصل یاشد شرط زیر اجرا می شود
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

                        AppController.getInstance().addToRequestQueue(SetTimeOuteToPost(request));
                    }

                } catch (Exception e) {
                    loading.setVisibility(View.GONE);
                }
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

        if (request!=null){
            requestQueue= Volley.newRequestQueue(getContext());

            request.setTag(TAG);

            requestQueue.add(request);

            if (requestQueue != null) {
                requestQueue.cancelAll(TAG);
            }
        }


        loading.setVisibility(View.GONE);
    }
}
