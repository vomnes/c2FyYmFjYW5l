package query

import (
	coltypes "../collections"

	mgo "gopkg.in/mgo.v2"
	"gopkg.in/mgo.v2/bson"
)

func InsertContact(fieldName coltypes.Contact, db *mgo.Database) error {
	return Insert(fieldName, "contacts", db)
}

func FindContact(data bson.M, db *mgo.Database) (coltypes.Contact, error) {
	output, err := Find(data, "contacts", db)
	return output.(coltypes.Contact), err
}

func FindContacts(data bson.M, db *mgo.Database) ([]coltypes.Contact, error) {
	var dataFound []coltypes.Contact
	if err := db.
		C("contacts").
		Find(data).
		All(&dataFound); err != nil {
		return nil, err
	}
	return dataFound, nil
}

func UpdateContacts(colQuerier, change bson.M, db *mgo.Database) error {
	return Update(colQuerier, change, "contacts", db)
}
