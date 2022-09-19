package com.ellenfang.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MenuSelectVo {
    private Long id;
    private String menuName;
    private Long parentId;
    private List<MenuSelectVo> children;
}
