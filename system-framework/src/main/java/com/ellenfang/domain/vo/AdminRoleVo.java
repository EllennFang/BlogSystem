package com.ellenfang.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminRoleVo {
    //角色ID@TableId
    private Long id;
    //角色名称
    private String roleName;
    //角色权限字符串
    private String roleKey;
    //显示顺序
    private Integer roleSort;
    //角色状态（0正常 1停用）
    private String status;
    // 创建人 id
    private Long createBy;
    // 创建时间
    private Date createTime;
    // 更新人 id
    private Long updateBy;
    // 更新时间
    private Date updateTime;
    //删除标志（0代表存在 1代表删除）
    private String delFlag;
    //备注
    private String remark;
}
