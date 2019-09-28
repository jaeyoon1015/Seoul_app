package com.example.bokjirock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class myAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ArrayList<policyInfo> policyInfoArrayList;
    private int positions;
    private likeDBHelper helper;
    private policyInfo item;
    private SharedPreferences mSharedPreference;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView text_title;
        private TextView text_content;
        private ImageButton scrap_button;

        MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            text_title = view.findViewById(R.id.tv_policyname);
            text_content = view.findViewById(R.id.tv_policycontent);
            scrap_button = view.findViewById(R.id.ib_scrapbutton);
            scrap_button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        Log.e("확인",policyInfoArrayList.get(pos).getpTitle());
                        if(!helper.isExist(policyInfoArrayList.get(pos).getId())) {
                            Drawable scrapstar = ResourcesCompat.getDrawable(context.getResources(), R.drawable.scrap_star, null);
                            scrap_button.setBackground(scrapstar);
                            helper.insert(policyInfoArrayList.get(pos).getId(), policyInfoArrayList.get(pos).getpTitle(), policyInfoArrayList.get(pos).getpContent());
                            Toast.makeText(context, "관심정책에 추가하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(context, "이미 추가하신 정책입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }


        @Override
        public void onClick(View view) {
            positions=getAdapterPosition();
            String add=getdetailnum(policyInfoArrayList,positions);
            Intent intent=new Intent(context, detailInfo.class);
            intent.putExtra("address",add);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public myAdapter(Context context, ArrayList<policyInfo> policyInfoArrayList) {
        this.context = context;
        this.policyInfoArrayList = policyInfoArrayList;
        helper=new likeDBHelper(context, "Likes.db", null, 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_cardview, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final MyViewHolder myViewHolder = (MyViewHolder) holder;
        item = policyInfoArrayList.get(position);

        myViewHolder.text_title.setText(policyInfoArrayList.get(position).getpTitle());
        myViewHolder.text_content.setText(policyInfoArrayList.get(position).getpContent());

        if(checkfavorite(item)){
            Drawable scrapstar = ResourcesCompat.getDrawable(context.getResources(), R.drawable.scrap_star, null);
            ((MyViewHolder) holder).scrap_button.setBackground(scrapstar);
            ((MyViewHolder) holder).scrap_button.setTag("filled");
        }else{
            Drawable starEmpty = ResourcesCompat.getDrawable(context.getResources(), R.drawable.scrap_button,null);;
            ((MyViewHolder) holder).scrap_button.setBackground(starEmpty);
            ((MyViewHolder) holder).scrap_button.setTag("empty");
        }
    }


    @Override
    public int getItemCount() {
        return policyInfoArrayList.size();
    }

    public String getdetailnum(List<policyInfo> policyInfos, int position) {

        List<policyInfo> policyInfos1 = policyInfos;

        String address= policyInfos1.get(position).getId();
        return address;
    }

    public boolean checkfavorite(policyInfo item){
        boolean check = false;
		String ids=helper.getidResult();

		if (!ids.equals("")) {
            String[] id=ids.split(";");
            for (int i=0;i<id.length;i++) {
                if (id[i].equals(item.getId())) {
                    check = true;
                    break;
                }
            }
        }
		return check;
    }
}