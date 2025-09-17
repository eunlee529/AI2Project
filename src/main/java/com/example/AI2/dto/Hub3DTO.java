package com.example.AI2.dto;

import com.example.AI2.entity.Hub3Entity;
import com.example.AI2.entity.HubEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Hub3DTO {
    private long docClass;
    private String docId;
    private String casenames;
    private String normalizedCourt;
    private String casetype;
    private String sentences;
    private String announceDate;

    public Hub3Entity entity() {
        return new Hub3Entity(docId, docClass, casenames, normalizedCourt, casetype, sentences, announceDate);
    }
}
