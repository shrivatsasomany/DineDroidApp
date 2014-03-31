package com.main.dinedroid;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import android.app.ActionBar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;

import com.main.dinedroid.customclasses.FoodMenuListAdapter;
import com.main.dinedroid.menu.FoodItem;

//import com.main.dinedroid.menu.Menu;

public class MainActivity extends FragmentActivity {

	private Socket s;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private BackgroundProcess b;
	private FragmentManager fm;
	private FrameLayout list;
	private FrameLayout details;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		list = (FrameLayout) this.findViewById(R.id.list_frame_layout);
		details = (FrameLayout) this.findViewById(R.id.detail_frame_layout);
		list.setVisibility(View.GONE);
		ft.add(R.id.detail_frame_layout, new FoodDetailFragment());
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

	public void loadMenu(View v) {
		list.setVisibility(View.VISIBLE);
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.list_frame_layout, new FoodListFragment());
		ft.commit();
	}

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item){
	 * switch(item.getItemId()){ case R.id.load_menu: FragmentTransaction ft =
	 * fm.beginTransaction(); ft.add(R.id.list_frame_layout, new
	 * FoodListFragment()); ft.commit(); } return true; }
	 */

	public class BackgroundProcess extends AsyncTask<Void, Integer, Void> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onPostExecute(Void result) {

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				s = new Socket("localhost", 4322);

				out = new ObjectOutputStream(s.getOutputStream());

				out.writeObject("Menu||Get_Menu");
				in = new ObjectInputStream(s.getInputStream());
				Menu menu = (Menu) in.readObject();
				in.close();
				out.close();
				s.close();
			} catch (Exception e) {
				Log.d("communication", e.getMessage());
			}
			return null;
		}

	}

}
