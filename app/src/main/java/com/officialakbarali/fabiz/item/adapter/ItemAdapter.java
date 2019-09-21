package com.officialakbarali.fabiz.item.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.item.data.ItemDetail;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private Context mContext;
    private ItemAdapterOnClickListener mClickHandler;
    private List<ItemDetail> itemDetailList;

    public interface ItemAdapterOnClickListener {
        void onClick(String mItemCurrentRaw, String mItemSelectedName);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.item_home_view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemDetail itemDetail = itemDetailList.get(position);

        String id = "" + itemDetail.getId();
        if (id.length() > 20) {
            holder.itemId.setText("Id: " + id.substring(0, 16) + "...");
        } else {
            holder.itemId.setText("Id: " + id);
        }

        String name = itemDetail.getName();
        if (name.length() > 40) {
            holder.itemName.setText("Name: " + name.substring(0, 36) + "...");
        } else {
            holder.itemName.setText("Name: " + name);
        }

        String brand = itemDetail.getBrand();
        if (brand.length() > 15) {
            holder.itemBrand.setText("Brand: " + brand.substring(0, 11) + "...");
        } else {
            holder.itemBrand.setText("Brand: " + brand);
        }


        String category = itemDetail.getCategory();
        if (category.length() > 15) {
            holder.itemCategory.setText("Category: " + category.substring(0, 11) + "...");
        } else {
            holder.itemCategory.setText("Category: " + category);
        }

        String price = itemDetail.getPrice() + "";
        if (price.length() > 20) {
            holder.itemPrice.setText("Price: " + price.substring(0, 16) + "...");
        } else {
            holder.itemPrice.setText("Price: " + price);
        }
    }

    @Override
    public int getItemCount() {
        if (itemDetailList == null) return 0;
        return itemDetailList.size();
    }

    public ItemAdapter(Context context, ItemAdapterOnClickListener itemAdapterOnClickListener) {
        this.mContext = context;
        this.mClickHandler = itemAdapterOnClickListener;
    }

    public List<ItemDetail> swapAdapter(List<ItemDetail> c) {
        List<ItemDetail> temp = itemDetailList;
        itemDetailList = c;
        notifyDataSetChanged();
        return temp;
    }


    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemId, itemName, itemBrand, itemCategory, itemPrice;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemId = itemView.findViewById(R.id.id);
            itemName = itemView.findViewById(R.id.name);
            itemBrand = itemView.findViewById(R.id.brand);
            itemCategory = itemView.findViewById(R.id.category);
            itemPrice = itemView.findViewById(R.id.price);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            ItemDetail itemDetail = itemDetailList.get(adapterPosition);
            String idCurrentSelected = "" + itemDetail.getId();
            mClickHandler.onClick(idCurrentSelected, itemDetail.getName());
        }
    }
}
