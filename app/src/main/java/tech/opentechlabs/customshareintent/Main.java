package tech.opentechlabs.customshareintent;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = findViewById(R.id.btn);

        /*
        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(extraText));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, extraSubject);
        emailIntent.setType("message/rfc822");

        PackageManager pm = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        ArrayList<ResolveInfo> dialogResInfo = new ArrayList<ResolveInfo>();

        for (int i=0; i<resInfo.size(); i++) {
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("android.email") ||
                    packageName.contains("twitter") ||
                    packageName.contains("facebook") ||
                    packageName.contains("android.gm")) {
                dialogResInfo.add(ri);
            }
        }

        Collections.sort(dialogResInfo, new ResolveInfo.DisplayNameComparator(pm));

        final Dialog dialog = new Dialog(BaseActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_share_chooser);
        dialog.setCancelable(true);
        ListView dialogListView = (ListView)dialog.findViewById(R.id.share_list);
        final ShareListAdapter adapter = new ShareListAdapter(this, pm, dialogResInfo);
        dialogListView.setAdapter(adapter);

        dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ResolveInfo ri  = adapter.getItem(position);
                String packageName = ri.activityInfo.packageName;
                if (packageName.contains("facebook")) {
                    FacebookDialog fbDialog = new FacebookDialog.ShareDialogBuilder(BaseActivity.this)
                            .setLink(extraText)
                            .build();
                    fbUiHelper.trackPendingDialogCall(fbDialog.present());
                    finish();
                } else {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    if(packageName.contains("twitter")) {
                        intent.putExtra(Intent.EXTRA_TEXT, extraText);
                    } else if(packageName.contains("mms")) {
                        intent.putExtra(Intent.EXTRA_TEXT, extraText);
                    } else if(packageName.contains("android.gm")) {
                        intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(extraText));
                        intent.putExtra(Intent.EXTRA_SUBJECT, extraSubject);
                        intent.setType("message/rfc822");
                    }
                    startActivity(intent);
                }
            }
        });

        dialog.show();

         */

        String urlToShare = "https://play.google.com/store/apps/details?id=com.yourapp.packagename";
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        // intent.putExtra(Intent.EXTRA_SUBJECT, "If any extra"); // NB: has no effect!
        intent.putExtra(Intent.EXTRA_TEXT, "Let me recommend you this application \n\n" + urlToShare);

        final List<ResolveInfo> activities = getPackageManager().queryIntentActivities(intent, 0);

        List<DialogItem> appNames = new ArrayList<DialogItem>();

        for (ResolveInfo info : activities) {
            appNames.add(new DialogItem(info.loadLabel(getPackageManager()).toString(),
                    info.loadIcon(getPackageManager())));
        }

        final List<DialogItem> newItem = appNames;

        ListAdapter adapter = new ArrayAdapter<DialogItem>(this, android.R.layout.select_dialog_item, android.R.id.text1, newItem) {
            public View getView(int position, View convertView, ViewGroup parent) {
                //Use super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = v.findViewById(android.R.id.text1);
                tv.setText(newItem.get(position).app);
                tv.setTextSize(15.0f);
                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(newItem.get(position).icon, null, null, null);

                //Add margin between image and text (support various screen densities)
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                tv.setCompoundDrawablePadding(dp5);

                return v;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Custom Sharing Dialog");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                ResolveInfo info = activities.get(item);

                if (info.activityInfo.packageName.equals("com.facebook.katana")) {
                    Toast.makeText(Main.this, "Facebook Selected ", Toast.LENGTH_LONG).show();
                } else {

                    // start the selected activity
                    Log.i("TAG", "Hi..hello. Intent is selected");
                    intent.setPackage(info.activityInfo.packageName);
                    startActivity(intent);
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(Main.this, "Facebook Selected ", Toast.LENGTH_LONG).show();
                //Log.e("this", "this");
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
}
