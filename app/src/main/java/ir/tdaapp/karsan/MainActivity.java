package ir.tdaapp.karsan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flurry.android.FlurryAgent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import ir.tdaapp.karsan.Adapter.DBAdapter;
import ir.tdaapp.karsan.DataBase.Tbl_User;
import ir.tdaapp.karsan.Enum.BottomBarItems;
import ir.tdaapp.karsan.Utility.Internet;
import ir.tdaapp.karsan.ViewModel.VM_SearchFilter;
import ir.tdaapp.karsan.Views.Dialog.Dialog_Filter_Search;
import ir.tdaapp.karsan.Views.Fragment_Activation_Code;
import ir.tdaapp.karsan.Views.Fragment_Add_CV;
import ir.tdaapp.karsan.Views.Fragment_Add_Item;
import ir.tdaapp.karsan.Views.Fragment_CV;
import ir.tdaapp.karsan.Views.Fragment_Contact_US;
import ir.tdaapp.karsan.Views.Fragment_Edit_Profile;
import ir.tdaapp.karsan.Views.Fragment_Favorites_Item;
import ir.tdaapp.karsan.Views.Fragment_Home;
import ir.tdaapp.karsan.Views.Fragment_Login_Home;
import ir.tdaapp.karsan.Views.Fragment_My_CV;
import ir.tdaapp.karsan.Views.Fragment_My_Items;
import ir.tdaapp.karsan.Views.Fragment_My_Karsan;
import ir.tdaapp.karsan.Views.Fragment_News;
import ir.tdaapp.karsan.Views.Fragment_Rules_And_Support;
import ir.tdaapp.karsan.Views.Fragment_Show_Details_CV;
import ir.tdaapp.karsan.Views.Fragment_Show_Details_Favorite;
import ir.tdaapp.karsan.Views.Fragment_Show_Details_Item;
import ir.tdaapp.karsan.Views.Fragment_Show_Details_News;
import ir.tdaapp.karsan.Views.Fragment_Show_National_Code;
import ir.tdaapp.karsan.Views.Fragment_Splash;
import ir.tdaapp.karsan.Views.Fragment_Wallet;

public class MainActivity extends AppCompatActivity {

    public LinearLayout BottomBar;
    public FrameLayout Frame_Main;
    public DBAdapter dbAdapter;
    public Internet internet;
    public Tbl_User tbl_user;

    public ImageView BottomBar_Home, BottomBar_News, BottomBar_Add, BottomBar_CV, BottomBar_Profile;

    //در اینجا فیلتر های جستجو ست می شوند
    public VM_SearchFilter SearchFilter;

    public Fragment_Home fragment_home;
    public Fragment_News fragment_news;
    public Fragment_CV fragment_cv;
    public Fragment_My_Karsan fragment_my_karsan;

    ///در اینجا صفحه جاری در میان صفحات رزومه و خبر و ایتم و پروفایل را نگه داری می کند
    BottomBarItems which_Page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .build(this, "KYD3X3WVPRQ3RHHVPGK8");

        FindItem();

        OnClick();

        Fragment_Splash fragment_splash=new Fragment_Splash();

        if (fragment_splash!=null){
            getSupportFragmentManager().beginTransaction().add(R.id.Frame_Main, fragment_splash).commit();
        }
    }

    void FindItem() {
        BottomBar = findViewById(R.id.BottomBar);
        BottomBar_Home = findViewById(R.id.BottomBar_Home);
        BottomBar_News = findViewById(R.id.BottomBar_News);
        BottomBar_Add = findViewById(R.id.BottomBar_Add);
        BottomBar_CV = findViewById(R.id.BottomBar_CV);
        BottomBar_Profile = findViewById(R.id.BottomBar_Profile);
        Frame_Main = findViewById(R.id.Frame_Main);
        dbAdapter = new DBAdapter(this);
        internet = new Internet(this);
        SearchFilter = new VM_SearchFilter();

        fragment_home = new Fragment_Home();
        fragment_news = new Fragment_News();
        fragment_cv = new Fragment_CV();
        fragment_my_karsan = new Fragment_My_Karsan();

        //در اینجا پیش فرض صفحه ایتم ها نگهداری می شود
        which_Page = BottomBarItems.Home;

        tbl_user=new Tbl_User(dbAdapter);
    }

    @Override
    public void onBackPressed() {
        Fragment fragmentInFrame = getSupportFragmentManager().findFragmentById(R.id.Frame_Main);

        ///در اینجا اگر کاربر در یکی ازصفحات رزومه یا اخبار یا کارسان من باشد با زدن دکمه بازگشت به صفحه آیتم ها ارسال می شوند
        if (fragmentInFrame instanceof Fragment_My_Karsan
                || fragmentInFrame instanceof Fragment_CV
                || fragmentInFrame instanceof Fragment_News
                || fragmentInFrame instanceof Fragment_Home) {
            if (which_Page==BottomBarItems.Home){

                try{

                    //در اینجا اگر کاربر در صفحه اصلی باشد و دکمه جستجو را فشار دهد ابتدا چک می کند که کاربر قبلا جستجو کرده است و اگر شرط درست باشد فیلتر های جستجو را خالی کرده و تمام داده ها را برگشت می دهد در غیر اینصورت از برنامه خارج می شود
                    if (UseSearched()){

                        Fragment_Home fragment_home=(Fragment_Home)getSupportFragmentManager().findFragmentByTag(Fragment_Home.TAG);
                        SearchFilter=new VM_SearchFilter();
                        fragment_home.Set_txt_Search("");
                        fragment_home.SetItems();
                    }else{
                        super.onBackPressed();
                    }

                }catch (Exception e){
                    super.onBackPressed();
                }
            }else{
                GetPage(BottomBarItems.Home);
            }
        } else {
            if (fragmentInFrame instanceof Fragment_Show_Details_Item) {
                GetPage(BottomBarItems.Home);
            } else if (fragmentInFrame instanceof Fragment_Show_Details_News) {
                GetPage(BottomBarItems.News);
            } else if (fragmentInFrame instanceof Fragment_Show_Details_CV) {
                GetPage(BottomBarItems.CV);
            } else if (fragmentInFrame instanceof Fragment_Edit_Profile) {
                GetPage(BottomBarItems.Profile);
            } else if (fragmentInFrame instanceof Fragment_Favorites_Item) {
                GetPage(BottomBarItems.Profile);
            }else if (fragmentInFrame instanceof Fragment_My_Items) {
                GetPage(BottomBarItems.Profile);
            }else if (fragmentInFrame instanceof Fragment_My_CV){
                GetPage(BottomBarItems.Profile);
            }else if (fragmentInFrame instanceof Fragment_Wallet){
                GetPage(BottomBarItems.Profile);
            }else if (fragmentInFrame instanceof Fragment_Rules_And_Support){
                GetPage(BottomBarItems.Profile);
            }else if (fragmentInFrame instanceof Fragment_Contact_US){
                GetPage(BottomBarItems.Profile);
            }else if (fragmentInFrame instanceof Fragment_Show_National_Code){
                GetPage(BottomBarItems.Profile);
            }else if (fragmentInFrame instanceof Fragment_Show_Details_Favorite){
                Fragment f=getSupportFragmentManager().findFragmentByTag(Fragment_Favorites_Item.TAG);
                if (f!=null){
                    ((Fragment_Favorites_Item)f).RefreshPage();
                }
            }

            super.onBackPressed();

            Fragment fragmentInFrame2 = getSupportFragmentManager().findFragmentById(R.id.Frame_Main);

            if (fragmentInFrame2 instanceof Fragment_My_Karsan
                    || fragmentInFrame2 instanceof Fragment_Home
                    || fragmentInFrame2 instanceof Fragment_CV
                    || fragmentInFrame2 instanceof Fragment_News) {

                if (fragmentInFrame instanceof Fragment_Activation_Code && which_Page == BottomBarItems.Profile) {
                    Fragment_My_Karsan fragment_my_karsan=(Fragment_My_Karsan) getSupportFragmentManager().findFragmentByTag(Fragment_My_Karsan.TAG);
                    if (fragment_my_karsan!=null){
                        fragment_my_karsan.SetText();
                    }

                    GetPage(BottomBarItems.Profile);
                }
                if (fragmentInFrame instanceof Fragment_Login_Home && which_Page == BottomBarItems.Profile) {
                    GetPage(BottomBarItems.Profile);
                }

                if (fragmentInFrame instanceof Fragment_Activation_Code && which_Page == BottomBarItems.Home) {
                    Fragment_My_Karsan fragment_my_karsan=(Fragment_My_Karsan) getSupportFragmentManager().findFragmentByTag(Fragment_My_Karsan.TAG);
                    if (fragment_my_karsan!=null){
                        fragment_my_karsan.SetText();
                    }
                    GetPage(BottomBarItems.Home);
                }
                if (fragmentInFrame instanceof Fragment_Login_Home && which_Page == BottomBarItems.Home) {
                    GetPage(BottomBarItems.Home);
                }


                if (fragmentInFrame instanceof Fragment_Activation_Code && which_Page == BottomBarItems.News) {
                    Fragment_My_Karsan fragment_my_karsan=(Fragment_My_Karsan) getSupportFragmentManager().findFragmentByTag(Fragment_My_Karsan.TAG);
                    if (fragment_my_karsan!=null){
                        fragment_my_karsan.SetText();
                    }
                    GetPage(BottomBarItems.News);
                }
                if (fragmentInFrame instanceof Fragment_Login_Home && which_Page == BottomBarItems.News) {
                    GetPage(BottomBarItems.News);
                }

                if (fragmentInFrame instanceof Fragment_Activation_Code && which_Page == BottomBarItems.CV) {
                    Fragment_My_Karsan fragment_my_karsan=(Fragment_My_Karsan) getSupportFragmentManager().findFragmentByTag(Fragment_My_Karsan.TAG);
                    if (fragment_my_karsan!=null){
                        fragment_my_karsan.SetText();
                    }
                    GetPage(BottomBarItems.CV);
                }
                if (fragmentInFrame instanceof Fragment_Login_Home && which_Page == BottomBarItems.CV) {
                    GetPage(BottomBarItems.CV);
                }

                ////////////////////////////////////////////////////////////////////
                if (fragmentInFrame instanceof Fragment_Add_Item && which_Page == BottomBarItems.CV) {
                    GetPage(BottomBarItems.CV);
                }
                if (fragmentInFrame instanceof Fragment_Add_Item && which_Page == BottomBarItems.CV) {
                    GetPage(BottomBarItems.CV);
                }

                if (fragmentInFrame instanceof Fragment_Add_Item && which_Page == BottomBarItems.Home) {
                    GetPage(BottomBarItems.Home);
                }
                if (fragmentInFrame instanceof Fragment_Add_Item && which_Page == BottomBarItems.Home) {
                    GetPage(BottomBarItems.Home);
                }

                if (fragmentInFrame instanceof Fragment_Add_Item && which_Page == BottomBarItems.News) {
                    GetPage(BottomBarItems.News);
                }
                if (fragmentInFrame instanceof Fragment_Add_Item && which_Page == BottomBarItems.News) {
                    GetPage(BottomBarItems.News);
                }

                if (fragmentInFrame instanceof Fragment_Add_Item && which_Page == BottomBarItems.Profile) {
                    GetPage(BottomBarItems.Profile);
                }
                if (fragmentInFrame instanceof Fragment_Add_Item && which_Page == BottomBarItems.Profile) {
                    GetPage(BottomBarItems.Profile);
                }
                //////////////////////////////////////////////////////////
                if (fragmentInFrame instanceof Fragment_Add_CV && which_Page == BottomBarItems.CV) {
                    GetPage(BottomBarItems.CV);
                }
                if (fragmentInFrame instanceof Fragment_Add_CV && which_Page == BottomBarItems.CV) {
                    GetPage(BottomBarItems.CV);
                }

                if (fragmentInFrame instanceof Fragment_Add_CV && which_Page == BottomBarItems.Home) {
                    GetPage(BottomBarItems.Home);
                }
                if (fragmentInFrame instanceof Fragment_Add_CV && which_Page == BottomBarItems.Home) {
                    GetPage(BottomBarItems.Home);
                }

                if (fragmentInFrame instanceof Fragment_Add_CV && which_Page == BottomBarItems.News) {
                    GetPage(BottomBarItems.News);
                }
                if (fragmentInFrame instanceof Fragment_Add_CV && which_Page == BottomBarItems.News) {
                    GetPage(BottomBarItems.News);
                }

                if (fragmentInFrame instanceof Fragment_Add_CV && which_Page == BottomBarItems.Profile) {
                    GetPage(BottomBarItems.Profile);
                }
                if (fragmentInFrame instanceof Fragment_Add_CV && which_Page == BottomBarItems.Profile) {
                    GetPage(BottomBarItems.Profile);
                }
            } else {
                if (fragmentInFrame instanceof Fragment_Show_Details_Item && fragmentInFrame2 instanceof Fragment_Show_Details_Item) {
                    GetPage(BottomBarItems.Home);
                    super.onBackPressed();
                }
            }
        }
    }

    public void ShowBottomBar(boolean show) {

        FrameLayout layout = Frame_Main;

        if (show == false) {

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            layout.setLayoutParams(params);

            BottomBar.setVisibility(View.GONE);

        } else {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
            params.setMargins(0, 0, 0, 35);
            layout.setLayoutParams(params);

            BottomBar.setVisibility(View.VISIBLE);
        }
    }

    public void FucosBottomBar(BottomBarItems items) {

        if (items == BottomBarItems.Home) {
            BottomBar_Home.setImageDrawable(getResources().getDrawable(R.drawable.home_selected));
            BottomBar_News.setImageDrawable(getResources().getDrawable(R.drawable.news));
            BottomBar_CV.setImageDrawable(getResources().getDrawable(R.drawable.cv));
            BottomBar_Profile.setImageDrawable(getResources().getDrawable(R.drawable.profile));
        } else if (items == BottomBarItems.News) {
            BottomBar_Home.setImageDrawable(getResources().getDrawable(R.drawable.home));
            BottomBar_News.setImageDrawable(getResources().getDrawable(R.drawable.news_selected));
            BottomBar_CV.setImageDrawable(getResources().getDrawable(R.drawable.cv));
            BottomBar_Profile.setImageDrawable(getResources().getDrawable(R.drawable.profile));
        } else if (items == BottomBarItems.CV) {
            BottomBar_Home.setImageDrawable(getResources().getDrawable(R.drawable.home));
            BottomBar_News.setImageDrawable(getResources().getDrawable(R.drawable.news));
            BottomBar_CV.setImageDrawable(getResources().getDrawable(R.drawable.cv_selected));
            BottomBar_Profile.setImageDrawable(getResources().getDrawable(R.drawable.profile));
        } else if (items == BottomBarItems.Profile) {
            BottomBar_Home.setImageDrawable(getResources().getDrawable(R.drawable.home));
            BottomBar_News.setImageDrawable(getResources().getDrawable(R.drawable.news));
            BottomBar_CV.setImageDrawable(getResources().getDrawable(R.drawable.cv));
            BottomBar_Profile.setImageDrawable(getResources().getDrawable(R.drawable.profile_selected));
        }

    }

    void OnClick() {
        BottomBar_Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragmentInFrame = getSupportFragmentManager().findFragmentById(R.id.Frame_Main);

                //در اینجا چک می کند که فیلتر جستجو بازشده است اگر شرط درست باشد فیلتر جستجو را مخفی می کند
                if (fragmentInFrame instanceof Dialog_Filter_Search) {
                    onBackPressed();
                } else {
                    //در اینجا چک می شود که اگر کاربر در صفحه ایتم ها روی باتوم بار ایتم کلیک کند شرط زیر اجرا نمی شود
                    if (which_Page != BottomBarItems.Home) {
                        //در اینجا صفحه مورد نظر کاربر نمایش داده می شود
                        GetPage(BottomBarItems.Home);
                    }
                }
            }
        });

        BottomBar_News.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragmentInFrame = getSupportFragmentManager().findFragmentById(R.id.Frame_Main);

                if (fragmentInFrame instanceof Dialog_Filter_Search) {
                    onBackPressed();
                } else {
                    if (which_Page != BottomBarItems.News) {
                        GetPage(BottomBarItems.News);
                    }
                }
            }
        });

        BottomBar_CV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragmentInFrame = getSupportFragmentManager().findFragmentById(R.id.Frame_Main);

                if (fragmentInFrame instanceof Dialog_Filter_Search) {
                    onBackPressed();
                } else {
                    if (which_Page != BottomBarItems.CV) {
                        GetPage(BottomBarItems.CV);
                    }
                }

            }
        });

        BottomBar_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragmentInFrame = getSupportFragmentManager().findFragmentById(R.id.Frame_Main);

                if (fragmentInFrame instanceof Dialog_Filter_Search) {
                    onBackPressed();
                } else {
                    if (which_Page != BottomBarItems.Profile) {
                        GetPage(BottomBarItems.Profile);
                    }
                }

            }
        });

        BottomBar_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragmentInFrame = getSupportFragmentManager().findFragmentById(R.id.Frame_Main);
                if (fragmentInFrame instanceof Dialog_Filter_Search) {
                    onBackPressed();
                }else {
                    final CharSequence[] options = {"درج آگهی", "درج رزومه", "انصراف"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle((Html.fromHtml("<font color='#FF7F27'>"+getResources().getString(R.string.SelectedOneItem)+"</font>")));
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (options[i].equals("درج آگهی")){
                                boolean HasAccount=tbl_user.HasAccount();

                                if (HasAccount){
                                    getSupportFragmentManager().beginTransaction()
                                            .add(R.id.Frame_Main,new Fragment_Add_Item())
                                            .addToBackStack(null).commit();
                                }else{
                                    getSupportFragmentManager().beginTransaction()
                                            .add(R.id.Frame_Main,new Fragment_Login_Home())
                                            .addToBackStack(null).commit();
                                }
                            }else if (options[i].equals("درج رزومه")){
                                boolean HasAccount=tbl_user.HasAccount();

                                if (HasAccount){
                                    getSupportFragmentManager().beginTransaction()
                                            .add(R.id.Frame_Main,new Fragment_Add_CV())
                                            .addToBackStack(null).commit();
                                }else{
                                    getSupportFragmentManager().beginTransaction()
                                            .add(R.id.Frame_Main,new Fragment_Login_Home())
                                            .addToBackStack(null).commit();
                                }
                            }
                        }
                    });

                    builder.show();
                }
            }
        });
    }

    //در اینجا کاربر هر دکمه ی باتوم بار را فشار دهد صفحه مربوط به آن نمایش داده می شود
    public void GetPage(BottomBarItems items) {

        if (items == BottomBarItems.Home) {

            Fragment fragment = getSupportFragmentManager().findFragmentByTag(Fragment_Home.TAG);

            if (fragment != null) {
                HidePage(items);
            } else {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                        .add(R.id.Frame_Main, fragment_home, Fragment_Home.TAG).commit();

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                        .show(fragment_home).commit();
            }

            which_Page = BottomBarItems.Home;
            ShowBottomBar(true);
            FucosBottomBar(BottomBarItems.Home);

        } else if (items == BottomBarItems.News) {

            Fragment fragment = getSupportFragmentManager().findFragmentByTag(Fragment_News.TAG);

            if (fragment != null) {
                HidePage(items);
            } else {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                        .add(R.id.Frame_Main, fragment_news, Fragment_News.TAG).commit();

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                        .show(fragment_news).commit();
            }

            which_Page = BottomBarItems.News;
            ShowBottomBar(true);
            FucosBottomBar(BottomBarItems.News);
        } else if (items == BottomBarItems.CV) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(Fragment_CV.TAG);

            if (fragment != null) {
                HidePage(items);
            } else {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                        .add(R.id.Frame_Main, fragment_cv, Fragment_CV.TAG).commit();

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                        .show(fragment_cv).commit();
            }

            which_Page = BottomBarItems.CV;
            ShowBottomBar(true);
            FucosBottomBar(BottomBarItems.CV);
        } else if (items == BottomBarItems.Profile) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(Fragment_My_Karsan.TAG);

            if (fragment != null) {
                HidePage(items);
            } else {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                        .add(R.id.Frame_Main, fragment_my_karsan, Fragment_My_Karsan.TAG).commit();


                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                        .show(fragment_my_karsan).commit();
            }

            which_Page = BottomBarItems.Profile;
            ShowBottomBar(true);
            FucosBottomBar(BottomBarItems.Profile);
        }
    }

    //در اینجا فرض می کنیم کاربر دکمه اخبار را انتخاب کرده است بقیه صفحات مخفی می شوند و اخبار نمایش داده می شود
    public void HidePage(BottomBarItems item) {
        if (item == BottomBarItems.Home) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .show(fragment_home).commit();

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .hide(fragment_news).commit();

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .hide(fragment_cv).commit();

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .hide(fragment_my_karsan).commit();

        } else if (item == BottomBarItems.News) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .show(fragment_news).commit();


            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .hide(fragment_home).commit();

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .hide(fragment_cv).commit();

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .hide(fragment_my_karsan).commit();

        } else if (item == BottomBarItems.CV) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .show(fragment_cv).commit();

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .hide(fragment_home).commit();

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .hide(fragment_news).commit();

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .hide(fragment_my_karsan).commit();
        } else if (item == BottomBarItems.Profile) {

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .show(fragment_my_karsan).commit();

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .hide(fragment_home).commit();

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .hide(fragment_news).commit();

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .hide(fragment_cv).commit();
        }
    }

    public boolean UseSearched(){

        boolean val=false;

        try {
            val=SearchFilter.IsEnabledSearched();

            if (!val){
                Fragment_Home fragment_home=(Fragment_Home) getSupportFragmentManager().findFragmentByTag(Fragment_Home.TAG);
                val=fragment_home.Check_txt_search();
            }

        }catch (Exception e){}

        return val;
    }

}
