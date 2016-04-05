package gov.texas.tpwd.mobileranger.list;


import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import gov.texas.tpwd.mobileranger.R;
import gov.texas.tpwd.mobileranger.report.data.TreeReport;

public class ReportAdapter  extends RecyclerView.Adapter<ReportAdapter.ReportHolder> {

    private List<TreeReport> reports;
    private OnReportClickListener listener;

    public interface OnReportClickListener {
        void onClick(TreeReport report);
    }

    public ReportAdapter(OnReportClickListener listener, List<TreeReport> reports) {
        this.listener = listener;
        this.reports = reports;
    }

    @Override
    public ReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_report, parent, false);
        ReportHolder holder = new ReportHolder(view);
        holder.reportDate = (TextView) view.findViewById(R.id.reportDate);

        return holder;
    }

    @Override
    public void onBindViewHolder(ReportHolder holder, int position) {
        final TreeReport report = reports.get(position);
        String date = report.getDate();
        if(TextUtils.isEmpty(date)) {
            date = holder.itemView.getContext().getString(R.string.no_date_title);
        }
        holder.reportDate.setText(date);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onClick(report);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(reports == null) {
            return 0;
        }
        return reports.size();
    }

    public static class ReportHolder extends RecyclerView.ViewHolder {

        TextView reportDate;

        public ReportHolder(View itemView) {
            super(itemView);
        }
    }
}
