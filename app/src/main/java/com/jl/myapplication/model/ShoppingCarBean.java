package com.jl.myapplication.model;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShoppingCarBean {
    public String title;
    public String imageUrl;
    public BigDecimal money;
    public int number;
    public Boolean isSelect = false;

    public ShoppingCarBean(String title, String imageUrl, BigDecimal money, int number) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.money = money;
        this.number = number;
    }

    public static List<ShoppingCarBean> getTestData3() {
        List<ShoppingCarBean> list = new ArrayList<>();
        list.add(new ShoppingCarBean("手机","https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg", new BigDecimal("5000"), 1));
        list.add(new ShoppingCarBean("平板","https://img.zcool.cn/community/01639a56fb62ff6ac725794891960d.jpg",  new BigDecimal("10000"), 1));
        list.add(new ShoppingCarBean("名字很长有没有问题","https://img.zcool.cn/community/01270156fb62fd6ac72579485aa893.jpg",  new BigDecimal("1.08"), 1));
        list.add(new ShoppingCarBean("游戏皮肤","https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg",  new BigDecimal("1.06"), 1));
        list.add(new ShoppingCarBean("零食","https://img.zcool.cn/community/016a2256fb63006ac7257948f83349.jpg",  new BigDecimal("130.0"), 1));
        return list;
    }
}
