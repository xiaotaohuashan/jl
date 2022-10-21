package com.jl.myapplication.jl_message.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jl.core.utils.DateUtils;
import com.jl.core.utils.ListUtil;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.ItemHomeBinding;
import com.jl.myapplication.databinding.ItemMessageBinding;

import java.util.List;

import cn.jpush.im.android.api.model.Conversation;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context mContext;
    private List<Conversation> mList;


    public MessageAdapter(Context context) {
        super();
        this.mContext = context;
    }

    public void setList(List<Conversation> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void addList(List<Conversation> list) {
        if (ListUtil.isEmpty(this.mList)) {
            this.mList = list;
        } else {
            this.mList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public List<Conversation> getList() {
        return mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_message, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mBinding.tvName.setText(mList.get(position).getTitle());

        holder.mBinding.tvMessage.setText(mList.get(position).getLatestText());
        holder.mBinding.tvTime.setText(DateUtils.timeStampToDay(mList.get(position).getLastMsgDate()));
        if (mList.get(position).getAvatarFile() != null){
            Glide.with(mContext).load(mList.get(position).getAvatarFile()).into(holder.mBinding.iv);
        }
        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(position,mList);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return ListUtil.getSize(this.mList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemMessageBinding mBinding;
        View mItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            this.mItemView = itemView;
        }
    }

    private onItemClickListener listener;

    public interface onItemClickListener {
        void onItemClickListener(int position, List<Conversation> mList);
    }

    public void setListener(onItemClickListener listener) {
        this.listener = listener;
    }
}
