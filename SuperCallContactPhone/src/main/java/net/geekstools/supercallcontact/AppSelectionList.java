package net.geekstools.supercallcontact;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.geekstools.supercallcontact.nav.AutoListAdapter;
import net.geekstools.supercallcontact.nav.NavDrawerItem;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AppSelectionList extends Activity implements View.OnClickListener {

    Activity activity;
    FunctionsClass functionsClass;
    ListView listView;
    RelativeLayout wholeAuto;
    LinearLayout indexView;
    RelativeLayout loadingSplash;
    TextView desc;

    String[] appData;
    Map<String, Integer> mapIndex;
    String[] appNameArray;
    ArrayList<NavDrawerItem> navDrawerItems;
    AutoListAdapter adapter;

    String PackageName;
    String AppName = "Application";
    String AppVersion = "0";
    Drawable AppIcon;

    List<String> appShortcuts;
    int maxShortcuts;

    @Override
    protected void onCreate(Bundle Saved) {
        super.onCreate(Saved);
        setContentView(R.layout.activity_app_selection_list);

        listView = (ListView) findViewById(R.id.listFav);
        indexView = (LinearLayout) findViewById(R.id.side_index);
        wholeAuto = (RelativeLayout) findViewById(R.id.wholeAuto);
        loadingSplash = (RelativeLayout) findViewById(R.id.loadingSplash);
        loadingSplash.bringToFront();

        functionsClass = new FunctionsClass(getApplicationContext(), this);
        activity = this;
        maxShortcuts = getSystemService(ShortcutManager.class).getMaxShortcutCountPerActivity();

        wholeAuto.setBackgroundColor(getColor(R.color.light));
        getActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.default_color)));
        getActionBar().setTitle(Html.fromHtml("<font color='" + getColor(R.color.light) + "'>" + getString(R.string.app_name) + "</font>"));
        getActionBar().setSubtitle(Html.fromHtml("<small><font color='" + getColor(R.color.light) + "'>" + "Maximum Choice " + maxShortcuts + "</font>"));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getColor(R.color.default_color_darker));
        getWindow().setNavigationBarColor(getColor(R.color.light));

        navDrawerItems = new ArrayList<NavDrawerItem>();
        mapIndex = new LinkedHashMap<String, Integer>();

        desc = (TextView) findViewById(R.id.desc);
        Typeface face = Typeface.createFromAsset(getAssets(), "upcil.ttf");
        desc.setTypeface(face);

        ProgressBar loadingBarLTR = (ProgressBar) findViewById(R.id.loadingProgressltr);
        loadingBarLTR.getIndeterminateDrawable().setColorFilter(getColor(R.color.dark), android.graphics.PorterDuff.Mode.MULTIPLY);

        if (!getFileStreamPath(".AppInfo").exists()) {
            functionsClass.loadData();
        } else {
            new LoadApplicationsOff().execute();
        }

        LayerDrawable drawIndex = (LayerDrawable) getResources().getDrawable(R.drawable.draw_index);
        GradientDrawable backIndex = (GradientDrawable) drawIndex.findDrawableByLayerId(R.id.backtemp);
        backIndex.setColor(getColor(R.color.green));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        final PackageManager manager = getPackageManager();
        final ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        ImageView confirm = (ImageView) findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    shortcutManager.removeAllDynamicShortcuts();
                    appShortcuts = Arrays.asList(functionsClass.readFileLine(".autoSuper"));

                    List<ShortcutInfo> shortcutInfos = new ArrayList<ShortcutInfo>();
                    shortcutInfos.clear();

                    System.out.println("Max Dynamic Shortcuts Per Activity :: " + maxShortcuts);
                    int maxLoop;
                    if (appShortcuts.size() > maxShortcuts) {
                        maxLoop = maxShortcuts;
                    } else {
                        maxLoop = appShortcuts.size();
                    }
                    for (int i = 0; i < maxLoop; i++) {
                        Intent intent = manager.getLaunchIntentForPackage(appShortcuts.get(i));
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(AppSelectionList.this, String.valueOf(i))
                                .setShortLabel(functionsClass.appName(appShortcuts.get(i)))
                                .setLongLabel(functionsClass.appName(appShortcuts.get(i)))
                                .setIcon(Icon.createWithBitmap(functionsClass.appIconBitmap(appShortcuts.get(i))))
                                .setIntent(intent)
                                .setRank(i)
                                .build();

                        shortcutInfos.add(shortcutInfo);
                    }
                    shortcutManager.addDynamicShortcuts(shortcutInfos);
                    Toast.makeText(getApplicationContext(), getString(R.string.done), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {/*add shortcuts to home screen*/}
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        TextView selectedIndex = (TextView) view;
        listView.setSelection(mapIndex.get(selectedIndex.getText().toString()));
    }

    /******************************************/
    public class LoadApplicationsOff extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            File f = getApplicationContext().getFileStreamPath(".AppInfo");
            if (!f.exists()) {
                functionsClass.loadData();
                return;
            }

            desc = (TextView) findViewById(R.id.desc);
            Typeface face = Typeface.createFromAsset(getAssets(), "upcil.ttf");
            desc.setTypeface(face);

            listView.clearChoices();
            indexView.removeAllViews();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                FileInputStream fin = new FileInputStream(getApplicationContext().getFileStreamPath(".AppInfo"));
                DataInputStream myDIS = new DataInputStream(fin);

                int count = functionsClass.countLine(".AppInfo");
                appData = new String[count];
                appNameArray = new String[count];
                String line = "";
                int i = 0;
                while ((line = myDIS.readLine()) != null) {
                    appData[i] = line;
                    System.out.println(appData[i]);
                    i++;
                }

                navDrawerItems = new ArrayList<NavDrawerItem>();
                mapIndex = new LinkedHashMap<String, Integer>();

                for (int navItem = 0; navItem < count; navItem++) {
                    try {
                        PackageName = appData[navItem];
                        System.out.println("Package Name >> " + navItem + " " + PackageName);
                        AppName = functionsClass.appName(PackageName);
                        appNameArray[navItem] = AppName;
                        AppVersion = functionsClass.appVersion(PackageName);
                        AppIcon = functionsClass.appIcon(PackageName);

                        String index = appNameArray[navItem].substring(0, 1).toUpperCase();
                        if (mapIndex.get(index) == null) {
                            mapIndex.put(index, navItem);
                        }

                        navDrawerItems.add(new NavDrawerItem(AppName, PackageName, AppIcon));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                adapter = new AutoListAdapter(activity, getApplicationContext(), navDrawerItems);
            } catch (Exception e) {
                System.out.println("Error: No App Info\n" + e);
                finish();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            TextView textView;
            List<String> indexList = new ArrayList<String>(mapIndex.keySet());
            for (String index : indexList) {
                textView = (TextView) getLayoutInflater().inflate(R.layout.side_index_item, null);
                textView.setText(index.toUpperCase());
                textView.setOnClickListener(AppSelectionList.this);
                indexView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                indexView.addView(textView);
            }
            listView.setAdapter(adapter);
            registerForContextMenu(listView);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                    loadingSplash.setVisibility(View.INVISIBLE);
                    loadingSplash.startAnimation(anim);
                }
            }, 100);
        }
    }
}
