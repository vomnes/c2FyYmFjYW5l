package route

import (
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"

	"../lib"
	"../lib/tests"
)

func TestAddContactsInvalidJSONBody(t *testing.T) {
	context := tests.ContextData{
		MongoDB: tests.MongoDB,
	}
	body, err := lib.InterfaceToByte([]map[string]interface{}{
		map[string]interface{}{
			"Prénom":      "Valentin",
			"phoneNumber": "0695873600",
			"Civilité":    "M",
			"Nom":         "Omnès",
			"email":       "valentin.omnes@gmail.com",
		},
		map[string]interface{}{
			"Prénom":      "Valentin",
			"phoneNumber": "0695873600",
			"Civilité":    "M",
			"col6":        "Hello",
			"Nom":         "Omnès",
			"email":       "valentin.omnes@gmail.com",
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
