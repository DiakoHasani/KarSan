package ir.tdaapp.karsan.Views.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ir.tdaapp.karsan.DataBase.Tbl_Insurance;
import ir.tdaapp.karsan.DataBase.Tbl_Madrak;
import ir.tdaapp.karsan.DataBase.Tbl_Major;
import ir.tdaapp.karsan.Enum.Gender;
import ir.tdaapp.karsan.MainActivity;
import ir.tdaapp.karsan.R;
import ir.tdaapp.karsan.Services.onClickDialogFilterSearh;
import ir.tdaapp.karsan.Utility.BaseDialogFragment;
import ir.tdaapp.karsan.ViewModel.VM_FilterCV;
import ir.tdaapp.karsan.ViewModel.VM_Gender;
import ir.tdaapp.karsan.ViewModel.VM_Insurance;
import ir.tdaapp.karsan.ViewModel.VM_Madrak;
import ir.tdaapp.karsan.ViewModel.VM_Major;

public class Dialog_Filter_CV extends BaseDialogFragment implements View.OnClickListener {

    onClickDialogFilterSearh clickDialogFilterSearh;

    public final static String TAG = "Dialog_Filter_CV";

    Spinner cmb_Madrak, cmb_Major, cmb_Insurance, cmb_Gender;
    CheckBox chk_Base1, chk_Base2, chk_Base3, chk_Special, chk_Motorcycle;
    Button btn_Search, btn_ClearOptions;
    VM_FilterCV filterCV;
    Tbl_Major tbl_major;
    Tbl_Insurance tbl_insurance;
    Tbl_Madrak tbl_madrak;

    public Dialog_Filter_CV(VM_FilterCV filterCV) {
        this.filterCV = filterCV;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_filter_cv, container, false);

        findItem(view);
        OnClick();
        setSpinners();
        getFilter();

        return view;
    }

    void findItem(View view) {
        cmb_Madrak = view.findViewById(R.id.cmb_Madrak);
        cmb_Major = view.findViewById(R.id.cmb_Major);
        cmb_Insurance = view.findViewById(R.id.cmb_Insurance);
        cmb_Gender = view.findViewById(R.id.cmb_Gender);
        chk_Base1 = view.findViewById(R.id.chk_Base1);
        chk_Base2 = view.findViewById(R.id.chk_Base2);
        chk_Base3 = view.findViewById(R.id.chk_Base3);
        chk_Special = view.findViewById(R.id.chk_Special);
        chk_Motorcycle = view.findViewById(R.id.chk_Motorcycle);
        btn_Search = view.findViewById(R.id.btn_Search);
        btn_ClearOptions = view.findViewById(R.id.btn_ClearOptions);

        tbl_major = new Tbl_Major(((MainActivity) getActivity()).dbAdapter);
        tbl_insurance = new Tbl_Insurance(((MainActivity) getActivity()).dbAdapter);
        tbl_madrak = new Tbl_Madrak(((MainActivity) getActivity()).dbAdapter);
    }

    void OnClick() {
        btn_Search.setOnClickListener(this);
        btn_ClearOptions.setOnClickListener(this);
    }

    public void setClickDialogFilterSearh(onClickDialogFilterSearh clickDialogFilterSearh) {
        this.clickDialogFilterSearh = clickDialogFilterSearh;
    }

    //در اینجا اسپینرها ست می شوند
    void setSpinners() {
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

    //در اینجا اگر کاربر قبلا فیلتر زده باشد آن ها را ست می کند
    void getFilter() {
        int pos_madrak = tbl_madrak.GetPosition(filterCV.getMadrakId());
        cmb_Madrak.setSelection(pos_madrak == -1 ? 0 : pos_madrak + 1);

        int pos_major = tbl_major.GetPosition(filterCV.getMajorId());
        cmb_Major.setSelection(pos_major == -1 ? 0 : pos_major + 1);

        int pos_Insurance = tbl_insurance.Position(filterCV.getInsuranceId());
        if (pos_Insurance != 0) {
            pos_Insurance++;
        } else if (pos_Insurance == 0 && filterCV.getInsuranceId() != 0) {
            pos_Insurance++;
        }
        cmb_Insurance.setSelection(pos_Insurance);
        cmb_Gender.setSelection(filterCV.getGenderId());
        chk_Base1.setChecked(filterCV.isBase1());
        chk_Base2.setChecked(filterCV.isBase2());
        chk_Base3.setChecked(filterCV.isBase3());
        chk_Special.setChecked(filterCV.isSpecial());
        chk_Motorcycle.setChecked(filterCV.isMotorcycle());
    }

    void setFilter() {
        filterCV.setMadrakId(((VM_Madrak) cmb_Madrak.getSelectedItem()).getId());
        filterCV.setMajorId(((VM_Major) cmb_Major.getSelectedItem()).getId());
        filterCV.setInsuranceId(((VM_Insurance) cmb_Insurance.getSelectedItem()).getId());
        filterCV.setGenderId(((VM_Gender) cmb_Gender.getSelectedItem()).getId());

        filterCV.setBase1(chk_Base1.isChecked());
        filterCV.setBase2(chk_Base2.isChecked());
        filterCV.setBase3(chk_Base3.isChecked());
        filterCV.setSpecial(chk_Special.isChecked());
        filterCV.setMotorcycle(chk_Motorcycle.isChecked());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Search:
                setFilter();
                if (clickDialogFilterSearh != null) {
                    clickDialogFilterSearh.onClickSearchButton();
                }
                getDialog().dismiss();
                break;
            case R.id.btn_ClearOptions:
                clearAll();
                break;
        }
    }

    void clearAll(){
        cmb_Madrak.setSelection(0);
        cmb_Major.setSelection(0);
        cmb_Insurance.setSelection(0);
        cmb_Gender.setSelection(0);

        chk_Base1.setChecked(false);
        chk_Base2.setChecked(false);
        chk_Base3.setChecked(false);
        chk_Special.setChecked(false);
        chk_Motorcycle.setChecked(false);
    }
}
