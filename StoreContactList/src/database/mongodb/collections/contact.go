package collections

import "time"

type InformationItem struct {
	FieldNameId string `json:"fieldName_id,omitempty" bson:"fieldName_id,omitempty"`
	Value       string `json:"value,omitempty" bson:"value,omitempty"`
}

type Contact struct {
	ID           string            `json:"_id,omitempty" bson:"_id,omitempty"`
	Email        string            `json:"email,omitempty" bson:"email,omitempty"`
	PhoneNumber  string            `json:"phoneNumber,omitempty" bson:"phoneNumber,omitempty"`
	CreatedAt    time.Time         `json:"createdat,omitempty" bson:"createdat,omitempty"`
	UpdatedAt    *time.Time        `json:"updateddat,omitempty" bson:"updateddat,omitempty"`
	Informations []InformationItem `json:"informations,omitempty" bson:"informations,omitempty"`
}

type Contacts struct {
	Contact []Contact `json:"contact,omitempty" bson:"contact,omitempty"`
}
