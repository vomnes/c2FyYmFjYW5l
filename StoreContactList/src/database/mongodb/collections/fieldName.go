package collections

type FieldName struct {
	ID          string `json:"_id,omitempty" bson:"_id,omitempty"`
	CaptionName string `json:"captionName,omitempty" bson:"captionName,omitempty"`
}

type FieldNames struct {
	FieldNames []FieldName `json:"fieldNames,omitempty" bson:"fieldNames,omitempty"`
}
