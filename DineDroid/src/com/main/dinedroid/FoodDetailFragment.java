package com.main.dinedroid;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.main.dinedroid.customclasses.ExtraMenuListAdapter;
import com.main.dinedroid.customclasses.FoodMenuListAdapter;
import com.main.dinedroid.menu.FoodItem;

public class FoodDetailFragment extends Fragment {

	public static final String ARG_ITEM_ID = "item_id";

	private ListView categories;
	private ImageView background;
	private Button backButton;
	private FoodMenuListAdapter listAdapter;
	private ExtraMenuListAdapter extrasListAdapter;
	private FoodItem selectedItem;
	private FoodItem passedItem;
	private int quantity_counter = 1;


	private float alpha = (float) 0.3;
	private DetailListSelectionListener mDetailListener;
	private FoodItemSelectionListener mFoodListener;

	public interface FoodItemSelectionListener {
		public void onFoodItemSelected(FoodItem item);
	}
	public interface DetailListSelectionListener {
		public void onDetailListSelection(FoodItem item);
	}

	public void onListItemClick(FoodMenuListAdapter adapter, View v, int pos,
			long id) {
		selectedItem = (FoodItem) adapter.getItem(pos);
		if (selectedItem.isCategory()) {
			mDetailListener.onDetailListSelection(selectedItem);
		} else {
			showFoodDialog(selectedItem);
		}
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FoodDetailFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mDetailListener = (DetailListSelectionListener) activity;
			mFoodListener = (FoodItemSelectionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement listeners!");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_food_detail,
				container, false);
		categories = (ListView) rootView
				.findViewById(R.id.fragment_food_detail_listview);
		categories.setVisibility(View.INVISIBLE);
		categories.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onListItemClick(listAdapter, view, position, id);
				// TODO Auto-generated method stub

			}
		});
		background = (ImageView) rootView
				.findViewById(R.id.fragment_food_detail_background);
		backButton = (Button) rootView
				.findViewById(R.id.fragment_food_detail_back_button);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				backToParent();
			}
		});
		backButton.setVisibility(View.GONE);
		return rootView;
	}

	public void clearFragment() {
		if (listAdapter != null) {
			categories.setVisibility(View.INVISIBLE);
			backButton.setVisibility(View.GONE);
		}
	}

	public void populateList(FoodItem item) {
		categories.setVisibility(View.VISIBLE);
		backButton.setVisibility(View.VISIBLE);
		passedItem = item;
		Log.d("FoodItem", "Items size: " + item.getItems().size());
		if (item.getItems().size() != 0) {
			listAdapter = new FoodMenuListAdapter(getActivity(),
					R.layout.food_list_item, R.id.food_list_item_name,
					R.id.food_list_item_price, item.getItems());
			categories.setAdapter(listAdapter);
			listAdapter.notifyDataSetChanged();
			background.setAlpha(alpha);
		} else {
			Toast.makeText(getActivity(),
					"Sorry, there are no items available to order",
					Toast.LENGTH_SHORT).show();
			mDetailListener.onDetailListSelection(null);
			clearFragment();
		}
	}

	public void backToParent() {
		passedItem = passedItem.getParent();
		if (passedItem == null) {
			clearFragment();
			background.setAlpha(new Float(1));
		}
		mDetailListener.onDetailListSelection(passedItem);

	}

	public void showFoodDialog(FoodItem item) {
		final HashMap<Integer, FoodItem> extrasList = new HashMap<Integer, FoodItem>();
		final EditText notes = new EditText(getActivity());
		final FoodItem temp = new FoodItem(item.getID(), item.getName(), item.getPrice(), false);
		AlertDialog.Builder customDialog = new AlertDialog.Builder(
				getActivity());
		customDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				temp.setQuantity(quantity_counter);
				Iterator it = extrasList.entrySet().iterator();
				while(it.hasNext())
				{
					Map.Entry pairs = (Map.Entry)it.next();
					temp.addExtra((FoodItem) pairs.getValue());
				}
				String itemNotes = notes.getText().toString();
				if(!itemNotes.equals(""))
				{
					temp.addNotes(itemNotes);
				}
				mFoodListener.onFoodItemSelected(temp);
				dialog.dismiss();
			}
		});

		customDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		DecimalFormat oneDigit = new DecimalFormat("#,##0");
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		customDialog.setTitle(item.getName() + "\t-\t $"+nf.format(item.getPrice()));
		LayoutInflater layoutInflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.add_food_dialog, null);
		Dialog d = customDialog.setView(view).create();

		quantity_counter = 1;

		TextView plus = (TextView)view.findViewById(R.id.add_food_dialog_quantity_textviewplus);
		TextView minus = (TextView)view.findViewById(R.id.add_food_dialog_quantity_textviewminus);
		TextView extra = (TextView)view.findViewById(R.id.add_food_dialog_quantity_extrastext);

		final EditText quantity = (EditText)view.findViewById(R.id.add_food_dialog_quantity_edittext);
		quantity.clearFocus();
		quantity.setText("1");
		
		extrasListAdapter = new ExtraMenuListAdapter(getActivity(), R.layout.extra_list_item, R.id.extra_list_item_name, R.id.extra_list_item_price,R.id.extra_list_item_check_box, item.getExtras());
		
		final ListView extras = (ListView)view.findViewById(R.id.add_food_dialog_quantity_extraslist);
		extras.addFooterView(notes);
		extras.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {				
				FoodItem selectedExtra = (FoodItem) extrasListAdapter.getItem(position);
				CheckBox x = (CheckBox)view.findViewById(R.id.extra_list_item_check_box);
				if(x.isChecked())
				{
					x.setChecked(false);
					extrasList.remove(selectedExtra.getID());
				}
				else
				{
					x.setChecked(true);
					extrasList.put(selectedExtra.getID(), selectedExtra);
				}

			}
		});

		plus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(quantity_counter < 20)
				{
					++quantity_counter;
					quantity.setText(""+quantity_counter);
				}
				else
				{
					Toast.makeText(getActivity(), "You can't order more than " + quantity_counter+"!", Toast.LENGTH_SHORT).show();
				}
			}
		});

		minus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(quantity_counter > 1)
				{
					--quantity_counter;
					quantity.setText(""+quantity_counter);
				}
				else
				{
					Toast.makeText(getActivity(), "You need to order at least one!", Toast.LENGTH_SHORT).show();
				}
			}
		});

		if(item.getExtras().size()==0)
		{
			extra.setText("No Extras");
		}
		else
		{
			extras.setAdapter(extrasListAdapter);
		}

		d.show();

	}

}
