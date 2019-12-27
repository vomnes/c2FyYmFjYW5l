package lib

import (
	"encoding/json"
	"io"
	"log"
)

// ReaderJSONToInterface decode the json from a io.Reader and store it in a interface
func ReaderJSONToInterface(reader io.Reader, data interface{}) (int, string, error) {
	decoder := json.NewDecoder(reader)
	err := decoder.Decode(data)
	if err != nil {
		log.Println(PrettyError("Failed to decode json reader" + err.Error()))
		return 406, "Failed to decode JSON reader", err
	}
	return 0, "", nil
}
