# Test Sarbacane Software

## Launch the service
Docker is required.

**Create the docker images:**
`sh create-docker-images.sh`
or
`docker-compose build`

**Build and run the docker containers:**
`sh run.sh`
or
`docker-compose up`

The name of the database in MongoDB *sarbacanes_contacts*.

## Organisation
### CSV Extractor
This microservice is an API that use :
- Java Spring
- Junit 5 for the tests

#### POST - /uploadCSV
This route allows to extract a contact list from a CSV.

Take as parameter in the 'form-data' body an item named 'file' that contains a file with the contact list :
- A file must be selected
- This file must be a CSV type
- The CSV delimiters handled are ";", ",", "\t", "|", "^"
- The CSV file must at least contains a valid 'email' or 'phone number'
- No field names are not necessary

Other details :
- If a email address is found his value is stored in 'email'
- If a phone number is found his value is stored in 'phoneNumber'
- If an element in email row is found but his content is not an email this data is cleared
- If an element in 'n° de mobile' row is found but his content is not an french phone number this data is cleared
- Content that doesn't have a field name is affected to 'col{rowIndex}' field name

The extracted data will be formatted in JSON and send to the route 'POST /v1/contacts' of the Manage Contacts microservice.
If you want to get the formatted JSON in the HTTP response (no connection with 'POST /v1/contacts') you just need to add in the request header "Content-Test: 'true'".

### Manage Contacts
This microservice is an API that use :
- Golang
- Unit testing with the Golang standard library
- MongoDB for the database containing the collections `Contacts` and `FieldNames`

#### POST - /v1/contacts
This route allows to store the contact list in the body in the mongodb database.

```
JSON Body :
  {
    "phoneNumber"   string,
    "email"         string,
    "xFieldName"    string,
    ...
  }
```

Details :
- Email are cleared if invalid
- PhoneNumber must be french number to be valid else cleared
- PhoneNumber are formatted for insertion with +33
Not inserted if :
- Email or PhoneNumber exists already in the database
- Email & PhoneNumber are empty (or invalid for email)
- New contact doesn't contains an email or phoneNumber
If failed to insert contacts the list of those contact is returned in the response JSON

Return HTTP Code 201 Status Created for success
