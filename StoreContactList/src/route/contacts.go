package route

import (
	"net/http"

	"../lib/handleHTTP"
)

func AddContacts(w http.ResponseWriter, r *http.Request) {
	handleHTTP.RespondWithJSON(w, 200, map[string]string{"content": "Hello World"})
}
