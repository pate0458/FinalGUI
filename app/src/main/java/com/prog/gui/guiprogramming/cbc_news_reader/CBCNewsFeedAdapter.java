package com.prog.gui.guiprogramming.cbc_news_reader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.prog.gui.guiprogramming.R;

import java.util.ArrayList;

public class CBCNewsFeedAdapter extends BaseAdapter {


    private ArrayList<CBCNewsModel> rowData;
    private static LayoutInflater inflater;

    /**
     * <p>ListAdapter constructor</p>
     * @param activity activity context to get LayoutInflaterService
     * @param rowData data to set to each row
     * */
    CBCNewsFeedAdapter(Activity activity, ArrayList<CBCNewsModel> rowData) {
        this.rowData = rowData;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return rowData.size();
    }

    @Override
    public Object getItem(int position) {
        return rowData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.cbc_row_news_story, null);
            holder.tvNewsTitle = view.findViewById(R.id.tvNewsTitle);
            holder.tvNewsDate = view.findViewById(R.id.tvNewsDate);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        CBCNewsModel model = rowData.get(position);
        String title = model.getTitle();
        String date = model.getPubDate();
        holder.tvNewsTitle.setText(title);
        holder.tvNewsDate.setText(date);
        return view;
    }

    static class ViewHolder {
        TextView tvNewsTitle, tvNewsDate;
    }
}
