package com.demo.dp.dto;

/**
 * 创建商家请求数据传输对象（DTO）。
 * 
 * <p>用于接收前端创建商家时提交的数据，与实体类 {@link com.demo.dp.domain.entity.Shop} 分离，
 * 避免直接暴露实体类的所有字段，提高API安全性。
 * 
 * <p>必填字段：
 * <ul>
 *   <li>name：商家名称</li>
 *   <li>category：商家分类</li>
 *   <li>address：商家地址</li>
 * </ul>
 * 
 * <p>可选字段：
 * <ul>
 *   <li>lng：经度（用于地理位置定位）</li>
 *   <li>lat：纬度（用于地理位置定位）</li>
 *   <li>avgPrice：人均价格（单位：元）</li>
 * </ul>
 * 
 * @author System
 * @version 1.0
 */
public class ShopCreateRequest {
    
    /**
     * 商家名称。
     * 必填字段，不能为空。
     * 例如："海底捞火锅"、"星巴克咖啡"等。
     */
    private String name;
    
    /**
     * 商家分类。
     * 必填字段，不能为空。
     * 例如："火锅"、"川菜"、"日料"、"咖啡"、"西餐"等。
     */
    private String category;
    
    /**
     * 商家地址。
     * 必填字段，不能为空。
     * 例如："北京市朝阳区xxx街道xxx号"。
     */
    private String address;
    
    /**
     * 经度（Longitude）。
     * 可选字段，用于地理位置定位。
     * 取值范围：-180.0 到 180.0。
     * 例如：116.397128（北京天安门附近）。
     */
    private Double lng;
    
    /**
     * 纬度（Latitude）。
     * 可选字段，用于地理位置定位。
     * 取值范围：-90.0 到 90.0。
     * 例如：39.916527（北京天安门附近）。
     */
    private Double lat;
    
    /**
     * 人均价格。
     * 可选字段，表示该商家的人均消费金额。
     * 单位：元（人民币）。
     * 例如：150.0 表示人均消费150元。
     */
    private Double avgPrice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(Double avgPrice) {
        this.avgPrice = avgPrice;
    }
}

