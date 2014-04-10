package com.main.dinedroid;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.main.dinedroid.customclasses.FoodMenuListAdapter;
import com.main.dinedroid.menu.FoodItem;
import com.main.dinedroid.menu.Menu;


public class FoodListFragment extends Fragment {
	
	private Socket s;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private BackgroundAsyncTask batFactor;
	private ListView lv;
	private ProgressBar sp;
    public static final String ARG_ITEM_ID = "item_id";
    private ArrayList<FoodItem> items = new ArrayList<FoodItem>();
    private FoodMenuListAdapter listAdapter;
    private View rootView;
    private SharedPreferences sharedPref;
    private Editor prefEditor;
    private Menu menu = null;
    private Gson gson;
    private FoodItem selectedItem;
    private final String MENU_OBJECT = "Menu_object";
    private MenuListSelectionListener mListener = null;
    
    
    public interface MenuListSelectionListener {
		public void onMenuListSelection(FoodItem item);
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mListener = (MenuListSelectionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement Poop");
		}
	}
    

	public void onListItemClick(FoodMenuListAdapter adapter, View v, int pos, long id) {
		selectedItem = (FoodItem) adapter.getItem(pos);
		mListener.onMenuListSelection(selectedItem);
		
	}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefEditor = sharedPref.edit();
		//getPreferences();
		loadMenu();
    	downloadMenu();
        rootView = inflater.inflate(R.layout.fragment_food_list, container, false);
		lv = (ListView)rootView.findViewById(R.id.fragment_food_list_listview);
		lv.setVisibility(View.INVISIBLE);
		
		lv.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				onListItemClick(listAdapter, view, position, id);
			}
		});

		sp = (ProgressBar)rootView.findViewById(R.id.fragment_food_list_spinner);
        return rootView;
    }
    
    public void downloadMenu()
    {
    	//call Async task to get menu
    	if(batFactor!=null)
			batFactor.cancel(false);
		batFactor=(BackgroundAsyncTask) new BackgroundAsyncTask().execute();
    }
    
    public boolean saveMenu() {
		try {
			ObjectOutputStream os = new ObjectOutputStream(
					new FileOutputStream("menu.dat"));
			os.writeObject(menu);
			os.close();
			return true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean loadMenu() {
		try {
			ObjectInputStream is = new ObjectInputStream(
					new FileInputStream("menu.dat"));
			menu = (Menu) is.readObject();
			is.close();
			return true;

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

    public void getPreferences(){
		//gson = new Gson();
        //String json = sharedPref.getString(MENU_OBJECT, "");
       // menu = gson.fromJson(json, Menu.class);
    }
    
    public void savePreferences(Menu result){
    	//gson = new Gson();
        //String json = gson.toJson(result);
        //prefEditor.putString(MENU_OBJECT, json);
        //prefEditor.commit();
    }
    
    public void displayFoodMenuListAdapter(Menu result){
    	items = result.getItems();
		listAdapter = new FoodMenuListAdapter(getActivity(),
			R.layout.food_list_item, R.id.food_list_item_name,
			R.id.food_list_item_price, items);
		lv.setAdapter(listAdapter);
		lv.setVisibility(View.VISIBLE);
		sp.setVisibility(View.GONE);
    }
    public class BackgroundAsyncTask extends AsyncTask<Void, Integer, Menu>{
		@Override
		protected void onPreExecute(){

		}
		@Override
		protected void onPostExecute(Menu result){
			if(result != null)
			{
				saveMenu();
				displayFoodMenuListAdapter(result);
				//savePreferences(result);
			}
			else
			{
				Toast.makeText(getActivity(), "Error downloading menu!", Toast.LENGTH_LONG).show();
			}
			/*items = result.getItems();
			listAdapter = new FoodMenuListAdapter(getActivity(),
				R.layout.food_list_item, R.id.food_list_item_name,
				R.id.food_list_item_price, items);
			lv.setAdapter(listAdapter);
			listAdapter.notifyDataSetChanged();
			lv.setVisibility(View.VISIBLE);
			sp.setVisibility(View.GONE);*/
		}
		@Override
		protected Menu doInBackground(Void... params) {
			//read from sharedPref
			//getPreferences();
			loadMenu();
			// TODO Auto-generated method stub
			try{
				s = new Socket("client-75-102-94-123.mobility-cl.psu.edu", 4322);
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
