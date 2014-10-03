package co.natsuhi.mushroomstep;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AppListAdapter extends ArrayAdapter<AppLoader.AppEntry> {
    private final LayoutInflater mLayoutInflater;

    public AppListAdapter(Context context) {
        super(context, R.layout.applist);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<AppLoader.AppEntry> data) {
        clear();

        if (data != null) {
            addAll(data);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.applist,parent,false);
        }

        AppLoader.AppEntry app = getItem(position);

        ((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(app.getIcon());
        ((TextView) convertView.findViewById(R.id.text)).setText(app.getLabel());
        return convertView;
    }
}
