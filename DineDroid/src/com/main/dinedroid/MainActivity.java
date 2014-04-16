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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.main.dinedroid.LoginFragment;
import com.main.dinedroid.FoodDetailFragment.DetailListSelectionListener;
import com.main.dinedroid.FoodDetailFragment.FoodItemSelectionListener;
import com.main.dinedroid.FoodListFragment.MenuListSelectionListener;
import com.main.dinedroid.customclasses.OrderListAdapter;
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

        private Menu myMenu;
        private ZXingLibConfig zxingLibConfig;
        private Integer tableId;
        private Integer waiterId;
        private OpenTableAysncTask tableBG;
        private OpenTempTableAsyncTask tempBG;
        private SendOrderAsyncTask orderBG;
        private AttachWaiterAsyncTask waiterBG;
        private HailWaiterAsyncTask hailBG;
        private final String SERVER_ADDRESS = "ServerAddress";
        private String server_address;
        private SharedPreferences spref;

        private OrderListAdapter orderListAdapter;
        private ArrayList<FoodItem> order = new ArrayList<FoodItem>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                spref = PreferenceManager.getDefaultSharedPreferences(this);
                getPreferences();
                zxingLibConfig = new ZXingLibConfig();

                orderListAdapter = new OrderListAdapter(this, R.layout.order_list_item,
                                R.id.order_list_name, R.id.order_list_quantity,
                                R.id.order_list_price, order);
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
                case R.id.table_icon:
                        openTemp();
                        return true;
                case R.id.menu_settings:
                		DialogFragment newFragment = new LoginFragment();
                		newFragment.show(getFragmentManager(), "LoginFragment");
                        return true;
                default:
                        return super.onOptionsItemSelected(item);
                }
        }

        public void hailWaiter(){
                if(waiterId!=null)
                {
                        if(hailBG != null)
                        {
                                hailBG.cancel(false);
                        }
                        hailBG = (HailWaiterAsyncTask) new HailWaiterAsyncTask().execute();
                }
                else
                {
                        Toast.makeText(getApplicationContext(), "Oops!: Waiter not assigned!", Toast.LENGTH_SHORT).show();
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
                        showOrderDialog();
                } else {
                        Toast.makeText(getApplicationContext(),
                                        "Error: Scan Table QR Code", Toast.LENGTH_SHORT).show();
                }

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
                                String[] split_result = result.split("\\|\\|");
                                if(split_result[0].equals("Table"))
                                {
                                        tableId = Integer.parseInt(split_result[1]);
                                        if (tableBG != null) {
                                                tableBG.cancel(false);
                                        }
                                        tableBG = (OpenTableAysncTask) new OpenTableAysncTask()
                                        .execute();
                                }
                                else if(split_result[0].equals("Waiter"))
                                {
                                        waiterId = Integer.parseInt(split_result[1]);
                                        if(waiterBG != null)
                                        {
                                                waiterBG.cancel(false);
                                        }
                                        waiterBG = (AttachWaiterAsyncTask) new AttachWaiterAsyncTask().execute();
                                }
                                else
                                {}
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
                orderListAdapter.notifyDataSetChanged();
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
                                s = new Socket(server_address, 4322);
                                out = new ObjectOutputStream(s.getOutputStream());
                                out.writeObject("Table||Open_Table||" + tableId);
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

        public class HailWaiterAsyncTask extends AsyncTask<Void, Integer, Void> {
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
                        if(waiterId != null)
                        {
                                try {
                                        s = new Socket(server_address, 4322);
                                        out = new ObjectOutputStream(s.getOutputStream());
                                        out.writeObject("Hail||Set_Hail||" + tableId);
                                        // in = new ObjectInputStream(s.getInputStream());
                                        //
                                        // //display this menu
                                        // in.close();
                                        out.close();
                                        s.close();
                                } catch (Exception e) {
                                        Log.d("communication", e.getMessage());
                                }
                        }
                        return null;
                }

        }

        public class AttachWaiterAsyncTask extends AsyncTask<Void, Integer, Integer> {
                @Override
                protected void onPreExecute() {

                }

                @Override
                protected void onPostExecute(Integer result) {
                        if(result == 0)
                        {
                                Toast.makeText(getApplicationContext(), "Error: Scan Table QR Code", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                                MenuItem t = (MenuItem)myMenu.findItem(R.id.qr_icon);
                                t.setVisible(false);
                        }
                }

                @Override
                protected Integer doInBackground(Void... params) {
                        // read from sharedPref
                        // getPreferences();
                        // TODO Auto-generated method stub
                        if(tableId != null)
                        {
                                try {
                                        s = new Socket(server_address, 4322);
                                        out = new ObjectOutputStream(s.getOutputStream());
                                        out.writeObject("Waiter||Assign_Waiter||" + tableId +"||"+ waiterId);
                                        // in = new ObjectInputStream(s.getInputStream());
                                        //
                                        // //display this menu
                                        // in.close();
                                        out.close();
                                        s.close();
                                        return 1;
                                } catch (Exception e) {
                                        Log.d("communication", e.getMessage());
                                }
                        }
                        return 0;
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
                                s = new Socket(server_address, 4322);
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
                        switch (result) {
                        case 1:
                                Toast.makeText(getApplicationContext(), "Order sent!",
                                                Toast.LENGTH_SHORT).show();
                                break;
                        case 2:
                                Toast.makeText(getApplicationContext(),
                                                "Error: Scan Table QR Code before sending order",
                                                Toast.LENGTH_SHORT).show();
                                break;
                        }
                }

                @Override
                protected Integer doInBackground(Void... params) {
                        // read from sharedPref
                        // getPreferences();
                        // TODO Auto-generated method stub
                        if (tableId != null) {
                                try {
                                        s = new Socket(server_address, 4322);
                                        out = new ObjectOutputStream(s.getOutputStream());
                                        out.writeObject("Table||Set_Table_Order||" + tableId);
                                        // in = new ObjectInputStream(s.getInputStream());
                                        //
                                        // //display this menu
                                        // in.close();
                                        Order o = new Order(order);
                                        o.setOrderTable(tableId);
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

        public void showOrderDialog() {
                final Order o = new Order(order);
                o.setOrderTable(tableId);

                AlertDialog.Builder customDialog = new AlertDialog.Builder(this);
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View view = layoutInflater.inflate(R.layout.order_dialog, null);

                TextView information = (TextView) view
                                .findViewById(R.id.order_detail_information);

                customDialog.setTitle("Table "+tableId+" Order");
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
                                FoodItem selectedItem = (FoodItem) orderListAdapter.getItem(position);
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
                                AlertDialog.Builder itemView = new AlertDialog.Builder(MainActivity.this);
                                TextView info = new TextView(MainActivity.this);
                                info.setText(selectedItem.displayString());
                                itemView.setView(info);
                                itemView.show();
                        }
                });

                final Dialog d = customDialog.setView(view).create();
                d.show();

        }

}