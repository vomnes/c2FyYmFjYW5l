package com.extractor.csv;

import java.io.BufferedReader;
import java.io.IOException;

import org.json.JSONObject;

public class CSV {
    private BufferedReader reader;
    private String fieldDelimiter = null;
    private String[] fieldsName = null;

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
        String name;

        for (int i = 0; i < array.length; i++) {
            // Is email or phone
            this.formatField(array[i]);
            if (i < this.fieldsName.length) {
                name = this.fieldsName[i];
            } else {
                // Create a new column if this line has more lines
                name = "col" + Integer.toString(i + 1);
            }
            obj.put(name, array[i]);
        }
        System.out.println(obj.toString());
     }

     private String formatField(String value) {
         return null;
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