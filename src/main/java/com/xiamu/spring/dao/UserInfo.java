package com.xiamu.spring.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {

    @TableId
    private String uuid;

    private String userName;

    private String describes;

    private String userPhone;

    private Date birthday;

    private Integer age;

    private String sex;

    private String nickName;

    private String email;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public UserInfo(String uuid, String userName) {
        this.uuid = uuid;
        this.userName = userName;
    }
}
