package com.zoop.checkout.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.zoop.checkout.app.Model.SellerSelected;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class SellerSelectedAdapter extends ArrayAdapter<SellerSelected> {
	private static class ViewHolder {
		TextView name;
		TextView id;
	}
	private final List<SellerSelected> mSeller;
	private final List<SellerSelected> mSeller_All;
	private final List<SellerSelected> mSeller_Suggestion;

	public SellerSelectedAdapter(Context context, List<SellerSelected> seller) {
		super(context, R.layout.list_seller, seller);
		this.mSeller = new ArrayList<>(seller);
		this.mSeller_All = new ArrayList<>(seller);
		this.mSeller_Suggestion = new ArrayList<>();	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		SellerSelected user = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		ViewHolder viewHolder; // view lookup cache stored in tag
		if (convertView == null) {
			// If there's no view to re-use, inflate a brand new view for row
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.list_seller, parent, false);
			viewHolder.name = (TextView) convertView.findViewById(R.id.lSellerName);
			viewHolder.id = (TextView) convertView.findViewById(R.id.lSellerId);
			// Cache the viewHolder object inside the fresh view
			convertView.setTag(viewHolder);
		} else {
			// View is being recycled, retrieve the viewHolder object from tag
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// Populate the data from the data object via the viewHolder object
		// into the template view.
		viewHolder.name.setText(user.getName());
		viewHolder.id.setText(user.getId());
		// Return the completed view to render on screen
		return convertView;
	}
	public int getCount() {
		return mSeller.size();
	}

	public SellerSelected getItem(int position) {
		return mSeller.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public Filter getFilter() {
		return new Filter() {
			@Override
			public String convertResultToString(Object resultValue) {
				return ((SellerSelected) resultValue).getName();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				if (constraint != null) {
					mSeller_Suggestion.clear();
					for (SellerSelected sellerSelected : mSeller_All) {
						if (sellerSelected.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
							mSeller_Suggestion.add(sellerSelected);
						}
					}
					FilterResults filterResults = new FilterResults();
					filterResults.values = mSeller_Suggestion;
					filterResults.count = mSeller_Suggestion.size();
					return filterResults;
				} else {
					return new FilterResults();
				}
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				mSeller.clear();
				if (results != null && results.count > 0) {
					// avoids unchecked cast warning when using mDepartments.addAll((ArrayList<Department>) results.values);
					List<?> result = (List<?>) results.values;
					for (Object object : result) {
						if (object instanceof SellerSelected) {
							mSeller.add((SellerSelected) object);
						}
					}
				} else if (constraint == null) {
					// no filter, add entire original list back in
					mSeller.addAll(mSeller_All);
				}
				notifyDataSetChanged();
			}
		};
	}
}

