package ir.tdaapp.karsanjob.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import ir.tdaapp.karsanjob.R;
import ir.tdaapp.karsanjob.ViewModel.VM_CV_Short;
import ir.tdaapp.karsanjob.Views.Fragment_Show_Details_CV;

public class CVAdapter extends RecyclerView.Adapter<CVAdapter.MyViewHolder> {

    Context context;
    List<VM_CV_Short> vals;

    public CVAdapter(Context context, List<VM_CV_Short> vals) {
        this.context = context;
        this.vals = vals;
    }

    public void Add(List<VM_CV_Short> cvs){
        for (int i=0;i<cvs.size();i++){
            int position=vals.size();
            vals.add(position,cvs.get(i));
            notifyItemInserted(position);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_cv, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        try {
            holder.lbl_FullName.setText(context.getResources().getString(R.string.Name)+" "+vals.get(position).getName());
            holder.lbl_Madrak.setText(context.getResources().getString(R.string.Madrak2)+" "+vals.get(position).getMadrak());
            holder.lbl_Major.setText(context.getResources().getString(R.string.Major2)+" "+vals.get(position).getMajor());



            Glide.with(context)
                    .load(vals.get(position).getImage())
                    .placeholder(R.drawable.cv_holder)
                    .error(R.drawable.cv_image)
                    .into(holder.img);

            holder.Layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle=new Bundle();

                    bundle.putInt("Id",vals.get(position).getId());

                    Fragment_Show_Details_CV fragment_show_details_cv=new Fragment_Show_Details_CV();

                    fragment_show_details_cv.setArguments(bundle);

                    ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction()
                            .add(R.id.Frame_Main,fragment_show_details_cv)
                            .addToBackStack(null).commit();
                }
            });
        }catch (Exception e){
        }
    }

    @Override
    public int getItemCount() {
        return vals.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView lbl_FullName, lbl_Madrak, lbl_Major;
        CircleImageView img;
        LinearLayout Layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                FindItem(itemView);

                lbl_FullName.setBackground(null);
                lbl_Madrak.setBackground(null);
                lbl_Major.setBackground(null);
            }catch (Exception e){
            }
        }

        void FindItem(View view) {
            lbl_FullName = view.findViewById(R.id.lbl_FullName);
            lbl_Madrak = view.findViewById(R.id.lbl_Madrak);
            lbl_Major = view.findViewById(R.id.lbl_Major);
            Layout = view.findViewById(R.id.Layout);
            img = view.findViewById(R.id.img);
        }
    }
}
