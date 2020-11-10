package ir.tdaapp.karsan.Views.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ir.tdaapp.karsan.R;
import ir.tdaapp.karsan.Services.onClickDialog_Confirm;
import ir.tdaapp.karsan.Utility.BaseDialogFragment;

//این دیالوگ برای عملیاتی که از کاربر پرسیده آیا مایل به این کار می باشید و جواب بله یا خیر از کاربر می گیرد
public class Dialog_Confirm extends BaseDialogFragment implements View.OnClickListener {

    public static final String TAG = "Dialog_Confirm";

    String titleText = "", btn_ok_Text = "", btn_cancel_Text = "";
    onClickDialog_Confirm clickDialog_confirm;

    public Dialog_Confirm(String titleText, onClickDialog_Confirm clickDialog_confirm) {
        this.titleText = titleText;
        this.clickDialog_confirm = clickDialog_confirm;
    }

    public Dialog_Confirm(String titleText, String btn_ok_Text, String btn_cancel_Text, onClickDialog_Confirm clickDialog_confirm) {
        this.titleText = titleText;
        this.btn_ok_Text = btn_ok_Text;
        this.btn_cancel_Text = btn_cancel_Text;
        this.clickDialog_confirm = clickDialog_confirm;
    }

    TextView lbl_Title;
    TextView btn_ok, btn_cancel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_confirm, container, false);

        findItem(view);
        implement();

        return view;
    }

    void findItem(View view) {
        lbl_Title = view.findViewById(R.id.lbl_Title);
        btn_ok = view.findViewById(R.id.btn_ok);
        btn_cancel = view.findViewById(R.id.btn_cancel);
    }

    void implement() {
        lbl_Title.setText(titleText);

        if (!btn_ok_Text.equalsIgnoreCase("")) {
            btn_ok.setText(btn_ok_Text);
        }

        if (!btn_cancel_Text.equalsIgnoreCase("")) {
            btn_cancel.setText(btn_cancel_Text);
        }

        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                clickDialog_confirm.ok();
                dismiss();
                break;
            case R.id.btn_cancel:
                clickDialog_confirm.cancel();
                dismiss();
                break;
        }
    }
}
