package com.main.dinedroid.models;

import java.io.Serializable;
import java.util.ArrayList;

import com.main.dinedroid.menu.FoodItem;

public class Order implements Serializable {
	
	private Table orderTable;
	private ArrayList<FoodItem> order;
	private String orderNotes;
	
	public Order(ArrayList<FoodItem> order)
	{
		this.order = order;
		this.orderTable = null;
	}
	
	public ArrayList<FoodItem> getOrder()
	{
		return order;
	}
	
	public void setOrder(ArrayList<FoodItem> order)
	{
		this.order = order;
	}
	
	public Table getOrderTable()
	{
		return orderTable;
	}
	
	public void setOrderTable(Table orderTable)
	{
		this.orderTable = orderTable;
	}
	
	public void setOrderNotes(String orderNotes)
	{
		this.orderNotes = orderNotes;
	}
	
	public String getOrderNotes()
	{
		return orderNotes;
	}
	
	public String toString()
	{
		return "Table: "+orderTable.getId()+"";
	}
	
	/**
	 * 
	 * @return Detailed Order information
	 */
	public String detailedString()
	{
		String temp;
		if(order.size() == 0)
		{
			temp = "Table: "+orderTable.getId()+"\nNo Order";
			return temp;
		}
		else
		{
			temp = "Table: "+orderTable.getId()+"\nOrder:\n";
			for(int i = 0; i < order.size(); ++i)
			{
				temp += order.get(i).displayString() + "\n";
			}
			
			return temp;
		}
	}
}
