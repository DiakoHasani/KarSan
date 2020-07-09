package ir.tdaapp.karsan.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import ir.tdaapp.karsan.R;
import ir.tdaapp.karsan.ViewModel.VM_Short_Item;
import ir.tdaapp.karsan.Views.Fragment_Show_Details_Favorite;
import ir.tdaapp.karsan.Views.Fragment_Show_Details_Item;

public class FavoritItemAdapter extends RecyclerView.Adapter<FavoritItemAdapter.MyViewHolder> {

    Context context;
    List<VM_Short_Item> vals;

    public FavoritItemAdapter(Context context, List<VM_Short_Item> vals) {
        this.context = context;
        this.vals = vals;
    }

    public void RemoveAll() {
        vals = new ArrayList<>();
    }

    //در اینجا هنگامی که کاربر پیجینگ می کند آیتم های جدید اضافه می شود
    public void AddItems(List<VM_Short_Item> values) {
        for (int i=0;i<values.size();i++){
            int position=vals.size();
            vals.add(position,values.get(i));
            notifyItemInserted(position);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new  MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.lbl_Title.setText(vals.get(position).getTitle());
        holder.lbl_Price.setText(vals.get(position).getPrice());
        holder.lbl_Time.setText(vals.get(position).getTime());

        int TypeItem = vals.get(position).getType();
        holder.lbl_Type.setVisibility(View.GONE);

        if (TypeItem == 1) {
            holder.lbl_Type.setText(context.getResources().getString(R.string.Special));
            holder.lbl_Type.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
            holder.lbl_Type.setVisibility(View.VISIBLE);
        } else if (TypeItem == 2) {
            holder.lbl_Type.setText(context.getResources().getString(R.string.instantaneous));
            holder.lbl_Type.setBackgroundColor(context.getResources().getColor(R.color.colorCentral));
            holder.lbl_Type.setVisibility(View.VISIBLE);
        }

        Glide.with(context)
                .load(vals.get(position).getImage())
                .placeholder(R.drawable.loading_slider)
                .error(R.drawable.error_slider)
                .into(holder.image_Item);

        holder.Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("Id", vals.get(position).getId());

                Fragment_Show_Details_Favorite item=new Fragment_Show_Details_Favorite();
                item.setArguments(bundle);

                ((AppCompatActivity) context).getSupportFragmentManager()
                        .beginTransaction().setCustomAnimations(R.anim.fadein, R.anim.fadeout).add(R.id.Frame_Main, item,Fragment_Show_Details_Item.TAG).addToBackStack(null).commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return vals.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView lbl_Title, lbl_Price, lbl_Time, lbl_Type;
        ImageView image_Item;
        CardView Layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            FindItem(itemView);

            lbl_Title.setBackground(null);
            lbl_Price.setBackground(null);
            lbl_Time.setBackground(null);
        }

        void FindItem(View view) {
            lbl_Title = view.findViewById(R.id.lbl_Title);
            lbl_Price = view.findViewById(R.id.lbl_Price);
            lbl_Time = view.findViewById(R.id.lbl_Time);
            lbl_Type = view.findViewById(R.id.lbl_Type);
            image_Item = view.findViewById(R.id.image_Item);
            Layout = view.findViewById(R.id.Layout);
        }
    }
}
