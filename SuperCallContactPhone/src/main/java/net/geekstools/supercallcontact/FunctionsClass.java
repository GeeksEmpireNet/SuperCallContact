package net.geekstools.supercallcontact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;

import net.geekstools.supercallcontact.nav.NavDrawerItem;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FunctionsClass {
    int API;
    Activity activity;
    Context context;
    PackageManager packageManager;
    ArrayList<NavDrawerItem> navDrawerItems;

    public FunctionsClass(Context context) {
        this.context = context;
        API = Build.VERSION.SDK_INT;
    }

    public FunctionsClass(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        API = Build.VERSION.SDK_INT;
    }

    public void saveFile(String fileName, String content) {
        try {
            FileOutputStream fOut = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fOut.write((content).getBytes());

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFileAppendLine(String fileName, String content) {
        try {
            FileOutputStream fOut = context.openFileOutput(fileName, Context.MODE_APPEND);
            fOut.write((content + "\n").getBytes());

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] readFileLine(String fileName) {
        String[] contentLine = null;
        if (context.getFileStreamPath(fileName).exists()) {
            try {
                FileInputStream fin = new FileInputStream(context.getFileStreamPath(fileName));
                DataInputStream myDIS = new DataInputStream(fin);

                int count = countLine(fileName);
                contentLine = new String[count];
                String line = "";
                int i = 0;
                while ((line = myDIS.readLine()) != null) {
                    contentLine[i] = line;
                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return contentLine;
    }

    public void removeLine(String fileName, String lineToRemove) {
        try {
            FileInputStream fin = context.openFileInput(fileName);
            DataInputStream myDIS = new DataInputStream(fin);
            OutputStreamWriter fOut = new OutputStreamWriter(context.openFileOutput(fileName + ".tmp", Context.MODE_APPEND));

            String tmp = "";
            while ((tmp = myDIS.readLine()) != null) {
                if (!tmp.trim().equals(lineToRemove)) {
                    fOut.write(tmp);
                    fOut.write("\n");
                }
            }
            fOut.close();
            myDIS.close();
            fin.close();

            File tmpD = context.getFileStreamPath(fileName + ".tmp");
            File New = context.getFileStreamPath(fileName);

            if (tmpD.isFile()) {
                System.out.println("File");
            }
            context.deleteFile(fileName);
            tmpD.renameTo(New);
        } catch (Exception E) {
            E.printStackTrace();
        } finally {
            try {
                if (API > 23) {
                    context.deleteSharedPreferences(lineToRemove);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int countLine(String fileName) {
        int nLines = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(context.getFileStreamPath(fileName)));

            while (reader.readLine() != null) {
                nLines++;
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nLines;
    }

    public String appName(String pack) {
        String Name = null;

        try {
            PackageManager packManager = context.getPackageManager();
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(pack, 0);
            Name = packManager.getApplicationLabel(app).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Name;
    }

    public String appVersion(String pack) {
        String Version = "0";

        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(pack, 0);
            Version = packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Version;
    }

    public Drawable appIcon(String pack) {
        Drawable icon = null;
        try {
            PackageManager packManager = context.getPackageManager();
            icon = packManager.getApplicationIcon(pack);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return icon;
    }

    public Bitmap appIconBitmap(String pack) {
        BitmapDrawable icon = null;
        try {
            PackageManager packManager = context.getPackageManager();
            icon = (BitmapDrawable) packManager.getApplicationIcon(pack);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return icon.getBitmap();
    }

    public void loadData() {
        LaunchLoadApplications launchLoadApplications = new LaunchLoadApplications();
        launchLoadApplications.execute();
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        File f = context.getFileStreamPath(".AppInfo");
        if (!f.exists()) {
            ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();

            Collections.sort(list, new ApplicationInfo.DisplayNameComparator(packageManager));
            for (ApplicationInfo info : list) {
                try {
                    if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                        applist.add(info);

                        saveAppInfo(info.packageName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return applist;
        } else {
            System.out.println(".AppInfo Found");
        }
        return null;
    }

    private void saveAppInfo(String packName) {
        try {
            //String toSave = i + " " + "[" + appName + "]" + " " + "*" + packName + "*" + "\n";
            String toSave = packName + "\n";
            FileOutputStream fOut = context.openFileOutput(".AppInfo", Context.MODE_APPEND);
            fOut.write((toSave).getBytes());

            fOut.close();
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addShortcutToHome(Class className) {
        //
    }

    private class LaunchLoadApplications extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            packageManager = context.getPackageManager();
            checkForLaunchIntent(packageManager.getInstalledApplications(0));

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            System.out.println("Data Created");
            activity.startActivity(new Intent(context, AppSelectionList.class));
        }
    }
}