package com.demo.dp.mapper;

import com.demo.dp.domain.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserReviewLikeMapper {
    int insertIgnore(@Param("userId") Long userId, @Param("reviewId") Long reviewId);

    int exists(@Param("userId") Long userId, @Param("reviewId") Long reviewId);

    int delete(@Param("userId") Long userId, @Param("reviewId") Long reviewId);

    List<Review> findLikedReviewsByUser(@Param("userId") Long userId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    long countLikedReviewsByUser(@Param("userId") Long userId);

    List<Long> findLikedReviewIds(@Param("userId") Long userId, @Param("reviewIds") List<Long> reviewIds);
}
