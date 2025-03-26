package com.project.movie.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelateDTO {
    /** 美食id */
    private String itemId;
    /** 评分 */
    private Double index;
}
