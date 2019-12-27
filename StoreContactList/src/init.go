package main

import (
	"context"
	"net/http"
	"os"

	"./lib"

	"github.com/gorilla/mux"
	"github.com/rs/cors"
	mgo "gopkg.in/mgo.v2"
)

type adapter func(http.Handler) http.Handler

// adapt transforms an handler without changing it's type. Usefull for authentification.
func adapt(h http.Handler, adapters ...adapter) http.Handler {
	for _, adapter := range adapters {
		h = adapter(h)
	}
	return h
}

// adapt the request by checking the auth and filling the context with usefull data
func enhanceHandlers(r *mux.Router, db *mgo.Session) http.Handler {
	return adapt(r, withRights(), withConnections(db), withCors())
	// return adapt(r, withRights(), withConnections(db, mailjet), withCors())
}

// withConnections is an adapter that copy the access to the database to serve a specific call
func withConnections(db *mgo.Session) adapter {
	return func(h http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			dbsession := db.Copy()
			defer dbsession.Close() // cleaning up
			db_name := os.Getenv("MONGO_DB_NAME")
			if db_name == "" {
				db_name = "sarbacanes_contacts_tests"
			}
			ctx := context.WithValue(r.Context(), lib.MongoDB, dbsession.DB(db_name))
			ctx = context.WithValue(ctx, lib.MongoDBSession, dbsession)
			h.ServeHTTP(w, r.WithContext(ctx))
		})
	}
}

// withRights is an adapter that check authentification
func withRights() adapter {
	return func(h http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			// No authentification to check
			h.ServeHTTP(w, r)
		})
	}
}

// withCors is an adpater that allowed the specific headers we need for our requests from a
// different domain.
func withCors() adapter {
	return func(h http.Handler) http.Handler {
		c := cors.New(cors.Options{
			AllowedOrigins:   []string{"http://localhost"},
			AllowedHeaders:   []string{""},
			AllowedMethods:   []string{"GET", "POST", "PUT", "DELETE"},
			AllowCredentials: true,
		})
		c = cors.AllowAll()
		return c.Handler(h)
	}
}
