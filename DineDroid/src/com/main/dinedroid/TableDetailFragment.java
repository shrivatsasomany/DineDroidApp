package com.main.dinedroid;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TableDetailFragment extends Fragment {
	private Integer tableId;
	private Integer waiterId;
	private String waiterName;

	private TextView tableIdTextView;
	private TextView waiterIdTextView;
	private TextView orderStatusTextView;
	private TextView tableNumTextView;
	private TextView waiterNameTextView;
	private TextView statusNumTextView;
	
	private Typeface ql;
	private Typeface qd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_table_detail,
				container, false);
		ql = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand_Book.otf");
		qd = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand_Dash.otf");

		int numInfo = getArguments().getInt("NumInfo");
		tableId = getArguments().getInt("tableId");
		if (numInfo == 2) {
			waiterId = getArguments().getInt("waiterId");
			waiterName = getArguments().getString("waiterName");
		}
		tableIdTextView = (TextView) rootView.findViewById(R.id.fragment_table_detail_tableInfo);
		waiterIdTextView = (TextView) rootView.findViewById(R.id.fragment_table_detail_waiterInfo);
		orderStatusTextView = (TextView) rootView.findViewById(R.id.fragment_table_detail_orderStatus);
		tableNumTextView = (TextView) rootView.findViewById(R.id.fragment_table_detail_tableNum);
		waiterNameTextView = (TextView) rootView.findViewById(R.id.fragment_table_detail_waiterName);
		statusNumTextView = (TextView) rootView.findViewById(R.id.fragment_table_detail_numStatus);
		tableIdTextView.setTypeface(qd);
		waiterIdTextView.setTypeface(qd);
		orderStatusTextView.setTypeface(qd);
		tableNumTextView.setTypeface(ql);
		waiterNameTextView.setTypeface(ql);
		statusNumTextView.setTypeface(ql);
		
		tableNumTextView.setText(""+tableId);
		
		if(waiterId!=null)
		{
			waiterNameTextView.setText(""+waiterName);
		}
		else
		{
			waiterNameTextView.setText("Unassigned");

		}
		return rootView;
	}
	
	public void setWaiterName(String waiterName)
	{
		this.waiterName = waiterName;
		this.waiterNameTextView.setText(""+this.waiterName);
	}
}
