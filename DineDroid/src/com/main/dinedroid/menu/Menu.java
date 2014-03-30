package com.main.dinedroid.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Menu implements Serializable {
	private ArrayList<FoodItem> items = new ArrayList<FoodItem>();

	public Menu() {
	}

	public void setParent() {
		if (items != null || !items.isEmpty()) {
			for (int i = 0; i < items.size(); ++i) {
				items.get(i).setParent(null);
			}
		}
	}

	public void populateMap(HashMap<Object, FoodItem> myMap) {
		if (items != null || !items.isEmpty()) {
			for (int i = 0; i < items.size(); ++i) {
				myMap.put(new Integer(items.get(i).getID()), items.get(i));
				items.get(i).populateMap(myMap);
			}
		}
	}

	public ArrayList<FoodItem> getItems() {
		return items;
	}

	public boolean addItem(FoodItem e) {
		boolean x = items.add(e);
		return x;
	}

	public boolean removeItem(FoodItem e) {
		return items.remove(e);
	}
	
	public FoodItem findItem(int itemId)
	{
		FoodItem result = null;
		for(int i = 0; i < items.size(); ++i)
		{
			result = items.get(i).findItem(itemId);
			if (result != null)
			{
				break;
			}
		}
		return result;
	}
}
