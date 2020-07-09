package ir.tdaapp.karsan.Utility;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.EditText;

import androidx.core.content.res.ResourcesCompat;
import ir.tdaapp.karsan.R;

public class Font {

    public static void SetFontNumber(EditText txt, Context context){
        Typeface typeface = ResourcesCompat.getFont(context, R.font.b_homa);
        txt.setTypeface(typeface);
    }

    public static void SetFontText(EditText txt, Context context){
        Typeface typeface = ResourcesCompat.getFont(context, R.font.iran_sans);
        txt.setTypeface(typeface);
    }
}
