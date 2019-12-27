package query

import (
	"errors"

	mgo "gopkg.in/mgo.v2"
	"gopkg.in/mgo.v2/bson"
)

func Insert(toInsert interface{}, collectionName string, db *mgo.Database) error {
	return db.C(collectionName).Insert(toInsert)
}

func Find(data bson.M, collectionName string, db *mgo.Database) (interface{}, error) {
	var dataFound interface{}
	if err := db.
		C(collectionName).
		Find(data).
		One(&dataFound); err != nil {
		if err == mgo.ErrNotFound {
			return nil, errors.New("Not Found")
		}
		return nil, err
	}
	return dataFound, nil
}

// func FindMany(data bson.M, collectionName string, db *mgo.Database) ([]interface{}, error) {
// 	var dataFound []interface{}
// 	if err := db.
// 		C(collectionName).
// 		Find(data).
// 		All(&dataFound); err != nil {
// 		return nil, err
// 	}
// 	return dataFound, nil
// }

func Update(colQuerier, change bson.M, collectionName string, db *mgo.Database) error {
	err := db.
		C(collectionName).
		Update(colQuerier, bson.M{"$set": change})
	if err != nil {
		return err
	}
	return nil
}
