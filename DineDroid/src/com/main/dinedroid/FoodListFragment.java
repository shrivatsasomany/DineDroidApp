package com.main.dinedroid;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.main.dinedroid.MainActivity.BackgroundProcess;
import com.main.dinedroid.dummy.DummyContent;

public class FoodListFragment extends Fragment {
	
	private Socket s;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private BackgroundProcess b;
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FoodListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_food_list, container, false);

        // Show the dummy content as text in a TextView.
      
        return rootView;
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

}
