package com.main.dinedroid;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.main.dinedroid.menu.Menu;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.util.Log;
import android.view.View;

public class MainActivity extends FragmentActivity
implements FoodListFragment.Callbacks {


	Socket s;
	ObjectInputStream in;
	ObjectOutputStream out;
	BackgroundProcess b;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment listfrag = fm.findFragmentById(R.id.listFragment);
		ft.hide(listfrag);

		if (findViewById(R.id.food_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((FoodListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.food_list))
					.setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}
	public void openMenu(){
		if(b == null)
			b.cancel(false);
		b = (BackgroundProcess) new BackgroundProcess().execute();;
	}

	public class BackgroundProcess extends AsyncTask<Void, Integer, Void>{
		@Override
		protected void onPreExecute(){
			
		}
		@Override
		protected void onPostExecute(Void result){
			
		}
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
			s = new Socket("localhost", 4322);

			out = new ObjectOutputStream(s.getOutputStream());
			
			out.writeObject("Menu||Get_Menu");
			in = new ObjectInputStream(s.getInputStream());
			Menu menu = (Menu)in.readObject();
			in.close();
			out.close();
			s.close();
			}
			catch(Exception e){
				Log.d("communication",e.getMessage());
			}
			return null;
		}

	}

	
	/**
	 * Callback method from {@link FoodListFragment.Callbacks}
	 * indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		
	}
}
