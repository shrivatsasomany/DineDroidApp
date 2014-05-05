package com.main.dinedroid.models;

import java.io.Serializable;
import java.util.ArrayList;

import com.main.dinedroid.menu.FoodItem;

public class Table implements Serializable {
	
	private Integer id;
	private Waiter waiter;
	Order order;
	private String customerName;
	private boolean isOccupied;
	
	/*
	 * 1 -> OK
	 * 2 -> Delayed
	 * 3 -> Problem
	 */
	private Integer orderStatus;
	
	public Table(Integer id)
	{
		this.id = id;
		waiter = null;
		order = null;
		customerName = null;
		isOccupied = false;
	}
	/**
	 * 
	 * @return id Table id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * 
	 * @param id Table id
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * 
	 * @return waiter Waiter Object
	 */
	public Waiter getWaiter() {
		return waiter;
	}
	/**
	 * 
	 * @param waiter
	 */
	public void setWaiter(Waiter waiter) {
		this.waiter = waiter;
	}
	/**
	 * 
	 * @return order
	 */
	public Order getOrder() {
		return order;
	}
	/**
	 * 
	 * @param order
	 */
	public void setOrder(Order order) {
		this.order = order;
	}
	/**
	 * 
	 * @return customerName Name of the customer at the table
	 */
	public String getCustomerName() {
		return customerName;
	}
	/**
	 * 
	 * @param customerName
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	/**
	 * 
	 * @return isOccupied A boolean set to true if the table is occupied and false otherwise
	 */
	public boolean isOccupied() {
		return isOccupied;
	}
	/**
	 * 
	 * @param isOccupied
	 */
	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}
	/**
	 * 
	 * @return orderStatus
	 * 1 -> OK
	 * 2 -> Delayed
	 * 3 -> Problem
	 */
	public Integer getOrderStatus() {
		return orderStatus;
	}
	/**
	 * 
	 * @param orderStatus
	 */
	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Table other = (Table) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String occupied = "Inactive";
		if(isOccupied)
		{
			occupied = "Active";
		}
		return "Table Number: " + id + " - " + occupied;
	}
	
}
