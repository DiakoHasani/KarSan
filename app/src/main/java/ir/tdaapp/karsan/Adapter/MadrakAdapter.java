package ir.tdaapp.karsan.Adapter;

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
import ir.tdaapp.karsan.R;
import ir.tdaapp.karsan.ViewModel.VM_Madrak;
import ir.tdaapp.karsan.ViewModel.VM_Major;

public class MadrakAdapter extends RecyclerView.Adapter<MadrakAdapter.MyViewHolder> {

    Context context;
    List<VM_Madrak> madraks;

    public MadrakAdapter(Context context) {
        this.context = context;
        madraks=new ArrayList<>();
    }

    public void Add(VM_Madrak madrak){
        boolean addMajor=true;

        for (int i=0;i<madraks.size();i++){
            if (madraks.get(i).getId()==madrak.getId()){
                addMajor=false;
            }
        }
        if (addMajor){
            int Position=madraks.size();
            madraks.add(Position,madrak);
            notifyDataSetChanged();
        }
    }

    public void AddAll(List<VM_Madrak>madrak){
        for (VM_Madrak i : madrak){
            madraks.add(i);
        }
        notifyDataSetChanged();
    }

    public List<VM_Madrak>GetAll(){
        return madraks;
    }

    public void RemoveAll(){
        madraks=new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<Integer>GetAllId(){
        List<Integer>vals=new ArrayList<>();

        for (int i=0;i<madraks.size();i++){
            vals.add(madraks.get(i).getId());
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
    public void onBindViewHolder(@NonNull MyViewHolder holder,final int position) {
        holder.Title.setText(madraks.get(position).getTitle());

        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                madraks.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return madraks.size();
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
