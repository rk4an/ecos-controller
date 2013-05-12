package com.ecos.train;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SpinAdapter extends ArrayAdapter<Train>{

	private Context context;

	private List<Train> values;

	public SpinAdapter(Context context, int textViewResourceId,
			List<Train> values) {
		super(context, textViewResourceId, values);
		this.context = context;
		this.values = values;
	}

	public int getCount(){
		return values.size();
	}

	public Train getItem(int position){
		return values.get(position);
	}

	public long getItemId(int position){
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return display(position, convertView, parent);
		

	}

	@Override
	public View getDropDownView(int position, View convertView,
			ViewGroup parent) {
		return display(position, convertView, parent);
	}
	
	public View display(int position, View convertView, ViewGroup parent) {
		TextView lblName = new TextView(context);
		lblName.setText(values.get(position).getName());
		lblName.setTypeface(null,Typeface.BOLD);

		TextView lblId = new TextView(context);
		lblId.setText(values.get(position).getId());
		lblId.setTextColor(Color.GRAY);
		
		LinearLayout ll = new LinearLayout(context);
		ll.setPadding(5, 5, 5, 5);
		
		ll.addView(lblName);
		ll.addView(lblId);
		
		return ll;
	}
}