package com.jl.myapplication.model;



import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * desc Table表格分页参数接收封装类
 * class-name HelpStoreParamForPage
 * method-name
 * param
 * return
 * throws @throws Exception
 *
 * @author z1126
 * date 2017/12/29
 */
public class HelpStoreParamForPage<T> implements Serializable {

	//排序
	private String orderBy = "desc";

	private Object object;
	private String comparyId; //公司id
	private String phone;
	private String saleOrderId;
	private String storeId;
	private String storeName;
	private String storeTel;
	private String orderId;
	private String userId;
	private String goodsCatId; //类目
	private String parentGoodsCatId;
	private String storeGroupId;
	private String customerId;
	private String customerName;
	private String goodsItemId;
	private String goodsEncoding;
	private String dataType;
	private String descType;
	private String dateType;
	private String keyWord;
	private String title;
	private String url;
	private String classified; // 归类
	private String brandId; // 品牌id
	private String entityTypeId;
	private String entityId;
	private String toEntityId;
	private String activityId;
	private float amount;
	private String payType;
	private String code;
	private String brandName;
	/**
	 * 　说明：积分数量
	 */
	private float intergral;
	/**
	 * 　说明：短信数
	 */
	private int smsCount;
	/**
	 * 　说明：借贷方式，全部以平台为基准进行
	 */
	private String borrowingType;
	private String content;
	/**
	 * 系统审核编码
	 */
	private String helpStoreStatus;
	private Date limitDate;
	private String startDate;
	private Date beginDate;
	private Date overDate;
	private String endDate;
	private int page = 1;  // 初始化页面
	private int rows = 20; // 条数
	private int typeNum;
	private int count;
	private int sum;
	private int number;
	private Long total;
	private String statusInCompary;
	private String statusInGroup;
	private String statusInStore;
	private String isGift;
	private String specOne;
	private String specTwo;
	private Long maxCount;
	private Long minCount;
	private String[] roles;
	private String toUserId;
	private String toStoreId;
	private String toComparyId;
	private String toDataType;
	private String productId; // 供应商id
	private String bdGoodsCatId;
	private String id;
	private String tag;
	private String[] tags;
	private String[] ids;
	private String userName;
	private String returnInfo;
	private String comShortUrl;
	private String storeShortUrl;
	private Map<String, String> map;
	private List<Map<String, Object>> maps;
	/**
	 * sql片断
	 */
	private String sqlStr;

	private String level;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "HelpStoreParamForPage{" +
				"orderBy='" + orderBy + '\'' +
				", object=" + object +
				", comparyId='" + comparyId + '\'' +
				", phone='" + phone + '\'' +
				", saleOrderId='" + saleOrderId + '\'' +
				", storeId='" + storeId + '\'' +
				", storeName='" + storeName + '\'' +
				", storeTel='" + storeTel + '\'' +
				", orderId='" + orderId + '\'' +
				", userId='" + userId + '\'' +
				", goodsCatId='" + goodsCatId + '\'' +
				", parentGoodsCatId='" + parentGoodsCatId + '\'' +
				", storeGroupId='" + storeGroupId + '\'' +
				", customerId='" + customerId + '\'' +
				", customerName='" + customerName + '\'' +
				", goodsItemId='" + goodsItemId + '\'' +
				", goodsEncoding='" + goodsEncoding + '\'' +
				", dataType='" + dataType + '\'' +
				", descType='" + descType + '\'' +
				", dateType='" + dateType + '\'' +
				", keyWord='" + keyWord + '\'' +
				", title='" + title + '\'' +
				", url='" + url + '\'' +
				", classified='" + classified + '\'' +
				", brandId='" + brandId + '\'' +
				", entityTypeId='" + entityTypeId + '\'' +
				", entityId='" + entityId + '\'' +
				", activityId='" + activityId + '\'' +
				", amount=" + amount +
				", payType='" + payType + '\'' +
				", code='" + code + '\'' +
				", brandName='" + brandName + '\'' +
				", intergral=" + intergral +
				", smsCount=" + smsCount +
				", borrowingType='" + borrowingType + '\'' +
				", content='" + content + '\'' +
				", helpStoreStatus='" + helpStoreStatus + '\'' +
				", limitDate=" + limitDate +
				", startDate=" + startDate +
				", beginDate=" + beginDate +
				", overDate=" + overDate +
				", endDate=" + endDate +
				", page=" + page +
				", rows=" + rows +
				", typeNum=" + typeNum +
				", count=" + count +
				", sum=" + sum +
				", number=" + number +
				", total=" + total +
				", statusInCompary='" + statusInCompary + '\'' +
				", statusInGroup='" + statusInGroup + '\'' +
				", statusInStore='" + statusInStore + '\'' +
				", isGift='" + isGift + '\'' +
				", specOne='" + specOne + '\'' +
				", specTwo='" + specTwo + '\'' +
				", maxCount=" + maxCount +
				", minCount=" + minCount +
				", roles=" + Arrays.toString( roles ) +
				", toUserId='" + toUserId + '\'' +
				", toStoreId='" + toStoreId + '\'' +
				", toComparyId='" + toComparyId + '\'' +
				", toDataType='" + toDataType + '\'' +
				", productId='" + productId + '\'' +
				", bdGoodsCatId='" + bdGoodsCatId + '\'' +
				", id='" + id + '\'' +
				", tag='" + tag + '\'' +
				", tags=" + Arrays.toString( tags ) +
				", ids=" + Arrays.toString( ids ) +
				", userName='" + userName + '\'' +
				", returnInfo='" + returnInfo + '\'' +
				", comShortUrl='" + comShortUrl + '\'' +
				", storeShortUrl='" + storeShortUrl + '\'' +
				", map=" + map +
				", maps=" + maps +
				", sqlStr='" + sqlStr + '\'' +
				'}';
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public String getComShortUrl() {
		return comShortUrl;
	}

	public void setComShortUrl(String comShortUrl) {
		this.comShortUrl = comShortUrl;
	}

	public String getStoreShortUrl() {
		return storeShortUrl;
	}

	public void setStoreShortUrl(String storeShortUrl) {
		this.storeShortUrl = storeShortUrl;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getOverDate() {
		return overDate;
	}

	public void setOverDate(Date overDate) {
		this.overDate = overDate;
	}

	public List<Map<String, Object>> getMaps() {
		return maps;
	}

	public void setMaps(List<Map<String, Object>> maps) {
		this.maps = maps;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStoreTel() {
		return storeTel;
	}

	public void setStoreTel(String storeTel) {
		this.storeTel = storeTel;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getSmsCount() {
		return smsCount;
	}

	public void setSmsCount(int smsCount) {
		this.smsCount = smsCount;
	}

	public Date getLimitDate() {
		return limitDate;
	}

	public void setLimitDate(Date limitDate) {
		this.limitDate = limitDate;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public float getIntergral() {
		return intergral;
	}

	public void setIntergral(float intergral) {
		this.intergral = intergral;
	}

	public String getBorrowingType() {
		return borrowingType;
	}

	public void setBorrowingType(String borrowingType) {
		this.borrowingType = borrowingType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getToEntityId() {
		return toEntityId;
	}

	public void setToEntityId(String toEntityId) {
		this.toEntityId = toEntityId;
	}

	public int getTypeNum() {
		return typeNum;
	}

	public void setTypeNum(int typeNum) {
		this.typeNum = typeNum;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String getReturnInfo() {
		return returnInfo;
	}

	public void setReturnInfo(String returnInfo) {
		this.returnInfo = returnInfo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String[] getIds() {
		return ids;
	}


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setIds(String[] ids) {
		this.ids = ids;
	}


	public String getEntityTypeId() {
		return entityTypeId;
	}

	public void setEntityTypeId(String entityTypeId) {
		this.entityTypeId = entityTypeId;
	}

	public String getDescType() {
		return descType;
	}

	public void setDescType(String descType) {
		this.descType = descType;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getSqlStr() {
		return sqlStr;
	}

	public void setSqlStr(String sqlStr) {
		this.sqlStr = sqlStr;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBdGoodsCatId() {
		return bdGoodsCatId;
	}

	public void setBdGoodsCatId(String bdGoodsCatId) {
		this.bdGoodsCatId = bdGoodsCatId;
	}

	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getToDataType() {
		return toDataType;
	}

	public void setToDataType(String toDataType) {
		this.toDataType = toDataType;
	}

	public String getToStoreId() {
		return toStoreId;
	}

	public void setToStoreId(String toStoreId) {
		this.toStoreId = toStoreId;
	}

	public String getToComparyId() {
		return toComparyId;
	}

	public void setToComparyId(String toComparyId) {
		this.toComparyId = toComparyId;
	}

	public String getToUserId() {
		return toUserId;
	}

	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}

	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	public String getSpecOne() {
		return specOne;
	}

	public void setSpecOne(String specOne) {
		this.specOne = specOne;
	}

	public String getSpecTwo() {
		return specTwo;
	}

	public void setSpecTwo(String specTwo) {
		this.specTwo = specTwo;
	}

	public Long getMinCount() {
		return minCount;
	}

	public void setMinCount(Long minCount) {
		this.minCount = minCount;
	}

	public Long getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(Long maxCount) {
		this.maxCount = maxCount;
	}

	public String getStatusInCompary() {
		return statusInCompary;
	}

	public void setStatusInCompary(String statusInCompary) {
		this.statusInCompary = statusInCompary;
	}

	public String getStatusInGroup() {
		return statusInGroup;
	}

	public void setStatusInGroup(String statusInGroup) {
		this.statusInGroup = statusInGroup;
	}

	public String getStatusInStore() {
		return statusInStore;
	}

	public void setStatusInStore(String statusInStore) {
		this.statusInStore = statusInStore;
	}

	public String getIsGift() {
		return isGift;
	}

	public void setIsGift(String isGift) {
		this.isGift = isGift;
	}

	public String getHelpStoreStatus() {
		return helpStoreStatus;
	}

	public void setHelpStoreStatus(String helpStoreStatus) {
		this.helpStoreStatus = helpStoreStatus;
	}

	public String getClassified() {
		return classified;
	}

	public void setClassified(String classified) {
		this.classified = classified;
	}

	public String getGoodsEncoding() {
		return goodsEncoding;
	}

	public void setGoodsEncoding(String goodsEncoding) {
		this.goodsEncoding = goodsEncoding;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}


	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDateType() {
		return dateType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}


	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public String getComparyId() {
		return comparyId;
	}

	public void setComparyId(String comparyId) {
		this.comparyId = comparyId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getSaleOrderId() {
		return saleOrderId;
	}

	public void setSaleOrderId(String saleOrderId) {
		this.saleOrderId = saleOrderId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGoodsCatId() {
		return goodsCatId;
	}

	public void setGoodsCatId(String goodsCatId) {
		this.goodsCatId = goodsCatId;
	}

	public String getParentGoodsCatId() {
		return parentGoodsCatId;
	}

	public void setParentGoodsCatId(String parentGoodsCatId) {
		this.parentGoodsCatId = parentGoodsCatId;
	}

	public String getStoreGroupId() {
		return storeGroupId;
	}

	public void setStoreGroupId(String storeGroupId) {
		this.storeGroupId = storeGroupId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getGoodsItemId() {
		return goodsItemId;
	}

	public void setGoodsItemId(String goodsItemId) {
		this.goodsItemId = goodsItemId;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
}
