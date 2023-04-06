# Backend

## Endpoints

### For all possible endpoint status codes and messages, check ./src/main/kotlin/infra/endpoints

### POST /api/login
Expected request example:
```
{
"user": "your UFRGS card number",
"password": "your UFRGS password"
}
```
Expected response:
`set-cookie header with PHPSESSID value`

### POST /api/classes
Expected request example:

Empty body with PHPSESSID cookie

Expected response:
```
{
    "message": "Ok",
    "content": 123456
}
```

### GET /api/classes/{key}
Expected request example:

Empty body with POST response content as key

Expected response:
```
{
    "message": "Processing",
    "content": [
    {
      "name": "Class1 - (CODE1)",
      "timeslots": [
        {
          "classIdentifier": "U",
          "availableSlots": 10,
          "professors": [
            "PROFESSOR1"
          ],
          "scheduledTimes": [
            {
              "day": "Segunda",
              "shortDay": "seg",
              "startTime": "08:30",
              "endTime": "10:10",
              "location": "Classroom 123 - Building 456",
              "locationMap": "http://link.com/to/location1"
            },
            {
              "day": "Quarta",
              "shortDay": "qua",
              "startTime": "08:30",
              "endTime": "10:10",
              "location": "Classroom 123 - Building 456",
              "locationMap": "http://link.com/to/location1"
            }
          ]
        }
      ],
      "credits": 4,
      "classPlan": "http://link.com/to/classplan1.pdf"
    },
    {
      "name": "Class2 - (CODE2)",
      "timeslots": null,
      "credits": null,
      "classPlan": null
    }
  ]
}
```
Where a null value represents a class still being processed

### GET /api/enrollmentdeclaration
Expected request example:

Empty body with PHPSESSID cookie

Expected response:
```
{
    "message": "Ok",
    "content": "http://www.website.com/path/to/enrollment/certificate.pdf"
}
```