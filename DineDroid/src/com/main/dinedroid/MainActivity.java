package com.main.dinedroid;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import jim.h.common.android.lib.zxing.config.ZXingLibConfig;
import jim.h.common.android.lib.zxing.integrator.IntentIntegrator;
import jim.h.common.android.lib.zxing.integrator.IntentResult;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.main.dinedroid.FoodDetailFragment.DetailListSelectionListener;
import com.main.dinedroid.FoodDetailFragment.FoodItemSelectionListener;
import com.main.dinedroid.FoodListFragment.MenuDownloadListener;
import com.main.dinedroid.FoodListFragment.MenuListSelectionListener;
import com.main.dinedroid.customclasses.OrderListAdapter;
import com.main.dinedroid.menu.FoodItem;
import com.main.dinedroid.models.Order;
import com.main.dinedroid.models.Restore;
import com.main.dinedroid.models.Waiter;

//import com.main.dinedroid.menu.Menu;

public class MainActivity extends FragmentActivity implements
MenuListSelectionListener, DetailListSelectionListener,
FoodItemSelectionListener, MenuDownloadListener {

	private Socket s;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private FragmentManager fm;
	private FoodListFragment menu_fragment = new FoodListFragment();
	private FoodDetailFragment detail_fragment = new FoodDetailFragment();
	private TableDetailFragment table_detail_fragment;
	private FrameLayout list;
	private FrameLayout details;
	private LinearLayout scanLayout;
	private LinearLayout tempTableLayout;
	private ImageView detailShadow;
	private ImageView menuShadow;

	private Menu myMenu;
	private ZXingLibConfig zxingLibConfig;
	private Integer tableId;
	private Integer waiterId;
	private String waiterName;
	private OpenTableAysncTask tableBG;
	private OpenTempTableAsyncTask tempBG;
	private SendOrderAsyncTask orderBG;
	private AttachWaiterAsyncTask waiterBG;
	private HailWaiterAsyncTask hailBG;
	private CloseOrderAsyncTask closeOrderBG;
	private final String SERVER_ADDRESS = "ServerAddress";
	private final String PASSWORD = "password";
	private final String ORDER_STATUS = "OrderStatus";
	private final int SOCKET_TIMEOUT = 10000;
	private final int TEMP_TABLE_OPTION = 0;
	private final int SETTINGS_OPTION = 1;
	private String server_address;
	private String password;
	private boolean order_status;
	private boolean sentOrder;
	private SharedPreferences spref;
	private OrderListAdapter orderListAdapter;
	private ArrayList<FoodItem> order = new ArrayList<FoodItem>();
	ArrayList<FoodItem> unavailableItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		spref = PreferenceManager.getDefaultSharedPreferences(this);
		getPreferences();
		zxingLibConfig = new ZXingLibConfig();

		orderListAdapter = new OrderListAdapter(this, R.layout.order_list_item,
				R.id.order_list_availability, R.id.order_list_name,
				R.id.order_list_quantity, R.id.order_list_price, order);
		fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		list = (FrameLayout) this.findViewById(R.id.list_frame_layout);
		details = (FrameLayout) this.findViewById(R.id.detail_frame_layout);
		detailShadow = (ImageView) this.findViewById(R.id.detail_frame_shadow);
		menuShadow = (ImageView) this.findViewById(R.id.list_frame_shadow);
		menuShadow.setVisibility(View.INVISIBLE);
		detailShadow.setVisibility(View.INVISIBLE);
		list.setVisibility(View.GONE);
		Bundle myBundle = new Bundle();
		myBundle.putInt("init", 1);
		detail_fragment.setArguments(myBundle);
		ft.add(R.id.detail_frame_layout, detail_fragment);
		ft.commit();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		getMenuInflater().inflate(R.menu.activity_main, menu);
		myMenu = menu;
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

		case R.id.hail_icon:
			hailWaiter();
			return true;
		case R.id.load_menu:
			loadMenu();
			return true;
		case R.id.qr_icon:
			startScan();
			return true;
		case R.id.order_icon:
			loadCart();
			return true;
		case R.id.temp_icon:
			openLoginDialog(TEMP_TABLE_OPTION);
			return true;
		case R.id.menu_settings:
			openLoginDialog(SETTINGS_OPTION);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void openLoginDialog(final int option) {
		password = spref.getString(PASSWORD, "admin");
		
		AlertDialog.Builder customDialog = new AlertDialog.Builder(this);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = layoutInflater.inflate(R.layout.login_fragment, null);
		final EditText et = (EditText) view.findViewById(R.id.password);

		customDialog.setTitle("Enter Password");
		customDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
		
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (et.getText().toString().equals(password)) {
					if(option == 0)
						openTemp();
					else{
						Intent i=new Intent(getApplicationContext(), SettingsActivity.class);
				    	startActivityForResult(i, 0);
					}
						
				} else {
					Toast.makeText(getApplicationContext(),
							"Incorrect Password! ", Toast.LENGTH_LONG)
							.show();
				}

			}
		});
		customDialog.setNegativeButton("Back",
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// Do nothing
			}
		});
		final Dialog d = customDialog.setView(view).create();
		d.show();
	}

	public void hailWaiter() {
		if (waiterId != null) {
			if (hailBG != null) {
				hailBG.cancel(false);
			}
			hailBG = (HailWaiterAsyncTask) new HailWaiterAsyncTask().execute();
		} else {
			Toast.makeText(getApplicationContext(),
					"Oops!: Waiter not assigned!", Toast.LENGTH_SHORT).show();
		}
	}

	public void startScan() {
		IntentIntegrator.initiateScan(MainActivity.this, zxingLibConfig);
	}

	public void openTemp() {
		if (tempBG != null) {
			tempBG.cancel(false);
		}
		tempBG = (OpenTempTableAsyncTask) new OpenTempTableAsyncTask()
		.execute();
	}

	public void loadCart() {
		if (tableId != null) {
			showOrderDialog("");
		} else {
			Toast.makeText(getApplicationContext(),
					"Error: Scan Table QR Code", Toast.LENGTH_SHORT).show();
		}

	}

	/*
	 * return table id
	 */
	public int getTableId() {
		return tableId;
	}

	/*
	 * return waiter id
	 */
	public int getWaiterId() {
		return waiterId;
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

	public void getPreferences() {
		server_address = spref.getString(SERVER_ADDRESS, "10.0.1.14");
		password = spref.getString(PASSWORD, "admin");
		order_status = spref.getBoolean(ORDER_STATUS, true);
		if(!order_status){
			if(closeOrderBG !=null){
				closeOrderBG.cancel(false);
			}
			closeOrderBG = (CloseOrderAsyncTask) new CloseOrderAsyncTask().execute();
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

				String[] split_result = result.split("\\|\\|");
				if (split_result[0].equals("Table")) {
					tableId = Integer.parseInt(split_result[1]);
					if (tableBG != null) {
						tableBG.cancel(false);
					}
					tableBG = (OpenTableAysncTask) new OpenTableAysncTask()
					.execute();
				} else if (split_result[0].equals("Waiter")) {
					waiterId = Integer.parseInt(split_result[1]);
					waiterName = split_result[2] + " " + split_result[3];
					if (waiterBG != null) {
						waiterBG.cancel(false);
					}
					waiterBG = (AttachWaiterAsyncTask) new AttachWaiterAsyncTask()
					.execute();
				} else {
					Toast.makeText(getApplicationContext(),
							"Error: Invalid QR Code", Toast.LENGTH_SHORT)
							.show();
				}
				/*
				 * AsyncTask to open table with QR code ID Hide QR Code and Temp
				 * table layouts
				 */
			}
		default:
			getPreferences();
			break;

		}
	}

	@Override
	public void onMenuListSelection(FoodItem item) {
		// TODO Auto-generated method stub

		detail_fragment = new FoodDetailFragment();
		Bundle myBundle = new Bundle();
		myBundle.putSerializable("Item", item);
		myBundle.putInt("init", 0);
		detail_fragment.setArguments(myBundle);
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.detail_frame_layout, detail_fragment);
		ft.commit();
		table_detail_fragment = null;

		highlightDetailFragment();
	}

	/**
	 * Listen to callbacks after downloading the menu in the FoodList fragment
	 * The class name clashes with android.Menu, and therefore has to be
	 * explicitly stated here.<br>
	 * If the order size is <b>not</b> 0, this should update the order with the
	 * new menu.
	 * 
	 * @param menu
	 *            The menu
	 */
	@Override
	public void onMenuDownload(com.main.dinedroid.menu.Menu menu) {
		// TODO Auto-generated method stub
		if (order.size() != 0) {
			for (FoodItem e : order) {
				e.setAvailable(menu.findItem(e.getID()).isAvailable());
				Log.d("FoodItem",
						"Refreshing orders: " + " avail: " + e.isAvailable());
			}
			orderListAdapter.notifyDataSetChanged();
		}
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
		Log.d("FoodItem", "Adding to order: " + item.getName()
				+ " Availability: " + item.isAvailable());
		order.add(item);
		orderListAdapter.notifyDataSetChanged();
	}

	public void highlightMenuFragment() {
		detailShadow.setVisibility(View.VISIBLE);
		menuShadow.setVisibility(View.GONE);
		if (sentOrder) {
			launchTableDetailFragment();
		}
	}

	public void highlightDetailFragment() {
		detailShadow.setVisibility(View.GONE);
		menuShadow.setVisibility(View.VISIBLE);
	}

	public void launchTableDetailFragment() {
		table_detail_fragment = new TableDetailFragment();
		Bundle myBundle = new Bundle();
		myBundle.putInt("NumInfo", 1);
		myBundle.putInt("tableId", tableId);
		if (waiterId != null) {
			myBundle.putInt("NumInfo", 2);
			myBundle.putInt("waiterId", waiterId);
			myBundle.putString("waiterName", waiterName);
		}
		table_detail_fragment.setArguments(myBundle);
		FragmentTransaction ft = fm.beginTransaction();
		if (table_detail_fragment != null) {
			ft.replace(R.id.detail_frame_layout, table_detail_fragment);
			ft.commit();
		}
	}

	public class OpenTableAysncTask extends AsyncTask<Void, Integer, Restore> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onPostExecute(Restore result) {
			if (result != null) {
				if (!result.isOccupied()) {
					showToast("Successfully opened table", Toast.LENGTH_SHORT);
				} else {
					Waiter w = result.getWaiter();
					Order o = result.getOrder();
					if (w != null) {
						waiterId = w.getId();
						waiterName = w.getFName() + " " + w.getLName();
					}
					if (o != null) {
						order = result.getOrder().getOrder();
						orderListAdapter = new OrderListAdapter(
								MainActivity.this, R.layout.order_list_item,
								R.id.order_list_availability,
								R.id.order_list_name, R.id.order_list_quantity,
								R.id.order_list_price, order);
					}

					showMessageDialog("Table " + tableId
							+ " was already open.\nRestored previous state...");
					if (o != null) {
						launchTableDetailFragment();
					}
				}
			} else {
				showMessageDialog("Communication error while trying to set table,\nplease try again");
				tableId = null;
			}
		}

		@Override
		protected Restore doInBackground(Void... params) {
			// read from sharedPref
			// getPreferences();
			// TODO Auto-generated method stub
			Restore serverResult;
			try {
				s = new Socket(server_address, 4322);
				s.setSoTimeout(SOCKET_TIMEOUT);
				out = new ObjectOutputStream(s.getOutputStream());
				out.writeObject("Table||Open_Table||" + tableId);
				in = new ObjectInputStream(s.getInputStream());
				serverResult = (Restore) in.readObject();
				in.close();
				out.close();
				s.close();
				return serverResult;
			} catch (Exception e) {
				Log.d("communication","Comm error");
			}
			return null;
		}

	}

	public class HailWaiterAsyncTask extends AsyncTask<Void, Integer, Boolean> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				showMessageDialog("Your waiter has been hailed!");
			} else {
				showMessageDialog("Oops! Something went wrong, please try again.\nIf this problem persists, please notify the wait staff!");
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// read from sharedPref
			// getPreferences();
			// TODO Auto-generated method stub
			boolean result;
			if (waiterId != null) {
				try {
					s = new Socket(server_address, 4322);
					s.setSoTimeout(SOCKET_TIMEOUT);
					out = new ObjectOutputStream(s.getOutputStream());
					out.writeObject("Hail||Set_Hail||" + tableId);
					in = new ObjectInputStream(s.getInputStream());
					result = in.readBoolean();
					in.close();
					out.close();
					s.close();
					return result;
				} catch (Exception e) {
					Log.d("communication", e.getMessage());
				}
			}
			return false;
		}

	}

	public class AttachWaiterAsyncTask extends AsyncTask<Void, Integer, Boolean> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				showToast("Successfully attached waiter", Toast.LENGTH_SHORT);
				if (table_detail_fragment != null) {
					table_detail_fragment.setWaiterName(waiterName);
				}
			} else {
				showMessageDialog("Could not assign waiter, please try again.");
				waiterId = null;
				waiterName = null;
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// read from sharedPref
			// getPreferences();
			// TODO Auto-generated method stub
			if (tableId != null) {
				boolean result = false;
				try {
					s = new Socket(server_address, 4322);
					s.setSoTimeout(SOCKET_TIMEOUT);
					out = new ObjectOutputStream(s.getOutputStream());
					out.writeObject("Waiter||Assign_Waiter||" + tableId + "||"
							+ waiterId);
					in = new ObjectInputStream(s.getInputStream());
					result = in.readBoolean();
					in.close();
					out.close();
					s.close();
					return result;
				} catch (Exception e) {
					Log.d("communication", e.getMessage());
					waiterId = null;
					waiterName = null;
				}
			}
			return false;
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
				showMessageDialog("Successfully opened table No. " + tableId);
			} else {
				showMessageDialog("Could not open temporary table, please try again");
			}
		}

		@Override
		protected Integer doInBackground(Void... params) {
			// read from sharedPref
			// getPreferences();
			// TODO Auto-generated method stub
			try {
				s = new Socket(server_address, 4322);
				s.setSoTimeout(SOCKET_TIMEOUT);
				out = new ObjectOutputStream(s.getOutputStream());
				out.writeObject("Table||Open_Temp_Table");
				in = new ObjectInputStream(s.getInputStream());
				tableId = in.readInt();
				in.close();
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
			switch (result) {
			case 0:
				showMessageDialog("Cannot reach server, please try again.\nIf the problem persists, contact the wait staff!");
				break;
			case 1:
				showToast("Order sent!", Toast.LENGTH_SHORT);
				sentOrder = true;
				launchTableDetailFragment();
				break;
			case 2:
				showMessageDialog("Scan Table QR Code before sending order");
				break;
			case 3:
				showMessageDialog("Yikes! We couldn't send your order, please try again.\nIf the problem persists, contact the wait staff!");
				break;
			case 4:
				for (FoodItem e : unavailableItems) {
					for (FoodItem f : order) {
						if (e.getID() == f.getID()) {
							f.setAvailable(e.isAvailable());
						}
					}
				}
				orderListAdapter.notifyDataSetChanged();
				unavailableItems = null;
				showOrderDialog(" Error: Remove crossed items");
				break;
			case 5:
				showMessageDialog("There are some items in the order that aren't available! Remove them to send the order.");
				break;
			}
		}

		@Override
		protected Integer doInBackground(Void... params) {
			// read from sharedPref
			// getPreferences();
			// TODO Auto-generated method stub
			if (tableId != null) {
				if (!verifyOrder()) {
					return new Integer(5);
				}
				try {
					s = new Socket(server_address, 4322);
					s.setSoTimeout(SOCKET_TIMEOUT);
					out = new ObjectOutputStream(s.getOutputStream());
					out.flush();
					out.writeObject("Table||Set_Table_Order||" + tableId);
					out.flush();
					Order o = new Order(order);
					o.setOrderTable(tableId);
					out.writeObject(o);
					out.flush();
					in = new ObjectInputStream(s.getInputStream());
					unavailableItems = (ArrayList<FoodItem>) in.readObject();
					in.close();
					out.close();
					s.close();
					/*
					 * Socket timeout error happening while trying to get input
					 * stream. MUST look into this, but after due date because
					 * of time constraints.
					 */
					if (unavailableItems != null) {
						if (unavailableItems.size() == 0) {
							return new Integer(1);
						} else {
							return new Integer(4);
						}
					} else {
						return new Integer(3);
					}

				} catch (Exception e) {
					Log.d("communication", "Socket error");
					return new Integer(0);
				}
			} else {
				return new Integer(2);
			}
		}

	}
	
	public class CloseOrderAsyncTask extends AsyncTask<Void, Integer, Integer>{

		@Override
		protected void onPostExecute(Integer result) {
			if (result.equals(1)) {
				showMessageDialog("Order Closed successfully");
			} else {
				showMessageDialog("Could not close the order, please try again");
			}
		}
		
		@Override
		protected Integer doInBackground(Void... params) {
			// read from sharedPref
			// getPreferences();
			// TODO Auto-generated method stub
			try {
				s = new Socket(server_address, 4322);
				s.setSoTimeout(SOCKET_TIMEOUT);
				out = new ObjectOutputStream(s.getOutputStream());
				out.writeObject("Order||Remove_Table_Order||"+ tableId);
				//in = new ObjectInputStream(s.getInputStream());
				//in.close();
				out.close();
				s.close();
				return new Integer(1);
			} catch (Exception e) {
				Log.d("communication", e.getMessage());
				return new Integer(0);
			}
		}
		
	}

	public void showOrderDialog(String message) {
		orderListAdapter.notifyDataSetChanged();
		final Order o = new Order(order);
		o.setOrderTable(tableId);

		AlertDialog.Builder customDialog = new AlertDialog.Builder(this);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = layoutInflater.inflate(R.layout.order_dialog, null);

		TextView information = (TextView) view
				.findViewById(R.id.order_detail_information);

		customDialog.setTitle("Table " + tableId + " Order" + message);
		customDialog.setPositiveButton("Submit",
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				if (orderBG != null) {
					orderBG.cancel(false);
				}
				orderBG = (SendOrderAsyncTask) new SendOrderAsyncTask()
				.execute();

				dialog.dismiss();
			}
		});
		customDialog.setNegativeButton("Back",
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		String info = "Total Price: $" + o.getTotalPrice();
		information.setText(info);

		final ListView orderList = (ListView) view
				.findViewById(R.id.order_detail_list);
		orderList.setAdapter(orderListAdapter);
		orderList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				FoodItem selectedItem = (FoodItem) orderListAdapter
						.getItem(position);
				orderListAdapter.remove(selectedItem);
				orderListAdapter.notifyDataSetChanged();
				return false;
			}
		});
		orderList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FoodItem selectedItem = (FoodItem) orderListAdapter
						.getItem(position);
				AlertDialog.Builder itemView = new AlertDialog.Builder(
						MainActivity.this);
				TextView info = new TextView(MainActivity.this);
				info.setText(selectedItem.displayString());
				itemView.setView(info);
				itemView.show();
			}
		});

		final Dialog d = customDialog.setView(view).create();
		d.show();

	}

	public void showMessageDialog(String message) {
		AlertDialog.Builder errorDialog = new AlertDialog.Builder(this);
		errorDialog.setMessage(message);
		errorDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		errorDialog.setIcon(android.R.drawable.ic_dialog_alert);
		errorDialog.setTitle("Message");
		final Dialog d = errorDialog.create();
		d.show();
	}

	public void showToast(String message, int duration) {
		Toast.makeText(getApplicationContext(), message, duration).show();
	}

	public boolean verifyOrder() {
		for (FoodItem e : order) {
			if (!e.isAvailable()) {
				return false;
			}
		}
		return true;
	}

}
