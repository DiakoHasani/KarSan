package ir.tdaapp.karsan.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import ir.tdaapp.karsan.DataBase.Tbl_Setting;
import ir.tdaapp.karsan.Enum.ForcedUpdate;
import ir.tdaapp.karsan.MainActivity;
import ir.tdaapp.karsan.R;
import ir.tdaapp.karsan.Utility.AppController;
import ir.tdaapp.karsan.Utility.BaseFragment;
import ir.tdaapp.karsan.ViewModel.VM_Versions;

public class Fragment_Splash extends BaseFragment implements View.OnClickListener {

    ImageView img_logo;
    TextView lbl_KarSan;
    ProgressBar progressBar;
    JsonObjectRequest request;
    Tbl_Setting tbl_setting;
    //در اینجا آپدیت کردن برنامه ست می شود
    ForcedUpdate forcedUpdate = ForcedUpdate.ItIsNotNecessary;
    Handler handler_img_logo;
    Handler handler_lbl_karSan;
    Handler handler_NextPage;
    ImageView img_Reload;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        ShowBottomBar(false);

        FindItem(view);

        DateTime();
        return view;
    }

    void FindItem(View view) {
        img_logo = view.findViewById(R.id.img_logo);
        lbl_KarSan = view.findViewById(R.id.lbl_KarSan);
        progressBar = view.findViewById(R.id.progressBar);
        img_Reload = view.findViewById(R.id.img_Reload);
        tbl_setting = new Tbl_Setting(((MainActivity) getActivity()).dbAdapter);

        img_Reload.setOnClickListener(this);
    }

    void DateTime() {

        img_Reload.setVisibility(View.INVISIBLE);

        //در اینجا لوگوی کارسان نمایش داده می شود
        handler_img_logo = new Handler();
        handler_img_logo.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation aniFade = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
                img_logo.startAnimation(aniFade);
                img_logo.setVisibility(View.VISIBLE);
            }
        }, 800);
        //////////////////////////////////////////
        handler_lbl_karSan = new Handler();

        handler_lbl_karSan.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation aniSlide = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
                lbl_KarSan.setAnimation(aniSlide);
                lbl_KarSan.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
        }, 1000);


        handler_NextPage = new Handler();
        handler_NextPage.postDelayed(new Runnable() {
            @Override
            public void run() {
                GetVersionsInServer();
            }
        }, 2000);
    }

    //در اینجا ورژن های دیتابیس و برنامه از سرور گرفته می شوند
    void GetVersionsInServer() {
        String Url = Api + "VersionApp";
        request = new JsonObjectRequest(Request.Method.GET, Url, null, response -> {

            try {

                //در اینجا ورژن برنامه و دیتابیس در سرور گرفته می شود
                JSONObject VersionApp = response.getJSONObject("VersionApp");
                JSONArray VersionSqls = response.getJSONArray("VersionSql");

                //در اینجا از سمت سرور دریافت می شود که برنامه نیاز به پاک کردن دیتابیس دارد
                boolean ClearCatch = VersionApp.getBoolean("ClearCatche");


                //در اینجا ورژن ها در گوشی کاربر خوانده می شود
                VM_Versions MyVesrsions = tbl_setting.GetVersions();
                float MyVersionApp = MyVesrsions.getVersionApp();
                float MyVersionSql = MyVesrsions.getVersionSql();

                //در اینجا ورژن برنامه گرفته می شود
                float VersionApplication = Float.valueOf(VersionApp.getString("Version"));

                //در اینجا چک می کند که ورژن اپلیکیشن دریافت شده از سرور بزرگتر از ورژن اپلیکیشن کاربر است
                if (VersionApplication > MyVersionApp) {

                    //در اینجا قسمت صحیح ورژن اپلیکیشن کاربر گرفته می شود
                    int Sahih_VersionAppUser = (int) MyVersionApp;

                    //در اینجا قسمت صحیح ورژن اپلکیشن ارسال شده در سمت سرور گرفته می شود
                    int Sahih_VersionAppServer = (int) VersionApplication;

                    //در اینجا اگر عدد صحیح ورژن ارسال شده در سمت سرور بزرگتر از عدد صحیح ورژن برنامه کاربر باشد آپدیت اجباری ست می شود
                    if (Sahih_VersionAppServer > Sahih_VersionAppUser) {
                        forcedUpdate = ForcedUpdate.Forced;
                    }
                    //اگر شرط بالا درست نباشد آپدیت برنامه اختیاری می شود
                    else {
                        forcedUpdate = ForcedUpdate.Optional;
                    }
                }
                //در اینجا اگر شرط بالا درست نباشد لازم نبودن آپدیت برنامه ست می شود
                else {
                    forcedUpdate = ForcedUpdate.ItIsNotNecessary;
                }

                //در اینجا چک می شود که آپدیت لازم است یا خیراگر لازم باشد شرط زیر اجرا می شود
                if (forcedUpdate != ForcedUpdate.ItIsNotNecessary) {

                    int TypeToUpdate = VersionApp.getInt("Code");
                    String Url2 = VersionApp.getString("Redirect");

                    //اگر آپدیت اجباری باشد کد زیر اجرا می شود
                    if (forcedUpdate == ForcedUpdate.Forced) {

                        progressBar.setVisibility(View.INVISIBLE);

                        new AlertDialog.Builder(getActivity())
                                .setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Message) + "</font>")))
                                .setMessage((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.TextNewVersionApp) + "</font>")))
                                .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Ok) + "</font>")),
                                        (dialog, which) -> {
                                            if (ClearCatch) {
                                                clearApplicationData();
                                            }
                                            GoToUpdate(TypeToUpdate, Url2);
                                        })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setCancelable(true)
                                .show();

                    }
                    //اگر آپدیت اختیاری باشد کد زیر اجرا می شود
                    else {
                        new AlertDialog.Builder(getActivity())
                                .setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Message) + "</font>")))
                                .setMessage((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.TextNewVersionApp2) + "</font>")))
                                .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Ok) + "</font>")),
                                        (dialog, which) -> {
                                            if (ClearCatch) {
                                                clearApplicationData();
                                            }

                                            progressBar.setVisibility(View.INVISIBLE);

                                            GoToUpdate(TypeToUpdate, Url2);

                                            getActivity().getSupportFragmentManager()
                                                    .beginTransaction().setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                                                    .replace(R.id.Frame_Main, ((MainActivity) getActivity()).fragment_home, Fragment_Home.TAG).commit();
                                        })
                                .setNegativeButton(R.string.NotNo, (dialogInterface, i) -> SetVersionSql(VersionSqls, MyVersionSql))
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setCancelable(true)
                                .show();
                    }
                }
                //اگر آپدیت لازم نباشد در کد زیر کویری های دیتابیس را ست می کند
                else {
                    SetVersionSql(VersionSqls, MyVersionSql);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            progressBar.setVisibility(View.INVISIBLE);


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

            img_logo.setVisibility(View.INVISIBLE);
            lbl_KarSan.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            img_Reload.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), errorText, Toast.LENGTH_SHORT).show();

        });

        AppController.getInstance().addToRequestQueue(SetTimeOut(request));
    }

    void SetVersionSql(JSONArray VersionSqls, float MyVersionSql) {
        //در اینجا اگر آپدیت اجباری نباشد شرط زیر اجرا می شود و ادامه برنامه اجرا می شود و اگر شرط درست نباشد کاربر در صفحه اسپلش باقی می ماند
        if (forcedUpdate != ForcedUpdate.Forced) {

            //در اینجا کویری های دیتابیس که از سرور ارسال شده اند ست می شوند
            float version = 0;
            for (int i = 0; i < VersionSqls.length(); i++) {
                JSONObject object = VersionSqls.optJSONObject(i);

                try {
                    version = Float.valueOf(object.getString("Version"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //در اینجا اگر ورژن اسکیوال ارسال شده از سمت سرور از ورژن فعلی کاربر بزرگتر باشد کویری ارسال شده در دیتابیس ست می شود
                if (version > MyVersionSql) {
                    try {
                        ((MainActivity) getActivity()).dbAdapter.ExecuteQ(object.getString("Query"));
                    } catch (Exception e) {

                    }
                }
            }

            //در اینجا ورژن اسکیوال در دیتابیس ویرایش می شود
            if (version > MyVersionSql) {
                tbl_setting.UpdateSqlVersion(version);
            }

            progressBar.setVisibility(View.INVISIBLE);

            getActivity().getSupportFragmentManager()
                    .beginTransaction().setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .replace(R.id.Frame_Main, ((MainActivity) getActivity()).fragment_home, Fragment_Home.TAG).commit();
        }
    }

    // با فراخوانی این متد از طریق وبسایت یا گوگل پلی یا بازار برنامه آپدیت می شود
    void GoToUpdate(int type, String Url) {

        switch (type) {
            //اگر عدد یک باشد از طریق گوگل پلی برنامه آپدیت می شود
            case 1:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri.Builder uriBuilder = Uri.parse(Url)
                        .buildUpon()
                        .appendQueryParameter("id", "ir.tdaapp.karsan")
                        .appendQueryParameter("launch", "false");

                intent.setData(uriBuilder.build());
                intent.setPackage("com.android.vending");
                startActivity(intent);
                break;

            //اگر عدد 2 باشد از طریق وب سایت برنامه آپدیت می شود
            case 2:
                Uri marketUri = Uri.parse(Url);
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(marketIntent);
                break;

            //اگر عدد 3 باشد از طریق بازار برنامه آپدیت می شود
            case 3:
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.setData(Uri.parse(Url));
                intent2.setPackage("com.farsitel.bazaar");
                startActivity(intent2);
                break;
        }
    }

    //برای پاک کردن حافظه دیتابیس
    public void clearApplicationData() {
        File cacheDirectory = getActivity().getCacheDir();
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    deleteFile(new File(applicationDirectory, fileName));
                }
            }
        }
    }

    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    deletedAll = deleteFile(new File(file, children[i])) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }

        return deletedAll;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_Reload:
                DateTime();
                break;
        }
    }
}
