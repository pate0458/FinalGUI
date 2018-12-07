package com.prog.gui.guiprogramming.food_nut_db;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prog.gui.guiprogramming.R;

import java.util.ArrayList;

public class FoodFavoritesAdapter extends BaseAdapter {

    private ArrayList<FoodModel> mRowData;
    private static LayoutInflater inflater;

    /**
     * <p>ListAdapter constructor</p>
     * @param activity activity context to get LayoutInflaterService
     * @param mRowData data to set to each row
     * */
    public FoodFavoritesAdapter(Activity activity, ArrayList<FoodModel> mRowData) {
        this.mRowData = mRowData;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mRowData.size();
    }

    @Override
    public Object getItem(int position) {
        return mRowData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.food_row_result_fav_list, null);
            holder = new ViewHolder();
            holder.tvRowFoodTitle = view.findViewById(R.id.tvRowFoodTitle);
            holder.tvRowFoodCategory = view.findViewById(R.id.tvRowFoodCategory);
            holder.tvRowFoodTAG = view.findViewById(R.id.tvRowFoodTAG);
            holder.linearTAG = view.findViewById(R.id.linearTAG);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        FoodModel model = mRowData.get(position);
        String label = model.getLabel();
        String category = model.getCategory();
        String tag = model.getTag();
        holder.tvRowFoodTitle.setText(label);
        holder.tvRowFoodCategory.setText(category);
        if(tag.trim().length() != 0){
            holder.tvRowFoodTAG.setText(tag);
        }else {
            holder.linearTAG.setVisibility(View.GONE);
        }
        return view;
    }

    static class ViewHolder {
        TextView tvRowFoodTitle, tvRowFoodCategory, tvRowFoodTAG;
        LinearLayout linearTAG;
    }
}
