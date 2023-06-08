package com.example.tqtruongcnpmct2.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tqtruongcnpmct2.databinding.ItemOptionProfileBinding;
import com.example.tqtruongcnpmct2.listener.IOnClickProfileitemListener;
import com.example.tqtruongcnpmct2.model.OptionProfile;

import java.util.List;

public class OptionProfileAdapter extends RecyclerView.Adapter<OptionProfileAdapter.OptionProfileViewHodel>{

    private List<OptionProfile> mListOption;
    public  IOnClickProfileitemListener iOnClickProfileitemListener;
    public void setData(List<OptionProfile> list){
        this.mListOption = list;
        notifyDataSetChanged();
    }
    public OptionProfileAdapter(IOnClickProfileitemListener iOnClickFoodItemListener){
        this.iOnClickProfileitemListener = iOnClickFoodItemListener;
    }
    @NonNull
    @Override
    public OptionProfileViewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOptionProfileBinding binding = ItemOptionProfileBinding.inflate(LayoutInflater.from(parent.getContext())
                ,parent,false);
        return new OptionProfileViewHodel(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionProfileViewHodel holder, int position) {
        OptionProfile profile = mListOption.get(position);
        if(profile == null){
            return;
        }
        holder.mbinding.profileImgSelect.setImageResource(profile.getImg());
        holder.mbinding.profileName.setText(profile.getNameoption());
        holder.mbinding.profileImgArrow.setImageResource(profile.getImgarrow());
        holder.mbinding.layoutProfile.setOnClickListener(v->{iOnClickProfileitemListener.onClickItemProfile(position);});
    }

    @Override
    public int getItemCount() {
        if (mListOption != null) {
            return mListOption.size();
        }
        return 0;
    }

    public static class OptionProfileViewHodel extends RecyclerView.ViewHolder{
        private final ItemOptionProfileBinding mbinding;
        public OptionProfileViewHodel(@NonNull ItemOptionProfileBinding mbinding) {
            super(mbinding.getRoot());
            this.mbinding = mbinding;
        }
    }
}
