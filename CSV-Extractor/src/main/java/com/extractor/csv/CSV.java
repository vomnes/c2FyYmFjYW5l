package com.extractor.csv;

import java.io.BufferedReader;
import java.io.IOException;

import com.extractor.csv.lib.Array;
import com.extractor.csv.lib.Validator;
import com.extractor.csv.model.TemporaryField;

import org.json.JSONArray;
import org.json.JSONObject;

public class CSV {
    private BufferedReader reader;
    private String fieldDelimiter = null;
    private String[] fieldsName = null;
    private Boolean noFieldNameAvailable = false;
    private Validator validator = new Validator();
    private Boolean hasEmailOrPhoneNumber;
    private Array array = new Array();

    private TemporaryField tmpField = new TemporaryField();
    private JSONArray csvDataFormated;

    public CSV(java.io.Reader in) {
        this.reader = new BufferedReader(in);
        this.tmpField.clear();
        this.csvDataFormated = new JSONArray();
        this.hasEmailOrPhoneNumber = false;
        this.noFieldNameAvailable = false;
    }

    public String getNextLine() throws IOException {
        String line = null;
        line = reader.readLine();
        String[] fields;

        // Check if file is empty or fully read
        if (line == null) {
            this.reader.close();
            return null;
        }
        // Get delimiter type
        if (this.fieldDelimiter == null) {
            this.fieldDelimiter = this.getDelimiter(line);
        }
        // Store fields
        if (this.fieldDelimiter == null) {
            fields = new String[]{ line };
        } else {
            fields = line.split(this.fieldDelimiter);
        }
        // Manage fieldsName or format data line
        if (this.fieldsName == null && !this.noFieldNameAvailable) {
            if (array.Contains(fields, new String[]{"Email", "N° de mobile"})) {
                this.fieldsName = fields;
            } else {
                // Handle if the first line doesn't contains any field name
                this.noFieldNameAvailable = true;
                this.csvDataFormated.put(this.formatLineToJSON(fields));
            }
        } else {
            this.csvDataFormated.put(this.formatLineToJSON(fields));
        }
        return line;
     }

     private JSONObject formatLineToJSON(String[] array) {
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
        } else if (fieldsNameArray != null && index < fieldsNameArray.length) { // Field name from Index is available in fields name array
            tmpField.setName(fieldsNameArray[index]);
            // Check if field name is 'email' but the content is not email type
            if (fieldsNameArray[index].toLowerCase().equals("email") && !validator.isEmail(value)) {
                tmpField.clear(); // If not email type set the content to null to skip the field
                return;
            }
            // +++++ Done in other microservice
            // // Check if field name is 'n° de téléphone' but the content is not a phone number type
            // if (fieldsNameArray[index].toLowerCase().equals("n° de mobile") &&! validator.isPhoneNumberFR(value)) {
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