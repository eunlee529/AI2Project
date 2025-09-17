package com.example.AI2.dto;

import com.example.AI2.entity.HubEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HubDTO {
    String c_id;
    int domain;
    int source;
    String source_spec;
    String creation_year;
    String content;

    public HubEntity entity(){
        return new HubEntity(c_id, domain, source, source_spec, creation_year, content);
    }
}
