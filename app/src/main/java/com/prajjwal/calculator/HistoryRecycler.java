package com.prajjwal.calculator;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class HistoryRecycler extends RecyclerView.Adapter<HistoryRecycler.HistoryViewHolder> {

    public ArrayList<HistoryItems> historyItems;
    private LayoutInflater layoutInflater;
    private Context context;

    public HistoryRecycler(ArrayList<HistoryItems> historyItems, Context context) {
        this.historyItems = historyItems;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryRecycler.HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.history_recycler_layout, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HistoryRecycler.HistoryViewHolder holder, final int position) {
        String exp = historyItems.get(position).getExpression();
        String rst = historyItems.get(position).getResult();
        if (exp == null) {
            holder.expressionHst.setVisibility(View.GONE);
            holder.expressionHst.setText("");
        }
        else {
            holder.expressionHst.setVisibility(View.VISIBLE);
            holder.expressionHst.setText(exp);
        }
        if (rst == null) {
            holder.resultHst.setVisibility(View.GONE);
            holder.resultHst.setText("");
        }
        else {
            holder.resultHst.setVisibility(View.VISIBLE);
            holder.resultHst.setText(rst);
        }
        holder.expressionHst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) context).setHistMethod(position, holder.expressionHst.getText().toString());
                ((MainActivity) context).setHist(holder.expressionHst.getText().toString(), true);
            }
        });
        holder.resultHst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) context).setHist(holder.resultHst.getText().toString(), false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView expressionHst, resultHst;
        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            expressionHst = itemView.findViewById(R.id.expression);
            resultHst = itemView.findViewById(R.id.resultExp);
            expressionHst.setOnCreateContextMenuListener(this);
            resultHst.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            if (view.getId() == expressionHst.getId()) {
                contextMenu.add(this.getAdapterPosition(), 121, 0, "COPY");
                contextMenu.add(this.getAdapterPosition(), 122, 1, "REMOVE");
            }
            else if (view.getId() == resultHst.getId()){
                contextMenu.add(this.getAdapterPosition(), 123, 0, "COPY");
                contextMenu.add(this.getAdapterPosition(), 124, 1, "REMOVE");
            }
        }
    }
}
