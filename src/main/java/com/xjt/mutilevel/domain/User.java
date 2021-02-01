package com.xjt.mutilevel.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author kevin
 * @date 2019-11-19 20:24
 */
@Data
@TableName("user")
public class User implements Serializable {

    @TableId
    private Long userId;

    private String userName;

    private String userPhone;

    private String address;

    private Integer weight;

    private Date createdAt;

    private Date updatedAt;
}
