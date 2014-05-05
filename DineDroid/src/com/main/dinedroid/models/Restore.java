package com.main.dinedroid.models;

import java.io.Serializable;

public class Restore implements Serializable {
	
	private boolean isOccupied;
	private Order o;
	private Waiter waiter;
	
	public Restore(boolean isOccupied, Order o, Waiter waiter) {
		this.isOccupied = isOccupied;
		this.o = o;
		this.waiter = waiter;
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
	 * @return o Order
	 */
	public Order getOrder() {
		return o;
	}
	/**
	 * 
	 * @param o Order
	 */
	public void setOrder(Order o) {
		this.o = o;
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
	
}
