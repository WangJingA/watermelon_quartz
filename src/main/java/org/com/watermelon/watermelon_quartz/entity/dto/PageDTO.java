package org.com.watermelon.watermelon_quartz.entity.dto;

import lombok.Data;

/**
 * 分页DTO
 * @author watermelon
 * @date 2024/08/17
 */
@Data
public class PageDTO {
    /**
     * 当前页
     */
    private Integer page;
    /**
     * 页大小
     */
    private Integer size;
}
