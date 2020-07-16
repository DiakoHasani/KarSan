package ir.tdaapp.karsan.Views;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.tdaapp.karsan.Adapter.MadrakAdapter;
import ir.tdaapp.karsan.Adapter.MajorAdapter;
import ir.tdaapp.karsan.DataBase.Tbl_Job;
import ir.tdaapp.karsan.DataBase.Tbl_Madrak;
import ir.tdaapp.karsan.DataBase.Tbl_Major;
import ir.tdaapp.karsan.DataBase.Tbl_User;
import ir.tdaapp.karsan.Enum.Gender;
import ir.tdaapp.karsan.MainActivity;
import ir.tdaapp.karsan.R;
import ir.tdaapp.karsan.Utility.AppController;
import ir.tdaapp.karsan.Utility.Base64Image;
import ir.tdaapp.karsan.Utility.BaseFragment;
import ir.tdaapp.karsan.Utility.CompressImage;
import ir.tdaapp.karsan.Utility.Replace;
import ir.tdaapp.karsan.Utility.Validation;
import ir.tdaapp.karsan.Utility.txt_Price_Watcher;
import ir.tdaapp.karsan.ViewModel.VM_Gender;
import ir.tdaapp.karsan.ViewModel.VM_Job;
import ir.tdaapp.karsan.ViewModel.VM_Madrak;
import ir.tdaapp.karsan.ViewModel.VM_Major;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.FileProvider.getUriForFile;

public class Fragment_Edit_Item extends BaseFragment implements View.OnClickListener {

    public final static String TAG = "Fragment_Edit_Item";

    Spinner cmb_Madrak, cmb_Major, cmb_JobType, cmb_Gender;
    Tbl_Madrak tbl_madrak;
    Tbl_Major tbl_major;
    Tbl_Job tbl_job;
    Tbl_User tbl_user;
    EditText txt_NationalCode, txt_MinPrice, txt_MaxPrice;
    CompressImage compressImage;
    ImageView image_item;
    RecyclerView RecyclerMajors, RecyclerMadraks;
    MajorAdapter majorAdapter;
    GridLayoutManager gridLayoutManager, gridLayoutManager2;
    MadrakAdapter madrakAdapter;
    EditText txt_Title, txt_WorkingTime, txt_HoursOfWork, txt_Description, txt_Age;
    RadioButton rdo_NoInsurance, rdo_YesInsurance, rdo_HaveNotWorkExperience, rdo_HaveWorkExperience;
    RadioButton rdo_TornTime, rdo_PartTime, rdo_FullTime;
    Button btn_ClearOptions, btn_Record;
    JsonObjectRequest EditRquest, GetDetailsRequest;
    LinearLayout Back;
    int ItemId;
    RequestQueue requestQueue;
    RadioGroup InsuranceGroup, WorkExperienceGroup, timeGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_item, container, false);

        FindItem(view);
        SetSpinner();

        RecyclerMajors.setAdapter(majorAdapter);
        RecyclerMajors.setLayoutManager(gridLayoutManager);

        RecyclerMadraks.setAdapter(madrakAdapter);
        RecyclerMadraks.setLayoutManager(gridLayoutManager2);

        OnClick();

        GetDetailsItem();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        requestQueue = Volley.newRequestQueue(getContext());

        GetDetailsRequest.setTag(TAG);

        requestQueue.add(GetDetailsRequest);

        if (EditRquest != null) {
            EditRquest.setTag(TAG);
            requestQueue.add(EditRquest);
        }

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowBottomBar(false);
    }

    void FindItem(View view) {
        cmb_Madrak = view.findViewById(R.id.cmb_Madrak);
        btn_Record = view.findViewById(R.id.btn_Record);
        btn_ClearOptions = view.findViewById(R.id.btn_ClearOptions);
        rdo_TornTime = view.findViewById(R.id.rdo_TornTime);
        rdo_PartTime = view.findViewById(R.id.rdo_PartTime);
        rdo_FullTime = view.findViewById(R.id.rdo_FullTime);
        rdo_NoInsurance = view.findViewById(R.id.rdo_NoInsurance);
        rdo_YesInsurance = view.findViewById(R.id.rdo_YesInsurance);
        rdo_HaveNotWorkExperience = view.findViewById(R.id.rdo_HaveNotWorkExperience);
        rdo_HaveWorkExperience = view.findViewById(R.id.rdo_HaveWorkExperience);
        txt_Title = view.findViewById(R.id.txt_Title);
        txt_WorkingTime = view.findViewById(R.id.txt_WorkingTime);
        txt_HoursOfWork = view.findViewById(R.id.txt_HoursOfWork);
        txt_Description = view.findViewById(R.id.txt_Description);
        txt_Age = view.findViewById(R.id.txt_Age);
        cmb_Major = view.findViewById(R.id.cmb_Major);
        cmb_JobType = view.findViewById(R.id.cmb_JobType);
        cmb_Gender = view.findViewById(R.id.cmb_Gender);
        txt_NationalCode = view.findViewById(R.id.txt_NationalCode);
        txt_MaxPrice = view.findViewById(R.id.txt_MaxPrice);
        txt_MinPrice = view.findViewById(R.id.txt_MinPrice);
        image_item = view.findViewById(R.id.image_item);
        RecyclerMajors = view.findViewById(R.id.RecyclerMajors);
        RecyclerMadraks = view.findViewById(R.id.RecyclerMadraks);
        Back = view.findViewById(R.id.Back);
        InsuranceGroup = view.findViewById(R.id.InsuranceGroup);
        WorkExperienceGroup = view.findViewById(R.id.WorkExperienceGroup);
        timeGroup = view.findViewById(R.id.timeGroup);

        tbl_madrak = new Tbl_Madrak(((MainActivity) getActivity()).dbAdapter);
        tbl_major = new Tbl_Major(((MainActivity) getActivity()).dbAdapter);
        tbl_job = new Tbl_Job(((MainActivity) getActivity()).dbAdapter);
        tbl_user = new Tbl_User(((MainActivity) getActivity()).dbAdapter);
        compressImage = new CompressImage(320, 450, 75, getActivity());

        majorAdapter = new MajorAdapter(getContext());
        madrakAdapter = new MadrakAdapter(getContext());
        gridLayoutManager = new GridLayoutManager(getContext(), 1, RecyclerView.HORIZONTAL, false);
        gridLayoutManager2 = new GridLayoutManager(getContext(), 1, RecyclerView.HORIZONTAL, false);
    }

    void SetSpinner() {

        //در اینجا اسپینر مدرک ست می شود
        List<VM_Madrak> madraks = tbl_madrak.GetAll();

        VM_Madrak Holder_Madrak = new VM_Madrak();

        Holder_Madrak.setTitle(getResources().getString(R.string.Grade));
        Holder_Madrak.setId(0);

        madraks.add(0, Holder_Madrak);

        ArrayAdapter<VM_Madrak> AdapterMadrak = new ArrayAdapter<VM_Madrak>(getContext(),
                R.layout.spinner_layout, madraks);

        cmb_Madrak.setAdapter(AdapterMadrak);
        //////////////////////////////////////////////////////

        //در اینجا اسپینر رشته تحصیلی ست می شود
        List<VM_Major> majors = tbl_major.GetAll();

        VM_Major HolderMajor = new VM_Major();
        HolderMajor.setId(0);
        HolderMajor.setTitle(getResources().getString(R.string.Major));
        majors.add(0, HolderMajor);

        ArrayAdapter AdapterMajor = new ArrayAdapter(getContext(),
                R.layout.spinner_layout, majors);

        cmb_Major.setAdapter(AdapterMajor);
        ///////////////////////////////////////////////////////

        List<VM_Job> jobs = tbl_job.GetAll();
        VM_Job HolderJob = new VM_Job();
        HolderJob.setId(0);
        HolderJob.setTitle(getResources().getString(R.string.JobType));

        jobs.add(0, HolderJob);

        ArrayAdapter AdapterJobType = new ArrayAdapter(getContext(),
                R.layout.spinner_layout, jobs);

        cmb_JobType.setAdapter(AdapterJobType);
        ///////////////////////////////////////////////////

        //در اینجا اسپینر جنسیت ست می شود
        List<VM_Gender> gender = new ArrayList<>();
        gender.add(new VM_Gender(Gender.both, "جنسیت", 0));
        gender.add(new VM_Gender(Gender.Man, "مرد", 1));
        gender.add(new VM_Gender(Gender.Women, "زن", 2));
        gender.add(new VM_Gender(Gender.both, "هردو", 3));

        ArrayAdapter<VM_Gender> GenderAdapter = new ArrayAdapter<VM_Gender>(getContext()
                , R.layout.spinner_layout, gender);

        cmb_Gender.setAdapter(GenderAdapter);
        ////////////////////////////////////////////////////
    }

    void OnClick() {

        txt_MaxPrice.addTextChangedListener(new txt_Price_Watcher(txt_MaxPrice));
        txt_MinPrice.addTextChangedListener(new txt_Price_Watcher(txt_MinPrice));

        txt_NationalCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    txt_NationalCode.setHint("");
                    txt_NationalCode.setTextDirection(View.TEXT_DIRECTION_LTR);
                    txt_NationalCode.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                } else {
                    if (!txt_NationalCode.getText().toString().equalsIgnoreCase("")) {
                        txt_NationalCode.setHint("");
                        txt_NationalCode.setTextDirection(View.TEXT_DIRECTION_LTR);
                        txt_NationalCode.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    } else {
                        txt_NationalCode.setHint(getResources().getString(R.string.NationalCode2));
                        txt_NationalCode.setTextDirection(View.TEXT_DIRECTION_RTL);
                        txt_NationalCode.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    }
                }
            }
        });

        txt_MinPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    txt_MinPrice.setHint("");
                    txt_MinPrice.setTextDirection(View.TEXT_DIRECTION_LTR);
                    txt_MinPrice.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                } else {
                    if (!txt_MinPrice.getText().toString().equalsIgnoreCase("")) {
                        txt_MinPrice.setHint("");
                        txt_MinPrice.setTextDirection(View.TEXT_DIRECTION_LTR);
                        txt_MinPrice.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    } else {
                        txt_MinPrice.setHint(getResources().getString(R.string.MinPrice2));
                        txt_MinPrice.setTextDirection(View.TEXT_DIRECTION_RTL);
                        txt_MinPrice.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    }
                }
            }
        });

        txt_MaxPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    txt_MaxPrice.setHint("");
                    txt_MaxPrice.setTextDirection(View.TEXT_DIRECTION_LTR);
                    txt_MaxPrice.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                } else {
                    if (!txt_MaxPrice.getText().toString().equalsIgnoreCase("")) {
                        txt_MaxPrice.setHint("");
                        txt_MaxPrice.setTextDirection(View.TEXT_DIRECTION_LTR);
                        txt_MaxPrice.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    } else {
                        txt_MaxPrice.setHint(getResources().getString(R.string.MaxPrice2));
                        txt_MaxPrice.setTextDirection(View.TEXT_DIRECTION_RTL);
                        txt_MaxPrice.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    }
                }
            }
        });

        image_item.setOnClickListener(this);
        btn_Record.setOnClickListener(this);
        btn_ClearOptions.setOnClickListener(this);
        Back.setOnClickListener(this);

        cmb_Major.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                VM_Major v = (VM_Major) adapterView.getSelectedItem();
                if (v.getId() != 0) {
                    if (majorAdapter.getItemCount() < 3) {
                        majorAdapter.Add(v);
                    }
                }
                cmb_Major.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        cmb_Madrak.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                VM_Madrak v = (VM_Madrak) adapterView.getSelectedItem();
                if (v.getId() != 0) {
                    if (madrakAdapter.getItemCount() < 3) {
                        madrakAdapter.Add(v);
                    }
                }
                cmb_Madrak.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_item:
                selectImage();
                break;
            case R.id.btn_Record:
                if (IsValid()) {
                    EditItem();
                }
                break;
            case R.id.btn_ClearOptions:
                ClearItems();
                break;
            case R.id.Back:
                getActivity().onBackPressed();
                break;
        }
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
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {
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

            } else if (requestCode == 2) {
                try {
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
                } catch (Exception e) {
                }
            }
        }
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
                                        startActivityForResult(takePictureIntent, 1);
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

    //در اینجا چک می کند که ورودی های کاربر ولید هستند
    public boolean IsValid() {
        boolean Valid = true;

        //در اینجا چک می شود که سرتیتر خبر خالی نباشد
        Valid = !Validation.Required(txt_Title, getResources().getString(R.string.ThisValueMustBeFilled));
        if (!Valid)
            return false;

        //در اینجا زمان کار چک می شود
        Valid = !Validation.Required(txt_WorkingTime, getResources().getString(R.string.ThisValueMustBeFilled));
        if (!Valid)
            return false;

        //در اینجا تعداد ساعت کار در روز چک می شود
        Valid = !Validation.Required(txt_HoursOfWork, getResources().getString(R.string.ThisValueMustBeFilled));
        if (!Valid)
            return false;

        //در اینجا حداکثر حقوق چک می شود
        Valid = !Validation.Required(txt_MaxPrice, getResources().getString(R.string.ThisValueMustBeFilled));
        if (!Valid)
            return false;

        //در اینجا توضیحات چک می شود
        Valid = !Validation.Required(txt_Description, getResources().getString(R.string.ThisValueMustBeFilled));
        if (!Valid)
            return false;

        //در اینجا معتبر بودن کد ملی چک می شود
        Valid = !Validation.NationalCode(getResources().getString(R.string.NationalCodeNotValid), txt_NationalCode);

        if (!Valid)
            return false;

        //در اینجا چک می شود که کاربر یک نوع کار ست می شود
        VM_Job jobType = (VM_Job) cmb_JobType.getSelectedItem();
        if (jobType.getId() == 0) {
            Toast.makeText(getContext(), getResources().getString(R.string.PleaseSelectedOneJobType), Toast.LENGTH_SHORT).show();
            return false;
        }

        //در اینجا جنسیت چک می شود
        VM_Gender gender = (VM_Gender) cmb_Gender.getSelectedItem();
        if (gender.getId() == 0) {
            Toast.makeText(getContext(), getResources().getString(R.string.PleaseSelectedOneGender), Toast.LENGTH_SHORT).show();
            return false;
        }

        //در اینجا سابقه بیمه چک می شود
        if (!rdo_YesInsurance.isChecked() && !rdo_NoInsurance.isChecked()) {
            Toast.makeText(getContext(), getResources().getString(R.string.PleaseSelectedHasInsurance), Toast.LENGTH_SHORT).show();
            return false;
        }

        //در اینجا سابقه کار چک می شود
        if (!rdo_HaveWorkExperience.isChecked() && !rdo_HaveNotWorkExperience.isChecked()) {
            Toast.makeText(getContext(), getResources().getString(R.string.PleaseSelectedHaveWorkExperience), Toast.LENGTH_SHORT).show();
            return false;
        }

        //در اینجا زمان کاری چک می شود
        if (!rdo_FullTime.isChecked() && !rdo_PartTime.isChecked() && !rdo_TornTime.isChecked()) {
            Toast.makeText(getContext(), getResources().getString(R.string.PleaseSelectedJobTime), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //در اینجا آیتم ویرایش می شود
    void EditItem() {

        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Sending) + "</font>")));
        progress.setMessage((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.PleaseWait) + "</font>")));
        progress.show();

        String Title = txt_Title.getText().toString();
        String WorkingTime = txt_WorkingTime.getText().toString();
        String NationalCode = Replace.Number_fn_To_en(txt_NationalCode.getText().toString());
        String Age = txt_Age.getText().toString();
        String HoursOfWork = txt_HoursOfWork.getText().toString();
        String MaxPrice = Replace.Number_fn_To_en(txt_MaxPrice.getText().toString().replace(",", "").replace("٬", ""));
        String MinPrice = Replace.Number_fn_To_en(txt_MinPrice.getText().toString().replace(",", "").replace("٬", ""));
        String Description = txt_Description.getText().toString();
        String UniCode = tbl_user.GetUniCode();

        //در اینجا مدارک تحصیلی ست می شود
        List<Integer> MadrakIds = madrakAdapter.GetAllId();
        JSONArray madraksArray = new JSONArray();
        for (int i = 0; i < MadrakIds.size(); i++) {
            madraksArray.put(MadrakIds.get(i).intValue());
        }

        //در اینجا رشته های تحصیلی ست می شود
        List<Integer> MajorIds = majorAdapter.GetAllId();
        JSONArray majorsArray = new JSONArray();
        for (int i = 0; i < MajorIds.size(); i++) {
            majorsArray.put(MajorIds.get(i).intValue());
        }

        //در اینجا نوع شغل ست می شود
        int JobType = ((VM_Job) cmb_JobType.getSelectedItem()).getId();

        //در اینجا جنسیت ست می شود
        int Gender = ((VM_Gender) cmb_Gender.getSelectedItem()).getId() - 1;

        //در اینجا سابقه بیمه ست می شود
        boolean HasInsurance = false;
        if (rdo_YesInsurance.isChecked()) {
            HasInsurance = true;
        } else if (rdo_NoInsurance.isChecked()) {
            HasInsurance = false;
        }

        //در اینجا سابقه کار ست می شود
        boolean JobHistory = false;
        if (rdo_HaveWorkExperience.isChecked()) {
            JobHistory = true;
        } else if (rdo_HaveNotWorkExperience.isChecked()) {
            JobHistory = false;
        }

        //در اینجا زمان کاری ست می شود
        int JobTime = 0;
        if (rdo_FullTime.isChecked()) {
            JobTime = 0;
        } else if (rdo_PartTime.isChecked()) {
            JobTime = 1;
        } else if (rdo_TornTime.isChecked()) {
            JobTime = 2;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("UniCode", UniCode);
            object.put("Title", Title);
            object.put("WorkingTime", WorkingTime);
            object.put("NationalCode", NationalCode);
            object.put("Age", Age);
            object.put("MinPrice", MinPrice);
            object.put("MaxPrice", MaxPrice);
            object.put("Description", Description);
            object.put("Madraks", madraksArray);
            object.put("Majors", majorsArray);
            object.put("JobType", JobType);
            object.put("Gender", Gender);
            object.put("HasInsurance", HasInsurance);
            object.put("JobTime", JobTime);
            object.put("JobHistory", JobHistory);
            object.put("HoursOfWork", HoursOfWork);
            object.put("ItemId", ItemId);

            if (!image_item.getTag().toString().equalsIgnoreCase("default")) {
                object.put("Picture", Base64Image.BitmapToBase64(((BitmapDrawable) image_item.getDrawable()).getBitmap()));
            } else {
                object.put("Picture", "");
            }

            String Url = Api + "Item";

            EditRquest = new JsonObjectRequest(Request.Method.PUT, Url, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progress.dismiss();
                    try {
                        Toast.makeText(getActivity(), response.getString("Message"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progress.dismiss();

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
                            .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Ok) + "</font>")),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            EditItem();
                                        }
                                    })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(true)
                            .show();
                }
            });

            AppController.getInstance().addToRequestQueue(SetTimeOuteToPost(EditRquest));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //در اینجا المنت ها را خالی می کند
    void ClearItems() {
        txt_Title.setText("");
        txt_WorkingTime.setText("");
        txt_HoursOfWork.setText("");

        txt_NationalCode.setText("");
        txt_NationalCode.setHint(getResources().getString(R.string.NationalCode2));
        txt_NationalCode.setTextDirection(View.TEXT_DIRECTION_RTL);
        txt_NationalCode.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        txt_Age.setText("");

        txt_MinPrice.setText("");
        txt_MinPrice.setHint(getResources().getString(R.string.MinPrice2));
        txt_MinPrice.setTextDirection(View.TEXT_DIRECTION_RTL);
        txt_MinPrice.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        txt_MaxPrice.setText("");
        txt_MaxPrice.setHint(getResources().getString(R.string.MaxPrice2));
        txt_MaxPrice.setTextDirection(View.TEXT_DIRECTION_RTL);
        txt_MaxPrice.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        cmb_JobType.setSelection(0);
        cmb_Gender.setSelection(0);

        InsuranceGroup.clearCheck();
        WorkExperienceGroup.clearCheck();
        timeGroup.clearCheck();

        txt_Description.setText("");
        image_item.setImageDrawable(getResources().getDrawable(R.drawable.add_image));
        image_item.setTag("default");

        madrakAdapter.RemoveAll();
        majorAdapter.RemoveAll();
    }

    void GetDetailsItem() {
        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Sending) + "</font>")));
        progress.setMessage((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.PleaseWait) + "</font>")));
        progress.show();

        ItemId = getArguments().getInt("Id");
        String UniCode = ((MainActivity) getActivity()).tbl_user.GetUniCode();

        String Url = Api + "Item/GetItemToEdit?Id=" + ItemId + "&UniCode=" + UniCode;

        GetDetailsRequest = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    txt_Title.setText(response.getString("Title"));
                    txt_WorkingTime.setText(response.getString("WorkingTime"));

                    txt_NationalCode.setHint("");
                    txt_NationalCode.setTextDirection(View.TEXT_DIRECTION_LTR);
                    txt_NationalCode.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    txt_NationalCode.setText(response.getString("NationalCode"));

                    txt_Age.setText(response.getString("Age"));
                    txt_HoursOfWork.setText(response.getString("HoursOfWork"));

                    //در اینجا حداقل حقوق ست می شود
                    if (!response.getString("MinPrice").equalsIgnoreCase("0") && !response.getString("MinPrice").equalsIgnoreCase("")) {
                        txt_MinPrice.setHint("");
                        txt_MinPrice.setTextDirection(View.TEXT_DIRECTION_LTR);
                        txt_MinPrice.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                        txt_MinPrice.setText(response.getString("MinPrice"));
                    }

                    txt_MaxPrice.setHint("");
                    txt_MaxPrice.setTextDirection(View.TEXT_DIRECTION_LTR);
                    txt_MaxPrice.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    txt_MaxPrice.setText(response.getString("MaxPrice"));

                    //در اینجا مدارک تحصیلی ست می شوند
                    List<Integer> MadraksIds = new ArrayList<>();
                    JSONArray ArrayMadraks = response.getJSONArray("Madraks");
                    for (int i = 0; i < ArrayMadraks.length(); i++) {
                        MadraksIds.add(ArrayMadraks.getInt(i));
                    }
                    madrakAdapter.AddAll(tbl_madrak.GetMadraksById(MadraksIds));

                    //در اینجا رشته های تحصیلی ست می شود
                    List<Integer> MajorIds = new ArrayList<>();
                    JSONArray ArrayMajors = response.getJSONArray("Majors");
                    for (int i = 0; i < ArrayMajors.length(); i++) {
                        MajorIds.add(ArrayMajors.getInt(i));
                    }
                    majorAdapter.AddAll(tbl_major.GetMajorsByIds(MajorIds));

                    cmb_JobType.setSelection(tbl_job.GetPositionById(response.getInt("JobType") + 1));

                    cmb_Gender.setSelection(response.getInt("Gender"));

                    //در اینجا سابقه بیمه ست می شود
                    if (response.getBoolean("HasInsurance")) {
                        rdo_YesInsurance.setChecked(true);
                    } else {
                        rdo_NoInsurance.setChecked(true);
                    }

                    //در اینجا سابقه کار ست می شود
                    if (response.get("JobHistory") != null) {
                        if (response.getBoolean("JobHistory")) {
                            rdo_HaveWorkExperience.setChecked(true);
                        } else {
                            rdo_HaveNotWorkExperience.setChecked(true);
                        }
                    }

                    //در اینجا زمان کاری ست می شود
                    int JobTime = response.getInt("JobTime");
                    if (JobTime == 0) {
                        rdo_FullTime.setChecked(true);
                    } else if (JobTime == 1) {
                        rdo_PartTime.setChecked(true);
                    } else if (JobTime == 2) {
                        rdo_TornTime.setChecked(true);
                    }

                    txt_Description.setText(response.getString("Description"));

                    //در اینجا عکس آیتم ست می شود
                    if (!response.getString("Picture").equalsIgnoreCase("")) {
                        String ImageUrl = ApiImage_Item + response.getString("Picture");

                        Glide.with(getActivity())
                                .load(ImageUrl)
                                .placeholder(R.drawable.add_image)
                                .into(image_item);

                        image_item.setTag("Selected");
                    } else {
                        image_item.setTag("default");
                    }
                } catch (Exception e) {
                } finally {
                    progress.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                boolean HasInternet = ((MainActivity) getActivity()).internet.HaveNetworkConnection();

                if (!HasInternet) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.CheckYourInternet), Toast.LENGTH_LONG).show();
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Error) + "</font>")))
                            .setMessage((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.YourInternetIsVerySlow) + "</font>")))
                            .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Ok) + "</font>")),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            GetDetailsItem();
                                        }
                                    })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(true)
                            .show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(SetTimeOut(GetDetailsRequest));
    }
}
