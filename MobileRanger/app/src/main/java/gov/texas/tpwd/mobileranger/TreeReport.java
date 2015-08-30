package gov.texas.tpwd.mobileranger;


import java.util.ArrayList;
import java.util.List;

public class TreeReport {

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
}
