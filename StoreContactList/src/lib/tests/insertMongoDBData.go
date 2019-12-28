package tests

import (
	"log"

	lib ".."
	coltype "../../database/mongodb/collections"
	mgoQuery "../../database/mongodb/query"
	mgo "gopkg.in/mgo.v2"
)

func InsertContact(contact coltype.Contact, db *mgo.Database) coltype.Contact {
	// Generate ID
	if contact.ID == "" {
		contact.ID = "test-" + lib.GetRandomString(42)
	}
	err := mgoQuery.InsertContact(contact, db)
	if err != nil {
		log.Fatal(lib.PrettyError("[TEST] Failed to insert contact data" + err.Error()))
	}
	return contact
}

func InsertFieldName(fieldName coltype.FieldName, db *mgo.Database) coltype.FieldName {
	// Generate ID
	if fieldName.ID == "" {
		fieldName.ID = "test-" + lib.GetRandomString(42)
	}
	err := mgoQuery.InsertFieldName(fieldName, db)
	if err != nil {
		log.Fatal(lib.PrettyError("[TEST] Failed to insert fieldName data" + err.Error()))
	}
	return fieldName
}
