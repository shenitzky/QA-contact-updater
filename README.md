# QA-contact-updater
This app add a QA contact cycles to RHV storage calendar.

To start:

1) Install gradle
2) Fill the needed data in ./src/main/resources/qa_contact_data.json
   For e.g:
   {
   	"startDay": "01-01-2019",
   	"calendarID": "XXX",
   	"numberOfCycles": "3",
   	"teamMembers": [
   		"memberMail@redhat.com",
   		"memberMail@redhat.com"
   	]
   }
4) run in under the root folder of the project - gradle -q run --stacktrace
