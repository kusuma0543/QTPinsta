package com.ringaapp.ringapartner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class OnGoingPartJobs extends Fragment {

    String ongoingjobspartuid;
    private ListView partnerhome_listview;
    private ProgressDialog dialog;

    private SessionManager session;
    private SQLiteHandler db;

    public static OnGoingPartJobs newInstance() {
        OnGoingPartJobs fragment= new OnGoingPartJobs();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
View view=inflater.inflate(R.layout.fragment_on_going_part_jobs, container, false);
        session = new SessionManager(getActivity());
        db = new SQLiteHandler(getActivity());

        final HashMap<String, String> user = db.getUserDetails();
        ongoingjobspartuid=user.get("uid");

        dialog=new ProgressDialog(getActivity());
        dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");
        partnerhome_listview=view.findViewById(R.id.partnerongoingjobs_listview);
        String URLL = GlobalUrl.partner_ongoingjobs+"?partner_uid="+ongoingjobspartuid;
        new kilomilo().execute(URLL);
        return view;
    }
    public class MovieAdap extends ArrayAdapter {
        private List<home_accerejjobss> movieModelList;
        private int resource;
        Context context;
        private LayoutInflater inflater;

        MovieAdap(Context context, int resource, List<home_accerejjobss> objects) {
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
            final MovieAdap.ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(resource, null);
                holder = new MovieAdap.ViewHolder();

                holder.textone = (TextView) convertView.findViewById(R.id.partnerhome_usernames);
                holder.textthree = (TextView)convertView.findViewById(R.id.partnerhome_usersubcategs);
                holder.textfour = (TextView)convertView.findViewById(R.id.partnerhome_useraddresss);

                convertView.setTag(holder);
            }//ino
            else {
                holder = (MovieAdap.ViewHolder) convertView.getTag();
            }
            home_accerejjobss ccitacc = movieModelList.get(position);
            holder.textthree.setText(ccitacc.getUser_name());
            holder.textone.setText(ccitacc.getService_subcateg_name());
            holder.textfour.setText(ccitacc.getService_booking_address());
            return convertView;
        }

        class ViewHolder {
            public TextView textone,textthree,textfour;

        }
    }

    public class kilomilo extends AsyncTask<String, String, List<home_accerejjobss>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<home_accerejjobss> doInBackground(String... params) {
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
                JSONArray parentArray = parentObject.getJSONArray("ongoing");
                List<home_accerejjobss> milokilo = new ArrayList<>();
                Gson gson = new Gson();
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    home_accerejjobss catego = gson.fromJson(finalObject.toString(), home_accerejjobss.class);
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
        protected void onPostExecute(final List<home_accerejjobss> movieMode) {
            super.onPostExecute(movieMode);
            dialog.dismiss();
            if (movieMode== null)
            {
                Toast.makeText(getActivity(),"No Services available for your selection", Toast.LENGTH_SHORT).show();

            }
            else
            {
                MovieAdap adapter = new MovieAdap(getActivity(), R.layout.ongoingjibs, movieMode);
                partnerhome_listview.setAdapter(adapter);
                partnerhome_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        home_accerejjobss item = movieMode.get(position);
                        Intent intent = new Intent(getActivity(),ServiceTracking.class);
                        intent.putExtra("partnerhome_bookingid",item.getBooking_uid());
                        intent.putExtra("partnerhome_subcategname",item.getService_subcateg_name());
                        intent.putExtra("partnerhome_username",item.getUser_name());
                        intent.putExtra("partnerhome_usermobile",item.getUser_mobile_number());
                        intent.putExtra("partnerhome_usermail",item.getUser_email());
                        intent.putExtra("partnerhome_address",item.getService_booking_address());
                        String intentm="checkintentfromgoing";
                        intent.putExtra("check",intentm);

                        startActivity(intent);
                    }
                });
                adapter.notifyDataSetChanged();
            }
        }
    }

}
