package ir.tdaapp.karsan.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import de.hdodenhof.circleimageview.CircleImageView;
import ir.tdaapp.karsan.DataBase.Tbl_Insurance;
import ir.tdaapp.karsan.DataBase.Tbl_Madrak;
import ir.tdaapp.karsan.DataBase.Tbl_Major;
import ir.tdaapp.karsan.Enum.BottomBarItems;
import ir.tdaapp.karsan.MainActivity;
import ir.tdaapp.karsan.R;
import ir.tdaapp.karsan.Services.onDownloadPDF;
import ir.tdaapp.karsan.Utility.AppController;
import ir.tdaapp.karsan.Utility.BaseFragment;
import ir.tdaapp.karsan.Utility.DownloadFile;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Fragment_Show_Details_CV extends BaseFragment {

    public static final String TAG = "Fragment_Show_Details_CV";

    ShimmerFrameLayout Loading;
    TextView lbl_Name, lbl_Major, lbl_Madrak, lbl_Gender, lbl_DateBirth, lbl_Insurance, lbl_Certificate;
    JsonObjectRequest request, DownloadCV;
    Tbl_Madrak tbl_madrak;
    Tbl_Major tbl_major;
    ImageView img_BackGround;
    ImageView img;
    RequestQueue requestQueue;
    LinearLayout Back, CanSeeCV;
    Button btn_Download;
    Tbl_Insurance tbl_insurance;
    String CellPhone = "";
    String PdfName = "";
    Button btn_ShowCv, btn_Call;
    int CVId = 0;
    DownloadFile downloadFile;
    TextView lbl_Date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_details_cv, container, false);

        FindView(view);

        SetVals();

        OnClick(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowBottomBar(false);
    }

    void OnClick(View view) {

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    btn_Download.setVisibility(View.GONE);
                    CanSeeCV.setVisibility(View.GONE);
                }
                return false;
            }
        });

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_Download.setVisibility(View.GONE);
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        btn_ShowCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PdfName.equalsIgnoreCase("")) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ApiPdf + PdfName)));
                    downloadPDF(ApiPdf + PdfName);
                }
            }
        });

        btn_Call.setOnClickListener(view1 -> {
            if (!CellPhone.equalsIgnoreCase("")) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + CellPhone));
                startActivity(intent);
            }
        });

        btn_Download.setOnClickListener(view12 -> {

            if (((MainActivity) getActivity()).tbl_user.HasAccount()) {

                Loading.startShimmerAnimation();
                btn_Download.setEnabled(false);

                String Unique = ((MainActivity) getActivity()).tbl_user.GetUniCode();

                String Url = Api + "CV/GetDownloadCV?Id=" + CVId + "&Unique=" + Unique;

                DownloadCV = new JsonObjectRequest(Request.Method.GET, Url, null, response -> {

                    try {

                        if (response.getBoolean("Resault")) {

                            PdfName = response.getString("PdfName");
                            CellPhone = response.getString("CellPhone");
                            btn_Download.setVisibility(View.GONE);
                            CanSeeCV.setVisibility(View.VISIBLE);

//                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ApiPdf + PdfName)));
                            downloadPDF(ApiPdf + PdfName);
                        } else {

                            Loading.stopShimmerAnimation();
                            btn_Download.setEnabled(true);

                            if (response.getInt("ErrorCode") == 1) {
                                Toast.makeText(getContext(), getResources().getString(R.string.YourWalletBalanceIsNotEnoughToDownload), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), getResources().getString(R.string.ErrorInPrograme), Toast.LENGTH_LONG).show();
                            }
                        }

                    } catch (JSONException e) {

                        Loading.stopShimmerAnimation();
                        btn_Download.setEnabled(true);
                        e.printStackTrace();
                    }

                }, error -> {
                    Loading.stopShimmerAnimation();
                    btn_Download.setEnabled(true);

                    boolean hasInternet = ((MainActivity) getActivity()).internet.HaveNetworkConnection();

                    if (hasInternet == false) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.CheckYourInternet), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.YourInternetIsVerySlow), Toast.LENGTH_SHORT).show();
                    }
                });

                AppController.getInstance().addToRequestQueue(SetTimeOut(DownloadCV));

            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.Create_An_Account_First), Toast.LENGTH_LONG).show();
            }
        });

    }

    //در اینجا اگر پی دی اف در گوشی کاربر باشد آن را نمایش می دهد در غیر این صورت آن را دانلود می کند
    void downloadPDF(String url) {

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), PdfName);

        if (!file.exists()) {
            downloadFile.downloadPDF(url, PdfName, new onDownloadPDF() {
                @Override
                public void onSuccess(String path) {
                    showPDF();
                }

                @Override
                public void onError(Exception e) {

                    Loading.stopShimmerAnimation();
                    btn_Download.setEnabled(true);

                    Toast.makeText(getContext(), getString(R.string.notFoundThisPDF), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            showPDF();
        }
    }

    //در اینجا پی دی اف نمایش داده می شود
    void showPDF() {

        Loading.stopShimmerAnimation();
        btn_Download.setEnabled(true);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), PdfName);

        if (file.exists()) {

            try {
                String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
                Uri uri = FileProvider.getUriForFile(getContext(), getActivity().getApplicationContext().getPackageName() + ".provider", file);

                intent.setDataAndType(uri, mimeType);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(intent, getString(R.string.ChoseApp)));
            } catch (Exception e) {
                Toast.makeText(getContext(), getString(R.string.ErrorInApplication), Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getContext(), getString(R.string.notFoundThisPDF), Toast.LENGTH_SHORT).show();
        }
    }

    void FindView(View view) {
        Loading = view.findViewById(R.id.Loading);
        lbl_Name = view.findViewById(R.id.lbl_Name);
        lbl_Major = view.findViewById(R.id.lbl_Major);
        lbl_Madrak = view.findViewById(R.id.lbl_Madrak);
        lbl_Gender = view.findViewById(R.id.lbl_Gender);
        lbl_DateBirth = view.findViewById(R.id.lbl_DateBirth);
        img_BackGround = view.findViewById(R.id.img_BackGround);
        btn_Download = view.findViewById(R.id.btn_Download);
        Back = view.findViewById(R.id.Back);
        img = view.findViewById(R.id.img_cv);
        lbl_Date = view.findViewById(R.id.lbl_Date);
        lbl_Insurance = view.findViewById(R.id.lbl_Insurance);
        lbl_Certificate = view.findViewById(R.id.lbl_Certificate);
        CanSeeCV = view.findViewById(R.id.CanSeeCV);
        btn_ShowCv = view.findViewById(R.id.btn_ShowCv);
        btn_Call = view.findViewById(R.id.btn_Call);
        tbl_madrak = new Tbl_Madrak(((MainActivity) getActivity()).dbAdapter);
        tbl_major = new Tbl_Major(((MainActivity) getActivity()).dbAdapter);
        tbl_insurance = new Tbl_Insurance(((MainActivity) getActivity()).dbAdapter);
        downloadFile = new DownloadFile();
    }

    void SetVals() {
        Loading.startShimmerAnimation();

        String UniCode = "";

        if (((MainActivity) getActivity()).tbl_user.HasAccount()) {
            UniCode = ((MainActivity) getActivity()).tbl_user.GetUniCode();
        }

        String Url = Api + "CV/" + getArguments().getInt("Id") + "?Unique=" + UniCode;

        request = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Loading.stopShimmerAnimation();

                    lbl_Name.setText(response.getString("Name"));
                    lbl_DateBirth.setText(response.getString("DateBirth"));

                    if (response.get("gender") != null) {
                        int gender = response.getInt("gender");

                        if (gender == 0) {
                            lbl_Gender.setText(getResources().getString(R.string.Man));
                        } else if (gender == 1) {
                            lbl_Gender.setText(getResources().getString(R.string.Women));
                        } else {
                            lbl_Gender.setText(getResources().getString(R.string.ManAndWomen));
                        }
                    }

                    lbl_Madrak.setText(tbl_madrak.GetTitleById(response.getInt("Madrak")));
                    lbl_Major.setText(tbl_major.GetTitleById(response.getInt("Major")));
                    lbl_Insurance.setText(tbl_insurance.GetTitleById(response.getInt("Insurance")));
                    CVId = response.getInt("Id");
                    PdfName = response.getString("PdfName");
                    lbl_Date.setText(getString(R.string.RecordedIn)+response.getString("DateBirth"));


                    //در اینجا گواهینامه ست می شود
                    switch (response.getInt("Certificate")) {
                        case 0:
                            lbl_Certificate.setText(getResources().getString(R.string.DontHaveCertificate));
                            break;
                        case 1:
                            lbl_Certificate.setText(getResources().getString(R.string.GradeOne));
                            break;
                        case 2:
                            lbl_Certificate.setText(getResources().getString(R.string.GradeTwo));
                            break;
                        case 3:
                            lbl_Certificate.setText(getResources().getString(R.string.GradeThird));
                            break;
                        case 4:
                            lbl_Certificate.setText(getResources().getString(R.string.Special));
                            break;
                    }

                    //در اینجا چک می شود که کاربر قبلا این رزومه را خرید کرده است یا نه
                    boolean CanSee = response.getBoolean("CanSeeCV");
                    if (CanSee) {
                        CanSeeCV.setVisibility(View.VISIBLE);
                        CellPhone = response.getString("CellPhone");
                        PdfName = response.getString("PdfName");
                    } else {
                        btn_Download.setVisibility(View.VISIBLE);
                    }

                    String Image = ApiImage_CV + response.getString("ImageUrl");

                    Glide.with(getActivity())
                            .load(Image)
                            .placeholder(R.drawable.cv_image)
                            .error(R.drawable.cv_image)
                            .into(img);

                    Glide.with(getActivity())
                            .load(Image)
                            .error(R.drawable.cv_image)
                            .placeholder(R.drawable.cv_image)
                            .into(img_BackGround);


                } catch (Exception e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                        .setTitle((Html.fromHtml("<font color='#FF7F27'>" + getString(R.string.Error) + "</font>")))
                        .setMessage((Html.fromHtml("<font color='#FF7F27'>" + errorText + "</font>")))
                        .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getString(R.string.Reload) + "</font>")), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SetVals();
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
    public void onDestroy() {
        super.onDestroy();

        requestQueue = Volley.newRequestQueue(getContext());

        request.setTag(TAG);

        requestQueue.add(request);

        if (DownloadCV != null) {
            DownloadCV.setTag(TAG);
            requestQueue.add(DownloadCV);
        }

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }

        if (downloadFile != null) {
            downloadFile.cancel();
        }
    }
}
