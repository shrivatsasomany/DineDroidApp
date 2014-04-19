package com.main.dinedroid;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
//import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("NewApi")
public class LoginFragment extends DialogFragment {
	View theView = null;
	//public String password;
	SharedPreferences pref;
	String password;

    @SuppressLint("NewApi")
	@Override 
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
    	
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Save view for later use
        theView = inflater.inflate(R.layout.login_fragment, null);
        Context context=getActivity();
        
        pref=PreferenceManager.getDefaultSharedPreferences(context);
        password=pref.getString("password", "admin");
        
     // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(theView)
              .setPositiveButton("OK", new
                 DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                          // Send the positive button event back to the host
                          // activity
                          handleOK();
                      }             
                  })
              .setNegativeButton("Cancel", new
                 DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
                       // Do nothing, and dialog framework will cancel the dialog
                    	 
                    	 
                     }
                  });
           return builder.create();
        
        
    }
    public void handleOK()
    {
    	EditText et= (EditText) theView.findViewById(R.id.password);
    	//password= et.getText().toString();
    	if(et.getText().toString().equals(password))
    	{  	
    	Intent i=new Intent();
    	i.setClass(getActivity(), SettingsActivity.class);
    	startActivityForResult(i, 0);
    	}
    	else
    	{
    		Toast.makeText(getActivity().getApplicationContext(), "Incorrect Password! ", Toast.LENGTH_LONG).show();

    	}
    	
    }
    


}
