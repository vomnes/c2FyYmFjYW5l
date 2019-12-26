package com.extractor.csv;

import java.io.BufferedReader;
import java.io.IOException;

import com.extractor.csv.lib.Validator;
import com.extractor.csv.model.TemporaryField;

import org.json.JSONArray;
import org.json.JSONObject;

public class CSV {
    private BufferedReader reader;
    private String fieldDelimiter = null;
    private String[] fieldsName = null;
    private Validator validator = new Validator();
    private Boolean hasEmailOrPhoneNumber;

    private TemporaryField tmpField = new TemporaryField();
    private JSONArray csvDataFormated;


    public CSV(java.io.Reader in) {
        this.reader = new BufferedReader(in);
        this.tmpField.clear();
        this.csvDataFormated = new JSONArray();
        this.hasEmailOrPhoneNumber = false;
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
            this.csvDataFormated.put(this.formatLines(fields));
        }
        return line;
     }

     private JSONObject formatLines(String[] array) {
        JSONObject obj = new JSONObject();

        for (int i = 0; i < array.length; i++) {
            tmpField.clear();
            this.formatFieldName(array[i], i, fieldsName);
            if (!tmpField.isEmpty()) {
                obj.put(tmpField.getName(), tmpField.getValue());
            }
        }
        return obj;
     }

     public void printCSVDataFormated() {
         System.out.println(this.csvDataFormated.toString());
     }

     public Boolean getHasEmailOrPhoneNumber() {
         return this.hasEmailOrPhoneNumber;
     }

     private void formatFieldName(String value, Integer index, String[] fieldsNameArray) {
        if (validator.isEmail(value)) {
            tmpField.setName("email");
            this.hasEmailOrPhoneNumber = true;
        } else if (validator.isPhoneNumberFR(value)) {
            tmpField.setName("phoneNumber");
            this.hasEmailOrPhoneNumber = true;
        } else if (index < fieldsNameArray.length) { // Field name from Index is available in fields name array
            tmpField.setName(fieldsNameArray[index]);
            // Check if field name is 'email' but the content is not email type
            if (fieldsNameArray[index].toLowerCase().equals("email") && !validator.isEmail(value)) {
                tmpField.clear(); // If not email type set the content to null to skip the field
                return;
            }
            // // Check if field name is 'n° de téléphone' but the content is not email type
            // if (fieldsNameArray[index].toLowerCase() == "n° de mobile" &&!validator.isPhoneNumberFR(value)) {
            //     tmpField.clear(); // If not email type set the content to null to skip the field
            //     return;
            // }
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