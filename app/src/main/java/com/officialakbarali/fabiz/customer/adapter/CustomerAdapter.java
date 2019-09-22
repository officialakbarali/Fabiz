package com.officialakbarali.fabiz.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.customer.data.CustomerDetail;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    private Context mContext;
    private CustomerAdapterOnClickListener mClickHandler;

    private List<CustomerDetail> customerList;

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
        CustomerDetail customer = customerList.get(position);

        String id = "" + customer.getId();
        if (id.length() > 10) {
            holder.custId.setText(id.substring(0, 6) + "...");
        } else {
            holder.custId.setText(id);
        }

        String name = customer.getName();
        if (name.length() > 40) {
            holder.custName.setText(name.substring(0, 36) + "...");
        } else {
            holder.custName.setText(name);
        }

        String phone = customer.getPhone();
        if (phone.length() > 13) {
            holder.custPhone.setText(phone.substring(0, 9) + "...");
        } else {
            holder.custPhone.setText(phone);
        }


        String address = customer.getAddress();
        if (address.length() > 33) {
            holder.custAddress.setText(address.substring(0, 29) + "...");
        } else {
            holder.custAddress.setText(address);
        }

        String email = customer.getEmail();
        if (email.length() > 30) {
            holder.custEmail.setText(email.substring(0, 26) + "...");
        } else {
            holder.custEmail.setText(email);
        }
    }

    @Override
    public int getItemCount() {
        if (customerList == null) return 0;
        return customerList.size();
    }

    public List<CustomerDetail> swapAdapter(List<CustomerDetail> c) {
        List<CustomerDetail> temp = customerList;
        customerList = c;
        notifyDataSetChanged();
        return temp;
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

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            CustomerDetail customer = customerList.get(adapterPosition);
            String idCurrentSelected = "" + customer.getId();
            mClickHandler.onClick(idCurrentSelected, customer.getName());
        }
    }
}
