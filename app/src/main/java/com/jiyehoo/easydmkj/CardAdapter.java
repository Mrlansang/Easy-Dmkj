package com.jiyehoo.easydmkj;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private Context mContext;
    private List<ActCardView> mActCardViewList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView cardImg;
        TextView actName;
        TextView actAid;
        TextView actTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            cardImg = itemView.findViewById(R.id.iv_card_img);
            actName = itemView.findViewById(R.id.tv_card_name);
            actAid = itemView.findViewById(R.id.tv_card_aid);
            actTime = itemView.findViewById(R.id.tv_card_time);
        }
    }

    public CardAdapter(List<ActCardView> cardViewList) {
        mActCardViewList = cardViewList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.act_card_item, parent, false);

        //点击card
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                //int position = 0;
                ActCardView actCardView = mActCardViewList.get(position);
                Intent intent = new Intent(mContext, ActInfoActivity.class);
                intent.putExtra(ActInfoActivity.ACT_NAME, actCardView.getActCardName());
                intent.putExtra(ActInfoActivity.ACT_IMG, actCardView.getImgUrl());
                //传jsonString
                intent.putExtra(ActInfoActivity.JSON, actCardView.getJson());
                mContext.startActivity(intent);
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //长按载入aid、时间
                int position = holder.getAdapterPosition();
                ActCardView actCardView = mActCardViewList.get(position);
//                ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
//                ClipData clipData = ClipData.newPlainText("Aid", actCardView.getActCardAid());
//                clipboardManager.setPrimaryClip(clipData);

                TextView mTvAid = view.getRootView().findViewById(R.id.et_aid);
                mTvAid.setText(actCardView.getActCardAid());
                EditText mEtHour = view.getRootView().findViewById(R.id.et_hour);
                mEtHour.setText(actCardView.getActCardTime().substring(11, 13));
                EditText mEtMinute = view.getRootView().findViewById(R.id.et_minute);
                mEtMinute.setText(actCardView.getActCardTime().substring(14, 16));
                EditText mEtSecond = view.getRootView().findViewById(R.id.et_second);
                mEtSecond.setText("00");

                //如果alarm布局没有显示，则展示
//                LinearLayout mLlAlarmShow = view.getRootView().findViewById(R.id.ll_aid_and_alarm_layout);
//                if (mLlAlarmShow.getVisibility() == View.GONE) {
//                    mLlAlarmShow.setVisibility(View.VISIBLE);
//                }

                Toast.makeText(mContext, "载入活动信息", Toast.LENGTH_LONG).show();

                return true;
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActCardView actCardView = mActCardViewList.get(position);
        holder.actName.setText(actCardView.getActCardName());
        holder.actAid.setText(actCardView.getActCardAid());
        holder.actTime.setText(actCardView.getActCardTime());
        Glide.with(mContext).load(actCardView.getImgUrl()).into(holder.cardImg);
    }

    @Override
    public int getItemCount() {
        return mActCardViewList.size();
    }


}
