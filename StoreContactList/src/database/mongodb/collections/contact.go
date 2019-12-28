package collections

import "time"

// InformationItem is the data structure of Informations in the mongodDB collection 'contacts'
type InformationItem struct {
	FieldNameID string `json:"fieldName_id,omitempty" bson:"fieldName_id,omitempty"`
	Value       string `json:"value,omitempty" bson:"value,omitempty"`
}

// Contact is the data structure of the mongodDB collection 'contacts'
type Contact struct {
	ID           string            `json:"_id,omitempty" bson:"_id,omitempty"`
	Email        string            `json:"email,omitempty" bson:"email,omitempty"`
	PhoneNumber  string            `json:"phoneNumber,omitempty" bson:"phoneNumber,omitempty"`
	CreatedAt    time.Time         `json:"createdat,omitempty" bson:"createdat,omitempty"`
	UpdatedAt    *time.Time        `json:"updateddat,omitempty" bson:"updateddat,omitempty"`
	Informations []InformationItem `json:"informations,omitempty" bson:"informations,omitempty"`
}
