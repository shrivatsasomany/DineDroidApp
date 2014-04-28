package com.main.dinedroid;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.main.dinedroid.menu.Menu;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
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
    private String server_address;
    private SharedPreferences spref;
    private Socket s;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private int orderStatus = 1;
    private getOrderStatusAsyncTask getStatus;

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
		spref = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
		
		getOrderStatus();
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
	
	public void getOrderStatus(){
		ScheduledExecutorService scheduler =
			    Executors.newSingleThreadScheduledExecutor();

			scheduler.scheduleAtFixedRate
			      (new Runnable() {
			         public void run() {
			            // call service
			        	 if(getStatus != null){
			        		 getStatus.cancel(false);
			        	 }
			        	 getStatus = (getOrderStatusAsyncTask) new getOrderStatusAsyncTask().execute();
			         }
			      }, 0, 1, TimeUnit.MINUTES);
	}
	
	public void setWaiterName(String waiterName)
	{
		this.waiterName = waiterName;
		this.waiterNameTextView.setText(""+this.waiterName);
	}
	
	public class getOrderStatusAsyncTask extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected void onPostExecute(Void result)
		{
			setOrderStatus();
		}
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
    		server_address = spref.getString("ServerAddress", "10.0.1.14");

			try{
				s = new Socket(server_address, 4322);
				out = new ObjectOutputStream(s.getOutputStream());
				out.flush();
				out.writeObject("Table||Get_Order_Status||"+ tableId);
				out.flush();
				in = new ObjectInputStream(s.getInputStream());
				orderStatus = in.readInt();
				in.close();
				out.close();
				s.close();
			}
			catch(Exception e){
				Log.d("communication","Comm Error");
			}
			return null;
		}
		
	}
	public void setOrderStatus(){
		switch(orderStatus){
		case 1:	statusNumTextView.setText("Processing...");
				break;
		case 2: statusNumTextView.setText("Delayed");
				break;
		case 3: statusNumTextView.setText("Problem");
				break;
		}
	}

}
