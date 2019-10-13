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

    private boolean FROM_PAYMENT_PAGE;

    public interface SalesReviewAdapterOnClickListener {
        void onClick(SalesReviewDetail salesReviewDetail);
    }

    public SalesReviewAdapter(Context context, SalesReviewAdapterOnClickListener salesReviewAdapterOnClickListener, boolean forPayment) {
        this.mContext = context;
        this.mClickHandler = salesReviewAdapterOnClickListener;
        this.FROM_PAYMENT_PAGE = forPayment;
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
        if (totalS.length() > 17) {
            holder.totV.setText("Total Amount :" + totalS.substring(0, 13) + "...");
        } else {
            holder.totV.setText("Total Amount :" + totalS);
        }

        String paidS = TruncateDecimal(salesReview.getPaid() + "");
        if (paidS.length() > 13) {
            holder.paidV.setText("Paid Amount :" + paidS.substring(0, 13) + "...");
        } else {
            holder.paidV.setText("Paid Amount :" + paidS);
        }

        String dueS = TruncateDecimal(salesReview.getDue() + "");
        if (dueS.length() > 13) {
            holder.dueV.setText("Due Amount :" + dueS.substring(0, 13) + "...");
        } else {
            holder.dueV.setText("Due Amount :" + dueS);
        }

        String returnS = TruncateDecimal(salesReview.getReturnedAmount() + "");
        if (returnS.length() > 13) {
            holder.returnV.setText("Return Amount :" + returnS.substring(0, 13) + "...");
        } else {
            holder.returnV.setText("Return Amount :" + returnS);
        }

        String currentTotalS = TruncateDecimal(salesReview.getCurrentTotal() + "");
        if (currentTotalS.length() > 13) {
            holder.cTotalV.setText("Current Total Amount :" + currentTotalS.substring(0, 13) + "...");
        } else {
            holder.cTotalV.setText("Current Total Amount :" + currentTotalS);
        }

        if(FROM_PAYMENT_PAGE){
            holder.viewB.setText("Pay this Bill");
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
        TextView billIdV, dateV, totQtyV, totV, paidV, dueV, returnV, cTotalV;
        Button viewB;

        public SalesReviewHolder(@NonNull View itemView) {
            super(itemView);
            billIdV = itemView.findViewById(R.id.sales_review_view_bill_id);
            dateV = itemView.findViewById(R.id.sales_review_view_date);
            totQtyV = itemView.findViewById(R.id.sales_review_view_tot_items);
            totV = itemView.findViewById(R.id.sales_review_view_total);


            paidV = itemView.findViewById(R.id.sales_review_view_paid);
            dueV = itemView.findViewById(R.id.sales_review_due);
            returnV = itemView.findViewById(R.id.sales_review_view_return_total);
            cTotalV = itemView.findViewById(R.id.sales_review_view_current_total);

            viewB = itemView.findViewById(R.id.sales_review_view_view);
            viewB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickHandler.onClick(salesList.get(getAdapterPosition()));
                }
            });
        }
    }
}
