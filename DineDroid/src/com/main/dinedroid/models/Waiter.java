package com.main.dinedroid.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Waiter implements Serializable {
	private int id;
	private String fname;
	private String lname;
	private ArrayList<Table> tables; 
	private ArrayList<Table> hailQueue;
	
	public Waiter(int id, String fname, String lname) {
		this.id = id;
		this.fname = fname;
		this.lname = lname;
		this.tables = new ArrayList<Table>();
		this.hailQueue = new ArrayList<Table>();
	}
	/**
	 * 
	 * @return id Waiter id
	 */
	public int getId() {
		return id;
	}
	/**
	 * 
	 * @param id Waiter id
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * 
	 * @return fname Waiter's First Name
	 */
	public String getFName() {
		return fname;
	}
	/**
	 * 
	 * @param fname Waiter's First Name
	 */
	public void setFName(String fname) {
		this.fname = fname;
	}
	/**
	 * 
	 * @return lname Waiter's last name
	 */
	public String getLName() {
		return lname;
	}
	/**
	 * 
	 * @param fname
	 */
	public void setLName(String fname) {
		this.lname = fname;
	}
	/**
	 * 
	 * @return tables List of Tables for the Waiter
	 */
	public ArrayList<Table> getTables() {
		return tables;
	}
	/**
	 * 
	 * @param tables
	 */
	public void setTables(ArrayList<Table> tables) {
		this.tables = tables;
	}
	/**
	 * 
	 * @param table
	 * @return boolean true on successful addition of table, false otherwise
	 */
	public boolean addTable(Table table)
	{
		for(Table t : tables)
		{
			if(t.getId() == table.getId())
			{
				return false;
			}
		}
		tables.add(table);
		return true;
	}
	/**
	 * 
	 * @param tableId
	 * @return boolean true on successful removal of table, false otherwise
	 */
	public boolean removeTable(int tableId)
	{
		for(int i = 0; i < tables.size(); ++i)
		{
			if(tables.get(i).getId() == tableId)
			{
				tables.remove(i);
				return true;
			}
		}
		return false;
	}
	/**
	 * 
	 * @return boolean true on successful removal of all tables, false otherwise
	 */
	public boolean removeAllTables()
	{
		for(int i = 0; i < tables.size(); ++i)
		{
			tables.remove(i);
		}
		return true;
	}
	/**
	 * 
	 * @return hailQueue Hail Queue for the waiter
	 */
	public ArrayList<Table> getHailQueue() {
		return hailQueue;
	}
	/**
	 * 
	 * @param hailQueue
	 */
	public void setHailQueue(ArrayList<Table> hailQueue) {
		this.hailQueue = hailQueue;
	}
	/**
	 * 
	 * @param table
	 * @return boolean true on successful addition of hail, false otherwise
	 */
	public synchronized boolean addHail(Table table)
	{
		for(int i = 0; i < hailQueue.size(); ++i)
		{
			if(hailQueue.get(i).getId() == table.getId())
			{
				return false;
			}
		}
		return (this.hailQueue.add(table));
	}
	/**
	 * 
	 * @param tableId
	 * @return boolean true on successful removal of hail, false otherwise
	 */
	public synchronized boolean removeHail(int tableId)
	{
		for(int i = 0; i < hailQueue.size(); ++i)
		{
			if(hailQueue.get(i).getId() == tableId)
			{
				hailQueue.remove(i);
				return true;
			}
		}
		return false;
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Waiter other = (Waiter) obj;
		if (id != other.id)
			return false;
		return true;
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String tableInfo = "";
		if(!tables.isEmpty())
		{
			tableInfo = "[-";
			for(Table e : tables)
			{
				tableInfo+=e.getId()+"-";
			}
			tableInfo += "]";
		}
		return "ID: "+id+"\tName: "+fname+ " "+lname + " - Tables: "+tableInfo;
	}
	
}
