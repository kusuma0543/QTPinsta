package com.ringaapp.ringapartner;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.ringaapp.ringapartner.dbhandlers.SQLiteHandler;
import com.ringaapp.ringapartner.dbhandlers.SessionManager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String partnerhome_partneruid;
    private ListView partnerhome_listview;
    private ProgressDialog dialog;
    String getmyrejectid;
    String URLCOUNT,jobcounttool;
    private Button partneraccreject_but,partneraccaccept_but;
    AlertDialog alertDialog1;
    TextView tv_toolbar;
    CharSequence[] values = {" I am on other Project "," I cant do the Service right now",
            " Its not my Requirement "," I am out of Station "," My reason is not listed "};

    private SessionManager session;
    private SQLiteHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());
        final HashMap<String, String> user = db.getUserDetails();
        partnerhome_partneruid=user.get("uid");
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragments, new com.ringaapp.ringapartner.MenusFragment()).commit();

         URLCOUNT=GlobalUrl.partner_getmyjobscount+"?partner_uid="+partnerhome_partneruid;
        getJobsMyCount(partnerhome_partneruid);
        tv_toolbar=findViewById(R.id.tv_toolbar);




        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                android.support.v4.app.Fragment selectedFragment = null;
                if (tabId == R.id.tab_new) {
                    selectedFragment = NewJobs.newInstance();
               }
                 else if (tabId == R.id.tab_ongoing) {
                    selectedFragment = OnGoingPartJobs.newInstance();
                                }
                else if (tabId == R.id.tab_finished) {
                    selectedFragment = FinishedPartJobs.newInstance();
                }
                //else if (tabId == R.id.tab_sckell) {
//                    selectedFragment = OnGoing.newInstance();
//                }else if (tabId == R.id.tab_recharge) {
//                    selectedFragment = OnGoing.newInstance();
//                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentContainer, selectedFragment);
                transaction.commit();
            }
        });

        Toast.makeText(this, partnerhome_partneruid, Toast.LENGTH_SHORT).show();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        partneraccreject_but=findViewById(R.id.partneraccrej_rejectbut);
        partneraccaccept_but=findViewById(R.id.partneraccrej_acceptbut);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        dialog=new ProgressDialog(this);
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");
       // partnerhome_listview=findViewById(R.id.partnerhome_listview);

//        String URLL = GlobalUrl.partner_homeaccrejjobs+"?partner_uid="+partnerhome_partneruid;
//        new kilomilo().execute(URLL);
        updatelastseen(partnerhome_partneruid);

    }
//    public class MovieAdap extends ArrayAdapter {
//        private List<home_accerejjobs> movieModelList;
//        private int resource;
//        Context context;
//        private LayoutInflater inflater;
//
//        MovieAdap(Context context, int resource, List<home_accerejjobs> objects) {
//            super(context, resource, objects);
//            movieModelList = objects;
//            this.context = context;
//            this.resource = resource;
//            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//        }
//
//        @Override
//        public int getViewTypeCount() {
//            return 1;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            final ViewHolder holder;
//            if (convertView == null) {
//                convertView = inflater.inflate(resource, null);
//                holder = new ViewHolder();
//
//                holder.textone = (TextView) convertView.findViewById(R.id.partnerhome_username);
//                holder.textthree = (TextView)convertView.findViewById(R.id.partnerhome_usersubcateg);
//                holder.textbookingid=(TextView)convertView.findViewById(R.id.partner_bookingid);
//                holder.butrejectbut=convertView.findViewById(R.id.partneraccrej_rejectbut);
//                holder.butaccept=convertView.findViewById(R.id.partneraccrej_acceptbut);
//
//                convertView.setTag(holder);
//            }//ino
//            else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//            home_accerejjobs ccitacc = movieModelList.get(position);
//            holder.textone.setText(ccitacc.getService_subcateg_name());
//            holder.textthree.setText(ccitacc.getUser_name());
//            holder.textbookingid.setText(ccitacc.getBooking_uid());
//                holder.butaccept.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String getmid=holder.textbookingid.getText().toString();
//                        acceptmeupdate(getmid);
//                        startActivity(new Intent(CategoryMain.this,CategoryMain.class));
//                    }
//                });
//
//                holder.butrejectbut.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                         getmyrejectid=holder.textbookingid.getText().toString();
//
//                        CreateAlertDialogWithRadioButtonGroup() ;
//
//                    }
//                });
//
//
//
//
//
//            return convertView;
//        }
//
//        class ViewHolder {
//            public TextView textone,textthree,textbookingid;
//            public Button butaccept,butrejectbut;
//
//        }
//    }
//
//    public class kilomilo extends AsyncTask<String, String, List<home_accerejjobs>> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            dialog.show();
//        }
//
//        @Override
//        protected List<home_accerejjobs> doInBackground(String... params) {
//            HttpURLConnection connection = null;
//            BufferedReader reader = null;
//            try {
//                URL url = new URL(params[0]);
//                connection = (HttpURLConnection) url.openConnection();
//                connection.connect();
//                InputStream stream = connection.getInputStream();
//                reader = new BufferedReader(new InputStreamReader(stream));
//                StringBuilder buffer = new StringBuilder();
//                String line = "";
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line);
//                }
//                String finalJson = buffer.toString();
//                JSONObject parentObject = new JSONObject(finalJson);
//                JSONArray parentArray = parentObject.getJSONArray("result");
//                List<home_accerejjobs> milokilo = new ArrayList<>();
//                Gson gson = new Gson();
//                for (int i = 0; i < parentArray.length(); i++) {
//                    JSONObject finalObject = parentArray.getJSONObject(i);
//                    home_accerejjobs catego = gson.fromJson(finalObject.toString(), home_accerejjobs.class);
//                    milokilo.add(catego);
//                }
//                return milokilo;
//            } catch (JSONException | IOException e) {
//                e.printStackTrace();
//            } finally {
//                if (connection != null) {
//                    connection.disconnect();
//                }
//                try {
//                    if (reader != null) {
//                        reader.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(final List<home_accerejjobs> movieMode) {
//            super.onPostExecute(movieMode);
//            dialog.dismiss();
//            if (movieMode== null)
//            {
//                Toast.makeText(getApplicationContext(),"No Services available for your selection", Toast.LENGTH_SHORT).show();
//
//            }
//            else
//            {
//                MovieAdap adapter = new MovieAdap(getApplicationContext(), R.layout.home_accerejjobs, movieMode);
//                partnerhome_listview.setAdapter(adapter);
////                partnerhome_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////                    @Override
////                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                        home_accerejjobs item = movieMode.get(position);
////                        Intent intent = new Intent(CategoryMain.this,AcceptReject.class);
////                        intent.putExtra("partnerhome_bookingid",item.getBooking_uid());
////                        intent.putExtra("partnerhome_subcategname",item.getService_subcateg_name());
////                        intent.putExtra("partnerhome_username",item.getUser_name());
////                        startActivity(intent);
////                    }
////                });
//                adapter.notifyDataSetChanged();
//            }
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.category_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
        } else if (id == R.id.nav_profile) {
            Intent intent1=new Intent(CategoryMain.this,ProfilePage.class);
            startActivity(intent1);


        } else if (id == R.id.nav_refer) {

        } else if (id == R.id.nav_chat) {

        } else if (id == R.id.nav_rate) {

        } else if (id == R.id.nav_share) {

        }
        else if (id == R.id.nav_about) {

        }
        else if (id == R.id.nav_contact) {

        }
        else if (id == R.id.nav_tc) {

        }
        else if (id == R.id.nav_logout) {
            Intent intent=new Intent(CategoryMain.this,LoginActivity.class);
            logmeout(partnerhome_partneruid);
            session.setLogin(false);
            db.deleteUsers();
            PreferenceManager.getDefaultSharedPreferences(getBaseContext()).
                    edit().clear().apply();
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void logmeout(final String s1) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalUrl.partner_logoutmode, new Response.Listener<String>() {
            public void onResponse(String response) {

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            { }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("partner_uid",s1);


                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
    public void updatelastseen(final String s1) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalUrl.partner_updatelastseen, new Response.Listener<String>() {
            public void onResponse(String response) {
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            { }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("partner_uid",s1);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

//
//    public void acceptmeupdate(final String s1) {
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalUrl.partner_updateaccept, new Response.Listener<String>() {
//            public void onResponse(String response) {
//
//            }
//
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error)
//            { }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//
//                params.put("booking_uid",s1);
//
//
//                return params;
//            }
//        };
//        AppController.getInstance().addToRequestQueue(stringRequest);
//    }
//    public void CreateAlertDialogWithRadioButtonGroup(){
//
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(CategoryMain.this);
//
//        builder.setTitle("Select Your Reason for Rejection");
//
//        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
//
//            public void onClick(DialogInterface dialog, int item) {
//
//                switch(item)
//                {
//                    case 0:
//                        String case0="I am on other Project";
//                        Toast.makeText(CategoryMain.this, case0, Toast.LENGTH_LONG).show();
//                        rejectmeupdate(getmyrejectid,case0);
//                        startActivity(new Intent(CategoryMain.this,CategoryMain.class));
//
//                        break;
//                    case 1:
//                        String case1="I cant do the Service right now";
//                        Toast.makeText(CategoryMain.this, case1, Toast.LENGTH_LONG).show();
//                        rejectmeupdate(getmyrejectid,case1);
//                        startActivity(new Intent(CategoryMain.this,CategoryMain.class));
//
//                        break;
//                    case 2:
//
//                        Toast.makeText(CategoryMain.this, "Third Item Clicked", Toast.LENGTH_LONG).show();
//                        String case2="Its not my Requirement";
//                        rejectmeupdate(getmyrejectid,case2);
//                        startActivity(new Intent(CategoryMain.this,CategoryMain.class));
//
//                        break;
//                    case 3:
//
//                        Toast.makeText(CategoryMain.this, "FOur Item Clicked", Toast.LENGTH_LONG).show();
//                        String case3="I am out of Station";
//                        rejectmeupdate(getmyrejectid,case3);
//                        startActivity(new Intent(CategoryMain.this,CategoryMain.class));
//
//                        break;
//                    case 4:
//
//                        Toast.makeText(CategoryMain.this, "Five Item Clicked", Toast.LENGTH_LONG).show();
//                        String case4="My reason is not listed";
//                        rejectmeupdate(getmyrejectid,case4);
//                        startActivity(new Intent(CategoryMain.this,CategoryMain.class));
//
//                        break;
//                }
//                alertDialog1.dismiss();
//            }
//        });
//        alertDialog1 = builder.create();
//        alertDialog1.show();
//
//    }
//    public void rejectmeupdate(final String s1,final String s2) {
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalUrl.getPartner_updatereject, new Response.Listener<String>() {
//            public void onResponse(String response) {
//
//            }
//
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error)
//            { }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//
//                params.put("booking_uid",s1);
//                params.put("service_partner_rejectedreason",s2);
//
//                return params;
//            }
//        };
//        AppController.getInstance().addToRequestQueue(stringRequest);
//    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {

            new SweetAlertDialog(CategoryMain.this, SweetAlertDialog.WARNING_TYPE).setTitleText("Are u sure to exit?").setContentText("you wont able to recover ")
                    .setConfirmText("Yes exit").setCancelText("No Dont").showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();

                }
            }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    session.setLogin(false);
                    db.deleteUsers();
                    Intent intent=new Intent(CategoryMain.this,LoginActivity.class);
                    startActivity(intent);
                }
            }).show();

        }
    }
    public void getJobsMyCount(final String sphone1) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLCOUNT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean abc = jObj.getBoolean("exits");
                    if (abc)
                    {
                        JSONObject users = jObj.getJSONObject("lead");
                        jobcounttool = users.getString("lead_partner_uid");
                      //  Toast.makeText(CategoryMain.this, jobcounttool, Toast.LENGTH_SHORT).show();
                        tv_toolbar.setTextColor(Color.WHITE);
                        tv_toolbar.setText(jobcounttool+" Jobs left");

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"No Jobs",Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> insert = new HashMap<String, String>();
                insert.put("partner_uid", sphone1);
                return insert;

            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }
}
