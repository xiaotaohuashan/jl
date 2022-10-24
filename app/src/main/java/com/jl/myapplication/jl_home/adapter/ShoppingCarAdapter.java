package com.jl.myapplication.jl_home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jl.core.utils.ListUtil;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.ItemHomeBinding;

import java.util.List;


public class ShoppingCarAdapter extends RecyclerView.Adapter<ShoppingCarAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mList;


    public ShoppingCarAdapter(Context context) {
        super();
        this.mContext = context;
    }

    public void setList(List<String> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void addList(List<String> list) {
        if (ListUtil.isEmpty(this.mList)) {
            this.mList = list;
        } else {
            this.mList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public List<String> getList() {
        return mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_home, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mBinding.tvName.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return ListUtil.getSize(this.mList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemHomeBinding mBinding;
        View mItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            this.mItemView = itemView;
        }
    }

    private onItemClickListener listener;

    public interface onItemClickListener {
        void onItemClickListener(int position, List<String> mList);
    }

    public void setListener(onItemClickListener listener) {
        this.listener = listener;
    }
}
