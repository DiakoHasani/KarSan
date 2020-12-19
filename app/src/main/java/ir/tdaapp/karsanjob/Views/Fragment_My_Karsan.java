package ir.tdaapp.karsanjob.Views;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import ir.tdaapp.karsanjob.BuildConfig;
import ir.tdaapp.karsanjob.DataBase.Tbl_User;
import ir.tdaapp.karsanjob.MainActivity;
import ir.tdaapp.karsanjob.R;
import ir.tdaapp.karsanjob.Utility.AppController;
import ir.tdaapp.karsanjob.Utility.BaseFragment;

public class Fragment_My_Karsan extends BaseFragment implements View.OnClickListener {

    public final static String TAG = "Fragment_My_Karsan";

    Tbl_User tbl_user;
    TextView lbl_Text;
    StringRequest request;
    RequestQueue requestQueue;
    LinearLayout Edit_Profile,FavoriteItems,MyItems,MyCv,Wallet,Rules_And_Support,ContactUs,RefrenceCode,ShareApp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmnt_my_karsan, container, false);

        FindItem(view);
        SetText();
        OnClick();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        requestQueue = Volley.newRequestQueue(getContext());

        if (request != null) {
            request.setTag(TAG);
            requestQueue.add(request);
        }

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    void FindItem(View view) {
        tbl_user = ((MainActivity)getActivity()).tbl_user;
        lbl_Text = view.findViewById(R.id.lbl_Text);

        Edit_Profile = view.findViewById(R.id.Edit_Profile);
        FavoriteItems = view.findViewById(R.id.FavoriteItems);
        MyItems = view.findViewById(R.id.MyItems);
        MyCv = view.findViewById(R.id.MyCv);
        Wallet = view.findViewById(R.id.Wallet);
        Rules_And_Support = view.findViewById(R.id.Rules_And_Support);
        ContactUs = view.findViewById(R.id.ContactUs);
        RefrenceCode = view.findViewById(R.id.RefrenceCode);
        ShareApp = view.findViewById(R.id.ShareApp);
    }

    //در اینجا متن بالای صفحه ست می شود
    public void SetText() {
        boolean hasAccount = tbl_user.HasAccount();

        if (!hasAccount) {
            lbl_Text.setText(getResources().getString(R.string.PleaseCreateNewsAccountInApplicationKarsan));
        } else {

            String UniCode = tbl_user.GetUniCode();

            String Url = Api + "User/GetCellPhoneByUniCode?code=" + UniCode;

            request = new StringRequest(Request.Method.GET, Url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    String Message = getResources().getString(R.string.you_With)
                            + " " + response.replace("\"", "") + " " + getResources().getString(R.string.You_Have_Signed_Up_For_The_Karsan_App);

                    lbl_Text.setText(Message);

                }
            }, error -> lbl_Text.setText(getResources().getString(R.string.YourAccountHasEncounteredAProblemPleaseEditYourAccount)));

            AppController.getInstance().addToRequestQueue(SetTimeOut(request));
        }
    }

    void OnClick() {
        Edit_Profile.setOnClickListener(this);
        FavoriteItems.setOnClickListener(this);
        MyItems.setOnClickListener(this);
        MyCv.setOnClickListener(this);
        Wallet.setOnClickListener(this);
        Rules_And_Support.setOnClickListener(this);
        ContactUs.setOnClickListener(this);
        RefrenceCode.setOnClickListener(this);
        ShareApp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Edit_Profile:
                GoToPageEditProfile();
                break;
            case R.id.FavoriteItems:
                GoToFavoriteItems();
                break;
            case R.id.MyItems:
                GoToMyItems();
                break;
            case R.id.MyCv:
                GoToMyCV();
                break;
            case R.id.Wallet:
                GoToWallet();
                break;
            case R.id.Rules_And_Support:
                GoToRules_And_Support();
                break;
            case R.id.ContactUs:
                GoToContactUs();
                break;
            case R.id.RefrenceCode:
                GoToRefrenceCode();
                break;
            case R.id.ShareApp:
                ShareApplication();
                break;
        }
    }

    //در اینجا زمانی که کاربر دکمه ویرایش پروفایل را فشار می دهد متد زیر فراخوانی می شود
    void GoToPageEditProfile() {
        boolean HasAccount = tbl_user.HasAccount();

        if (HasAccount) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.Frame_Main, new Fragment_Edit_Profile(), Fragment_Edit_Profile.TAG)
                    .addToBackStack(null).commit();
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.Create_An_Account_First), Toast.LENGTH_LONG).show();

            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.Frame_Main, new Fragment_Login_Home())
                    .addToBackStack(null).commit();
        }
    }

    //در اینجا زمانی که کاربر دکمه آگهی های انتخاب شده کلیک می کند متد زیر اجرا می شود
    void GoToFavoriteItems(){
        boolean HasAccount = tbl_user.HasAccount();

        if (HasAccount) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.Frame_Main, new Fragment_Favorites_Item(), Fragment_Favorites_Item.TAG)
                    .addToBackStack(null).commit();
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.Create_An_Account_First), Toast.LENGTH_LONG).show();

            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.Frame_Main, new Fragment_Login_Home())
                    .addToBackStack(null).commit();
        }
    }

    //در اینجا آگهی های من نمایش داده می شود
    void GoToMyItems(){
        boolean HasAccount = tbl_user.HasAccount();

        if (HasAccount) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.Frame_Main, new Fragment_My_Items(), Fragment_My_Items.TAG)
                    .addToBackStack(null).commit();
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.Create_An_Account_First), Toast.LENGTH_LONG).show();

            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.Frame_Main, new Fragment_Login_Home())
                    .addToBackStack(null).commit();
        }
    }

    //در اینجا رزومه من نمایش داده می شود
    void GoToMyCV(){
        boolean HasAccount = tbl_user.HasAccount();

        if (HasAccount) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.Frame_Main, new Fragment_My_CV(), Fragment_My_CV.TAG)
                    .addToBackStack(null).commit();
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.Create_An_Account_First), Toast.LENGTH_LONG).show();

            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.Frame_Main, new Fragment_Login_Home())
                    .addToBackStack(null).commit();
        }
    }

    //در اینجا کیف پول نمایش داده می شود
    void GoToWallet(){
        boolean HasAccount = tbl_user.HasAccount();

        if (HasAccount) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.Frame_Main, new Fragment_Wallet(), Fragment_Wallet.TAG)
                    .addToBackStack(null).commit();
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.Create_An_Account_First), Toast.LENGTH_LONG).show();

            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.Frame_Main, new Fragment_Login_Home())
                    .addToBackStack(null).commit();
        }
    }

    ///در اینجا قوانین و پشتیبانی نمایش داده می شود
    void GoToRules_And_Support(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.Frame_Main, new Fragment_Rules_And_Support())
                .addToBackStack(null).commit();
    }

    //در اینجا تماس با ما نمایش داده می شود
    void GoToContactUs(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.Frame_Main, new Fragment_Contact_US())
                .addToBackStack(null).commit();
    }

    //در اینجا کد معرف من نمایش داده می شود
    void GoToRefrenceCode(){
        boolean HasAccount = tbl_user.HasAccount();
        if (HasAccount) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.Frame_Main, new Fragment_Show_National_Code(), Fragment_Show_National_Code.TAG)
                    .addToBackStack(null).commit();
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.Create_An_Account_First), Toast.LENGTH_LONG).show();

            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.Frame_Main, new Fragment_Login_Home())
                    .addToBackStack(null).commit();
        }
    }

    //در اینجا اپلیکیشن ارسال می شود
    void ShareApplication(){
        ApplicationInfo app = getContext().getApplicationInfo();
        String filePath = app.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);

        // MIME of .apk is "application/vnd.android.package-archive".
        // but Bluetooth does not accept this. Let's use "*/*" instead.
        intent.setType("*/*");

        // Append file and send Intent
        File originalApk = new File(filePath);

        try {
            //Make new directory in new location=
            File tempFile = new File(getActivity().getExternalCacheDir() + "/ExtractedApk");
            //If directory doesn't exists create new
            if (!tempFile.isDirectory())
                if (!tempFile.mkdirs())
                    return;
            //Get application's name and convert to lowercase
            tempFile = new File(tempFile.getPath() + "/" + getString(app.labelRes).replace(" ","").toLowerCase() + ".apk");
            //If file doesn't exists create new
            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }
            //Copy file to new location
            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
            //Open share dialog
//          intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
            Uri photoURI = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", tempFile);
//          intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
            intent.putExtra(Intent.EXTRA_STREAM, photoURI);
            startActivity(Intent.createChooser(intent, "Share app via"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
