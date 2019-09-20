package com.officialakbarali.fabiz.customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.officialakbarali.fabiz.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>{
    private Context mContext;
    private CustomerAdapterOnClickListener mClickHandler;

    public interface CustomerAdapterOnClickListener {
        void onClick(String mCustomerCurrentRaw, String mCustomerSelectedName);
    }

    public CustomerAdapter(Context context, CustomerAdapterOnClickListener customerAdapterOnClickListener) {
        mContext = context;
        mClickHandler = customerAdapterOnClickListener;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.customer_home_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class CustomerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView custId, custName, custPhone, custEmail, custAddress;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            custId = itemView.findViewById(R.id.id);
            custName = itemView.findViewById(R.id.name);
            custPhone = itemView.findViewById(R.id.phone);
            custEmail = itemView.findViewById(R.id.email);
            custAddress = itemView.findViewById(R.id.address);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
//            Customer customer = customerList.get(adapterPosition);
//            String idCurrentSelected = "" + customer.getId();
//            mClickHandler.onClick(idCurrentSelected, customer.getName());
        }
    }
}
