import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class AddQaContactCyclesToStorageCalendar {
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String TEAM_MEMBERS_FILE_PATH = "/qa_contact_data.json";

    private static QaContactData QA_CONTACT_DATA;


    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = AddQaContactCyclesToStorageCalendar.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        init();

        addQaContactsToCalendar(service);
    }

    private static void addQaContactsToCalendar(Calendar servise) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date startDate = null;
        try {
            startDate = dateFormat.parse(QA_CONTACT_DATA.getStartDay());
        } catch (ParseException e) {
            System.out.println("failed to parse the start day");
            e.printStackTrace();
        }
        for (int cycleNum = 0; cycleNum < QA_CONTACT_DATA.getNumberOfCycles(); cycleNum++) {
            addStorageTeamCycle(servise, 7 * (QA_CONTACT_DATA.getTeamMembers().size()) * cycleNum, startDate);
        }

    }

    private static void addStorageTeamCycle(Calendar service, int daysToAdd, Date startDate) {
        int index = 0;
        for (String member : QA_CONTACT_DATA.getTeamMembers()) {
            try {
                service.events().insert(QA_CONTACT_DATA.getCalendarID(), createNewQaContactEvent(startDate, daysToAdd, index, member)).execute();
            } catch (IOException e) {
                System.out.println("Failed to add QA contact to - " + member);
                e.printStackTrace();
            }
            index++;
        }
    }

    private static Event createNewQaContactEvent(Date startDate, int daysToAdd, int index, String member) {
        //Create new Event
        Event qaContactEvent = new Event();
        qaContactEvent.setDescription("QA contact");

        //Calculate the event start day
        daysToAdd = daysToAdd + (7 * index);
        DateTime startDateTime = new DateTime(addDays(startDate, daysToAdd));
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime);
        qaContactEvent.setStart(start);

        //Calculate the event end day
        DateTime endDateTime = new DateTime(addDays(startDate, daysToAdd + 7));
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime);
        qaContactEvent.setEnd(end);

        //Add the event attendees
        EventAttendee[] attendees = new EventAttendee[] {
                new EventAttendee().setEmail(member),
        };
        qaContactEvent.setAttendees(Arrays.asList(attendees));

        //Add event reminder
        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("email").setMinutes(24 * 60),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        qaContactEvent.setReminders(reminders);

        //Add event summery
        qaContactEvent.setSummary("QA contact - " + member);

        //Set event to transparent - not busy
        qaContactEvent.setTransparency("transparent");

        System.out.println("new event: " + qaContactEvent.toString());
        return qaContactEvent;
    }

    public static Date addDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(java.util.Calendar.DATE, days);
        return cal.getTime();
    }

    private static void init(){
        // Load QA contact needed data from the JSON file.
        InputStream teamMembersIn = AddQaContactCyclesToStorageCalendar.class.getResourceAsStream(TEAM_MEMBERS_FILE_PATH);
        try {
            QA_CONTACT_DATA = QaContactData.load(JSON_FACTORY, new InputStreamReader(teamMembersIn));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
