package com.main.dinedroid.customclasses;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.main.dinedroid.menu.FoodItem;

public class FoodMenuListAdapter extends ArrayAdapter 
{ 
	Activity context;
	ArrayList<FoodItem> items;
	boolean[] arrows;
	int layoutId;
	int name_id;
	int desc_id;
	int price_id;
	int tags_id;
	int street_id;
	int neighborhood_id;



	public FoodMenuListAdapter(Activity context, int layoutId, int name_id, int desc_id, int price_id, ArrayList<FoodItem>items)
	{ 
		super(context, layoutId, items); 

		this.context = context; 
		this.items = items;
		this.layoutId = layoutId;
		this.name_id = name_id;
		this.desc_id = desc_id;
		this.price_id = price_id;

	} 

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) 
	{ 
		View row = convertView;
		if (row == null)
		{
			LayoutInflater inflater=context.getLayoutInflater(); 
			row=inflater.inflate(layoutId, null); 
		}
		TextView title=(TextView)row.findViewById(name_id);
		ScrollingTextView description=(ScrollingTextView)row.findViewById(desc_id);
		TextView price = (TextView)row.findViewById(price_id);
	


		title.setText(items.get(pos).getName()); 
		
		if(items.get(pos).toString() == null)
		{
			description.setVisibility(ScrollingTextView.GONE);
		}
		else
		{
			description.setText(items.get(pos).toString());
		}
		
		DecimalFormat oneDigit = new DecimalFormat("#,##0");
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		if(items.get(pos).getPrice()==null)
		{
			price.setText("...");
		}
		else if(items.get(pos).getPrice() == 0)
		{
			price.setText("Free");
		}
		else if(!items.get(pos).isCategory())
		{
			price.setText("...");
		}
		else
		{
			//price.setText(new Double(oneDigit.format(items.get(pos).getPrice())).toString());
			price.setText(nf.format(items.get(pos).getPrice()));
		}
		
		

		return(row); 
	} 

	public void refill(ArrayList<FoodItem> refillItems)
	{
		items = refillItems;
		notifyDataSetChanged();
	}
} 
