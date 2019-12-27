package handleHTTP

import (
	"encoding/json"
	"net/http"

	"../../lib"
)

// RespondWithJSON set the content of the http response
func RespondWithJSON(w http.ResponseWriter, code int, payload interface{}) {
	response, err := json.Marshal(payload)
	if err != nil {
		lib.PrettyError(err.Error() + "Failed to marshal response")
		response, _ = json.Marshal(map[string]interface{}{"error": "Failed to marshal response"})
		code = 401
	}
	w.Header().Set("X-XSS-Protection", "1; mode=block")
	w.Header().Set("X-Content-Type-Options", "nosniff")
	w.Header().Set("X-Frame-Options", "DENY")
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(code)
	w.Write(response)
}

// RespondWithError set the content of the http response in error case
func RespondWithError(w http.ResponseWriter, code int, errorMessage string) {
	RespondWithJSON(w, code, map[string]interface{}{"error": errorMessage})
}

// RespondEmpty set empty compte for the http response
func RespondEmpty(w http.ResponseWriter, code int) {
	w.Header().Set("X-XSS-Protection", "1; mode=block")
	w.Header().Set("X-Content-Type-Options", "nosniff")
	w.Header().Set("X-Frame-Options", "DENY")
	w.WriteHeader(code)
	w.Write(nil)
}

// CheckMethod check the method in the request to see if it is part of the allowed method for a route
func CheckMethod(method string, allowedMethods []string) bool {
	return lib.StringInArray(method, allowedMethods)
}
