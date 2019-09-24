package com.officialakbarali.fabiz.customer.sale.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.customer.sale.data.Cart;

import java.util.List;

import static com.officialakbarali.fabiz.data.CommonInformation.TruncateDecimal;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.SalesViewHolder> {
    private Context mContext;
    private SalesAdapterOnClickListener mClickHandler;

    private List<Cart> cartList;

    public interface SalesAdapterOnClickListener {
        void onClick(int indexToBeRemoved);
    }

    public SalesAdapter(Context context, SalesAdapterOnClickListener salesAdapterOnClickListener) {
        this.mContext = context;
        this.mClickHandler = salesAdapterOnClickListener;
    }

    @NonNull
    @Override
    public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.customer_sales_view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new SalesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesViewHolder holder, int position) {
        Cart cart = cartList.get(position);

        String name = cart.getItemId() + " / " + cart.getName() + " / " + cart.getBrand() + " / " + cart.getCategory();
        if (name.length() > 130) {
            holder.itemDetail.setText(name.substring(0, 126) + "...");
        } else {
            holder.itemDetail.setText(name);
        }

        String price = TruncateDecimal(cart.getPrice() + "");
        if (price.length() > 80) {
            holder.itemPrice.setText(price.substring(0, 76) + "...");
        } else {
            holder.itemPrice.setText(price);
        }

        String quantity = cart.getQty() + "";
        if (quantity.length() > 30) {
            holder.itemQty.setText(quantity.substring(0, 26) + "...");
        } else {
            holder.itemQty.setText(quantity);
        }

        String total = TruncateDecimal(cart.getTotal() + "");
        if (total.length() > 80) {
            holder.itemTotal.setText(total.substring(0, 76) + "...");
        } else {
            holder.itemTotal.setText(total);
        }
    }

    @Override
    public int getItemCount() {
        if (cartList == null) return 0;
        return cartList.size();
    }

    public List<Cart> swapAdapter(List<Cart> c) {
        List<Cart> temp = cartList;
        cartList = c;
        notifyDataSetChanged();
        return temp;
    }

    class SalesViewHolder extends RecyclerView.ViewHolder {
        TextView itemDetail, itemPrice, itemQty, itemTotal;
        Button removeBtn;

        public SalesViewHolder(@NonNull View itemView) {
            super(itemView);

            removeBtn = itemView.findViewById(R.id.cust_sale_view_rmv_btn);
            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickHandler.onClick(getAdapterPosition());
                }
            });

            itemDetail = itemView.findViewById(R.id.cust_sale_view_detail);
            itemPrice = itemView.findViewById(R.id.cust_sale_view_price);
            itemQty = itemView.findViewById(R.id.cust_sale_view_qty);
            itemTotal = itemView.findViewById(R.id.cust_sale_view_total);
        }
    }
}
