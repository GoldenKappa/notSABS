package com.layoutxml.sabs.fragments;

import android.annotation.SuppressLint;
import android.app.enterprise.ApplicationPolicy;
import android.arch.lifecycle.LifecycleFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.SearchView;

import com.layoutxml.sabs.App;
import com.layoutxml.sabs.BuildConfig;
import com.layoutxml.sabs.MainActivity;
import com.layoutxml.sabs.R;
import com.layoutxml.sabs.db.AppDatabase;
import com.layoutxml.sabs.db.entity.AppInfo;
import com.layoutxml.sabs.utils.AppsListDBInitializer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.util.List;
import java.io.*;
import java.util.Objects;

import javax.inject.Inject;

public class PackageDisablerFragment extends LifecycleFragment {
    private static final String TAG = PackageDisablerFragment.class.getCanonicalName();
    private final int SORTED_ALPHABETICALLY = 0;
    private final int SORTED_INSTALL_TIME = 1;
    private final int SORTED_DISABLED = 2;
    @Nullable
    @Inject
    ApplicationPolicy appPolicy;
    @Inject
    AppDatabase mDb;
    @Inject
    PackageManager packageManager;
    private ListView installedAppsView;
    private Context context;
    private List<AppInfo> packageList;
    private DisablerAppAdapter adapter;
    //private EditText editText;
    private int sortState = SORTED_ALPHABETICALLY;
    private AppCompatActivity parentActivity;
    private FragmentManager fragmentManager;
    private SwipeRefreshLayout swipeToRefresh;
    private Handler mHandler = new Handler();
    private String filename;
    private static final Character[] ReservedCharacters = {'\\','/',':','*','?','"','<','>','|'};
    private Integer count;
    private String searchtext;


    public PackageDisablerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get().getAppComponent().inject(this);
        parentActivity = (AppCompatActivity) getActivity();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.package_disabler_fragment_title));
        if (parentActivity.getSupportActionBar() != null) {
            parentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            parentActivity.getSupportActionBar().setHomeButtonEnabled(false);
        }
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_package_disabler, container, false);
        context = getActivity().getApplicationContext();
        //editText = view.findViewById(R.id.disabledFilter);
        //editText.setOnClickListener(v -> editText.setCursorVisible(true));
        swipeToRefresh = view.findViewById(R.id.swipeToRefresh);
        swipeToRefresh.setOnRefreshListener(getSwipeRefreshListener());

        ((MainActivity)getActivity()).showBottomBar();

        installedAppsView = view.findViewById(R.id.installed_apps_list);
        installedAppsView = view.findViewById(R.id.installed_apps_list);
        installedAppsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adView, View v, int i, long l) {
                DisablerAppAdapter disablerAppAdapter = (DisablerAppAdapter) adView.getAdapter();
                final String name = disablerAppAdapter.getItem(i).packageName;
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... o) {
                        AppInfo appInfo = mDb.applicationInfoDao().getByPackageName(name);
                        appInfo.disabled = !appInfo.disabled;
                        if (appInfo.disabled) {
                            appPolicy.setDisableApplication(name);
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Disabled " + name, Snackbar.LENGTH_SHORT).show();
                        } else {
                            appPolicy.setEnableApplication(name);
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Enabled " + name, Snackbar.LENGTH_SHORT).show();
                        }
                        mDb.applicationInfoDao().insert(appInfo);
                        disablerAppAdapter.applicationInfoList.set(i, appInfo);
                        return appInfo.disabled;
                    }

                    @Override
                    protected void onPostExecute(Boolean b) {
                        ((Switch) v.findViewById(R.id.switchDisable)).setChecked(!b);
                    }
                }.execute();
            }
        });

        installedAppsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adView, View v, int i, long l) {
                DisablerAppAdapter disablerAppAdapter = (DisablerAppAdapter) adView.getAdapter();
                final String name = disablerAppAdapter.getItem(i).packageName;
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + name));
                startActivity(intent);
                return true;
            }
        });

        loadApplicationsList(false, "");
        /*editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                loadApplicationsList(false, "");
            }
        });*/
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.package_disabler_menu, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(context, query, Toast.LENGTH_SHORT).show();
                /*if(!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();*/
                //searchtext = query;
                loadApplicationsList(false, query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                //Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                loadApplicationsList(false, s);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        Boolean blackTheme = sharedPreferences.getBoolean("blackTheme", false);
        switch (item.getItemId()) {
            case R.id.action_pack_dis_sort:
                break;
            case R.id.sort_alphabetically_item:
                if (sortState == SORTED_ALPHABETICALLY) break;
                sortState = SORTED_ALPHABETICALLY;
                Toast.makeText(context, getString(R.string.app_list_sorted_by_alphabet), Toast.LENGTH_SHORT).show();
                loadApplicationsList(false, "");
                break;
            case R.id.sort_by_time_item:
                if (sortState == SORTED_INSTALL_TIME) break;
                sortState = SORTED_INSTALL_TIME;
                Toast.makeText(context, getString(R.string.app_list_sorted_by_date), Toast.LENGTH_SHORT).show();
                loadApplicationsList(false, "");
                break;
            case R.id.sort_disabled_item:
                sortState = SORTED_DISABLED;
                Toast.makeText(context, getString(R.string.app_list_sorted_by_disabled), Toast.LENGTH_SHORT).show();
                loadApplicationsList(false, "");
                break;
            case R.id.disabler_import_storage:
                if(blackTheme)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.BlackAppThemeDialog);
                    builder.setTitle("Choose file name");
                    builder.setMessage("Choose a file name of your package list. File must not have any of these characters: |\\?*<\":>/' and must end with \".txt\"");
                    final EditText input = new EditText(getContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            filename = input.getText().toString();
                            for (Character ReservedCharacter : ReservedCharacters) {
                                filename = filename.replace(ReservedCharacter.toString(), "");
                            }
                            filename = filename.replace(".txt","");
                            if (Objects.equals(filename, ""))
                                Snackbar.make(getActivity().findViewById(android.R.id.content), "Empty file name. 0 packages blocked", Snackbar.LENGTH_LONG).show();
                            else
                                importList(filename);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MainAppThemeDialog);
                    builder.setTitle("Choose file name");
                    builder.setMessage("Choose a file name of your package list. File must not have any of these characters: |\\?*<\":>/' and must end with \".txt\"");
                    final EditText input = new EditText(getContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            filename = input.getText().toString();
                            for (Character ReservedCharacter : ReservedCharacters) {
                                filename = filename.replace(ReservedCharacter.toString(), "");
                            }
                            filename = filename.replace(".txt","");
                            if (Objects.equals(filename, ""))
                                Snackbar.make(getActivity().findViewById(android.R.id.content), "Empty file name. 0 packages blocked", Snackbar.LENGTH_LONG).show();
                            else
                                importList(filename);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
                break;
            case R.id.disabler_export_storage:
                if (blackTheme)
                {AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext(), R.style.BlackAppThemeDialog);
                    builder1.setTitle("Choose file name");
                    builder1.setMessage("Choose a file name of your package list. File must not have any of these characters: |\\?*<\":>/' and must end with \".txt\"");
                    final EditText input1 = new EditText(getContext());
                    input1.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder1.setView(input1);
                    builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            filename = input1.getText().toString();
                            for (Character ReservedCharacter : ReservedCharacters) {
                                filename = filename.replace(ReservedCharacter.toString(), "");
                            }
                            filename = filename.replace(".txt","");
                            if (Objects.equals(filename, ""))
                                Snackbar.make(getActivity().findViewById(android.R.id.content), "Empty file name. 0 packages blocked", Snackbar.LENGTH_LONG).show();
                            else
                                exportList(filename);
                        }
                    });
                    builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder1.show();
                } else
                {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext(), R.style.MainAppThemeDialog);
                    builder1.setTitle("Choose file name");
                    builder1.setMessage("Choose a file name of your package list. File must not have any of these characters: |\\?*<\":>/' and must end with \".txt\"");
                    final EditText input1 = new EditText(getContext());
                    input1.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder1.setView(input1);
                    builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            filename = input1.getText().toString();
                            for (Character ReservedCharacter : ReservedCharacters) {
                                filename = filename.replace(ReservedCharacter.toString(), "");
                            }
                            filename = filename.replace(".txt","");
                            if (Objects.equals(filename, ""))
                                Snackbar.make(getActivity().findViewById(android.R.id.content), "Empty file name. 0 packages blocked", Snackbar.LENGTH_LONG).show();
                            else
                                exportList(filename);
                        }
                    });
                    builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder1.show();
                }

                break;
            case R.id.disabler_share:
                filename = "SABS";
                exportList(filename);
                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                File fileWithinMyDir = new File(Environment.getExternalStorageDirectory().toString() + "/"  + filename + ".txt");
                if(fileWithinMyDir.exists()) {
                    intentShareFile.setType("text/*");
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+fileWithinMyDir.getAbsolutePath()));
                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "SABS.txt");
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "SABS.txt");
                    startActivity(Intent.createChooser(intentShareFile, "Share File"));
                }
                break;
            case R.id.disabler_enable_all:
                Toast.makeText(context, getString(R.string.enabled_all_disabled), Toast.LENGTH_SHORT).show();
                enableAllPackages();
                break;
            case R.id.settings_in_options:
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new AppSettingsFragment(), AppSettingsFragment.class.getCanonicalName())
                        .addToBackStack(AppSettingsFragment.class.getCanonicalName())
                        .commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    private void importList(String filename) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... o) {
                File file = new File(Environment.getExternalStorageDirectory(), filename+".txt");
                count=0;
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        try {
                            count++;
                            AppInfo appInfo = mDb.applicationInfoDao().getByPackageName(line);
                            appInfo.disabled = true;
                            assert appPolicy != null;
                            appPolicy.setDisableApplication(line);
                            mDb.applicationInfoDao().insert(appInfo);
                        }
                        catch (Exception e) {
                            // Ignore any potential errors
                            count--;
                        }
                    }
                }
                catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                }
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Disabled " + count + " packages", Snackbar.LENGTH_LONG).show();
                return null;
            }

            @Override
            protected void onPostExecute(Void o) {
                super.onPostExecute(o);
                loadApplicationsList(true, "");
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void exportList(String filename) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... o) {
                File file = new File(Environment.getExternalStorageDirectory(), filename+".txt");
                count=0;
                List<AppInfo> disabledAppList = mDb.applicationInfoDao().getDisabledApps();
                try {
                    FileOutputStream stream = new FileOutputStream(file);
                    OutputStreamWriter writer = new OutputStreamWriter(stream);
                    writer.write("");
                    for (AppInfo app : disabledAppList) {
                        count++;
                        writer.append(app.packageName).append("\n");
                    }
                    writer.close();
                    stream.flush();
                    stream.close();
                }
                catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                }
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Exported " + count + " packages", Snackbar.LENGTH_LONG).show();
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void enableAllPackages() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... o) {
                List<AppInfo> disabledAppList = mDb.applicationInfoDao().getDisabledApps();

                for (AppInfo app : disabledAppList) {
                    app.disabled = false;
                    assert appPolicy != null;
                    appPolicy.setEnableApplication(app.packageName);
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Enabled " + app.packageName, Snackbar.LENGTH_SHORT).show();
                    mDb.applicationInfoDao().insert(app);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void o) {
                super.onPostExecute(o);
                loadApplicationsList(true, "");
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void loadApplicationsList(boolean clear, String text) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... o) {
                if (clear) mDb.applicationInfoDao().deleteAll();
                else {
                    packageList = getListFromDb(text);
                    assert packageList != null;
                    if (packageList.size() != 0) return null;
                }
                AppsListDBInitializer.getInstance().fillPackageDb(packageManager);
                packageList = getListFromDb(text);
                return null;
            }

            @Override
            protected void onPostExecute(Void o) {
                super.onPostExecute(o);
                adapter = new DisablerAppAdapter(packageList);
                installedAppsView.setAdapter(adapter);
                installedAppsView.invalidateViews();
            }
        }.execute();
    }

    private List<AppInfo> getListFromDb(String text) {
        String filterText = '%' + text + '%';
        switch (sortState) {
            case SORTED_ALPHABETICALLY:
                if (filterText.length() == 0) return mDb.applicationInfoDao().getAll();
                return mDb.applicationInfoDao().getAllAppsWithStrInName(filterText);
            case SORTED_INSTALL_TIME:
                if (filterText.length() == 0) return mDb.applicationInfoDao().getAllRecentSort();
                return mDb.applicationInfoDao().getAllAppsWithStrInNameTimeOrder(filterText);
            case SORTED_DISABLED:
                if (filterText.length() == 0)
                    return mDb.applicationInfoDao().getAllSortedByDisabled();
                return mDb.applicationInfoDao().getAllAppsWithStrInNameDisabledOrder(filterText);
        }
        return null;
    }

    public static class ViewHolder {
        TextView nameH;
        TextView packageH;
        Switch switchH;
        ImageView imageH;
    }

    private class DisablerAppAdapter extends BaseAdapter {
        // field variable
        private Picasso mPicasso;
        List<AppInfo> applicationInfoList;
        DisablerAppAdapter(List<AppInfo> appInfoList) {
            applicationInfoList = appInfoList;
            // in constructor
            Picasso.Builder builder = new Picasso.Builder(context);
            builder.addRequestHandler(new AppIconRequestHandler(context));
            mPicasso = builder.build();
        }

        @Override
        public int getCount() {
            return this.applicationInfoList.size();
        }

        @Override
        public AppInfo getItem(int position) {
            return this.applicationInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_disable_app_list_view, parent, false);
                holder = new ViewHolder();
                holder.nameH = convertView.findViewById(R.id.appName);
                holder.packageH = convertView.findViewById(R.id.packName);
                holder.switchH = convertView.findViewById(R.id.switchDisable);
                holder.imageH = convertView.findViewById(R.id.appIcon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            AppInfo appInfo = applicationInfoList.get(position);
            holder.nameH.setText(appInfo.appName);
            holder.packageH.setText(appInfo.packageName);
            holder.switchH.setChecked(!appInfo.disabled);
            if (!appInfo.system) {
                TextView SystemOrNotHereICome = convertView.findViewById(R.id.systemOrNot);
                SystemOrNotHereICome.setText(R.string.system_not);
                SystemOrNotHereICome.setTextColor(Color.parseColor("#4CAF50"));
            } else
            {
                TextView SystemOrNotHereICome = convertView.findViewById(R.id.systemOrNot);
                SystemOrNotHereICome.setText(R.string.system);
                SystemOrNotHereICome.setTextColor(Color.RED);
            }
            SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            Boolean blackTheme = sharedPreferences.getBoolean("blackTheme", false);
            if (blackTheme)
            {
                holder.nameH.setTextColor(Color.parseColor("#FFFFFF"));
                holder.packageH.setTextColor(Color.parseColor("#808080"));
            }
            mPicasso.load(AppIconRequestHandler.getUri(appInfo.packageName)).into(holder.imageH);
            return convertView;
        }
    }

    public static class AppIconRequestHandler extends RequestHandler {
        /** Uri scheme for app icons */
        static final String SCHEME_APP_ICON = "app-icon";
        private PackageManager mPackageManager;
        AppIconRequestHandler(Context context) {
            mPackageManager = context.getPackageManager();
        }
        /**
         * Create an Uri that can be handled by this RequestHandler based on the package name
         */
        static Uri getUri(String packageName) {
            return Uri.fromParts(SCHEME_APP_ICON, packageName, null);
        }
        @Override
        public boolean canHandleRequest(Request data) {
            // only handle Uris matching our scheme
            return (SCHEME_APP_ICON.equals(data.uri.getScheme()));
        }
        @Override
        public Result load(Request request, int networkPolicy) throws IOException {
            String packageName = request.uri.getSchemeSpecificPart();
            Drawable drawable;
            try {
                drawable = mPackageManager.getApplicationIcon(packageName);
            } catch (PackageManager.NameNotFoundException ignored) {
                return null;
            }
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            return new Result(bitmap, Picasso.LoadedFrom.DISK);
        }
    }

    protected SwipeRefreshLayout.OnRefreshListener getSwipeRefreshListener(){

        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //editText.setText("");
                loadApplicationsList(true, "");
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        if (swipeToRefresh != null) {
                            swipeToRefresh.setRefreshing(false);
                        }
                    }
                }, 5000);
            }
        };
    }
}
