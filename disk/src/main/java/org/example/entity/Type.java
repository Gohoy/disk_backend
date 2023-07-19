package org.example.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Type implements Serializable {

    private int id;
    private String label;
    private String childrenIds;
    private int classId;
    private String value;
}
