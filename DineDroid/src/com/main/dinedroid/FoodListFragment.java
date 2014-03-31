package com.main.dinedroid;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;

import com.main.dinedroid.customclasses.FoodMenuListAdapter;
import com.main.dinedroid.menu.FoodItem;
import com.main.dinedroid.menu.Menu;


public class FoodListFragment extends Fragment {
	
	private Socket s;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private BackgroundAsyncTask batFactor;
	private ListView lv;
	private Spinner sp;
    public static final String ARG_ITEM_ID = "item_id";
    private ArrayList<FoodItem> items = new ArrayList<FoodItem>();
    private FoodMenuListAdapter listAdapter;
    private View rootView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	//call Async task to get menu
    	if(batFactor!=null)
			batFactor.cancel(false);
		batFactor=(BackgroundAsyncTask) new BackgroundAsyncTask().execute();
        rootView = inflater.inflate(R.layout.fragment_food_list, container, false);
        
        /*ArrayList<FoodItem> items = new ArrayList<FoodItem>();
        items.add(new FoodItem(1000, "Pizza", 10, false));
        items.add(new FoodItem(2000, "Pasta", 15, false));
        items.add(new FoodItem(5000, "Sandwiches", 0, true));*/
		/*FoodMenuListAdapter adapter = new FoodMenuListAdapter(getActivity(),
				R.layout.food_list_item, R.id.food_list_item_name,
				R.id.food_list_item_price, items);*/
		lv = (ListView)rootView.findViewById(R.id.fragment_food_list_listview);
		lv.setVisibility(View.INVISIBLE);
		sp = (Spinner)rootView.findViewById(R.id.fragment_food_list_spinner);
        return rootView;
    }
    
    public class BackgroundAsyncTask extends AsyncTask<Void, Integer, Menu>{
		@Override
		protected void onPreExecute(){

		}
		@Override
		protected void onPostExecute(Menu result){
			items = result.getItems();
			listAdapter = new FoodMenuListAdapter(getActivity(),
				R.layout.food_list_item, R.id.food_list_item_name,
				R.id.food_list_item_price, items);
			lv.setAdapter(listAdapter);
			lv.setVisibility(View.VISIBLE);
			sp.setVisibility(View.GONE);
		}
		@Override
		protected Menu doInBackground(Void... params) {
			Menu menu = null;
			// TODO Auto-generated method stub
			try{
				s = new Socket("Shrivatsas-MacBook-Pro.local", 4322);
				out = new ObjectOutputStream(s.getOutputStream());
				out.writeObject("Menu||Get_Menu");
				in = new ObjectInputStream(s.getInputStream());
				menu = (Menu)in.readObject();
				//display this menu
				in.close();
				out.close();
				s.close();
			}
			catch(Exception e){
				Log.d("communication",e.getMessage());
			}
			return menu;
		}

	}

}
