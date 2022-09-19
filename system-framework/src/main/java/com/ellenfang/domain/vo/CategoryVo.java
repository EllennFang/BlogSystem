package com.ellenfang.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryVo {
    // 分类id
    private Long id;
    // 分类名称
    private String name;
    // 描述
    private String description;
    // 状态
    private String status;
}
