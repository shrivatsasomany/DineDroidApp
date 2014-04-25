package com.main.dinedroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TableDetailFragment extends Fragment {
	private TextView tableId;
	private TextView waiterId;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

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
		tableId = (TextView) getActivity().findViewById(R.id.tableInfo);
		waiterId = (TextView) getActivity().findViewById(R.id.waiterInfo);
		tableId.setText("Table : " + ((MainActivity)getActivity()).getTableId());
		tableId.setText("Waiter : " + ((MainActivity)getActivity()).getWaiterId());

		return rootView;
	}
}
