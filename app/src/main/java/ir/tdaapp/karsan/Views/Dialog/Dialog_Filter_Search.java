package ir.tdaapp.karsan.Views.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import ir.tdaapp.karsan.DataBase.Tbl_Madrak;
import ir.tdaapp.karsan.DataBase.Tbl_Major;
import ir.tdaapp.karsan.Enum.InsuranceHistory;
import ir.tdaapp.karsan.Enum.JobHistory;
import ir.tdaapp.karsan.Enum.Job_Time;
import ir.tdaapp.karsan.MainActivity;
import ir.tdaapp.karsan.R;
import ir.tdaapp.karsan.Services.onClickDialogFilterSearh;
import ir.tdaapp.karsan.Utility.BaseDialogFragment;
import ir.tdaapp.karsan.Utility.BaseFragment;
import ir.tdaapp.karsan.Utility.KeyBoard;
import ir.tdaapp.karsan.Utility.txt_Price_Watcher;
import ir.tdaapp.karsan.ViewModel.VM_Madrak;
import ir.tdaapp.karsan.ViewModel.VM_Major;
import ir.tdaapp.karsan.ViewModel.VM_SearchFilter;
import ir.tdaapp.karsan.Views.Fragment_Home;

public class Dialog_Filter_Search extends BaseDialogFragment {

    public static final String TAG = "Dialog_Filter_Search";

    //مربوط به حداقل حقوق
    EditText txt_MinRights;

    //مربوط به حداکثر حقوق
    EditText txt_MaxRights;

    Spinner cmb_Madrak, cmb_Major;
    Tbl_Madrak tbl_madrak;
    Tbl_Major tbl_major;
    CheckBox chk_FullTime, chk_PartTime, chk_TornTime;
    RadioButton rdo_IHaveInsuranceHistory, rdo_IHaveNotInsuranceHistory;
    RadioButton rdo_IHaveJobHistory, rdo_IHaveNotJobHistory;
    Button btn_ClearOptions, btn_Search;
    onClickDialogFilterSearh clickDialogFilterSearh;
    RadioGroup IHaveJobHistoryGroup, IHaveInsuranceHistoryGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_filter_search, container, false);

        KeyBoard.hideKeyboard(getActivity());
        FindItem(view);
        SetSpinner();
        OnClick();
        GetData();

        return view;
    }

    void FindItem(View view) {
        txt_MinRights = view.findViewById(R.id.txt_MinRights);
        txt_MaxRights = view.findViewById(R.id.txt_MaxRights);
        cmb_Madrak = view.findViewById(R.id.cmb_Madrak);
        tbl_madrak = new Tbl_Madrak(((MainActivity) getActivity()).dbAdapter);
        cmb_Major = view.findViewById(R.id.cmb_Major);
        tbl_major = new Tbl_Major(((MainActivity) getActivity()).dbAdapter);
        chk_FullTime = view.findViewById(R.id.chk_FullTime);
        chk_PartTime = view.findViewById(R.id.chk_PartTime);
        chk_TornTime = view.findViewById(R.id.chk_TornTime);
        rdo_IHaveInsuranceHistory = view.findViewById(R.id.rdo_IHaveInsuranceHistory);
        rdo_IHaveNotInsuranceHistory = view.findViewById(R.id.rdo_IHaveNotInsuranceHistory);
        rdo_IHaveJobHistory = view.findViewById(R.id.rdo_IHaveJobHistory);
        rdo_IHaveNotJobHistory = view.findViewById(R.id.rdo_IHaveNotJobHistory);
        btn_ClearOptions = view.findViewById(R.id.btn_ClearOptions);
        btn_Search = view.findViewById(R.id.btn_Search);
        IHaveJobHistoryGroup = view.findViewById(R.id.IHaveJobHistoryGroup);
        IHaveInsuranceHistoryGroup = view.findViewById(R.id.IHaveInsuranceHistoryGroup);
    }

    public void setClickDialogFilterSearh(onClickDialogFilterSearh clickDialogFilterSearh) {
        this.clickDialogFilterSearh = clickDialogFilterSearh;
    }

    void OnClick() {

        txt_MinRights.addTextChangedListener(new txt_Price_Watcher(txt_MinRights));
        txt_MaxRights.addTextChangedListener(new txt_Price_Watcher(txt_MaxRights));

        txt_MinRights.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    txt_MinRights.setHint("");
                    txt_MinRights.setTextDirection(View.TEXT_DIRECTION_LTR);
                    txt_MinRights.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                } else {
                    if (txt_MinRights.getText().toString().equalsIgnoreCase("")) {
                        txt_MinRights.setHint(getResources().getString(R.string.MinRights));
                        txt_MinRights.setTextDirection(View.TEXT_DIRECTION_RTL);
                        txt_MinRights.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    }
                }
            }
        });

        txt_MaxRights.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    txt_MaxRights.setHint("");
                    txt_MaxRights.setTextDirection(View.TEXT_DIRECTION_LTR);
                    txt_MaxRights.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                } else {
                    if (txt_MaxRights.getText().toString().equalsIgnoreCase("")) {
                        txt_MaxRights.setHint(getResources().getString(R.string.MaxRights));
                        txt_MaxRights.setTextDirection(View.TEXT_DIRECTION_RTL);
                        txt_MaxRights.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    }
                }
            }
        });
        btn_ClearOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearOptions();
            }
        });

        btn_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetFilters();

                if (clickDialogFilterSearh != null) {
                    clickDialogFilterSearh.onClickSearchButton();
                }

                getDialog().dismiss();
            }
        });
    }

    //در اینجا داده های اسپینر ست می شود
    void SetSpinner() {
        //در اینجا اسپینر مدرک ست می شود
//        List<VM_Madrak> madraks = tbl_madrak.GetAll();
        List<VM_Madrak> madraks = new ArrayList<>();

        madraks.add(new VM_Madrak(0, getString(R.string.Madrak)));
        madraks.addAll(tbl_madrak.GetAll());

        ArrayAdapter<VM_Madrak> Adapter_Madrak = new ArrayAdapter<VM_Madrak>
                (getActivity(), R.layout.spinner_layout, madraks);
        cmb_Madrak.setAdapter(Adapter_Madrak);
        /////////////////////////////////////////////

        //در اینجا اسپینر رشته تحصیلی ست می شود
//        List<VM_Major> majors = tbl_major.GetAll();
        List<VM_Major> majors = new ArrayList<>();
        majors.add(new VM_Major(0, getString(R.string.Major)));
        majors.addAll(tbl_major.GetAll());

        ArrayAdapter<VM_Major> Adapter_Major = new ArrayAdapter<VM_Major>(getContext(), R.layout.spinner_layout, majors);
        cmb_Major.setAdapter(Adapter_Major);
        ////////////////////////////////////////////
    }

    //در اینجا فیلترهای جستجو ریست می شوند
    void ClearOptions() {
        txt_MaxRights.setText("");
        txt_MinRights.setText("");
        chk_FullTime.setChecked(false);
        chk_PartTime.setChecked(false);
        chk_TornTime.setChecked(false);
        IHaveJobHistoryGroup.clearCheck();
        IHaveInsuranceHistoryGroup.clearCheck();
        cmb_Madrak.setSelection(0);
        cmb_Major.setSelection(0);
        ((MainActivity) getActivity()).SearchFilter = new VM_SearchFilter();
    }

    //در اینجا فیلتر های جستجو ست می شوند
    void SetFilters() {

        ((MainActivity) getActivity()).SearchFilter = new VM_SearchFilter();

        //در اینجا حداقل حقوق ست می شود
        ((MainActivity) getActivity()).SearchFilter.setMinRights(txt_MinRights.getText().toString().replace(",", "").replace("٬", ""));

        //در اینجا حداکثر حقوق ست می شود
        ((MainActivity) getActivity()).SearchFilter.setMaxRights(txt_MaxRights.getText().toString().replace(",", "").replace("٬", ""));

        //در اینجا تایم کاری ست می شود
        List<Job_Time> job_times = new ArrayList<>();

        if (chk_FullTime.isChecked()) {
            job_times.add(Job_Time.FullTime);
        }
        if (chk_PartTime.isChecked()) {
            job_times.add(Job_Time.PartTime);
        }
        if (chk_TornTime.isChecked()) {
            job_times.add(Job_Time.TornTime);
        }
        ((MainActivity) getActivity()).SearchFilter.setJob_time(job_times);

        //در اینجا سابقه بیمه ست می شود
        if (rdo_IHaveInsuranceHistory.isChecked()) {
            ((MainActivity) getActivity()).SearchFilter.setInsuranceHistory(InsuranceHistory.Yes);
        } else if (rdo_IHaveNotInsuranceHistory.isChecked()) {
            ((MainActivity) getActivity()).SearchFilter.setInsuranceHistory(InsuranceHistory.No);
        } else {
            ((MainActivity) getActivity()).SearchFilter.setInsuranceHistory(InsuranceHistory.Ignored);
        }

        //در اینجا سابقه کار ست می شود
        if (rdo_IHaveJobHistory.isChecked()) {
            ((MainActivity) getActivity()).SearchFilter.setJobHistory(JobHistory.Yes);
        } else if (rdo_IHaveNotJobHistory.isChecked()) {
            ((MainActivity) getActivity()).SearchFilter.setJobHistory(JobHistory.No);
        } else {
            ((MainActivity) getActivity()).SearchFilter.setJobHistory(JobHistory.Ignored);
        }

        //در اینجا مدرک ست می شود
        ((MainActivity) getActivity()).SearchFilter.setMadrak(((VM_Madrak) cmb_Madrak.getSelectedItem()).getId());

        //در اینجا رشته تحصیلی ست می شود
        ((MainActivity) getActivity()).SearchFilter.setMajor(((VM_Major) cmb_Major.getSelectedItem()).getId());
    }

    ///در اینجا مقادیر فیلتر هارا در اکتیویتی گرفته و در المنت ها قرار می دهد
    void GetData() {

        VM_SearchFilter searchFilter = ((MainActivity) getActivity()).SearchFilter;

        //در اینجا حداقل قیمت ست می شود
        txt_MinRights.setText(searchFilter.getMinRights());

        ///در اینجا اگر تکست باکس حداقل قیمت خالی نباشد چپ به راست می شود
        if (!txt_MinRights.getText().toString().equalsIgnoreCase("")) {
            txt_MinRights.setHint("");
            txt_MinRights.setTextDirection(View.TEXT_DIRECTION_LTR);
            txt_MinRights.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        //در اینجا حداکثر قیمت ست می شود
        txt_MaxRights.setText(searchFilter.getMaxRights());

        ///در اینجا اگر تکست باکس حداکثر قیمت خالی نباشد چپ به راست می شود
        if (!txt_MaxRights.getText().toString().equalsIgnoreCase("")) {
            txt_MaxRights.setHint("");
            txt_MaxRights.setTextDirection(View.TEXT_DIRECTION_LTR);
            txt_MaxRights.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        //در اینجا زمان کاری ها ست می شود
        List<Job_Time> times = searchFilter.getJob_time();
        for (int i = 0; i < times.size(); i++) {
            if (times.get(i) == Job_Time.FullTime) {
                chk_FullTime.setChecked(true);
            }
            if (times.get(i) == Job_Time.PartTime) {
                chk_PartTime.setChecked(true);
            }
            if (times.get(i) == Job_Time.TornTime) {
                chk_TornTime.setChecked(true);
            }
        }
        /////////////////////////

        //در اینجا سابقه بیمه ست می شود
        if (searchFilter.getInsuranceHistory() == InsuranceHistory.Yes) {
            rdo_IHaveInsuranceHistory.setChecked(true);
        } else if (searchFilter.getInsuranceHistory() == InsuranceHistory.No) {
            rdo_IHaveNotInsuranceHistory.setChecked(true);
        }

        //در اینجا سابقه کار ست می شود
        if (searchFilter.getJobHistory() == JobHistory.Yes) {
            rdo_IHaveJobHistory.setChecked(true);
        } else if (searchFilter.getJobHistory() == JobHistory.No) {
            rdo_IHaveNotJobHistory.setChecked(true);
        }

        //در اینجا پوزیشن اسپینر مدرک ست می شود
        if (searchFilter.getMadrak() != 0) {
            int pos = tbl_madrak.GetPosition(searchFilter.getMadrak());
            if (pos != -1) {
                cmb_Madrak.setSelection(pos + 1);
            }
        }

        //در اینجا پوزیشن اسپینر رشته تحصیلی ست می شود
        if (searchFilter.getMajor() != 0) {
            int pos = tbl_major.GetPosition(searchFilter.getMajor());
            if (pos != -1) {
                cmb_Major.setSelection(pos + 1);
            }
        }
    }

}
