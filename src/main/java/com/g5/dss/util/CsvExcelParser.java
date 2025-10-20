package com.g5.dss.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvExcelParser {

    public List<String[]> parseCsv(MultipartFile file) throws Exception {
        List<String[]> records = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(values);
            }
        }
        
        return records;
    }

    public byte[] exportToCsv(List<String[]> data) {
        // TODO: Implement CSV export
        return new byte[0];
    }
}

