package ir.tdaapp.karsanjob.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.tdaapp.karsanjob.R;
import ir.tdaapp.karsanjob.ViewModel.VM_Major;

public class MajorAdapter extends RecyclerView.Adapter<MajorAdapter.MyViewHolder> {

    Context context;
    List<VM_Major> majors;

    public MajorAdapter(Context context) {
        this.context = context;
        majors=new ArrayList<>();
    }

    public void Add(VM_Major major){
        boolean addMajor=true;

        for (int i=0;i<majors.size();i++){
            if (majors.get(i).getId()==major.getId()){
                addMajor=false;
            }
        }
        if (addMajor){
            int Position=majors.size();
            majors.add(Position,major);
            notifyDataSetChanged();
        }
    }

    public void AddAll(List<VM_Major> major){
        for (VM_Major i : major){
            majors.add(i);
        }
        notifyDataSetChanged();
    }

    public List<VM_Major> GetAll(){
        return majors;
    }

    public void RemoveAll(){
        majors=new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<Integer>GetAllId(){
        List<Integer>vals=new ArrayList<>();

        for (int i=0;i<majors.size();i++){
            vals.add(majors.get(i).getId());
        }
        return vals;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_major,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.Title.setText(majors.get(position).getTitle());

        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                majors.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return majors.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout close;
        TextView Title;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            close=itemView.findViewById(R.id.close);
            Title=itemView.findViewById(R.id.Title);
        }
    }
}
