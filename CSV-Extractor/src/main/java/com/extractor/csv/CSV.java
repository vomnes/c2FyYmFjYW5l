package com.extractor.csv;

import java.io.BufferedReader;
import java.io.IOException;

import com.extractor.csv.lib.Validator;
import com.extractor.csv.model.TemporaryField;

import org.json.JSONObject;

public class CSV {
    private BufferedReader reader;
    private String fieldDelimiter = null;
    private String[] fieldsName = null;
    private Validator validator = new Validator();

    private TemporaryField tmpField = new TemporaryField();

    public CSV(java.io.Reader in) {
        this.reader = new BufferedReader(in);
    }

    public String getNextLine() throws IOException {
        String line = null;
        line = reader.readLine();
        String[] fields;

        if (this.fieldDelimiter == null) {
            this.fieldDelimiter = this.getDelimiter(line);
        }
        if (line == null) {
            this.reader.close();
            return null;
        }
        if (this.fieldDelimiter == null) {
            fields = new String[]{ line };
        } else {
            fields = line.split(this.fieldDelimiter);
        }
        if (this.fieldsName == null) {
            this.fieldsName = fields;
        } else {
            this.formatLines(fields);
        }
        return line;
     }

     private void formatLines(String[] array) {
        JSONObject obj = new JSONObject();

        for (int i = 0; i < array.length; i++) {
            tmpField.clear();
            this.formatFieldName(array[i], i, fieldsName);
            if (!tmpField.isEmpty()) {
                obj.put(tmpField.getName(), tmpField.getValue());
            }
        }
        System.out.println(obj.toString());
     }

     private void formatFieldName(String value, Integer index, String[] fieldsNameArray) {
        if (validator.isEmail(value)) {
            tmpField.setName("email");
        } else if (validator.isPhoneNumber(value)) {
            tmpField.setName("phoneNumber");
        } else if (index < fieldsNameArray.length) { // Field name from Index is available in fields name array
            tmpField.setName(fieldsNameArray[index]);
            // Check if field name is 'email' but the content is not email type
            if (fieldsNameArray[index].toLowerCase().equals("email") && !validator.isEmail(value)) {
                tmpField.clear(); // If not email type set the content to null to skip the field
                return;
            }
            // Check if field name is 'n° de téléphone' but the content is not email type
            if (fieldsNameArray[index].toLowerCase() == "n° de mobile" &&!validator.isPhoneNumber(value)) {
                tmpField.clear(); // If not email type set the content to null to skip the field
                return;
            }
        } else {
            // Create a new column if this line has more lines
            tmpField.setName("col" + Integer.toString(index + 1));
        }
        tmpField.setValue(value);
     }

     private String getDelimiter(String str) {
         if (str == null)
            return null;
         String[] delimiters = {
             ";",
             ",",
             "\t",
             "|",
             "^",
         };
         for (String delimiter : delimiters) {
             if (str.contains(delimiter))
                return delimiter;
         }
         return null;
     }
}