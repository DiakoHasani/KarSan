package ir.tdaapp.karsanjob.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ir.tdaapp.karsanjob.R;
import ir.tdaapp.karsanjob.Utility.BaseFragment;

public class Fragment_Rules_And_Support extends BaseFragment {

    LinearLayout Back;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_rules_and_support,container,false);

        Back=view.findViewById(R.id.Back);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowBottomBar(false);
    }
}
