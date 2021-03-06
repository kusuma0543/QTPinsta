package com.ringaapp.ringapartner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.ringaapp.ringapartner.dbhandlers.SQLiteHandler;
import com.ringaapp.ringapartner.dbhandlers.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class NewJobs extends Fragment {

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
    public static NewJobs newInstance() {
        NewJobs fragment= new NewJobs();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View view= inflater.inflate(R.layout.fragment_new_jobs, container, false);

        session = new SessionManager(getActivity());
        db = new SQLiteHandler(getActivity());
        final HashMap<String, String> user = db.getUserDetails();
        partnerhome_partneruid=user.get("uid");
         partnerhome_listview=view.findViewById(R.id.partnerhome_listview);
        dialog=new ProgressDialog(getActivity());
        dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");
        String URLL = GlobalUrl.partner_homeaccrejjobs+"?partner_uid="+partnerhome_partneruid;
        new kilomilo().execute(URLL);
       return view;
    }
    public class MovieAdap extends ArrayAdapter {
        private List<home_accerejjobs> movieModelList;
        private int resource;
        Context context;
        private LayoutInflater inflater;

        MovieAdap(Context context, int resource, List<home_accerejjobs> objects) {
            super(context, resource, objects);
            movieModelList = objects;
            this.context = context;
            this.resource = resource;
            inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(resource, null);
                holder = new ViewHolder();

                holder.textone = (TextView) convertView.findViewById(R.id.partnerhome_username);
                holder.textthree = (TextView)convertView.findViewById(R.id.partnerhome_usersubcateg);
                holder.textbookingid=(TextView)convertView.findViewById(R.id.partner_bookingid);
                holder.butrejectbut=convertView.findViewById(R.id.partneraccrej_rejectbut);
                holder.butaccept=convertView.findViewById(R.id.partneraccrej_acceptbut);

                convertView.setTag(holder);
            }//ino
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            home_accerejjobs ccitacc = movieModelList.get(position);
            holder.textone.setText(ccitacc.getService_subcateg_name());
            holder.textthree.setText(ccitacc.getUser_name());
            holder.textbookingid.setText(ccitacc.getBooking_uid());
                holder.butaccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String getmid=holder.textbookingid.getText().toString();
                        acceptmeupdate(getmid);
                        startActivity(new Intent(getActivity(),CategoryMain.class));
                    }
                });

                holder.butrejectbut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         getmyrejectid=holder.textbookingid.getText().toString();

                        CreateAlertDialogWithRadioButtonGroup() ;

                    }
                });

            return convertView;
        }

        class ViewHolder {
            public TextView textone,textthree,textbookingid;
            public Button butaccept,butrejectbut;

        }
    }

    public class kilomilo extends AsyncTask<String, String, List<home_accerejjobs>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<home_accerejjobs> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finalJson = buffer.toString();
                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("result");
                List<home_accerejjobs> milokilo = new ArrayList<>();
                Gson gson = new Gson();
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    home_accerejjobs catego = gson.fromJson(finalObject.toString(), home_accerejjobs.class);
                    milokilo.add(catego);
                }
                return milokilo;
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<home_accerejjobs> movieMode) {
            super.onPostExecute(movieMode);
            dialog.dismiss();
            if (movieMode== null)
            {
                Toast.makeText(getActivity(),"No Services available for your selection", Toast.LENGTH_SHORT).show();

            }
            else
            {
                MovieAdap adapter = new MovieAdap(getActivity(), R.layout.home_accerejjobs, movieMode);
                partnerhome_listview.setAdapter(adapter);
//                partnerhome_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        home_accerejjobs item = movieMode.get(position);
//                        Intent intent = new Intent(CategoryMain.this,AcceptReject.class);
//                        intent.putExtra("partnerhome_bookingid",item.getBooking_uid());
//                        intent.putExtra("partnerhome_subcategname",item.getService_subcateg_name());
//                        intent.putExtra("partnerhome_username",item.getUser_name());
//                        startActivity(intent);
//                    }
//                });
                adapter.notifyDataSetChanged();
            }
        }
    }


    public void acceptmeupdate(final String s1) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalUrl.partner_updateaccept, new Response.Listener<String>() {
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

                params.put("booking_uid",s1);


                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
    public void CreateAlertDialogWithRadioButtonGroup(){


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Select Your Reason for Rejection");

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        String case0="I am on other Project";
                        Toast.makeText(getActivity(), case0, Toast.LENGTH_LONG).show();
                        rejectmeupdate(getmyrejectid,case0);
                        startActivity(new Intent(getActivity(),CategoryMain.class));

                        break;
                    case 1:
                        String case1="I cant do the Service right now";
                        Toast.makeText(getActivity(), case1, Toast.LENGTH_LONG).show();
                        rejectmeupdate(getmyrejectid,case1);
                        startActivity(new Intent(getActivity(),CategoryMain.class));

                        break;
                    case 2:

                        Toast.makeText(getActivity(), "Third Item Clicked", Toast.LENGTH_LONG).show();
                        String case2="Its not my Requirement";
                        rejectmeupdate(getmyrejectid,case2);
                        startActivity(new Intent(getActivity(),CategoryMain.class));

                        break;
                    case 3:

                        Toast.makeText(getActivity(), "FOur Item Clicked", Toast.LENGTH_LONG).show();
                        String case3="I am out of Station";
                        rejectmeupdate(getmyrejectid,case3);
                        startActivity(new Intent(getActivity(),CategoryMain.class));

                        break;
                    case 4:

                        Toast.makeText(getActivity(), "Five Item Clicked", Toast.LENGTH_LONG).show();
                        String case4="My reason is not listed";
                        rejectmeupdate(getmyrejectid,case4);
                        startActivity(new Intent(getActivity(),CategoryMain.class));

                        break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();

    }
    public void rejectmeupdate(final String s1,final String s2) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalUrl.getPartner_updatereject, new Response.Listener<String>() {
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

                params.put("booking_uid",s1);
                params.put("service_partner_rejectedreason",s2);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
