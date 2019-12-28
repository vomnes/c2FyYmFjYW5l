package route

import (
	"log"
	"net/http"
	"strings"
	"time"

	coltypes "../database/mongodb/collections"
	query "../database/mongodb/query"
	"../lib"
	"../lib/handleHTTP"
	mgo "gopkg.in/mgo.v2"
	"gopkg.in/mgo.v2/bson"
)

// AddContacts is the route '/v1/contacts' with the method POST.
func AddContacts(w http.ResponseWriter, r *http.Request) {
	var contactList []map[string]string
	var contactToAdd coltypes.Contact
	var contactListToInsert []coltypes.Contact

	var emailList, phoneNumberList, fieldNameList []string
	var tmpPhoneNumber string

	db, ok := r.Context().Value(lib.MongoDB).(*mgo.Database)
	if !ok {
		log.Println(lib.PrettyError("Register - Database Connection Failed"))
		handleHTTP.RespondWithError(w, http.StatusInternalServerError, "Problem with database connection")
		return
	}

	errorCode, errorContent, err := lib.ReaderJSONToInterface(r.Body, &contactList)
	if err != nil {
		handleHTTP.RespondWithError(w, errorCode, errorContent)
		return
	}
	// Collection new email and phoneNumber
	for _, contact := range contactList {
		for contactItemName, contactItemValue := range contact {
			if contactItemName == "email" {
				emailList = append(emailList, contactItemValue)
			} else if contactItemName == "phoneNumber" {
				// Check if french phone number
				if strings.HasPrefix(contactItemValue, "02") || strings.HasPrefix(contactItemValue, "06") {
					tmpPhoneNumber = "+33" + strings.TrimPrefix(contactItemValue, "0")
				} else {
					tmpPhoneNumber = contactItemValue
				}
				phoneNumberList = append(phoneNumberList, tmpPhoneNumber)
			} else {
				if !lib.StringInArray(contactItemName, fieldNameList) {
					fieldNameList = append(fieldNameList, contactItemName)
				}
			}
		}
	}
	// Create a list with the existing contacts
	existingContacts, err := query.FindContacts(
		bson.M{
			"$or": []bson.M{
				bson.M{"email": bson.M{"$in": emailList}},
				bson.M{"phoneNumber": bson.M{"$in": phoneNumberList}},
			},
		},
		bson.M{
			"_id":         0,
			"email":       1,
			"phoneNumber": 1,
		},
		db,
	)
	if err != nil {
		log.Println(lib.PrettyError("Register - Find contacts has failed" + err.Error()))
	}
	emailList, phoneNumberList = nil, nil
	for _, existingContact := range existingContacts {
		if existingContact.Email != "" {
			emailList = append(emailList, existingContact.Email)
		}
		if existingContact.PhoneNumber != "" {
			phoneNumberList = append(phoneNumberList, existingContact.PhoneNumber)
		}
	}
	// Create a list with the existing field names
	existingFieldNames, err := query.FindFieldNames(
		bson.M{
			"captionName": bson.M{"$in": fieldNameList},
		},
		bson.M{},
		db,
	)
	if err != nil {
		log.Println(lib.PrettyError("Register - Find Field Names has failed" + err.Error()))
	}
	// Iter through contact list from body
	for _, contact := range contactList {
		// If contact contains an email or a phone number append to contactList
		if contact["email"] == "" && contact["phoneNumber"] == "" {
			continue
		}
		// Init new contact structure
		contactToAdd = coltypes.Contact{
			ID:        lib.GetRandomString(42),
			CreatedAt: time.Now(),
		}
		// Iter through contact items
		for contactItemName, contactItemValue := range contact {
			// fmt.Println(contactItemName, contactItemValue)
			if contactItemName == "email" {
				contactToAdd.Email = contactItemValue
			} else if contactItemName == "phoneNumber" {
				// Check if french phone number
				if strings.HasPrefix(contactItemValue, "02") || strings.HasPrefix(contactItemValue, "06") {
					contactToAdd.PhoneNumber = "+33" + strings.TrimPrefix(contactItemValue, "0")
				} else {
					contactToAdd.PhoneNumber = contactItemValue
				}
			} else {
				fieldNameID, _ := getOrCreateFieldName(&existingFieldNames, contactItemName, db)
				contactToAdd.Informations = append(
					contactToAdd.Informations,
					coltypes.InformationItem{
						FieldNameID: fieldNameID,
						Value:       contactItemValue,
					},
				)
			}
		}
		contactListToInsert = append(contactListToInsert, contactToAdd)
	}
	// Not possible to use the InsertMany from MongDB with mgo
	for _, contactToInsert := range contactListToInsert {
		if !lib.StringInArray(contactToInsert.Email, emailList) && !lib.StringInArray(contactToInsert.PhoneNumber, phoneNumberList) {
			err = query.InsertContact(contactToInsert, db)
			if err != nil {
				log.Println(lib.PrettyError("Register - Contact Insertion Failed" + err.Error()))
			}
		}
	}
	handleHTTP.RespondEmpty(w, http.StatusCreated)
}

func getOrCreateFieldName(fieldNames *[]coltypes.FieldName, captionName string, db *mgo.Database) (string, error) {
	for _, fieldName := range *fieldNames {
		if fieldName.CaptionName == captionName {
			return fieldName.ID, nil
		}
	}
	fieldNameCreated := coltypes.FieldName{
		ID:          lib.GetRandomString(42),
		CaptionName: captionName,
	}
	err := query.InsertFieldName(
		fieldNameCreated,
		db)
	if err != nil {
		return "", err
	}
	*fieldNames = append(*fieldNames, fieldNameCreated)
	return fieldNameCreated.ID, nil
}
