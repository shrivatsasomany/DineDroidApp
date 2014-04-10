package com.main.dinedroid;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import jim.h.common.android.lib.zxing.config.ZXingLibConfig;
import jim.h.common.android.lib.zxing.integrator.IntentIntegrator;
import jim.h.common.android.lib.zxing.integrator.IntentResult;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.main.dinedroid.FoodDetailFragment.DetailListSelectionListener;
import com.main.dinedroid.FoodListFragment.MenuListSelectionListener;
import com.main.dinedroid.menu.FoodItem;

//import com.main.dinedroid.menu.Menu;

public class MainActivity extends FragmentActivity implements
MenuListSelectionListener, DetailListSelectionListener {

	private Socket s;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private FragmentManager fm;
	FoodListFragment menu_fragment = new FoodListFragment();
	FoodDetailFragment detail_fragment = new FoodDetailFragment();
	private FrameLayout list;
	private FrameLayout details;
	private ImageView detailShadow;
	private ImageView menuShadow;
	private boolean rightShadowEnabled = false;
	private ZXingLibConfig zxingLibConfig;


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
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		View mActionBar = getLayoutInflater()
				.inflate(R.layout.action_bar, null);
		actionBar.setCustomView(mActionBar);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		return true;
	}

	public void startScan(View v)
	{
        IntentIntegrator.initiateScan(MainActivity.this, zxingLibConfig);
	}
	
	public void loadCart(View v)
	{
		
	}

	public void loadMenu(View v) {
		list.setVisibility(View.VISIBLE);
		highlightMenuFragment();
		detail_fragment.clearFragment();



		FragmentTransaction ft = fm.beginTransaction();
		if (!menu_fragment.isAdded()) {
			ft.add(R.id.list_frame_layout, menu_fragment);
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
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (scanResult == null) {
                    return;
                }
                final String result = scanResult.getContents();
                if (result != null) {
                    Toast.makeText(getApplicationContext(), "SCAN: "+result, Toast.LENGTH_LONG).show();
                    /* 
                     * AsyncTask to open table with QR code ID 
                     * Hide QR Code and Temp table layouts
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

		if (item != null)
		{
			detail_fragment.populateList(item);
		}
		else
		{
			highlightMenuFragment();
		}

	}

	public void highlightMenuFragment()
	{
		detailShadow.setVisibility(View.VISIBLE);
		menuShadow.setVisibility(View.GONE);
	}
	public void highlightDetailFragment()
	{
		detailShadow.setVisibility(View.GONE);
		menuShadow.setVisibility(View.VISIBLE);
	}



	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item){
	 * switch(item.getItemId()){ case R.id.load_menu: FragmentTransaction ft =
	 * fm.beginTransaction(); ft.add(R.id.list_frame_layout, new
	 * FoodListFragment()); ft.commit(); } return true; }
	 */

}
