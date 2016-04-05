package gov.texas.tpwd.mobileranger.report.data;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class TreeReport implements Parcelable {

    private long id;
    private String mDate;
    private String mReportingEmployee;
    private List<TreeLocation> locations;

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public String getReportingEmployee() {
        return mReportingEmployee;
    }

    public void setReportingEmployee(String reportingEmployee) {
        this.mReportingEmployee = reportingEmployee;
    }

    public List<TreeLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<TreeLocation> locations) {
        this.locations = locations;
    }

    public void addLocation(TreeLocation location) {
        if(locations == null) {
            locations = new ArrayList<TreeLocation>();
        }
        locations.add(location);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.mDate);
        dest.writeString(this.mReportingEmployee);
        dest.writeTypedList(locations);
    }

    public TreeReport() {
    }

    protected TreeReport(Parcel in) {
        this.id = in.readLong();
        this.mDate = in.readString();
        this.mReportingEmployee = in.readString();
        this.locations = in.createTypedArrayList(TreeLocation.CREATOR);
    }

    public static final Parcelable.Creator<TreeReport> CREATOR = new Parcelable.Creator<TreeReport>() {
        @Override
        public TreeReport createFromParcel(Parcel source) {
            return new TreeReport(source);
        }

        @Override
        public TreeReport[] newArray(int size) {
            return new TreeReport[size];
        }
    };
}
