package com.apiiungo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("blog_type")
public class BlogType {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;

    // MySQL 关键字 show，需要用反引号包裹
    @TableField("`show`")
    private Integer show;

    private String description;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getShow() { return show; }
    public void setShow(Integer show) { this.show = show; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}


