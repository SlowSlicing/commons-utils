package com.lynchj.bean;

import java.math.BigDecimal;
import java.util.Date;

import com.lynchj.annotation.PropertiesName;

public class Book {

	@PropertiesName(name = "书本编号")
	private Integer id;

	@PropertiesName(name = "书本名称")
	private String name;

	@PropertiesName(name = "书本价格")
	private BigDecimal price;

	@PropertiesName(name = "书本二次价格")
	private Double priceTwo;
	
	@PropertiesName(name = "出版日期")
	private Date chuBanRiQi;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Double getPriceTwo() {
		return priceTwo;
	}

	public void setPriceTwo(Double priceTwo) {
		this.priceTwo = priceTwo;
	}

	public Date getChuBanRiQi() {
		return chuBanRiQi;
	}

	public void setChuBanRiQi(Date chuBanRiQi) {
		this.chuBanRiQi = chuBanRiQi;
	}

	@Override
	public String toString() {
		return "Book [id=" + id + ", name=" + name + ", price=" + price + ", priceTwo=" + priceTwo + ", chuBanRiQi="
				+ chuBanRiQi + "]";
	}
	
}
