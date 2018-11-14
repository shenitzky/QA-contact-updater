import java.io.IOException;
import java.io.Reader;
import java.util.List;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Key;

public class QaContactData extends GenericJson {
    @Key("startDay")
    private String startDay;
    @Key("calendarID")
    private String calendarID;
    @Key("numberOfCycles")
    private int numberOfCycles;
    @Key("teamMembers")
    private List<String> teamMembers;

    public QaContactData(){}

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getCalendarID() {
        return calendarID;
    }

    public void setCalendarID(String calendarID) {
        this.calendarID = calendarID;
    }

    public List<String> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<String> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public int getNumberOfCycles() {
        return numberOfCycles;
    }

    public void setNumberOfCycles(int numberOfCycles) {
        this.numberOfCycles = numberOfCycles;
    }

    public static QaContactData load(JsonFactory jsonFactory, Reader reader)
            throws IOException {
        return jsonFactory.fromReader(reader, QaContactData.class);
    }

    @Override
    public String toString() {
        return "QaContactData{" +
                "startDay='" + startDay + '\'' +
                ", calendarID='" + calendarID + '\'' +
                ", numberOfCycles='" + numberOfCycles + '\'' +
                ", teamMembers=" + teamMembers +
                '}';
    }
}
