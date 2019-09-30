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
import com.officialakbarali.fabiz.customer.sale.data.SalesReviewDetail;

import java.text.ParseException;
import java.util.List;

import static com.officialakbarali.fabiz.data.CommonInformation.TruncateDecimal;

public class SalesReviewAdapter extends RecyclerView.Adapter<SalesReviewAdapter.SalesReviewHolder> {
    private Context mContext;
    private SalesReviewAdapterOnClickListener mClickHandler;

    private List<SalesReviewDetail> salesList;


    public interface SalesReviewAdapterOnClickListener {
        void onClick(int idOfBill);
    }

    public SalesReviewAdapter(Context context, SalesReviewAdapterOnClickListener salesReviewAdapterOnClickListener) {
        this.mContext = context;
        this.mClickHandler = salesReviewAdapterOnClickListener;
    }


    @NonNull
    @Override
    public SalesReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.sales_review_view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new SalesReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesReviewHolder holder, int position) {
        SalesReviewDetail salesReview = salesList.get(position);


        String salesIdS = salesReview.getId() + "";
        if (salesIdS.length() > 10) {
            holder.billIdV.setText("Bill Id :" + salesIdS.substring(0, 7) + "...");
        } else {
            holder.billIdV.setText("Bill Id :" + salesIdS);
        }


        String dateS = salesReview.getDate();
        if (dateS.length() > 24) {
            holder.dateV.setText("Date :" + dateS.substring(0, 16) + "...");
        } else {
            holder.dateV.setText("Date :" + dateS);
        }


        String qtyS = salesReview.getQty() + "";
        if (qtyS.length() > 4) {
            holder.totQtyV.setText("Total Items :" + qtyS.substring(0, 1) + "..");
        } else {
            holder.totQtyV.setText("Total Items :" + qtyS);
        }


        String totalS = TruncateDecimal(salesReview.getTotal() + "");
        if (qtyS.length() > 17) {
            holder.totV.setText("Total Amount :" + totalS.substring(0, 13) + "...");
        } else {
            holder.totV.setText("Total Amount :" + totalS);
        }
    }

    @Override
    public int getItemCount() {
        if (salesList == null) return 0;
        return salesList.size();
    }

    public List<SalesReviewDetail> swapAdapter(List<SalesReviewDetail> c) {
        List<SalesReviewDetail> temp = salesList;
        salesList = c;
        notifyDataSetChanged();
        return temp;
    }


    class SalesReviewHolder extends RecyclerView.ViewHolder {
        TextView billIdV, dateV, totQtyV, totV;
        Button viewB;

        public SalesReviewHolder(@NonNull View itemView) {
            super(itemView);
            billIdV = itemView.findViewById(R.id.sales_review_view_bill_id);
            dateV = itemView.findViewById(R.id.sales_review_view_date);
            totQtyV = itemView.findViewById(R.id.sales_review_view_tot_items);
            totV = itemView.findViewById(R.id.sales_review_view_total);

            viewB = itemView.findViewById(R.id.sales_review_view_view);
            viewB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SalesReviewDetail salesReview = salesList.get(getAdapterPosition());
                    mClickHandler.onClick(salesReview.getId());
                }
            });
        }
    }
}
