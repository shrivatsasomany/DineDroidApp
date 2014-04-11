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
import android.widget.CheckBox;
import android.widget.TextView;

import com.main.dinedroid.R;
import com.main.dinedroid.menu.FoodItem;

public class ExtraMenuListAdapter extends ArrayAdapter 
{ 
	Activity context;
	ArrayList<FoodItem> extras;
	int layoutId;
	int name_id;
	int price_id;
	int check_id;



	public ExtraMenuListAdapter(Activity context, int layoutId, int name_id, int price_id,int check_id,  ArrayList<FoodItem>extras)
	{ 
		super(context, layoutId, extras); 

		this.context = context; 
		this.extras = extras;
		this.layoutId = layoutId;
		this.name_id = name_id;
		this.price_id = price_id;
		this.check_id = check_id;

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
		ScrollingTextView title=(ScrollingTextView)row.findViewById(name_id);
		TextView price = (TextView)row.findViewById(price_id);
		CheckBox box = (CheckBox)row.findViewById(check_id);


		title.setText(extras.get(pos).getName()); 


		DecimalFormat oneDigit = new DecimalFormat("#,##0");
		NumberFormat nf = NumberFormat.getInstance(Locale.US);

		if(extras.get(pos).isCategory())
		{
			price.setText("\u2192");
		}
		else if(extras.get(pos).getPrice() == 0)
		{
			price.setText("Free");
		}
		else
		{
			//price.setText(new Double(oneDigit.format(items.get(pos).getPrice())).toString());
			price.setText("$"+nf.format(extras.get(pos).getPrice()));
		}



		return(row); 
	} 

	public void refill(ArrayList<FoodItem> refillItems)
	{
		extras = refillItems;
		notifyDataSetChanged();
	}
} 
