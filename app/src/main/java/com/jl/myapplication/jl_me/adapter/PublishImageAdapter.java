package com.jl.myapplication.jl_me.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jl.core.utils.ListUtil;
import com.jl.core.utils.StringUtil;
import com.jl.myapplication.R;
import com.jl.myapplication.databinding.ItemPublishImageBinding;
import com.jl.myapplication.jl_me.activity.PublishActivity;

import java.util.List;



public class PublishImageAdapter extends RecyclerView.Adapter<PublishImageAdapter.ViewHolder> {
    private PublishActivity context;
    private List<String> list;

    public PublishImageAdapter(PublishActivity context) {
        super();
        this.context = context;
    }

    public void setList(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void addList(List<String> list) {
        if (ListUtil.isEmpty(this.list)) {
            this.list = list;
        } else {
            this.list.addAll(list);
        }
        notifyDataSetChanged();
    }

    public List<String> getList() {
        return list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_publish_image, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!StringUtil.isEmpty(list.get(position))) {
            Glide.with(holder.binding.ivImage).load(list.get(position)).into(holder.binding.ivImage);
        }

        holder.binding.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClickListener(position,list);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ListUtil.getSize(this.list);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemPublishImageBinding binding;


        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            this.itemView = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onItemClickListener(getPosition(),list);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    list.remove(getPosition());
                    notifyDataSetChanged();
                    return false;
                }
            });
        }
    }

    private onItemClickListener listener;

    public interface onItemClickListener {
        void onItemClickListener(int position, List<String> image);
    }

    public void setListener(onItemClickListener listener) {
        this.listener = listener;
    }


}
