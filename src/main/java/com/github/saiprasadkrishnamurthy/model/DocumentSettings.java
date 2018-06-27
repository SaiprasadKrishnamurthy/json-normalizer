package com.github.saiprasadkrishnamurthy.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DocumentSettings {
    private String id;
    private String description;
    private List<FieldsGroup> fieldsGroup = new ArrayList<>();
}
