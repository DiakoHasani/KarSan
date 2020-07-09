package ir.tdaapp.karsan.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import ir.tdaapp.karsan.MainActivity;
import ir.tdaapp.karsan.R;
import ir.tdaapp.karsan.ViewModel.VM_ShortNews;
import ir.tdaapp.karsan.Views.Fragment_Show_Details_News;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    Context context;
    List<VM_ShortNews> news;

    public NewsAdapter(Context context, List<VM_ShortNews> news) {
        this.context = context;
        this.news = news;
    }

    public void Add(List<VM_ShortNews> news1){
        for (int i=0;i<news1.size();i++){
            int position=news.size();
            news.add(position,news1.get(i));
            notifyItemInserted(position);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_news,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.lbl_Title.setText(news.get(position).getTitle());
        holder.lbl_AbstractNews.setText(news.get(position).getAbstractNews());
        holder.lbl_DateInsert.setText(news.get(position).getDateInsert());

        Glide.with(context)
                .load(news.get(position).getImage())
                .placeholder(R.drawable.news_holder)
                .error(R.drawable.error_image_news)
                .into(holder.img_News);

        holder.Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment_Show_Details_News fragment_show_details_news=new Fragment_Show_Details_News();
                Bundle bundle=new Bundle();
                bundle.putInt("Id",news.get(position).getId());
                fragment_show_details_news.setArguments(bundle);

                ((AppCompatActivity)context).getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.Frame_Main,fragment_show_details_news)
                        .addToBackStack(null).commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img_News;
        TextView lbl_Title,lbl_AbstractNews,lbl_DateInsert;
        CardView Layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            FindItem(itemView);

            lbl_Title.setBackground(null);
        }

        void FindItem(View view){
            img_News=view.findViewById(R.id.img_News);
            lbl_Title=view.findViewById(R.id.lbl_Title);
            lbl_AbstractNews=view.findViewById(R.id.lbl_AbstractNews);
            lbl_DateInsert=view.findViewById(R.id.lbl_DateInsert);
            Layout=view.findViewById(R.id.Layout);
        }
    }

}
