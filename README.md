# QA-contact-updater
This app add a QA contact cycles to RHV storage calendar.

To start:

1) Install gradle
2) Download the credentials.json from - https://developers.google.com/calendar/quickstart/java -> 
   "Enable the Google Calendar API"
3) Put the credentials.js under /src/main/resources/
4) Get the user name from - https://console.developers.google.com/apis/ -> credentials -> OAuth 2.0 Client IDs
5) Fill the user name from step 4 in qa_contact_data.json
6) Fill the needed data in ./src/main/resources/qa_contact_data.json
   For e.g:
   {
   	"startDay": "01-01-2019",
      "userName": "OAuth client",
   	"calendarID": "XXX",
   	"numberOfCycles": "3",
   	"teamMembers": [
   		"memberMail@redhat.com",
   		"memberMail@redhat.com"
   	]
   }
7) run in under the root folder of the project - gradle -q run --stacktrace
