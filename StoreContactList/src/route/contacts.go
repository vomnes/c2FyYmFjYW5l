package route

import (
	"net/http"
	"strings"
	"time"

	colTypes "../database/mongodb/collections"
	"../lib"
	"../lib/handleHTTP"

	"github.com/kylelemons/godebug/pretty"
)

func AddContacts(w http.ResponseWriter, r *http.Request) {
	var contactList []map[string]string
	var contactToAdd colTypes.Contact
	var contactListToInsert []colTypes.Contact

	errorCode, errorContent, err := lib.ReaderJSONToInterface(r.Body, &contactList)
	if err != nil {
		handleHTTP.RespondWithError(w, errorCode, errorContent)
		return
	}
	// Iter through contact list from body
	for _, contact := range contactList {
		// Init new contact structure
		contactToAdd = colTypes.Contact{
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
				contactToAdd.Informations = append(
					contactToAdd.Informations,
					colTypes.InformationItem{
						Value: contactItemValue,
					},
				)
			}
		}
		// If contact contains an email or a phone number append to contactList
		if contactToAdd.Email != "" || contactToAdd.PhoneNumber != "" {
			contactListToInsert = append(contactListToInsert, contactToAdd)
		}
	}
	pretty.Print(contactListToInsert)
	handleHTTP.RespondWithJSON(w, 200, map[string]string{"content": "Hello World"})
}
