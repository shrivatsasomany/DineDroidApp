package com.main.dinedroid.customclasses;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.main.dinedroid.R;
import com.main.dinedroid.menu.FoodItem;

public class OrderListAdapter extends ArrayAdapter {
	Activity context;
	ArrayList<FoodItem> order;
	int availability_id;
	int layout_id;
	int name_id;
	int price_id;
	int quantity_id;
	Typeface ql;

	public OrderListAdapter(Activity context, int layoutId,
			int availability_id, int name_id, int quantity_id, int price_id,
			ArrayList<FoodItem> order) {
		super(context, layoutId, order);

		this.context = context;
		this.order = order;
		this.layout_id = layoutId;
		this.availability_id = availability_id;
		this.name_id = name_id;
		this.price_id = price_id;
		this.quantity_id = quantity_id;
		ql = Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand_Bold.otf");

	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(layout_id, null);
		}
		ScrollingTextView title = (ScrollingTextView) row.findViewById(name_id);
		TextView price = (TextView) row.findViewById(price_id);
		TextView quantity = (TextView) row.findViewById(quantity_id);
		TextView availability = (TextView) row.findViewById(availability_id);
		title.setTypeface(ql);
		price.setTypeface(ql);
		quantity.setTypeface(ql);
		if (!order.get(pos).isAvailable()) {
			
			availability.setVisibility(View.VISIBLE);
		}
		title.setText(order.get(pos).getName());
		quantity.setText(order.get(pos).getQuantity() + "");

		DecimalFormat oneDigit = new DecimalFormat("#,##0");
		NumberFormat nf = NumberFormat.getInstance(Locale.US);

		if (order.get(pos).getPrice() == 0) {
			price.setText("Free");
		} else {
			// price.setText(new
			// Double(oneDigit.format(items.get(pos).getPrice())).toString());
			double fullPrice = order.get(pos).getPrice();
			for (FoodItem e : order.get(pos).getExtras()) {
				fullPrice += (e.getPrice());
			}
			fullPrice = fullPrice * order.get(pos).getQuantity();
			price.setText("$" + nf.format(fullPrice));
		}

		return (row);
	}

	public void refill(ArrayList<FoodItem> refillItems) {
		order = refillItems;
		notifyDataSetChanged();
	}
}
