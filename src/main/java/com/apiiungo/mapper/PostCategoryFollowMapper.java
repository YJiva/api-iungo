package com.apiiungo.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PostCategoryFollowMapper {

    @Select("SELECT COUNT(1) FROM post_category_follow WHERE category_id = #{categoryId} AND user_id = #{userId}")
    int exists(@Param("categoryId") Long categoryId, @Param("userId") Long userId);

    @Insert("INSERT INTO post_category_follow(category_id, user_id, create_time) VALUES(#{categoryId}, #{userId}, NOW())")
    int insert(@Param("categoryId") Long categoryId, @Param("userId") Long userId);

    @Delete("DELETE FROM post_category_follow WHERE category_id = #{categoryId} AND user_id = #{userId}")
    int delete(@Param("categoryId") Long categoryId, @Param("userId") Long userId);

    @Select("SELECT COUNT(1) FROM post_category_follow WHERE category_id = #{categoryId}")
    int countByCategory(@Param("categoryId") Long categoryId);

    /** 当前用户关注的贴吧 id，按关注时间倒序 */
    List<Long> selectCategoryIdsByUser(@Param("userId") Long userId);
}
