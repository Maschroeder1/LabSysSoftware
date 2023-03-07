# Backend

## Endpoints

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
    "message": "Ok",
    "content": {
        "CLASS 1 - (CODE1)": {
            "timeslots": [
                {
                    "classIdentifier": "U",
                    "availableSlots": 30,
                    "professors": [
                        "PROFESSOR NAME"
                    ],
                    "scheduledTimes": [
                        {
                            "day": "Quarta",
                            "startTime": "08:30",
                            "endTime": "11:50",
                            "location": "SALA DE AULA 123 - PRÉDIO 456 4",
                            "locationMap": null
                        }
                    ]
                }
            ],
            "credits": 4
        },
        "CLASS 2 (CODE2)": null
    }
}
```
Where a null value represents a class still being processed