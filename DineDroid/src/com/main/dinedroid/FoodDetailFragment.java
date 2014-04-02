package com.main.dinedroid;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.ListView;

import com.main.dinedroid.customclasses.FoodMenuListAdapter;
import com.main.dinedroid.menu.FoodItem;

public class FoodDetailFragment extends Fragment {

	public static final String ARG_ITEM_ID = "item_id";

	private ListView categories;
	private ImageView background;
	private Button backButton;
	private FoodMenuListAdapter listAdapter;
	private FoodItem selectedItem;
	private FoodItem passedItem;

	private float alpha = (float) 0.3;
	private DetailListSelectionListener mListener;

	public interface DetailListSelectionListener {
		public void onDetailListSelection(FoodItem item);
	}

	public void onListItemClick(FoodMenuListAdapter adapter, View v, int pos,
			long id) {
		selectedItem = (FoodItem) adapter.getItem(pos);
		mListener.onDetailListSelection(selectedItem);
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
			mListener = (DetailListSelectionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement Poop");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		return rootView;
	}

	public void populateList(FoodItem item) {
		categories.setVisibility(View.VISIBLE);
		passedItem = item;
		Log.d("FoodItem", "Items size: " + item.getItems().size());
		listAdapter = new FoodMenuListAdapter(getActivity(),
				R.layout.food_list_item, R.id.food_list_item_name,
				R.id.food_list_item_price, item.getItems());
		categories.setAdapter(listAdapter);
		listAdapter.notifyDataSetChanged();
		background.setAlpha(alpha);
	}

	public void backToParent() {
		passedItem = passedItem.getParent();
		if (passedItem == null) {		
			categories.setVisibility(View.INVISIBLE);
			background.setAlpha(new Float(1));
		}
		mListener.onDetailListSelection(passedItem);

	}

}
