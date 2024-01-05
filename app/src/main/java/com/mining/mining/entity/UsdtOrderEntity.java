package com.mining.mining.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class UsdtOrderEntity {
    /**
     * buyUserId
     */
    @SerializedName("buy_user_id")
    public String buyUserId;
    /**
     * c2cUsdtId
     */
    @SerializedName("c2c_usdt_id")
    public String c2cUsdtId;
    /**
     * id
     */
    @SerializedName("id")
    public String id;
    /**
     * isAuthentication
     */
    @SerializedName("is_authentication")
    public Integer isAuthentication;
    /**
     * name
     */
    @SerializedName("name")
    public String name;
    /**
     * payType
     */
    @SerializedName("pay_type")
    public String payType;
    /**
     * price
     */
    @SerializedName("price")
    public String price;
    /**
     * rmb
     */
    @SerializedName("rmb")
    public String rmb;
    /**
     * time
     */
    @SerializedName("time")
    public String time;
    /**
     * type
     */
    @SerializedName("type")
    public String type;
    /**
     * usdt
     */
    @SerializedName("usdt")
    public String usdt;
    /**
     * userId
     */
    @SerializedName("user_id")
    public String userId;


    public String json;

    public static UsdtOrderEntity objectFromData(String str) {
        UsdtOrderEntity entity = new Gson().fromJson(str, UsdtOrderEntity.class);
        entity.json = str;
        return entity;
    }
}
