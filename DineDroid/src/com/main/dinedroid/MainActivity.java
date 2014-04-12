package com.main.dinedroid;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import jim.h.common.android.lib.zxing.config.ZXingLibConfig;
import jim.h.common.android.lib.zxing.integrator.IntentIntegrator;
import jim.h.common.android.lib.zxing.integrator.IntentResult;
import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.main.dinedroid.FoodDetailFragment.DetailListSelectionListener;
import com.main.dinedroid.FoodDetailFragment.FoodItemSelectionListener;
import com.main.dinedroid.FoodListFragment.MenuListSelectionListener;
import com.main.dinedroid.menu.FoodItem;
import com.main.dinedroid.models.Order;

//import com.main.dinedroid.menu.Menu;

public class MainActivity extends FragmentActivity implements
		MenuListSelectionListener, DetailListSelectionListener,
		FoodItemSelectionListener {

	private Socket s;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private FragmentManager fm;
	FoodListFragment menu_fragment = new FoodListFragment();
	FoodDetailFragment detail_fragment = new FoodDetailFragment();
	private FrameLayout list;
	private FrameLayout details;
	private LinearLayout scanLayout;
	private LinearLayout tempTableLayout;
	private ImageView detailShadow;
	private ImageView menuShadow;
	private boolean rightShadowEnabled = false;
	private ZXingLibConfig zxingLibConfig;
	private String scannedId;
	private OpenTableAysncTask tableBG;
	private OpenTempTableAsyncTask tempBG;
	private SendOrderAsyncTask orderBG;

	private ArrayList<FoodItem> order = new ArrayList<FoodItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		zxingLibConfig = new ZXingLibConfig();

		fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		list = (FrameLayout) this.findViewById(R.id.list_frame_layout);
		details = (FrameLayout) this.findViewById(R.id.detail_frame_layout);
		detailShadow = (ImageView) this.findViewById(R.id.detail_frame_shadow);
		menuShadow = (ImageView) this.findViewById(R.id.list_frame_shadow);
		menuShadow.setVisibility(View.INVISIBLE);
		detailShadow.setVisibility(View.INVISIBLE);
		list.setVisibility(View.GONE);
		ft.add(R.id.detail_frame_layout, detail_fragment);
		ft.commit();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		getMenuInflater().inflate(R.menu.activity_main, menu);
		// View mActionBar = getLayoutInflater()
		// .inflate(R.layout.action_bar, null);
		// actionBar.setCustomView(mActionBar);
		// actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		// scanLayout = (LinearLayout)mActionBar.findViewById(R.id.qr_layout);
		// tempTableLayout =
		// (LinearLayout)mActionBar.findViewById(R.id.table_layout);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.load_menu:
			loadMenu();
			return true;
		case R.id.qr_icon:
			startScan();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void startScan() {
		IntentIntegrator.initiateScan(MainActivity.this, zxingLibConfig);
	}

	public void openTemp(View v) {
		if (tempBG != null) {
			tempBG.cancel(false);
		}
		tempBG = (OpenTempTableAsyncTask) new OpenTempTableAsyncTask()
				.execute();
	}

	public void loadCart(View v) {
		Toast.makeText(getApplicationContext(), "Order Size: " + order.size(),
				Toast.LENGTH_SHORT).show();
		if (orderBG != null) {
			orderBG.cancel(false);
		}
		orderBG = (SendOrderAsyncTask) new SendOrderAsyncTask().execute();
	}

	public void loadMenu() {
		list.setVisibility(View.VISIBLE);
		highlightMenuFragment();
		detail_fragment.clearFragment();

		FragmentTransaction ft = fm.beginTransaction();
		if (!menu_fragment.isAdded()) {
			ft.add(R.id.list_frame_layout, menu_fragment, "food_list");
			ft.commit();
		} else {
			menu_fragment.downloadMenu();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case IntentIntegrator.REQUEST_CODE:
			IntentResult scanResult = IntentIntegrator.parseActivityResult(
					requestCode, resultCode, data);
			if (scanResult == null) {
				return;
			}
			final String result = scanResult.getContents();
			if (result != null) {
				Toast.makeText(getApplicationContext(), "SCAN: " + result,
						Toast.LENGTH_LONG).show();
				// tempTableLayout.setVisibility(View.GONE);
				// scanLayout.setVisibility(View.GONE);
				scannedId = result;
				if (tableBG != null) {
					tableBG.cancel(false);
				}
				tableBG = (OpenTableAysncTask) new OpenTableAysncTask()
						.execute();
				/*
				 * AsyncTask to open table with QR code ID Hide QR Code and Temp
				 * table layouts
				 */
			}
			break;
		default:
		}
	}

	@Override
	public void onMenuListSelection(FoodItem item) {
		// TODO Auto-generated method stub
		detail_fragment.populateList(item);
		highlightDetailFragment();
	}

	@Override
	public void onDetailListSelection(FoodItem item) {
		// TODO Auto-generated method stub

		if (item != null) {
			detail_fragment.populateList(item);
		} else {
			highlightMenuFragment();
		}

	}

	@Override
	public void onFoodItemSelected(FoodItem item) {
		// TODO Auto-generated method stub
		order.add(item);
	}

	public void highlightMenuFragment() {
		detailShadow.setVisibility(View.VISIBLE);
		menuShadow.setVisibility(View.GONE);
	}

	public void highlightDetailFragment() {
		detailShadow.setVisibility(View.GONE);
		menuShadow.setVisibility(View.VISIBLE);
	}

	public class OpenTableAysncTask extends AsyncTask<Void, Integer, Void> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onPostExecute(Void result) {

		}

		@Override
		protected Void doInBackground(Void... params) {
			// read from sharedPref
			// getPreferences();
			// TODO Auto-generated method stub
			try {
				s = new Socket("10.0.1.14", 4322);
				out = new ObjectOutputStream(s.getOutputStream());
				out.writeObject("Table||Open_Table||" + scannedId);
				// in = new ObjectInputStream(s.getInputStream());
				//
				// //display this menu
				// in.close();
				out.close();
				s.close();
			} catch (Exception e) {
				Log.d("communication", e.getMessage());
			}
			return null;
		}

	}

	public class OpenTempTableAsyncTask extends
			AsyncTask<Void, Integer, Integer> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result.equals(1)) {
				tempTableLayout.setVisibility(View.GONE);
				scanLayout.setVisibility(View.GONE);
			}
		}

		@Override
		protected Integer doInBackground(Void... params) {
			// read from sharedPref
			// getPreferences();
			// TODO Auto-generated method stub
			try {
				s = new Socket("10.0.1.14", 4322);
				out = new ObjectOutputStream(s.getOutputStream());
				out.writeObject("Table||Open_Temp_Table");
				// in = new ObjectInputStream(s.getInputStream());
				//
				// //display this menu
				// in.close();
				out.close();
				s.close();
				return new Integer(1);
			} catch (Exception e) {
				Log.d("communication", e.getMessage());
				return new Integer(0);
			}
		}

	}

	public class SendOrderAsyncTask extends AsyncTask<Void, Integer, Integer> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result.equals(2)) {
				Toast.makeText(getApplicationContext(),
						"Error: Scan Table QR Code before sending order",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected Integer doInBackground(Void... params) {
			// read from sharedPref
			// getPreferences();
			// TODO Auto-generated method stub
			if (scannedId != null) {
				try {
					s = new Socket("10.0.1.14", 4322);
					out = new ObjectOutputStream(s.getOutputStream());
					out.writeObject("Table||Set_Table_Order||" + scannedId);
					// in = new ObjectInputStream(s.getInputStream());
					//
					// //display this menu
					// in.close();
					Order o = new Order(order);
					o.setOrderTable(Integer.parseInt(scannedId));
					out.writeObject(new Order(order));
					out.close();
					s.close();
					return new Integer(1);
				} catch (Exception e) {
					Log.d("communication", e.getMessage());
					return new Integer(0);
				}
			} else {
				return new Integer(2);
			}
		}

	}

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item){
	 * switch(item.getItemId()){ case R.id.load_menu: FragmentTransaction ft =
	 * fm.beginTransaction(); ft.add(R.id.list_frame_layout, new
	 * FoodListFragment()); ft.commit(); } return true; }
	 */

}
