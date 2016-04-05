package gov.texas.tpwd.mobileranger.report.data;

import android.os.Parcel;
import android.os.Parcelable;

public class TreeLocation implements Parcelable {

    private long id;
    private long treeReportId;
    private String mLocation;
    private String mDetails;
    private String mActionTaken;
    private String mBeforeImagePath;
    private String mAfterImagePath;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTreeReportId() {
        return treeReportId;
    }

    public void setTreeReportId(long treeReportId) {
        this.treeReportId = treeReportId;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation = location;
    }

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        this.mDetails = details;
    }

    public String getActionTaken() {
        return mActionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.mActionTaken = actionTaken;
    }

    public String getBeforeImagePath() {
        return mBeforeImagePath;
    }

    public void setBeforeImagePath(String beforeImagePath) {
        this.mBeforeImagePath = beforeImagePath;
    }

    public String getAfterImagePath() {
        return mAfterImagePath;
    }

    public void setAfterImagePath(String afterImagePath) {
        this.mAfterImagePath = afterImagePath;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.treeReportId);
        dest.writeString(this.mLocation);
        dest.writeString(this.mDetails);
        dest.writeString(this.mActionTaken);
        dest.writeString(this.mBeforeImagePath);
        dest.writeString(this.mAfterImagePath);
    }

    public TreeLocation() {
    }

    protected TreeLocation(Parcel in) {
        this.id = in.readLong();
        this.treeReportId = in.readLong();
        this.mLocation = in.readString();
        this.mDetails = in.readString();
        this.mActionTaken = in.readString();
        this.mBeforeImagePath = in.readString();
        this.mAfterImagePath = in.readString();
    }

    public static final Parcelable.Creator<TreeLocation> CREATOR = new Parcelable.Creator<TreeLocation>() {
        @Override
        public TreeLocation createFromParcel(Parcel source) {
            return new TreeLocation(source);
        }

        @Override
        public TreeLocation[] newArray(int size) {
            return new TreeLocation[size];
        }
    };
}
