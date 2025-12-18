package com.demo.dp.dto;

/**
 * 更新商家请求数据传输对象（DTO）。
 * 
 * <p>用于接收前端更新商家时提交的数据，支持部分字段更新。
 * 所有字段都是可选的，只更新请求体中提供的非null字段。
 * 
 * <p>可更新字段：
 * <ul>
 *   <li>name：商家名称</li>
 *   <li>category：商家分类</li>
 *   <li>address：商家地址</li>
 *   <li>lng：经度</li>
 *   <li>lat：纬度</li>
 *   <li>avgPrice：人均价格</li>
 *   <li>avgScore：平均评分（通常由系统根据用户点评自动计算，手动更新需谨慎）</li>
 *   <li>status：状态（1=正常，0=已删除/下线）</li>
 * </ul>
 * 
 * <p>注意：
 * <ul>
 *   <li>id字段通常从URL路径中获取，不需要在请求体中提供</li>
 *   <li>如果某个字段为null，则该字段不会被更新，保持数据库中的原值</li>
 *   <li>avgScore字段通常应该由系统根据用户点评自动计算，不建议手动更新</li>
 * </ul>
 * 
 * @author System
 * @version 1.0
 */
public class ShopUpdateRequest {
    
    /**
     * 商家ID。
     * 通常从URL路径中获取，不需要在请求体中提供。
     * 如果请求体中提供了ID，也会被URL路径中的ID覆盖，确保ID一致性。
     */
    private Long id;
    
    /**
     * 商家名称。
     * 可选字段，如果提供则更新，为null则不更新。
     */
    private String name;
    
    /**
     * 商家分类。
     * 可选字段，如果提供则更新，为null则不更新。
     */
    private String category;
    
    /**
     * 商家地址。
     * 可选字段，如果提供则更新，为null则不更新。
     */
    private String address;
    
    /**
     * 经度（Longitude）。
     * 可选字段，如果提供则更新，为null则不更新。
     */
    private Double lng;
    
    /**
     * 纬度（Latitude）。
     * 可选字段，如果提供则更新，为null则不更新。
     */
    private Double lat;
    
    /**
     * 人均价格。
     * 可选字段，如果提供则更新，为null则不更新。
     * 单位：元（人民币）。
     */
    private Double avgPrice;
    
    /**
     * 平均评分。
     * 可选字段，如果提供则更新，为null则不更新。
     * 取值范围：0.0 到 5.0。
     * 
     * <p>注意：通常平均评分应该由系统根据用户点评自动计算，
     * 手动更新此字段需谨慎，建议只在特殊情况下使用（如管理后台调整）。
     */
    private Double avgScore;
    
    /**
     * 商家状态。
     * 可选字段，如果提供则更新，为null则不更新。
     * 取值：
     * <ul>
     *   <li>1：正常营业</li>
     *   <li>0：已删除/下线</li>
     * </ul>
     */
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(Double avgScore) {
        this.avgScore = avgScore;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

