package route

import (
	"fmt"
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
	var contactListToInsert []coltypes.Contact

	var emailList, phoneNumberList, fieldNameList []string

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

	emailList, phoneNumberList, fieldNameList = listNewEmailPhoneNumberFieldName(contactList)
	existingEmailList, existingPhoneNumberList, err := listExistingEmailPhoneNumber(emailList, phoneNumberList, db)
	if err != nil {
		fmt.Println(err)
	}
	existingFieldNames, err := listExistingFieldNames(fieldNameList, db)
	if err != nil {
		fmt.Println(err)
	}
	contactListToInsert = formatContactListToAdd(contactList, existingFieldNames, db)
	insertContactInDatabase(contactListToInsert, existingEmailList, existingPhoneNumberList, db)
	handleHTTP.RespondEmpty(w, http.StatusCreated)
}

// listNewEmailPhoneNumberFieldName collect the new emails, phoneNumbers and fieldName
func listNewEmailPhoneNumberFieldName(contactList []map[string]string) ([]string, []string, []string) {
	var emailList, phoneNumberList, fieldNameList []string

	for _, contact := range contactList {
		for contactItemName, contactItemValue := range contact {
			if contactItemName == "email" {
				emailList = append(emailList, contactItemValue)
			} else if contactItemName == "phoneNumber" {
				phoneNumberList = append(phoneNumberList, handlePhoneNumber(contactItemValue))
			} else {
				if !lib.StringInArray(contactItemName, fieldNameList) {
					fieldNameList = append(fieldNameList, contactItemName)
				}
			}
		}
	}
	return emailList, phoneNumberList, fieldNameList
}

func listExistingEmailPhoneNumber(emailList, phoneNumberList []string, db *mgo.Database) ([]string, []string, error) {
	var existingContacts []coltypes.Contact

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
		return []string{}, []string{}, lib.PrettyError("Register - Find contacts has failed " + err.Error())
	}
	// Reset emailList, phoneNumberList
	var existingEmailList, existingPhoneNumberList []string
	for _, existingContact := range existingContacts {
		if existingContact.Email != "" {
			existingEmailList = append(existingEmailList, existingContact.Email)
		}
		if existingContact.PhoneNumber != "" {
			existingPhoneNumberList = append(existingPhoneNumberList, existingContact.PhoneNumber)
		}
	}
	return existingEmailList, existingPhoneNumberList, nil
}

func listExistingFieldNames(fieldNameList []string, db *mgo.Database) ([]coltypes.FieldName, error) {
	existingFieldNames, err := query.FindFieldNames(
		bson.M{
			"captionName": bson.M{"$in": fieldNameList},
		},
		bson.M{},
		db,
	)
	if err != nil {
		return []coltypes.FieldName{}, lib.PrettyError("Register - Find Field Names has failed" + err.Error())
	}
	return existingFieldNames, nil
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

func formatContactListToAdd(contactList []map[string]string, existingFieldNames []coltypes.FieldName, db *mgo.Database) []coltypes.Contact {
	var contactToAdd coltypes.Contact
	var contactListToInsert []coltypes.Contact

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
			if contactItemName == "email" {
				contactToAdd.Email = contactItemValue
			} else if contactItemName == "phoneNumber" {
				contactToAdd.PhoneNumber = handlePhoneNumber(contactItemValue)
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
	return contactListToInsert
}

func handlePhoneNumber(phoneNumber string) string {
	// Check if french phone number
	if strings.HasPrefix(phoneNumber, "02") || strings.HasPrefix(phoneNumber, "06") {
		return "+33" + strings.TrimPrefix(phoneNumber, "0")
	}
	return phoneNumber
}

func insertContactInDatabase(contactListToInsert []coltypes.Contact, existingEmailList, existingPhoneNumberList []string, db *mgo.Database) {
	// Not possible to use the InsertMany from MongDB with mgo
	for _, contactToInsert := range contactListToInsert {
		if !lib.StringInArray(contactToInsert.Email, existingEmailList) && !lib.StringInArray(contactToInsert.PhoneNumber, existingPhoneNumberList) {
			err := query.InsertContact(contactToInsert, db)
			if err != nil {
				log.Println(lib.PrettyError("Register - Contact Insertion Failed" + err.Error()))
			}
		}
	}
}
