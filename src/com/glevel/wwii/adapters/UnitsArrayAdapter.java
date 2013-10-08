package com.glevel.wwii.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.glevel.wwii.R;
import com.glevel.wwii.game.GameUtils;
import com.glevel.wwii.game.model.units.Soldier;
import com.glevel.wwii.game.model.units.Unit;
import com.glevel.wwii.game.model.units.Weapon;

public class UnitsArrayAdapter extends ArrayAdapter<Unit> {

	private List<Unit> mUnits;
	private boolean mIsMyArmy;
	private Context mContext;

	public UnitsArrayAdapter(Context context, int textViewResourceId,
			List<Unit> units, boolean isMyArmy) {
		super(context, textViewResourceId, units);
		this.mUnits = units;
		this.mIsMyArmy = isMyArmy;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		if (mIsMyArmy) {
			// display a fixed number of unit slots
			return GameUtils.MAX_UNIT_PER_ARMY;
		} else {
			return mUnits.size();
		}
	}

	public static class ViewHolder {
		private ImageView unitImage;
		private TextView unitName;
		private TextView unitExperience;
		private TextView unitPrice;
		private TextView unitFrags;
		private ViewGroup unitMainWeapon;
		private TextView unitMainWeaponName;
		private TextView unitMainWeaponAP;
		private TextView unitMainWeaponAT;
		private ViewGroup unitSecondaryWeapon;
		private TextView unitSecondaryWeaponName;
		private TextView unitSecondaryWeaponAP;
		private TextView unitSecondaryWeaponAT;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;

		// check if the view is null then if so inflate it.
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.army_list_item, null);
		}

		ViewHolder viewHolder = (ViewHolder) view.getTag(R.string.viewholder);
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			viewHolder.unitImage = (ImageView) view
					.findViewById(R.id.unitImage);
			viewHolder.unitName = (TextView) view.findViewById(R.id.unitName);
			viewHolder.unitExperience = (TextView) view
					.findViewById(R.id.unitExperience);
			viewHolder.unitPrice = (TextView) view.findViewById(R.id.unitPrice);
			viewHolder.unitFrags = (TextView) view.findViewById(R.id.unitFrags);
			viewHolder.unitMainWeapon = (ViewGroup) view
					.findViewById(R.id.unitMainWeapon);
			viewHolder.unitMainWeaponName = (TextView) view
					.findViewById(R.id.unitMainWeaponName);
			viewHolder.unitMainWeaponAP = (TextView) view
					.findViewById(R.id.unitMainWeaponAP);
			viewHolder.unitMainWeaponAT = (TextView) view
					.findViewById(R.id.unitMainWeaponAT);
			viewHolder.unitSecondaryWeapon = (ViewGroup) view
					.findViewById(R.id.unitSecondaryWeapon);
			viewHolder.unitSecondaryWeaponName = (TextView) view
					.findViewById(R.id.unitSecondaryWeaponName);
			viewHolder.unitSecondaryWeaponAP = (TextView) view
					.findViewById(R.id.unitSecondaryWeaponAP);
			viewHolder.unitSecondaryWeaponAT = (TextView) view
					.findViewById(R.id.unitSecondaryWeaponAT);
			view.setTag(R.string.viewholder, viewHolder);
		}

		if (position < mUnits.size()) {
			// fill view with unit characteristics
			Unit unit = mUnits.get(position);

			// image
			viewHolder.unitImage.setVisibility(View.VISIBLE);
			viewHolder.unitImage.setImageResource(unit.getImage());

			// name
			viewHolder.unitName.setVisibility(View.VISIBLE);
			if (mIsMyArmy && unit instanceof Soldier) {
				// display real name
				viewHolder.unitName.setText(((Soldier) unit).getRealName());
			} else {
				viewHolder.unitName.setText(unit.getName());
			}

			// experience
			viewHolder.unitExperience.setVisibility(View.VISIBLE);
			viewHolder.unitExperience.setText(unit.getExperience().name());
			viewHolder.unitExperience.setTextColor(mContext.getResources()
					.getColor(unit.getExperience().getColor()));

			// weapons
			// main weapon
			viewHolder.unitMainWeapon.setVisibility(View.VISIBLE);
			Weapon mainWeapon = unit.getWeapons().get(0);
			viewHolder.unitMainWeaponName.setText(mainWeapon.getName());
			viewHolder.unitMainWeaponName
					.setCompoundDrawablesWithIntrinsicBounds(
							mainWeapon.getImage(), 0, 0, 0);
			viewHolder.unitMainWeaponAP.setBackgroundResource(mainWeapon
					.getAPColorEfficiency());
			viewHolder.unitMainWeaponAT.setBackgroundResource(mainWeapon
					.getATColorEfficiency());

			// secondary weapon
			if (unit.getWeapons().size() > 1) {
				viewHolder.unitSecondaryWeapon.setVisibility(View.VISIBLE);
				Weapon secondaryWeapon = unit.getWeapons().get(1);
				viewHolder.unitSecondaryWeaponName.setText(secondaryWeapon
						.getName());
				viewHolder.unitSecondaryWeaponName
						.setCompoundDrawablesWithIntrinsicBounds(
								secondaryWeapon.getImage(), 0, 0, 0);
				viewHolder.unitSecondaryWeaponAP
						.setBackgroundResource(secondaryWeapon
								.getAPColorEfficiency());
				viewHolder.unitSecondaryWeaponAT
						.setBackgroundResource(secondaryWeapon
								.getATColorEfficiency());
			} else {
				viewHolder.unitSecondaryWeapon.setVisibility(View.INVISIBLE);
			}

			if (mIsMyArmy) {
				// frags
				viewHolder.unitFrags.setVisibility(View.VISIBLE);
				viewHolder.unitFrags.setText(mContext.getString(
						R.string.frags_number, unit.getFrags()));
				viewHolder.unitPrice.setVisibility(View.INVISIBLE);
			} else {
				viewHolder.unitFrags.setVisibility(View.INVISIBLE);
				// price
				viewHolder.unitPrice.setVisibility(View.VISIBLE);
				viewHolder.unitPrice.setText(mContext.getString(
						R.string.points, unit.getRealSellPrice(mIsMyArmy)));
			}

		} else {
			// hide views to have an empty slot
			viewHolder.unitImage.setVisibility(View.INVISIBLE);
			viewHolder.unitName.setVisibility(View.INVISIBLE);
			viewHolder.unitExperience.setVisibility(View.INVISIBLE);
			viewHolder.unitPrice.setVisibility(View.INVISIBLE);
			viewHolder.unitFrags.setVisibility(View.INVISIBLE);
			viewHolder.unitMainWeapon.setVisibility(View.INVISIBLE);
			viewHolder.unitSecondaryWeapon.setVisibility(View.INVISIBLE);
		}

		return view;
	}

}
