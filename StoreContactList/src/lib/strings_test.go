package lib

import (
	"testing"
)

var RemoveCharactersTests = []struct {
	toClear     string // input
	characters  string // input
	expected    string // expected result
	testContent string // test details
}{
	{" +33 6 00 00 00 ", " ", "+336000000", "Only spaces to clear"},
	{"+33 6 00 00 00", " +", "336000000", "Spaces and '+' to clear"},
	{"+33 6", "", "+33 6", "Nothing to clear"},
}

func TestRemoveCharacters(t *testing.T) {
	for _, tt := range RemoveCharactersTests {
		actual := RemoveCharacters(tt.toClear, tt.characters)

		if actual != tt.expected {
			t.Errorf("RemoveCharacters(%s, %s): expected %s, actual %s - Test type: \033[31m%s\033[0m", tt.toClear, tt.characters, tt.expected, actual, tt.testContent)
		}
	}
}
