package org.example.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class FormatType {
    private int id;
    private String value;
    private String label;
    List<FormatType> children;
    private int level;
}
