package net.geekstools.supercallcontact.nav;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.geekstools.supercallcontact.FunctionsClass;
import net.geekstools.supercallcontact.R;

import java.io.File;
import java.util.ArrayList;

public class AutoListAdapter extends BaseAdapter {

    FunctionsClass functionsClass;
    CheckBox[] autoChoice;
    private Context context;
    private Activity activity;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public AutoListAdapter(Activity activity, Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.activity = activity;
        this.context = context;
        this.navDrawerItems = navDrawerItems;

        autoChoice = new CheckBox[navDrawerItems.size()];
        functionsClass = new FunctionsClass(context, activity);
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.auto_item_card_list, null);
        }

        RelativeLayout item = (RelativeLayout) convertView.findViewById(R.id.item);
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtDesc = (TextView) convertView.findViewById(R.id.desc);
        autoChoice[position] = (CheckBox) convertView.findViewById(R.id.autoChoice);

        imgIcon.setImageDrawable(navDrawerItems.get(position).getIcon());
        txtDesc.setText(navDrawerItems.get(position).getDesc());

        final String pack = navDrawerItems.get(position).getTitle();
        File autoFile = context.getFileStreamPath(pack + ".Super");
        autoChoice[position].setChecked(false);
        if (autoFile.exists()) {
            autoChoice[position].setChecked(true);
        } else {
            autoChoice[position].setChecked(false);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String pack = navDrawerItems.get(position).getTitle();
                File autoFile = context.getFileStreamPath(pack + ".Super");
                if (autoFile.exists()) {
                    context.deleteFile(
                            navDrawerItems.get(position).getTitle() + ".Super");
                    functionsClass.removeLine(".autoSuper", navDrawerItems.get(position).getTitle());
                    autoChoice[position].setChecked(false);
                } else {
                    functionsClass.saveFile(
                            navDrawerItems.get(position).getTitle() + ".Super",
                            navDrawerItems.get(position).getTitle());
                    functionsClass.saveFileAppendLine(
                            ".autoSuper",
                            navDrawerItems.get(position).getTitle());
                    autoChoice[position].setChecked(true);
                }
            }
        });

        return convertView;
    }
}
