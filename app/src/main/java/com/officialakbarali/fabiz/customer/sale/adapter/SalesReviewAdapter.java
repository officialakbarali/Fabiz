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

        import java.util.List;

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

        holder.billIdV.setText("Bill Id :" + salesReview.getId());
        holder.dateV.setText("Date :" + salesReview.getDate());
        holder.totQtyV.setText("Total Items :" + salesReview.getQty());
        holder.totV.setText("Total Amount :" + salesReview.getTotal());
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
