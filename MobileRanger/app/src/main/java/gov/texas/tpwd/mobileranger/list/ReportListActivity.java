package gov.texas.tpwd.mobileranger.list;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import gov.texas.tpwd.mobileranger.R;
import gov.texas.tpwd.mobileranger.db.TreeReportManager;
import gov.texas.tpwd.mobileranger.report.data.TreeReport;
import gov.texas.tpwd.mobileranger.report.ui.ReportActivity;

public class ReportListActivity extends AppCompatActivity {

    private Button newFormButton;
    private RecyclerView recyclerView;
    private ReportAdapter adapter;
    private TreeReportManager treeReportManager;
    private DateFormat format = new SimpleDateFormat("MMMM d yyyy");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupButton();

    }

    @Override
    protected void onResume() {
        super.onResume();
        List<TreeReport> reports = getTreeReports();
        adapter = new ReportAdapter(listener, reports);
        recyclerView.setAdapter(adapter);

    }

    private List<TreeReport> getTreeReports() {
        treeReportManager = TreeReportManager.getInstance(this);
        List<TreeReport> reports = treeReportManager.getTreeReports();
        Collections.sort(reports, comparator);
        return reports;
    }

    private void setupButton() {
        newFormButton = (Button) findViewById(R.id.addButton);
        newFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportListActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });
    }

    ReportAdapter.OnReportClickListener listener = new ReportAdapter.OnReportClickListener() {
        @Override
        public void onClick(TreeReport report) {
            Intent intent = new Intent(ReportListActivity.this, ReportActivity.class);
            intent.putExtra(ReportActivity.TREE_REPORT_EXTRA, report);
            startActivity(intent);
        }
    };

    Comparator comparator = new Comparator<TreeReport>() {
        @Override
        public int compare(TreeReport report1, TreeReport report2) {
            try {
                if(report2 == null || report2.getDate() == null) {
                    return -1;
                } else if(report1 == null || report1.getDate() == null) {
                    return 1;
                }
                Date date1 = format.parse(report1.getDate());
                Date date2 = format.parse(report2.getDate());
                if(date1.before(date2)) {
                    return 1;
                }
                return -1;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }

    };

}
