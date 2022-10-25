package com.jl.myapplication.jl_home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jl.core.log.LogUtils;
import com.jl.core.utils.ListUtil;
import com.jl.core.utils.StringUtil;
import com.jl.core.utils.ToastUtils;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.ItemHomeBinding;
import com.jl.myapplication.databinding.ItemShoppingCarBinding;
import com.jl.myapplication.model.ShoppingCarBean;

import java.util.List;


public class ShoppingCarAdapter extends RecyclerView.Adapter<ShoppingCarAdapter.ViewHolder> {
    private Context mContext;
    private List<ShoppingCarBean> mList;


    public ShoppingCarAdapter(Context context) {
        super();
        this.mContext = context;
    }

    public void setList(List<ShoppingCarBean> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void addList(List<ShoppingCarBean> list) {
        if (ListUtil.isEmpty(this.mList)) {
            this.mList = list;
        } else {
            this.mList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public List<ShoppingCarBean> getList() {
        return mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_shopping_car, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mBinding.tvName.setText(mList.get(position).title);
        holder.mBinding.tvMoney.setText(mList.get(position).money.toString());
        holder.mBinding.tvNumber.setText(mList.get(position).number + "");
        if (!StringUtil.isEmpty(mList.get(position).imageUrl)){
            Glide.with(mContext).load(mList.get(position).imageUrl).into(holder.mBinding.ivHeadImage);
        }
        if (mList.get(position).isSelect) {
            holder.mBinding.ivSelect.setBackgroundResource(R.mipmap.confirm);
        } else {
            holder.mBinding.ivSelect.setBackgroundResource(R.mipmap.confirm_empty);
        }
        holder.mBinding.tvReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList.get(position).number == 1) {
                    ToastUtils.show("该宝贝不能减少了~");
                } else {
                    mList.get(position).number --;
                    holder.mBinding.tvNumber.setText(mList.get(position).number + "");
                    if (listener != null) {
                        listener.onNumberChangeClickListener(position,mList.get(position).number);
                    }
                }
            }
        });
        holder.mBinding.tvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.get(position).number ++;
                holder.mBinding.tvNumber.setText(mList.get(position).number + "");
                if (listener != null) {
                    listener.onNumberChangeClickListener(position,mList.get(position).number);
                }
            }
        });
        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(position,mList);
                }
            });
            holder.mBinding.ivSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.i("123");
                    mList.get(position).isSelect = !mList.get(position).isSelect;
                    listener.onSelectClickListener(position,!mList.get(position).isSelect);
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return ListUtil.getSize(this.mList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemShoppingCarBinding mBinding;
        View mItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            this.mItemView = itemView;
        }
    }

    private onItemClickListener listener;

    public interface onItemClickListener {
        void onItemClickListener(int position, List<ShoppingCarBean> mList);
        void onSelectClickListener(int position, boolean b);
        void onNumberChangeClickListener(int position, int number);
    }

    public void setListener(onItemClickListener listener) {
        this.listener = listener;
    }
}
