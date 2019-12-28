package query

import (
	coltypes "../collections"

	mgo "gopkg.in/mgo.v2"
	"gopkg.in/mgo.v2/bson"
)

func InsertFieldName(fieldName coltypes.FieldName, db *mgo.Database) error {
	return Insert(fieldName, "fieldNames", db)
}

func FindFieldName(data bson.M, db *mgo.Database) (coltypes.FieldName, error) {
	output, err := Find(data, "fieldNames", db)
	return output.(coltypes.FieldName), err
}

func FindFieldNames(data, selectData bson.M, db *mgo.Database) ([]coltypes.FieldName, error) {
	var dataFound []coltypes.FieldName
	if err := db.
		C("fieldNames").
		Find(data).
		Select(selectData).
		All(&dataFound); err != nil {
		return nil, err
	}
	return dataFound, nil
}

func UpdateFieldNames(colQuerier, change bson.M, db *mgo.Database) error {
	return Update(colQuerier, change, "fieldNames", db)
}
