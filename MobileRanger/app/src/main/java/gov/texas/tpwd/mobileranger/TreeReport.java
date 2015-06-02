package gov.texas.tpwd.mobileranger;


public class TreeReport {

    private String mDate;
    private String mReportingEmployee;
    private String mLocation;
    private String mDetails;
    private String mActionTaken;
    private String mBeforeImagePath;
    private String mAfterImagePath;

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
