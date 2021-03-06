package com.ringaapp.ringapartner;

import android.*;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.ringaapp.ringapartner.dbhandlers.SQLiteHandler;
import com.ringaapp.ringapartner.dbhandlers.SessionManager;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.sql.Types.NULL;

public class HomeScreen extends AppCompatActivity implements View.OnClickListener{
private TextView docv_imagesel;
private ImageView docv_itemsel;
    String hsleradio;

    private RadioGroup shome_groupone;
    private RadioButton shome_oneradio,shom_tworadio;


    public static final String UPLOAD_KEY = "partner_images";
    public static final String UPLOAD_KEYTWO="partner_uid";
    private int PICK_IMAGE_REQUEST = 11;
    private int PICK_PDF_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private Bitmap bitmap;
    private Uri filePath;
    private String uidimagex;
    private GridView linearLayout;
    EditText mEdit;
    String sleradio;
    private ProgressDialog dialog;
    private Button butallupload;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Documents Verification");

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        dialog = new ProgressDialog(this);
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");
        Intent intent=getIntent();
        uidimagex=intent.getStringExtra("uidimagex");
      //  x="5a2799e95c05f9.57886214";

//        final HashMap<String, String> user = db.getUserDetails();
//        uidimagex=user.get("uid");

      Toast.makeText(this, uidimagex, Toast.LENGTH_SHORT).show();
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
       linearLayout=findViewById(R.id.listview);
        mEdit = findViewById(R.id.getcharge);

        shome_groupone=(RadioGroup) findViewById(R.id.shome_radioone);

      // listdoc=findViewById(R.id.listviewk);
        docv_imagesel= findViewById(R.id.docv_imagesel);
        docv_itemsel=findViewById(R.id.docv_itemsel);

        butallupload=findViewById(R.id.alluploadbut);
        shom_tworadio=findViewById(R.id.sradio_two);
        shome_oneradio=findViewById(R.id.sradio_one);

        docv_imagesel.setOnClickListener(this);
       docv_itemsel.setOnClickListener(this);
        butallupload.setOnClickListener(this);
//        shome_oneradio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sleradio = "Free";
//                butallupload.setVisibility(View.VISIBLE);
//
//            }
//        });
//        shom_tworadio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sleradio = mEdit.getText().toString();
//            butallupload.setVisibility(View.VISIBLE);
//            }
//        });
//        mEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEdit.setSelection(0);
//                butallupload.setVisibility(View.VISIBLE);
//
//            }
//        });
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(PICK_IMAGE_REQUEST==11)
        {
             if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

                filePath = data.getData();
                 try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                         uploadImage();
                            new JSONTask().execute(GlobalUrl.partner_imageret+"?"+UPLOAD_KEYTWO+"="+uidimagex);

                    } catch (IOException e) {
                            e.printStackTrace();
                    }
             }
//             else if(PICK_PDF_REQUEST==1)
//             {
//                  if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//                         filePath = data.getData();
//                         uploadMultipart();
//                      Toast.makeText(this, "Document Uploaded Successfully", Toast.LENGTH_SHORT).show();
//                      new JSONTasks().execute(GlobalUrl.partner_docret+"?"+UPLOAD_KEYTWO+"="+uidimage);
//
//                  }
//
//             }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    private void uploadImage(){
        class UploadImage extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(HomeScreen.this, "Uploading Image", "Please wait...",true,true);
                mEdit.setVisibility(View.VISIBLE);

            }

            @Override
            protected void onPostExecute(String s) {

                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                HashMap<String,String> data = new HashMap<>();
                data.put(UPLOAD_KEYTWO,uidimagex);
                data.put(UPLOAD_KEY, uploadImage);


                String result = rh.sendPostRequest(GlobalUrl.partner_imageupload,data);

                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }

    @Override
    public void onClick(View v) {
        if (v == docv_imagesel) {
            showFileChooser();
        }
        if(v==docv_itemsel)
        {
            showFileChooser();
        }
//        if (v == docv_docsel) {
//            showFileChoosers();
//        }
        if(v==butallupload)
//        { int selectedId = shome_groupone.getCheckedRadioButtonId();
//            shome_oneradio =  findViewById(selectedId);
//            if (shome_oneradio.getText().toString().equals("Free")) {
//                sleradio = "Free";
//            }
//            if (shome_oneradio.getText().toString().equals("Type Your amount")) {
//                sleradio = mEdit.getText().toString();
//            }


        {
            int selectedId = shome_groupone.getCheckedRadioButtonId();

            shome_oneradio =  findViewById(selectedId);

            sleradio=shome_oneradio.getText().toString();

                if(sleradio.equals("Free"))
                {
                    hsleradio="Free";
                    callmetoupload();

                }
                else if(sleradio.equals("Custom Charge"))
                {

                    hsleradio=mEdit.getText().toString();

                        callmetoupload();


                }
                else if(sleradio.equals(""))
                {

                    Toast.makeText(this, "please enter value between 1-1000", Toast.LENGTH_SHORT).show();


                }










//
       }

    }
    public void callmetoupload()
    {
        uploadbf(uidimagex,hsleradio);
        Toast.makeText(this, sleradio, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(HomeScreen.this,DocVerification.class));

    }
//    private void showFileChoosers() {
//        Intent intent = new Intent();
//        intent.setType("application/pdf");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_REQUEST);
//    }
//
//
//
//    private void requestStoragePermission() {
//        if (ContextCompat.checkSelfPermission(this,     android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
//            return;
//
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this,    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
//        }
//
//        ActivityCompat.requestPermissions(this, new String[]{    android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        if (requestCode == STORAGE_PERMISSION_CODE) {
//
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//    public void uploadMultipart() {
//       // String name = editText.getText().toString().trim();
//        String path = FilePath.getPath(this, filePath);
//
//        if (path == null) {
//
//            Toast.makeText(this, "Please move your .pdf file to internal storage and retry", Toast.LENGTH_LONG).show();
//        } else {
//
//            try {
//                String uploadId = UUID.randomUUID().toString();
//
//                //Creating a multi part request
//                new MultipartUploadRequest(this, uploadId, GlobalUrl.partner_docv_upload)
//                        .addFileToUpload(path, "file") //Adding file
//                        .addParameter("name", uidimage) //Adding text parameter to the request
//                        .setNotificationConfig(new UploadNotificationConfig())
//                        .setMaxRetries(2)
//                        .startUpload(); //Starting the upload
//            } catch (Exception exc) {
//                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    public class JSONTask extends AsyncTask<String,String, List<Imageret>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();

        }
        @Override
        protected List<Imageret> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line ="";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }
                String finalJson = buffer.toString();
                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("result");
                List<Imageret> movieModelList = new ArrayList<>();
                Gson gson = new Gson();
                for(int i=0; i<parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);

                    Imageret categorieslist = gson.fromJson(finalObject.toString(),Imageret.class);
                    movieModelList.add(categorieslist);


                }
                return movieModelList;
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return  null;
        }
        @Override
        protected void onPostExecute(final List<Imageret> movieModelList) {
            super.onPostExecute(movieModelList);
            dialog.dismiss();
            if(movieModelList != null) {
                MovieAdapter adapter = new MovieAdapter(getApplicationContext(), R.layout.imageret, movieModelList);
                LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getApplicationContext());
                MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
              linearLayout.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            else {
                Toast.makeText(getApplicationContext(),"Check your internet connection",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class MovieAdapter extends ArrayAdapter {
        private List<Imageret> movieModelList;
        private int resource;
        Context context;
        private LayoutInflater inflater;
        MovieAdapter(Context context, int resource, List<Imageret> objects) {
            super(context, resource, objects);
            movieModelList = objects;
            this.context =context;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
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
            final ViewHolder holder  ;
            if(convertView == null){
                convertView = inflater.inflate(resource,null);
                holder = new ViewHolder();
                holder.menuimage = (ImageView)convertView.findViewById(R.id.docv_imagedissi);


                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            Imageret categorieslist= movieModelList.get(position);
            Picasso.with(context).load(categorieslist.getPartner_images()).fit().error(R.drawable.phone_otp).fit().into(holder.menuimage);


            return convertView;
        }
        class ViewHolder{
            private ImageView menuimage;

        }
    }





//    public class JSONTasks extends AsyncTask<String,String, List<docret>> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            dialog.show();
//
//        }
//        @Override
//        protected List<docret> doInBackground(String... params) {
//            HttpURLConnection connection = null;
//            BufferedReader reader = null;
//            try {
//                URL url = new URL(params[0]);
//
//                connection = (HttpURLConnection) url.openConnection();
//                connection.connect();
//                InputStream stream = connection.getInputStream();
//                reader = new BufferedReader(new InputStreamReader(stream));
//                StringBuilder buffer = new StringBuilder();
//                String line ="";
//                while ((line = reader.readLine()) != null){
//                    buffer.append(line);
//                }
//                String finalJson = buffer.toString();
//                JSONObject parentObject = new JSONObject(finalJson);
//                JSONArray parentArray = parentObject.getJSONArray("result");
//                List<docret> movieModelList = new ArrayList<>();
//                Gson gson = new Gson();
//                for(int i=0; i<parentArray.length(); i++) {
//                    JSONObject finalObject = parentArray.getJSONObject(i);
//
//                    docret categorieslist = gson.fromJson(finalObject.toString(),docret.class);
//                    movieModelList.add(categorieslist);
//
//
//                }
//                return movieModelList;
//            } catch (JSONException | IOException e) {
//                e.printStackTrace();
//            } finally {
//                if(connection != null) {
//                    connection.disconnect();
//                }
//                try {
//                    if(reader != null) {
//                        reader.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            return  null;
//        }
//        @Override
//        protected void onPostExecute(final List<docret> movieModelList) {
//            super.onPostExecute(movieModelList);
//            dialog.dismiss();
//            if(movieModelList != null) {
//                MovieAdapters adapter = new MovieAdapters(getApplicationContext(), R.layout.docret, movieModelList);
//                listdoc.setVisibility(View.VISIBLE);
//                listdoc.setAdapter(adapter);
//                butallupload.setVisibility(View.VISIBLE);
//                adapter.notifyDataSetChanged();
//                listdoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        docret item = movieModelList.get(position);
//                        String gg=item.getPartner_documents();
//
//                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ gg);
//                        Intent target = new Intent(Intent.ACTION_VIEW);
//                        target.setDataAndType(Uri.fromFile(file),"application/pdf");
//                        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//
//                        Intent intent = Intent.createChooser(target, "Open File");
//                        try {
//                            startActivity(intent);
//                        } catch (ActivityNotFoundException e) {
//                            // Instruct the user to install a PDF reader here, or something
//                        }
//                    }
//                });
//            }
//            else {
//                Toast.makeText(getApplicationContext(),"Check your internet connection",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    public class MovieAdapters extends ArrayAdapter {
//        private List<docret> movieModelList;
//        private int resource;
//        Context context;
//        private LayoutInflater inflater;
//        MovieAdapters(Context context, int resource, List<docret> objects) {
//            super(context, resource, objects);
//            movieModelList = objects;
//            this.context =context;
//            this.resource = resource;
//            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//        }
//        @Override
//        public int getViewTypeCount() {
//            return 1;
//        }
//        @Override
//        public int getItemViewType(int position) {
//            return position;
//        }
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            final ViewHolder holder  ;
//            if(convertView == null){
//                convertView = inflater.inflate(resource,null);
//                holder = new ViewHolder();
//                holder.menuimage = convertView.findViewById(R.id.texttopic);
//
//
//
//                convertView.setTag(holder);
//            }
//            else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//            docret categorieslist= movieModelList.get(position);
//            //Picasso.with(context).load(categorieslist.getPartner_documentname()).fit().error(R.drawable.texttopic).fit().into(holder.menuimage);
//            holder.menuimage.setText(categorieslist.getPartner_documentname());
//            return convertView;
//        }
//        class ViewHolder{
//            private TextView menuimage;
//
//        }
//    }

    public void uploadbf(final String sdpartneruid, final String sdpartnerbudeget) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalUrl.partner_allbudfeadet, new Response.Listener<String>() {
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
                params.put("partner_uid", sdpartneruid);
                params.put("partner_budget", sdpartnerbudeget);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}


