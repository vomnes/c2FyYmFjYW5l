package lib

import (
	"encoding/json"
	"io"
	"net/http"
)

// ReaderJSONToInterface decode the json from a io.Reader and store it in a interface
func ReaderJSONToInterface(reader io.Reader, data interface{}) (int, string, error) {
	decoder := json.NewDecoder(reader)
	err := decoder.Decode(data)
	if err != nil {
		return http.StatusNotAcceptable, "Failed to decode the body JSON", err
	}
	return 0, "", nil
}
