package route

import (
	"net/http"
	"net/http/httptest"
	"sort"
	"strings"
	"testing"
	"time"

	coltypes "../database/mongodb/collections"
	query "../database/mongodb/query"
	"../lib"
	"../lib/tests"
	"github.com/kylelemons/godebug/pretty"
	"gopkg.in/mgo.v2/bson"
)

// This aim of this sorter is to fix the random order
type listInformationSorter []coltypes.InformationItem

func (a listInformationSorter) Len() int      { return len(a) }
func (a listInformationSorter) Swap(i, j int) { a[i], a[j] = a[j], a[i] }
func (a listInformationSorter) Less(i, j int) bool {
	return strings.Compare(a[i].Value, a[j].Value) == -1
}

// This aim of this sorter is to fix the random order
type fieldNamesSorter []coltypes.FieldName

func (a fieldNamesSorter) Len() int      { return len(a) }
func (a fieldNamesSorter) Swap(i, j int) { a[i], a[j] = a[j], a[i] }
func (a fieldNamesSorter) Less(i, j int) bool {
	return strings.Compare(a[i].CaptionName, a[j].CaptionName) == -1
}

func TestAddContactsInvalidJSONBody(t *testing.T) {
	context := tests.ContextData{
		MongoDB: tests.MongoDB,
	}
	body, err := lib.InterfaceToByte(map[string]interface{}{
		"content": "",
	})
	if err != nil {
		t.Error(err)
	}

	r := tests.CreateRequest("POST", "/v1/contacts", body, context)
	r.Header.Add("Content-Type", "application/json")
	w := httptest.NewRecorder()
	output := tests.CaptureOutput(func() {
		AddContacts(w, r)
	})
	// Check : Content stardard output
	if output != "" {
		t.Error(output)
	}
	strError := tests.CompareResponseJSONCode(w, http.StatusNotAcceptable, map[string]interface{}{
		"error": "Failed to decode the body JSON",
	})
	if strError != nil {
		t.Errorf("%v", strError)
	}
}

func TestAddContactsNoDatabase(t *testing.T) {
	tests.DbClean()

	context := tests.ContextData{}
	body, err := lib.InterfaceToByte(map[string]interface{}{})
	if err != nil {
		t.Error(err)
	}

	r := tests.CreateRequest("POST", "/v1/contacts", body, context)
	r.Header.Add("Content-Type", "application/json")
	w := httptest.NewRecorder()
	output := tests.CaptureOutput(func() {
		AddContacts(w, r)
	})
	// Check : Content stardard output
	if !strings.Contains(output, "Database Connection Failed") {
		t.Error(output)
	}
	strError := tests.CompareResponseJSONCode(w, http.StatusInternalServerError, map[string]interface{}{
		"error": "Problem with database connection",
	})
	if strError != nil {
		t.Errorf("%v", strError)
	}
}

func TestAddContactsWithOneContact(t *testing.T) {
	tests.DbClean()
	timeNow := time.Now().Local()
	_ = tests.InsertContact(coltypes.Contact{
		ID:          "testA",
		Email:       "t@t.a",
		PhoneNumber: "+33600000001",
		CreatedAt:   timeNow,
	}, tests.MongoDB)
	_ = tests.InsertContact(coltypes.Contact{
		ID:          "testB",
		Email:       "t@t.b",
		PhoneNumber: "+33600000002",
		CreatedAt:   timeNow,
	}, tests.MongoDB)
	_ = tests.InsertFieldName(coltypes.FieldName{
		CaptionName: "Civilité",
	}, tests.MongoDB)
	context := tests.ContextData{
		MongoDB: tests.MongoDB,
	}

	body, err := lib.InterfaceToByte([]map[string]interface{}{
		map[string]interface{}{
			"Prénom":      "a1",
			"phoneNumber": "+447911123456",
			"Civilité":    "b1",
			"Nom":         "c1",
			"email":       "test.test@a.com",
		},
		// Case: Phone Number already exists in database
		map[string]interface{}{
			"Prénom":      "a2",
			"phoneNumber": "0600000001",
			"Civilité":    "b2",
			"Nom":         "c2",
			"email":       "test.test@b.com",
		},
		// Case: No Email/PhoneNumber
		map[string]interface{}{
			"Prénom":   "a3",
			"Civilité": "b3",
			"Nom":      "c3",
		},
		// Case: Email or PhoneNumber Just Inserted
		map[string]interface{}{
			"Prénom":      "a4",
			"phoneNumber": "+447911123456",
			"Civilité":    "b4",
			"Nom":         "c4",
			"email":       "test.test@a.com",
		},
		// Case: Email already exists in database
		map[string]interface{}{
			"Prénom":      "a5",
			"phoneNumber": "0600000005",
			"Civilité":    "b5",
			"Nom":         "c5",
			"email":       "t@t.a",
		},
	})
	if err != nil {
		t.Error(err)
	}

	r := tests.CreateRequest("POST", "/v1/contacts", body, context)
	r.Header.Add("Content-Type", "application/json")
	w := httptest.NewRecorder()
	output := tests.CaptureOutput(func() {
		AddContacts(w, r)
	})
	// Check : Content stardard output
	if output != "" {
		t.Error(output)
	}
	if w.Code != http.StatusCreated {
		t.Errorf("Expected HTTP Code %d has %d", http.StatusCreated, w.Code)
	}
	expectedDatabaseStateContacts := []coltypes.Contact{
		coltypes.Contact{
			ID:          "",
			Email:       "t@t.a",
			PhoneNumber: "+33600000001",
			CreatedAt:   timeNow,
			UpdatedAt:   nil,
		},
		coltypes.Contact{
			ID:          "",
			Email:       "t@t.b",
			PhoneNumber: "+33600000002",
			CreatedAt:   timeNow,
			UpdatedAt:   nil,
		},
		coltypes.Contact{
			ID:          "",
			Email:       "test.test@a.com",
			PhoneNumber: "+447911123456",
			CreatedAt:   timeNow,
			UpdatedAt:   nil,
			Informations: []coltypes.InformationItem{
				coltypes.InformationItem{
					FieldNameID: "",
					Value:       "a1",
				},
				coltypes.InformationItem{
					FieldNameID: "",
					Value:       "b1",
				},
				coltypes.InformationItem{
					FieldNameID: "",
					Value:       "c1",
				},
			},
		},
	}
	var dbStateContacts []coltypes.Contact
	dbStateContacts, err = query.FindContacts(bson.M{}, bson.M{"_id": 0, "informations.fieldName_id": 0}, tests.MongoDB)
	if err != nil {
		t.Error("Error with mongoDB in FindContacts()")
		return
	}
	for _, contact := range dbStateContacts {
		// Fix the random order
		sort.Sort(listInformationSorter(contact.Informations))
	}
	if compare := pretty.Compare(&expectedDatabaseStateContacts, dbStateContacts); compare != "" {
		t.Error(compare)
	}

	var dbStateFieldNames []coltypes.FieldName
	err = tests.MongoDB.
		C("fieldNames").
		Find(bson.M{}).
		Select(bson.M{"_id": 0}).
		Sort("captionName").
		All(&dbStateFieldNames)
	if err != nil {
		t.Error("Error with mongoDB in FindFieldNames()")
	}
	expectedDatabaseStateFieldNames := []coltypes.FieldName{
		coltypes.FieldName{
			ID:          "",
			CaptionName: "Civilité",
		},
		coltypes.FieldName{
			ID:          "",
			CaptionName: "Nom",
		},
		coltypes.FieldName{
			ID:          "",
			CaptionName: "Prénom",
		},
	}
	if compare := pretty.Compare(&expectedDatabaseStateFieldNames, dbStateFieldNames); compare != "" {
		t.Error(compare)
	}
}
