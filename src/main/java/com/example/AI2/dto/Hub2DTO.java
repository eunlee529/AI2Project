package com.example.AI2.dto;

import com.example.AI2.entity.Hub2Entity;
import com.example.AI2.entity.HubEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Hub2DTO {
    long num;
    int qa_id;
    int domain;
    int q_type;
    String question;
    String answer;

    public Hub2Entity entity(){
        return new Hub2Entity(null, qa_id, domain, q_type, question, answer);
    }
}
