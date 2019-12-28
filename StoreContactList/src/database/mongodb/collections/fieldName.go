package collections

// FieldName is the data structure of the mongodDB collection 'fieldNames'
type FieldName struct {
	ID          string `json:"_id,omitempty" bson:"_id,omitempty"`
	CaptionName string `json:"captionName,omitempty" bson:"captionName,omitempty"`
}
