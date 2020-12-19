package ir.tdaapp.karsanjob.Utility;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import androidx.fragment.app.Fragment;
import ir.tdaapp.karsanjob.MainActivity;

import static com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES;

/**
 * Created by Diako on 8/27/2019.
 */

public class BaseFragment extends Fragment {

    //1- Volley
    //2- Request
    //3- Fragment_Home


    //این متغیر برای میزان تایم اوت 1 می باشد
    public int TimeOut = 10000;

    //برای میزان زمان پیج کردن اسلایدر در 3
    public int TimePageSlider = 5000;

    //در اینجا برای جلوگیری از تکرار آدرس ثابت سرویس دهنده قرار داده می شود
    public String Api = "http://karsanjob.ir/api/";

    public String ApiStyles = "http://karsanjob.ir/";

    //در اینجا آدرس عکس های ایتم نگهداری می شود
    public String ApiImage_Item = "http://karsanjob.ir/wwwroot/Images/Items/";

    //در اینجا آدرس عکس های اسلایدر نگهداری می شود
    public String ApiImage_Slider = "http://karsanjob.ir/wwwroot/Images/Sliders/";

    //آدرس عکس های اخبار
    public String ApiImage_News = "http://karsanjob.ir/wwwroot/Images/News/";

    //آدرس عکس های رزومه
    public String ApiImage_CV = "http://karsanjob.ir/wwwroot/Images/CVs/";

    //آدرس پی دی اف
    public String ApiPdf = "http://karsanjob.ir/wwwroot/Cvs/";

    //این متقیر برای ست کردن تایم اوت 2 در 1 می باشد
    public StringRequest SetTimeOut(StringRequest request) {
        RetryPolicy policy = new DefaultRetryPolicy(TimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        return request;
    }

    //این متقیر برای ست کردن تایم اوت 2 در 1 می باشد
    public JsonObjectRequest SetTimeOut(JsonObjectRequest request) {
        RetryPolicy policy = new DefaultRetryPolicy(TimeOut, DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        return request;
    }

    //این متقیر برای ست کردن تایم اوت 2 در 1 می باشد
    public JsonArrayRequest SetTimeOut(JsonArrayRequest request) {
        RetryPolicy policy = new DefaultRetryPolicy(TimeOut, DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        return request;
    }

    public CustomRequest SetTimeOut(CustomRequest request) {
        RetryPolicy policy = new DefaultRetryPolicy(TimeOut, DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        return request;
    }

    public StringRequest SetTimeOuteToPost(StringRequest request) {
        RetryPolicy policy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        return request;
    }

    public JsonObjectRequest SetTimeOuteToPost(JsonObjectRequest request) {
        RetryPolicy policy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        return request;
    }

    public JsonArrayRequest SetTimeOuteToPost(JsonArrayRequest request) {
        RetryPolicy policy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        return request;
    }

    public void ShowBottomBar(boolean show) {

        FrameLayout layout = ((MainActivity) getActivity()).Frame_Main;

        if (show == false) {

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            layout.setLayoutParams(params);

            ((MainActivity) getActivity()).BottomBar.setVisibility(View.GONE);

        } else {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
            params.setMargins(0, 0, 0, 45);
            layout.setLayoutParams(params);

            ((MainActivity) getActivity()).BottomBar.setVisibility(View.VISIBLE);
        }
    }

}
