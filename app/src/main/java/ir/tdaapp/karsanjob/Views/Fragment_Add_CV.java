package ir.tdaapp.karsanjob.Views;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.obsez.android.lib.filechooser.ChooserDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import ir.tdaapp.karsanjob.MainActivity;
import ir.tdaapp.karsanjob.R;
import ir.tdaapp.karsanjob.Utility.AppController;
import ir.tdaapp.karsanjob.Utility.Base64Image;
import ir.tdaapp.karsanjob.Utility.BaseFragment;
import ir.tdaapp.karsanjob.Utility.CompressImage;

import static android.app.Activity.RESULT_OK;

public class Fragment_Add_CV extends BaseFragment implements View.OnClickListener {

    public static final String TAG="Fragment_Add_CV";

    Button btn_Add;
    LinearLayout Upload,Back;
    TextView lbl_CV;
    ImageView image_item,img_ClearImage;
    CompressImage compressImage;
    JsonObjectRequest PostRequest;
    RequestQueue requestQueue;
    File pdf;
    JsonObjectRequest GetToCheckEditProfile;
    ShimmerFrameLayout Loading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_cv, container, false);

        FindItem(view);
        SetEnabledElements(false);
        CheckToEdit();
        OnClick();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowBottomBar(false);
    }

    void FindItem(View view) {
        btn_Add = view.findViewById(R.id.btn_Add);
        Upload = view.findViewById(R.id.Upload);
        lbl_CV = view.findViewById(R.id.lbl_CV);
        image_item = view.findViewById(R.id.image_item);
        Loading = view.findViewById(R.id.Loading);
        Back = view.findViewById(R.id.Back);
        img_ClearImage = view.findViewById(R.id.img_ClearImage);
        compressImage = new CompressImage(320, 450, 75, getActivity());
    }

    public void openPDF() {

        Dexter.withActivity(getActivity()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(
                new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        new ChooserDialog(getActivity())
                                .withFilterRegex(false, false, ".*\\.(pdf)")
                                .withResources(R.string.title_choose_file, R.string.Select, R.string.Cancel)
                                .withChosenListener(new ChooserDialog.Result() {
                                    @Override
                                    public void onChoosePath(String s, File file) {
                                        pdf=file;
                                        lbl_CV.setText(file.getName());
                                    }
                                })
                        .build().show();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }
        ).check();
    }

    //در اینجا اگر کاربر بخواهد یک عکس را انتخاب کند در اینجا نشان می دهد که از کدام راه یک عکس انتخاب کند مثل دوربین یا گالری
    private void selectImage() {
        final CharSequence[] options = {"دوربین", "از گالری", "انصراف"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle((Html.fromHtml("<font color='#FF7F27'>عکس مورد نظر</font>")));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("دوربین")) {

                    Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).withListener(
                            new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {
                                    String fileName = "temp.jpg";
                                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName));
                                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                        startActivityForResult(takePictureIntent, 3);
                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                }
                            }
                    ).check();


                } else if (options[item].equals("از گالری")) {
                    Dexter.withActivity(getActivity()).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(
                            new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse response) {
                                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI).setType("image/jpg");
                                    startActivityForResult(intent, 2);
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse response) {

                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                                }
                            }
                    ).check();


                } else if (options[item].equals("انصراف")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private Uri getCacheImagePath(String fileName) {
        File path = new File(Environment.getExternalStorageDirectory(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        Uri uri = FileProvider.getUriForFile(getContext(), getActivity().getApplicationContext().getPackageName() + ".provider", image);

        return uri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == RESULT_OK) {

                if (requestCode==3){

                    Uri selectedImage = getCacheImagePath("temp.jpg");

                    try {
                        getActivity().getContentResolver().notifyChange(selectedImage, null);
                        ContentResolver cr = getActivity().getContentResolver();
                        Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                        Bitmap b = compressImage.Compress(decoded);
                        image_item.setImageBitmap(b);
                        image_item.setTag("Selected");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (requestCode==2){
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    final int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    Bitmap b = compressImage.Compress(picturePath);
                    image_item.setImageBitmap(b);
                    image_item.setTag("Selected");
                }
            }

        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Upload:
                openPDF();
                break;
            case R.id.btn_Add:
                AddCv();
                break;
            case R.id.image_item:
                selectImage();
                break;
            case R.id.Back:
                getActivity().onBackPressed();
                break;
            case R.id.img_ClearImage:
                image_item.setImageDrawable(getResources().getDrawable(R.drawable.add_image));
                image_item.setTag("default");
                break;
        }
    }

    void OnClick() {
        Upload.setOnClickListener(this);
        image_item.setOnClickListener(this);
        btn_Add.setOnClickListener(this);
        Back.setOnClickListener(this);
        img_ClearImage.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        requestQueue = Volley.newRequestQueue(getContext());

        GetToCheckEditProfile.setTag(TAG);
        requestQueue.add(GetToCheckEditProfile);

        if (PostRequest!=null){
            PostRequest.setTag(TAG);
            requestQueue.add(PostRequest);
        }

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    //در اینجا یک رزومه جدید اضافه می شود
    void AddCv(){

        if (pdf!=null){

            final ProgressDialog progress = new ProgressDialog(getActivity());
            progress.setTitle((Html.fromHtml("<font color='#FF7F27'>"+getResources().getString(R.string.Sending)+"</font>")));
            progress.setMessage((Html.fromHtml("<font color='#FF7F27'>"+getResources().getString(R.string.PleaseWait)+"</font>")));
            progress.show();

            String UniCode=((MainActivity)getActivity()).tbl_user.GetUniCode();

            String Url=Api+"CV";

            JSONObject object=new JSONObject();
            try {
                object.put("UniCode",UniCode);
                object.put("pdf",Base64Image.PDF_To_Base64(pdf));

                if (!image_item.getTag().toString().equalsIgnoreCase("default")) {
                    object.put("Image", Base64Image.BitmapToBase64(((BitmapDrawable) image_item.getDrawable()).getBitmap()));
                } else {
                    object.put("Image", "");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            PostRequest=new JsonObjectRequest(Request.Method.POST, Url, object, response -> {
                progress.dismiss();

                try {
                    Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                progress.dismiss();
                boolean HasInternet=((MainActivity)getActivity()).internet.HaveNetworkConnection();

                if (!HasInternet){
                    Toast.makeText(getActivity(), getResources().getString(R.string.CheckYourInternet), Toast.LENGTH_LONG).show();
                }else{
                    new AlertDialog.Builder(getActivity())
                            .setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Error) + "</font>")))
                            .setMessage((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.YourInternetIsVerySlow) + "</font>")))
                            .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Ok) + "</font>")),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            AddCv();
                                        }
                                    })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(true)
                            .show();
                }
            });

            AppController.getInstance().addToRequestQueue(SetTimeOuteToPost(PostRequest));

        }else{
            Toast.makeText(getContext(), getResources().getString(R.string.PleaseInputOnePDF), Toast.LENGTH_SHORT).show();
        }
    }

    void CheckToEdit(){
        Loading.startShimmerAnimation();

        String UniCode=((MainActivity)getActivity()).tbl_user.GetUniCode();
        String Url=Api+"User/GetCheckToEditProfile?UniCode="+UniCode;

        GetToCheckEditProfile=new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Loading.stopShimmerAnimation();

                try {
                    boolean resault=response.getBoolean("Resalt");

                    if (resault){
                        SetEnabledElements(true);
                    }else{
                        Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Loading.stopShimmerAnimation();
                boolean HasInternet=((MainActivity)getActivity()).internet.HaveNetworkConnection();

                if (!HasInternet){
                    Toast.makeText(getActivity(), getResources().getString(R.string.CheckYourInternet), Toast.LENGTH_LONG).show();
                }else{
                    new AlertDialog.Builder(getActivity())
                            .setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Error) + "</font>")))
                            .setMessage((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.YourInternetIsVerySlow) + "</font>")))
                            .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Ok) + "</font>")),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            CheckToEdit();
                                        }
                                    })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(true)
                            .show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(SetTimeOut(GetToCheckEditProfile));
    }

    //در اینجا المنت ها را فعال یا غیر فعال می کند
    void SetEnabledElements(boolean enable){
        Upload.setEnabled(enable);
        image_item.setEnabled(enable);
        btn_Add.setEnabled(enable);
    }
}
