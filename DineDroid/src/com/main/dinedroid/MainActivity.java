package com.main.dinedroid;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.main.dinedroid.menu.Menu;

public class MainActivity extends FragmentActivity {


	Socket s;
	ObjectInputStream in;
	ObjectOutputStream out;
	BackgroundProcess b;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
