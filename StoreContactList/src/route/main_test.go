package route

import (
	"log"
	"net/http"
	"os"
	"testing"

	mdb "../database/mongodb"
	"../lib/tests"
	"github.com/gorilla/mux"
)

func testServer() http.Handler {
	r := mux.NewRouter()
	// r.HandleFunc("/v1/xxx/{xxx}", xxx).Methods("GET")
	return r
}

func TestMain(m *testing.M) {
	var err string
	dbsession, err := mdb.MongoDBConn("")
	if err != "" {
		log.Fatal(err)
	}
	dbsession.Copy()
	defer dbsession.Close() // cleaning up
	tests.MongoDB = dbsession.DB("sarbacanes_contacts_tests")
	tests.DbClean()
	ret := m.Run()
	tests.DbClean()
	os.Exit(ret)
}
