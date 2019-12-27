package com.extractor.csv.lib;

import java.util.Arrays;
import java.util.List;

public class Array {
    public Boolean Contains(String[] array, String str) {
        // Convert String Array to List
        List<String> list = Arrays.asList(array);
 
        return list.contains(str);
    }

    public Boolean Contains(String[] array, String[] itemArray) {
        // Convert String Array to List
        List<String> list = Arrays.asList(array);

        for (String item : itemArray) {
            if (list.contains(item))
                return true;
        }
        return false;
    }
}