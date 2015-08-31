package gov.texas.tpwd.mobileranger;

import io.realm.RealmObject;

/**
 * Created by kris on 7/4/15.
 */
public class TreeLocation {

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
}
