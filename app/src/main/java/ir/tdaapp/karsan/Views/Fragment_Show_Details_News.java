package ir.tdaapp.karsan.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ir.tdaapp.karsan.Enum.BottomBarItems;
import ir.tdaapp.karsan.MainActivity;
import ir.tdaapp.karsan.R;
import ir.tdaapp.karsan.Utility.AppController;
import ir.tdaapp.karsan.Utility.BaseFragment;

public class Fragment_Show_Details_News extends BaseFragment {

    public static final String TAG = "Fragment_Show_Details_News";

    ShimmerFrameLayout Loading;
    WebView lbl_TextNews;
    TextView lbl_Title, lbl_DateInsert, lbl_News_Summary;
    ImageView img_News, Share;
    JsonObjectRequest request;
    RequestQueue requestQueue;
    LinearLayout Back;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_details_news, container, false);

        FindItem(view);
        SetNews();
        OnClick();

        return view;
    }

    void FindItem(View view) {
        lbl_Title = view.findViewById(R.id.lbl_Title);
        lbl_DateInsert = view.findViewById(R.id.lbl_DateInsert);
        lbl_News_Summary = view.findViewById(R.id.lbl_News_Summary);
        lbl_TextNews = view.findViewById(R.id.lbl_TextNews);

        //در اینجا اگر عرض اطلاعات بیشتر از عرض صفحه باشد از اسکرول هوریزنتال آن جلوگیری می کند
        lbl_TextNews.getSettings().setLoadWithOverviewMode(true);
        lbl_TextNews.getSettings().setUseWideViewPort(true);
        lbl_TextNews.setOnTouchListener((v, event) -> (event.getAction() == MotionEvent.ACTION_MOVE));

        Loading = view.findViewById(R.id.Loading);
        img_News = view.findViewById(R.id.img_News);
        Back = view.findViewById(R.id.Back);
        Share = view.findViewById(R.id.Share);
    }

    void SetNews() {
        Loading.startShimmerAnimation();
        Share.setEnabled(false);

        String Url = Api + "News/GetOneNews?Id=" + getArguments().getInt("Id");

        request = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Loading.stopShimmerAnimation();

                    lbl_Title.setText(response.getString("Title"));
                    lbl_DateInsert.setText(response.getString("DateInsert"));
                    lbl_News_Summary.setText(response.getString("AbstractNews"));

                    String Text = "<html>";
                    Text += "<head>";
                    Text += "<meta name='viewport' content='width=device-width' />";
                    Text += "<link href='" + ApiStyles + "wwwroot/Styles/bootstrap-theme.css' rel='stylesheet'>";
                    Text += "<link href='" + ApiStyles + "wwwroot/Styles/bootstrap3-wysihtml5.min.css' rel='stylesheet'>";
                    Text += "<link href='" + ApiStyles + "wwwroot/Styles/rtl.css' rel='stylesheet'>";
                    Text += "<style>*{width:auto!important}</style>";
                    Text += "</head>";
                    Text += "<body>";
                    Text += "<div class='col-xs-12 Padding_0'>";
                    Text += response.getString("TextNews");
                    Text += "</div>";
                    Text += "</body>";
                    Text += "</html>";

                    Text = Text.replace("<img", "<img class='img-responsive'");

                    Text = Text.replace("<table", "<table class='table table-hover table-responsive'");

                    lbl_TextNews.loadData(Text, "text/html; charset=UTF-8", null);

                    Share.setEnabled(true);

                    Glide.with(getActivity())
                            .load(ApiImage_News + response.getString("ImageNews"))
                            .placeholder(R.drawable.news_holder)
                            .error(R.drawable.error_image_news)
                            .into(img_News);

                } catch (Exception e) {
                    Loading.stopShimmerAnimation();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Loading.stopShimmerAnimation();
                Share.setEnabled(false);

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
                                SetNews();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        AppController.getInstance().addToRequestQueue(SetTimeOut(request));
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowBottomBar(false);
    }

    void OnClick() {

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        Share.setOnClickListener(view -> {

            String Text = "";
            Text += getResources().getString(R.string.Titr) + lbl_Title.getText() + "\n\n";
            Text += getResources().getString(R.string.AbsuluteNews) + lbl_News_Summary.getText() + "\n\n";
            Text += getResources().getString(R.string.Date) + lbl_DateInsert.getText() + "\n\n";
            Text += getResources().getString(R.string.ReadMoreNewsKarsan);
            Text+="\n\n https://play.google.com/store/apps/details?id=ir.tdaapp.karsan&hl=en";

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Text);
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.Share)));

        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        requestQueue = Volley.newRequestQueue(getContext());

        request.setTag(TAG);

        requestQueue.add(request);

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}
