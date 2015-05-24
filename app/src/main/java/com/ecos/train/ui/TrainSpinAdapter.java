package com.ecos.train.ui;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecos.train.R;
import com.ecos.train.object.Train;

public class TrainSpinAdapter extends ArrayAdapter<Train>{

	private Context context;

	private List<Train> values;

	public TrainSpinAdapter(Context context, int textViewResourceId, List<Train> values) {
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
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return display(position, convertView, parent);
	}

	public View display(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = 
				(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View spinnerEntry = inflater.inflate(R.layout.spinner_entry_with_icon, null);

		TextView name = (TextView) spinnerEntry.findViewById(R.id.spinnerName);

		name.setText(values.get(position).toString());

		ImageView icon = (ImageView) spinnerEntry.findViewById(R.id.spinnerLogo);

		int symbol = values.get(position).getSymbol();
		Resources res = getContext().getResources();
		int resourceId = res.getIdentifier("loco"+symbol, "drawable", getContext().getPackageName());
		icon.setImageResource(resourceId);

		return spinnerEntry;
	}
}