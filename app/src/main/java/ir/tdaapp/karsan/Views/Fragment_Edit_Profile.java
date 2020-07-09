package ir.tdaapp.karsan.Views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;
import ir.tdaapp.karsan.DataBase.Tbl_Insurance;
import ir.tdaapp.karsan.DataBase.Tbl_Madrak;
import ir.tdaapp.karsan.DataBase.Tbl_Major;
import ir.tdaapp.karsan.DataBase.Tbl_User;
import ir.tdaapp.karsan.Enum.Gender;
import ir.tdaapp.karsan.MainActivity;
import ir.tdaapp.karsan.R;
import ir.tdaapp.karsan.Utility.AppController;
import ir.tdaapp.karsan.Utility.BaseFragment;
import ir.tdaapp.karsan.Utility.Validation;
import ir.tdaapp.karsan.ViewModel.VM_Gender;
import ir.tdaapp.karsan.ViewModel.VM_Insurance;
import ir.tdaapp.karsan.ViewModel.VM_Madrak;
import ir.tdaapp.karsan.ViewModel.VM_Major;

public class Fragment_Edit_Profile extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "Fragment_Edit_Profile";

    Spinner cmb_Major, cmb_Insurance, cmb_Gender, cmb_Madrak;
    Tbl_Major tbl_major;
    Tbl_Insurance tbl_insurance;
    Tbl_Madrak tbl_madrak;
    JsonObjectRequest GetRequest, PostRequest;
    RequestQueue requestQueue;
    Tbl_User tbl_user;
    EditText txt_BirthDate, txt_FullName;
    RadioButton rdo_Base1, rdo_Base2, rdo_Base3, rdo_Special, rdo_FullTime, rdo_PartTime, rdo_Both;
    CheckBox chk_Motorcycle;
    Button btn_Edit, btn_ClearOptions;
    int GenderId, MajorId, InsuranceId, MadrakId;
    LinearLayout Back;
    PersianDatePickerDialog picker;
    PersianCalendar initDate;
    RadioGroup radioGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        FindItem(view);
        SetSpinners();
        GetVals();
        OnClick();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        requestQueue = Volley.newRequestQueue(getContext());

        if (GetRequest != null) {
            GetRequest.setTag(TAG);
            requestQueue.add(GetRequest);
        }

        if (PostRequest != null) {
            PostRequest.setTag(TAG);
            requestQueue.add(PostRequest);
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

    //در اینجا مشخصات کاربر در سرور گرفته می شود و در المنت ها ست می شوند
    void GetVals() {

        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.GettingInformation) + "</font>")));
        progress.setMessage((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.PleaseWait) + "</font>")));
        progress.show();

        String UniqueCode = tbl_user.GetUniCode();
        String Url = Api + "User/GetUserInfoToEdit?UniCode=" + UniqueCode;

        GetRequest = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progress.dismiss();

                    //در اینجا نام کاربر ست می شود
                    if (!response.getString("DateBirth").equalsIgnoreCase("")) {
                        txt_BirthDate.setTextDirection(View.TEXT_DIRECTION_LTR);
                        txt_BirthDate.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    }
                    txt_FullName.setText(response.getString("FullName"));
                    ///////////////////////////////

                    //در اینجا جنسیت کاربر ست می شود
                    int Gender = response.getInt("Gender");
                    if (Gender != 0) {
                        cmb_Gender.setSelection(Gender);
                    }
                    //////////////////////////

                    //در اینجا رشته تحصیلی ست می شود
                    int MajorId = response.getInt("MajorId");
                    if (MajorId != 0) {
                        cmb_Major.setSelection(tbl_major.GetPosition(MajorId) + 1);
                    }
                    //////////////////////////////

                    //در اینجا سابقه بیمه ست می شود
                    int InsuranceId = response.getInt("InsuranceId");
                    if (InsuranceId != 0) {
                        cmb_Insurance.setSelection(tbl_insurance.Position(InsuranceId) + 1);
                    }
                    ////////////////////////////////

                    //در اینجا مدرک ست می شود
                    int MadrakId = response.getInt("MadrakId");
                    if (MadrakId != 0) {
                        cmb_Madrak.setSelection(tbl_madrak.GetPosition(MadrakId) + 1);
                    }
                    ////////////////////////////

                    //در اینجا تاریخ تولد ست می شود
                    txt_BirthDate.setText(response.getString("DateBirth"));
                    //////////////////

                    //در اینجا گواهینامه ست می شود
                    int Certificate = response.getInt("Certificate");
                    if (Certificate != 0) {
                        if (Certificate == 1) {
                            rdo_Base1.setChecked(true);
                        } else if (Certificate == 2) {
                            rdo_Base2.setChecked(true);
                        } else if (Certificate == 3) {
                            rdo_Base3.setChecked(true);
                        } else if (Certificate == 4) {
                            rdo_Special.setChecked(true);
                        }
                    }
                    ////////////////////////////////

                    //در اینجا گواهینامه موتور ست می شود
                    chk_Motorcycle.setChecked(response.getBoolean("Motorcycle"));
                    //////////////////////////////////////

                    //در اینجا وقت کار ست می شود
                    int JobType = response.getInt("JobType");
                    if (JobType != 0) {
                        if (JobType == 1) {
                            rdo_FullTime.setChecked(true);
                        } else if (JobType == 2) {
                            rdo_PartTime.setChecked(true);
                        } else if (JobType == 3) {
                            rdo_Both.setChecked(true);
                        }
                    }
                    /////////////////////////////////
                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progress.dismiss();

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
                        .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Reload) + "</font>")), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                GetVals();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(true)
                        .show();

            }
        });

        AppController.getInstance().addToRequestQueue(SetTimeOuteToPost(GetRequest));
    }

    void OnClick() {

        rdo_Base3.setOnClickListener(view -> {
            radioGroup.clearCheck();
            rdo_Base3.setChecked(true);
        });

        //در اینجا زمانی کاربر روی ادیت تکست تاریخ تولد فوکوس می کند ادیت تکست چپ نشین می شود
        txt_BirthDate.setOnFocusChangeListener((view, b) -> {
            if (b) {
                txt_BirthDate.setHint("");
                txt_BirthDate.setTextDirection(View.TEXT_DIRECTION_LTR);
                txt_BirthDate.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

                picker = new PersianDatePickerDialog(getActivity())
                        .setPositiveButtonString("باشه")
                        .setNegativeButton("بیخیال")
                        .setTodayButton("امروز")
                        .setTodayButtonVisible(true)
                        .setInitDate(initDate)
                        .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                        .setMinYear(1300)
                        .setActionTextColor(Color.GRAY)
                        .setListener(new Listener() {
                            @Override
                            public void onDateSelected(PersianCalendar persianCalendar) {
                                txt_BirthDate.setText(persianCalendar.getPersianYear() + "/" + persianCalendar.getPersianMonth() + "/" + persianCalendar.getPersianDay());
                            }

                            @Override
                            public void onDismissed() {

                            }
                        });

                picker.show();

            } else {
                if (!txt_BirthDate.getText().toString().equalsIgnoreCase("")) {
                    txt_BirthDate.setHint("");
                    txt_BirthDate.setTextDirection(View.TEXT_DIRECTION_LTR);
                    txt_BirthDate.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                } else {
                    txt_BirthDate.setHint(getResources().getString(R.string.BirthDate2));
                    txt_BirthDate.setTextDirection(View.TEXT_DIRECTION_RTL);
                    txt_BirthDate.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                }
            }
        });

        cmb_Gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                VM_Gender v = (VM_Gender) adapterView.getSelectedItem();
                GenderId = v.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        cmb_Major.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                VM_Major v = (VM_Major) adapterView.getSelectedItem();
                MajorId = v.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        cmb_Insurance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                VM_Insurance v = (VM_Insurance) adapterView.getSelectedItem();
                InsuranceId = v.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        cmb_Madrak.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                VM_Madrak v = (VM_Madrak) adapterView.getSelectedItem();
                MadrakId = v.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_Edit.setOnClickListener(this);
        btn_ClearOptions.setOnClickListener(this);
        Back.setOnClickListener(this);
    }

    void FindItem(View view) {
        cmb_Gender = view.findViewById(R.id.cmb_Gender);
        cmb_Major = view.findViewById(R.id.cmb_Major);
        cmb_Madrak = view.findViewById(R.id.cmb_Madrak);
        cmb_Insurance = view.findViewById(R.id.cmb_Insurance);
        Back = view.findViewById(R.id.Back);
        btn_Edit = view.findViewById(R.id.btn_Edit);
        btn_ClearOptions = view.findViewById(R.id.btn_ClearOptions);
        txt_FullName = view.findViewById(R.id.txt_FullName);
        rdo_Base1 = view.findViewById(R.id.rdo_Base1);
        rdo_Base2 = view.findViewById(R.id.rdo_Base2);
        rdo_Base3 = view.findViewById(R.id.rdo_Base3);
        rdo_FullTime = view.findViewById(R.id.rdo_FullTime);
        rdo_PartTime = view.findViewById(R.id.rdo_PartTime);
        chk_Motorcycle = view.findViewById(R.id.chk_Motorcycle);
        rdo_Both = view.findViewById(R.id.rdo_Both);
        rdo_Special = view.findViewById(R.id.rdo_Special);
        tbl_major = new Tbl_Major(((MainActivity) getActivity()).dbAdapter);
        tbl_insurance = new Tbl_Insurance(((MainActivity) getActivity()).dbAdapter);
        tbl_madrak = new Tbl_Madrak(((MainActivity) getActivity()).dbAdapter);
        tbl_user = new Tbl_User(((MainActivity) getActivity()).dbAdapter);
        txt_BirthDate = view.findViewById(R.id.txt_BirthDate);
        radioGroup = view.findViewById(R.id.radioGroup);
        PersianCalendar initDate = new PersianCalendar();
        initDate.setPersianDate(1378, 3, 10);
    }

    void SetSpinners() {

        //در اینجا اسپینر جنسیت ست می شود
        List<VM_Gender> gender = new ArrayList<>();
        gender.add(new VM_Gender(Gender.both, "جنسیت", 0));
        gender.add(new VM_Gender(Gender.Man, "مرد", 1));
        gender.add(new VM_Gender(Gender.Women, "زن", 2));

        ArrayAdapter<VM_Gender> GenderAdapter = new ArrayAdapter<VM_Gender>(getContext()
                , R.layout.spinner_layout, gender);

        cmb_Gender.setAdapter(GenderAdapter);

        //در اینجا اسپینر رشته تحصیلی ست می شود
        List<VM_Major> majors = tbl_major.GetAll();

        VM_Major HolderMajor = new VM_Major();
        HolderMajor.setId(0);
        HolderMajor.setTitle(getResources().getString(R.string.Major));
        majors.add(0, HolderMajor);

        ArrayAdapter AdapterMajor = new ArrayAdapter(getContext(),
                R.layout.spinner_layout, majors);

        cmb_Major.setAdapter(AdapterMajor);

        //در اینجا اسپینر سابقه بیمه ست می شود
        List<VM_Insurance> insurances = tbl_insurance.GetAll();

        VM_Insurance HolderInsurances = new VM_Insurance();
        HolderInsurances.setId(0);
        HolderInsurances.setTitle(getResources().getString(R.string.Insurances));

        insurances.add(0, HolderInsurances);

        ArrayAdapter<VM_Insurance> AdapterInsurance = new ArrayAdapter<VM_Insurance>(getContext(),
                R.layout.spinner_layout, insurances);

        cmb_Insurance.setAdapter(AdapterInsurance);

        //در اینجا اسپینر مدرک ست می شود
        List<VM_Madrak> madraks = tbl_madrak.GetAll();

        VM_Madrak Holder_Madrak = new VM_Madrak();

        Holder_Madrak.setTitle(getResources().getString(R.string.Grade));
        Holder_Madrak.setId(0);

        madraks.add(0, Holder_Madrak);
        ArrayAdapter<VM_Madrak> AdapterMadrak = new ArrayAdapter<VM_Madrak>(getContext(),
                R.layout.spinner_layout, madraks);

        cmb_Madrak.setAdapter(AdapterMadrak);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Edit:
                if (!Validation()) {
                    EditProfile();
                }
                break;
            case R.id.btn_ClearOptions:
                ClearOptions();
                break;
            case R.id.Back:
                ((MainActivity) getActivity()).onBackPressed();
                break;
        }
    }

    //هنگامی کاربر دکمه ادیت را فشار می دهد کد زیر فراخوانی می شود
    void EditProfile() {
        btn_Edit.setEnabled(false);

        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setTitle((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Sending) + "</font>")));
        progress.setMessage((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.PleaseWait) + "</font>")));
        progress.show();

        String Url = Api + "User/EditProfile";

        JSONObject object = new JSONObject();

        try {

            object.put("UniCode", tbl_user.GetUniCode());
            object.put("FullName", txt_FullName.getText().toString());
            object.put("Gender", GenderId);
            object.put("MajorId", MajorId);
            object.put("InsuranceId", InsuranceId);
            object.put("MadrakId", MadrakId);
            object.put("DateBirth", txt_BirthDate.getText().toString());
            object.put("Motorcycle", chk_Motorcycle.isChecked());

            //در اینجا گواهینامه ست می شود
            int Certificate = 0;
            if (rdo_Base1.isChecked()) {
                Certificate = 1;
            } else if (rdo_Base2.isChecked()) {
                Certificate = 2;
            } else if (rdo_Base3.isChecked()) {
                Certificate = 3;
            } else if (rdo_Special.isChecked()) {
                Certificate = 4;
            }
            object.put("Certificate", Certificate);

            //در اینجا وقت کار ست می شود
            int JobType = 0;
            if (rdo_FullTime.isChecked()) {
                JobType = 1;
            } else if (rdo_PartTime.isChecked()) {
                JobType = 2;
            } else if (rdo_Both.isChecked()) {
                JobType = 3;
            }

            object.put("JobType", JobType);

        } catch (Exception e) {

        }

        PostRequest = new JsonObjectRequest(Request.Method.PUT, Url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                btn_Edit.setEnabled(true);
                progress.dismiss();

                try {

                    boolean Resault = response.getBoolean("Resalt");
                    String Message = response.getString("Message");

                    if (Resault) {
                        Toast.makeText(getActivity(), Message, Toast.LENGTH_SHORT).show();
                    } else {

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
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                btn_Edit.setEnabled(true);
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
                        .setPositiveButton((Html.fromHtml("<font color='#FF7F27'>" + getResources().getString(R.string.Reload) + "</font>")), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                GetVals();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(true)
                        .show();
            }
        });

        AppController.getInstance().addToRequestQueue(SetTimeOuteToPost(PostRequest));
    }

    //کاربر با فشار دادن این دکمه تمام المنت ها را خالی می کند
    void ClearOptions() {

        //در اینجا آی دی اسپینر ها که ست شده اند خالی می شوند
        GenderId = 0;
        MajorId = 0;
        InsuranceId = 0;
        MadrakId = 0;

        //در اینجا اسپینر ها ریست می شوند
        cmb_Gender.setSelection(0);
        cmb_Madrak.setSelection(0);
        cmb_Insurance.setSelection(0);
        cmb_Major.setSelection(0);

        //در اینجا ادیت تکست ها خال می شوند
        txt_FullName.setText("");
        txt_BirthDate.setText("");
        txt_BirthDate.setTextDirection(View.TEXT_DIRECTION_LTR);
        txt_BirthDate.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        //در اینجا رادیو باتن ها خالی می شوند
        rdo_Both.setChecked(false);
        rdo_PartTime.setChecked(false);
        rdo_FullTime.setChecked(false);
        rdo_Special.setChecked(false);
        rdo_Base3.setChecked(false);
        rdo_Base2.setChecked(false);
        rdo_Base1.setChecked(false);

        //در اینجا چک باکس ها خالی می شوند
        chk_Motorcycle.setChecked(false);
    }

    boolean Validation() {
        boolean valid_txt_FullName = Validation.MaxLength(txt_FullName, getResources().getString(R.string.YourNameMustBeAtMost40CharactersLong), 40);
        return valid_txt_FullName;
    }
}
