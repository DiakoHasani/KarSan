package ir.tdaapp.karsan.Utility;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;

///در اینجا هنگامی که کاربر قیمت را وارد می کند سه عدد از هم جدا می شوند

public class txt_Price_Watcher implements TextWatcher {

    private EditText editText;

    public txt_Price_Watcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        editText.removeTextChangedListener(this);

        String s = editText.getText().toString();

        s = s.replace(",", "");
        s = s.replace("٬", "");
        s = Replace.Number_fn_To_en(s);

        if (s.length() > 0) {
            DecimalFormat sdd = new DecimalFormat("#,###");
            Double doubleNumber = Double.parseDouble(s);

            String format = sdd.format(doubleNumber);
            editText.setText(format);
            editText.setSelection(format.length());

        }
        editText.addTextChangedListener(this);
    }
}
