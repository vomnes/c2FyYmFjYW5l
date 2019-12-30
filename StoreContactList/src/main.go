package main

import (
	"flag"
	"fmt"
	"log"
	"net/http"
	"os"

	"./database/mongodb"
	"./lib"
	"./lib/handleHTTP"
	"./route"

	"github.com/gorilla/mux"
	mgo "gopkg.in/mgo.v2"
)

// HandleAPIRoutes instantiates and populates the router
func handleAPIRoutes() *mux.Router {
	// instantiating the router
	api := mux.NewRouter()

	api.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		handleHTTP.RespondWithJSON(w, http.StatusOK, "OK")
	})
	api.HandleFunc("/v1/contacts", route.AddContacts).Methods("POST")
	return api
}

func main() {
	portPtr := flag.String("port", "3000", "port your want to listen on")
	flag.Parse()
	if *portPtr != "" {
		fmt.Printf("running on port: %s\n", *portPtr)
	}
	var dbName = os.Getenv("MONGO_DB_NAME")
	if dbName == "" {
		dbName = "sarbacanes_contacts"
	}
	var db *mgo.Session
	var connectionError string
	for {
		db, connectionError = mongodb.MongoDBConn(dbName)
		if connectionError == "" {
			break
		}
		fmt.Print(connectionError, ", go retry - MongoDB Connection\n")
	}

	router := handleAPIRoutes()
	enhancedRouter := enhanceHandlers(router, db)
	fmt.Println("Server is running...")
	if err := http.ListenAndServe(":"+*portPtr, enhancedRouter); err != nil {
		log.Fatal(lib.PrettyError(err.Error()))
	}
}
