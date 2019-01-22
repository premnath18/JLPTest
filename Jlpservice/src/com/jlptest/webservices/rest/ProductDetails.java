package com.jlptest.webservices.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductDetails {
	public String productId = null;
	public String title = null;
	public List<HashMap<String,String>> colorSwatches = new ArrayList<HashMap<String,String>>();
	public String nowPrice = null;
	public String priceLabel = null;
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<HashMap<String,String>> getColorSwatches() {
		return colorSwatches;
	}
	public void setColorSwatches(List<HashMap<String,String>> colorSwatches) {
		this.colorSwatches = colorSwatches;
	}
	public String getNowPrice() {
		return nowPrice;
	}
	public void setNowPrice(String nowPrice) {
		this.nowPrice = nowPrice;
	}
	public String getPriceLabel() {
		return priceLabel;
	}
	public void setPriceLabel(String priceLabel) {
		this.priceLabel = priceLabel;
	}
		

}
