package com.example.aunnie_iw.ntcardreader;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.R.attr.bitmap;

/**
 * Created by Aunnie-IW on 12/6/2560.
 */

public class LocationCard extends AppCompatActivity implements View.OnClickListener {
    private ImageView viewImage;
    private Button BSelectPhoto;
    public static final int MY_PERMISSIONS_REQUEST_STORED = 90;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 98;
    private Bitmap ImgLocationCard;
    public static final int REQUEST_GALLERY = 1;
    public static final int REQUEST_CAMERA = 2;
    public static final int REQUEST_ADDRESS = 3;
    private Uri uri;
    private File f;
    private StorageReference folderRef, imageRef;
    private TextView SLatLng;
    private Double latitude;
    private Double longitude;
    private People people;
    private AddressData addressData;
    private EditText EHouseNumber, EMoo,ESoi,ERoad,EPostcode,ELandmark,EPhotourl;
    private Spinner mProvince,mAmphur,mTambon;

    private String[] Province,Amphur,Tambon,ProvinceId,AmphurId,TambonId;
    private JSONArray JAProvince,JAAmphur,JATambon,JAProvinceId,JAAmphurId,JATambonId;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;

    private String PathImgLocationCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.LocationCard);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addressData = new AddressData();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//        folderRef = storageRef.child("photos");
//        imageRef = folderRef.child("firebase.png");

        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);

/*------------------- intent ข้อมูล --------------------------------------------------------------------------------------------------*/
        Intent intent = getIntent();
        people = (People) intent.getExtras().getSerializable("data");

        Log.d(people.getProfileData().getPrefixThai(), "LocationCard: ");
        Log.d(people.getProfileData().getFirstNameThai(), "LocationCard: ");
        Log.d(people.getProfileData().getLastNameThai(), "LocationCard: ");
        Log.d(people.getProfileData().getBirthday(), "LocationCard: ");
        Log.d(people.getProfileData().getMarriage(), "LocationCard: ");
        Log.d(people.getProfileData().getSex(), "LocationCard: ");
        Log.d(people.getProfileData().getBloodType(), "LocationCard: ");
        Log.d(people.getProfileData().getReligion(), "LocationCard: ");
        Log.d(people.getProfileData().getTell(), "LocationCard: ");
        Log.d(people.getProfileData().getHometell(), "LocationCard: ");
        Log.d(people.getProfileData().getDisease(), "LocationCard: ");
        Log.d(people.getProfileData().getAllergy(), "LocationCard: ");
        Log.d(people.getProfileData().getHospitalNear(), "LocationCard: ");
        Log.d(people.getProfileData().getHospitalUse(), "LocationCard: ");
        Log.d(people.getProfileData().getAlive(), "LocationCard: ");


/*------------------- Photo--------------------------------------------------*/

        BSelectPhoto = (Button) findViewById(R.id.BSelectPhoto);
        viewImage = (ImageView) findViewById(R.id.viewImage);
        BSelectPhoto.setOnClickListener(LocationCard.this);

        /*------------------- TextView Next--------------------------------------------------------------------------------------------------*/
        TextView Next = (TextView) findViewById(R.id.Next);
        Next.setOnClickListener(LocationCard.this);
            /*------------------- Button BLatLng --------------------------------------------------------------------------------------------------*/
        Button BLatLng = (Button) findViewById(R.id.BLatLng);
        BLatLng.setOnClickListener(LocationCard.this);


        /*------------------- TextView ------------------------------------*/

        EHouseNumber = (EditText) findViewById(R.id.EHouseNumber);
        if(people.getAddressCard() !=null && people.getAddressCard().getHouseNumber()!=null)
            EHouseNumber.setText(people.getAddressCard().getHouseNumber());

        EMoo = (EditText) findViewById(R.id.EMoo);
        if(people.getAddressCard() !=null && people.getAddressCard().getMoo()!=null)
            EMoo.setText(people.getAddressCard().getMoo());

        ESoi = (EditText) findViewById(R.id.ESoi);
        if(people.getAddressCard() !=null && people.getAddressCard().getSoi()!=null)
            ESoi.setText(people.getAddressCard().getSoi());

        ERoad = (EditText) findViewById(R.id.ERoad);
        if(people.getAddressCard() !=null && people.getAddressCard().getRoad()!=null)
            ERoad.setText(people.getAddressCard().getRoad());

        EPostcode = (EditText) findViewById(R.id.EPostCode);
        if(people.getAddressCard() !=null && people.getAddressCard().getPostcode()!=null)
            EPostcode.setText(people.getAddressCard().getPostcode());

        ELandmark = (EditText) findViewById(R.id.ELanmark);
        if(people.getAddressCard() !=null && people.getAddressCard().getLandmark()!=null)
            ELandmark.setText(people.getAddressCard().getLandmark());

        SLatLng = (TextView) findViewById(R.id.SLatLng);
        if(people.getAddressCard() !=null && people.getAddressCard().getLatitude()!=null&&people.getAddressCard().getLongitude()!=null)
            SLatLng.setText("( " + String.valueOf(people.getAddressCard().getLatitude()) +", "+String.valueOf(people.getAddressCard().getLongitude() +" )"));
//
        folderRef = storageRef.child("photos");
//        imageRef = folderRef.child("firebase.png");

        //StorageReference storageRef = storage.getReference();
        imageRef = folderRef.child(people.getProfileData().getCitizenID() +"_LocationCard.jpg");
        downloadInMemory();
        /*------------------- Spinner Province--------------------------------------------------------------------------------------------------*/
        mProvince = (Spinner) findViewById(R.id.Province);

        try {
            JAProvince = new FeedTask().execute("dataprovince").get();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error");
        }

        List<String> listName = new ArrayList<String>();
        List<String> listId = new ArrayList<String>();
        System.out.println(JAProvince.toString());
        for (int i = 0; i < JAProvince.length(); i++) {
            try {
                listName.add(JAProvince.getJSONObject(i).getString("province_name"));
                listId.add(JAProvince.getJSONObject(i).getString("province_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Province = new String[listName.size()];
        ProvinceId = new String[listId.size()];
        Province = listName.toArray(Province);
        ProvinceId = listId.toArray(ProvinceId);
        for (String s : Province)
            System.out.println(s);
        for (String s : ProvinceId)
            System.out.println(s);

        for (int i = 0; i <= Province.length - 1; i++) {
            System.out.println(Province[i] + "+++++++++");
        }
        Province = Arrays.copyOf(Province, Province.length + 1);
        Province[Province.length - 1] = "--เลือกจังหวัด--";



        ArrayAdapter<String> adapterProvince = new ArrayAdapter<String>(LocationCard.this, android.R.layout.simple_spinner_dropdown_item, Province) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(getItem(getCount()), "getView: ");
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText(getItem(getCount()));
                    ((TextView) v.findViewById(android.R.id.text1)).setTextSize(14);
                    //((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1; // you dont display last item. It is used as hint.
            }
        };
        adapterProvince.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mProvince.setAdapter(adapterProvince);
        int indexProvince;
        if(people.getAddressCard() != null && people.getAddressCard().getProvince()!=null){
            //Province[Province.length-1] = people.getAddressCard().getProvince();
            indexProvince = Arrays.asList(Province).indexOf(people.getAddressCard().getProvince());
            mProvince.setSelection(indexProvince);
        }
        else {
            mProvince.setSelection(adapterProvince.getCount());
        }
        mProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l) {
                TextView myText = (TextView) v;
                Toast.makeText(LocationCard.this, "You Selected " + myText.getText(), Toast.LENGTH_SHORT).show();
                ((TextView) adapterView.getChildAt(0)).setTextSize(14);
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLUE);

                Log.d(String.valueOf(i), "onItemSelected: ");
                String id;
                if ( i!= ProvinceId.length  ) {
                    id = ProvinceId[i];
                    try {
                        JAAmphur = new FeedTask().execute("dataamphur", String.valueOf(id)).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error");
                    }
                    List<String> listName = new ArrayList<String>();
                    List<String> listId = new ArrayList<String>();
                    System.out.println(JAAmphur.toString());
                    for (i = 0; i < JAAmphur.length(); i++) {
                        try {
                            listName.add(JAAmphur.getJSONObject(i).getString("amphur_name"));
                            listId.add(JAAmphur.getJSONObject(i).getString("amphur_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Amphur = new String[listName.size()];
                    AmphurId = new String[listId.size()];
                    Amphur = listName.toArray(Amphur);
                    AmphurId = listId.toArray(AmphurId);
                    for (String s : Amphur)
                        System.out.println(s);
                    for (String s : AmphurId)
                        System.out.println(s);

                    for (int j = 0; j <= Amphur.length - 1; j++) {
                        System.out.println(Amphur[j] + "+++++++++");
                    }

                    Amphur = Arrays.copyOf(Amphur, Amphur.length + 1);
                    Amphur[Amphur.length - 1] = "--เลือกอำเภอ--";



                }
                else {
                    id = "0";
                    Amphur = new String[2];
                    Amphur[0] = "--เลือกอำเภอ--";
                    Amphur[1] = "--เลือกอำเภอ--";


                }
                spinnerAmphur(Amphur, id);
                Log.d(String.valueOf(id), "onItemSelected: Amphur");
                Log.d(String.valueOf(Amphur[0]), "onItemSelected: Amphur");
            }



            // end onItemSelected method

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(LocationCard.this, "You Must Buy-In To Play", Toast.LENGTH_SHORT).show();
            } // end onNothingSelected method
        });
    }

    public void spinnerAmphur(final String[] amphur, final String province_id){
        Log.d(amphur[0], "spinnerAmphur: AAAAAA");
        mAmphur = (Spinner) findViewById(R.id.Amphur);

        ArrayAdapter<String> adapterAmphur = new ArrayAdapter<String>(LocationCard.this, android.R.layout.simple_spinner_dropdown_item,amphur){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(getItem(getCount()), "getView: ");
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText(getItem(getCount()));
                    ((TextView)v.findViewById(android.R.id.text1)).setTextSize(14);
                    Log.d(getItem(getCount()), "getView: ");
                    //((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }
            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }
        };
        adapterAmphur.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAmphur.setAdapter(adapterAmphur);
        int indexAmphur;
        if(people.getAddressCard() != null && people.getAddressCard().getAmphur()!=null){
            //Amphur[Amphur.length-1] = people.getAddressCard().getAmphur();
            indexAmphur = Arrays.asList(Amphur).indexOf(people.getAddressCard().getAmphur());
            mAmphur.setSelection(indexAmphur);
        }
        else {
            mAmphur.setSelection(adapterAmphur.getCount());
        }
        mAmphur.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l) {
                TextView myText = (TextView) v;
                Toast.makeText(LocationCard.this, "You Selected " + myText.getText(), Toast.LENGTH_SHORT).show();
                ((TextView) adapterView.getChildAt(0)).setTextSize(14);
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLUE);


                Log.d(String.valueOf(i), "onItemSelected: Tamboniiii");
                String id ;
                if ( i!= Amphur.length-1 && !Amphur[1].equals("--เลือกอำเภอ--") ) {
                    for(String s: Amphur)
                        System.out.println(s);
                    Log.d(String.valueOf(Amphur.length), "onItemSelected: Amphur.length");
                    id = AmphurId[i];
                    try {
                        JATambon = new FeedTask().execute("datadistrict", province_id, id).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error");
                    }

                    List<String> listName = new ArrayList<String>();
                    List<String> listId = new ArrayList<String>();
                    System.out.println(JATambon.toString());
                    for (i = 0; i < JATambon.length(); i++) {
                        try {
                            listName.add(JATambon.getJSONObject(i).getString("district_name"));
                            listId.add(JATambon.getJSONObject(i).getString("district_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Tambon = new String[listName.size()];
                        TambonId = new String[listId.size()];
                        Tambon = listName.toArray(Tambon);
                        TambonId = listId.toArray(TambonId);
                        for (String s : Tambon)
                            System.out.println(s);
                        for (String s : TambonId)
                            System.out.println(s);
                        for (int j = 0; j <= Tambon.length - 1; j++) {
                            System.out.println(Tambon[j] + "+++++++++");
                        }
                        Tambon = Arrays.copyOf(Tambon, Tambon.length + 1);
                        Tambon[Tambon.length - 1] = "--เลือกตำบล--";
                    }
                }
                else {
                    id = "0";
                    Tambon = new String[2];
                    Tambon[0] = "--เลือกตำบล--";
                    Tambon[1] = "--เลือกตำบล--";
                }
                //spinnerAmphur(Tambon, id);
                Log.d(String.valueOf(id), "onItemSelected: Tambon");


                Log.d(String.valueOf(i), "onItemSelected: Tamboniiii");

                spinnerTambon(Tambon);
            } // end onItemSelected method
            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                Toast.makeText(LocationCard.this, "You Must Buy-In To Play", Toast.LENGTH_SHORT).show();
            } // end onNothingSelected method
        });
    }
    public void spinnerTambon(String[] Tambon){

        mTambon = (Spinner) findViewById(R.id.Tambon);
        ArrayAdapter<String> adapterTambon = new ArrayAdapter<String>(LocationCard.this, android.R.layout.simple_spinner_dropdown_item,Tambon){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText(getItem(getCount()));
                    ((TextView)v.findViewById(android.R.id.text1)).setTextSize(14);
                    Log.d(getItem(getCount()), "getView: ");
                    //((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }
            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }
        };
        adapterTambon.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTambon.setAdapter(adapterTambon);
        int indexTambon;
        if(people.getAddressCard() != null && people.getAddressCard().getAmphur()!=null){
            //Amphur[Amphur.length-1] = people.getAddressCard().getAmphur();
            indexTambon = Arrays.asList(Tambon).indexOf(people.getAddressCard().getTambon());
            mTambon.setSelection(indexTambon);
        }
        else {
            mTambon.setSelection(adapterTambon.getCount());
        }
        mTambon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l)
            {

                TextView myText = (TextView) v;
                Toast.makeText(LocationCard.this, "You Selected "+myText.getText(), Toast.LENGTH_SHORT).show();
                ((TextView) adapterView.getChildAt(0)).setTextSize(14);
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLUE);
            } // end onItemSelected method
            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                Toast.makeText(LocationCard.this, "You Must Buy-In To Play", Toast.LENGTH_SHORT).show();
            } // end onNothingSelected method
        });
  }

    public void onClick(View view) {
        //String Uid = mUser.getUid();
        switch (view.getId()) {
            case R.id.BLatLng:
                Intent intent = new Intent(LocationCard.this, LocationMap.class);
                //intent.putExtra("data", cardFirebase);
                startActivityForResult(intent, REQUEST_ADDRESS);

                break;

            case R.id.Next:
                Intent intent2 = new Intent(LocationCard.this, LocationNow.class);
                //intent.putExtra("data", cardFirebase);

                addressData.setHouseNumber(EHouseNumber.getText().toString());
                addressData.setMoo(EMoo.getText().toString());
                addressData.setSoi(ESoi.getText().toString());
                addressData.setRoad(ERoad.getText().toString());

                addressData.setProvince(mProvince.getSelectedItem().toString());
                addressData.setAmphur(mAmphur.getSelectedItem().toString());
                addressData.setTambon(mTambon.getSelectedItem().toString());

                addressData.setPostcode(EPostcode.getText().toString());

                addressData.setLandmark(ELandmark.getText().toString());
                addressData.setLatitude(latitude);
                addressData.setLongitude(longitude);
                people.setAddressCard(addressData);
                //Convert to byte array



                Log.d(people.getAddressCard().getHouseNumber(), "LocationCard: ");
                Log.d(people.getAddressCard().getMoo(), "LocationCard: ");
                Log.d(people.getAddressCard().getSoi(), "LocationCard: ");
                Log.d(people.getAddressCard().getRoad(), "LocationCard: ");
                Log.d(people.getAddressCard().getPostcode(), "LocationCard: ");
                Log.d(people.getAddressCard().getLandmark(), "LocationCard: ");
                //Log.d(people.getAddressCard().getLatitude().toString(), "LocationCard: ");
                //Log.d(people.getAddressCard().getLongitude().toString(), "LocationCard: ");
                //uploadFromFile(uri.getPath());
                intent2.putExtra("data", people);

                intent2.putExtra("PathImgLocationCard",PathImgLocationCard);

                startActivity(intent2);

                break;
            case R.id.BSelectPhoto:
                selectImage();
                break;

        }
    }

    private void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(LocationCard.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))

                {/*------------------- Permission --------------------------------------*/


                    if (ContextCompat.checkSelfPermission(LocationCard.this,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {

                        checkCameraPermission();
                    }
                    else {


                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        String timeStamp =
                                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "IMG_" + timeStamp + ".jpg";
                        f = new File(Environment.getExternalStorageDirectory()
                                , "DCIM/Camera/" + imageFileName);
                        uri = Uri.fromFile(f);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(intent, 2);
                    }
                }
                else if (options[item].equals("Choose from Gallery"))
                {

                    /*------------------- Permission --------------------------------------*/
                    if (ContextCompat.checkSelfPermission(LocationCard.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        checkWRITE_EXTERNAL_STORAGEPermission();
                    } else {

//                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.setType("image/*");
//                    startActivityForResult(Intent.createChooser(intent
//                            , "Select photo from"), 1);
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 1);
                }
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("onActivityResult", "onActivityResult: ");
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            uri = data.getData();

            PathImgLocationCard = Helper.getPath(this, Uri.parse(data.getData().toString()));
            //uploadFromFile(PathImgLocationCard);
            //File imageFile = new File(getRealPathFromURI(uri));
            Log.d(uri.toString(), "onActivityResult: ");
            Log.d(uri.getPath(), "onActivityResult: ");
            Log.d(PathImgLocationCard, "onActivityResult: ");
            try {
                ImgLocationCard = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                ImgLocationCard= RotateImg.Rotate(PathImgLocationCard,ImgLocationCard);
                Log.d("bitmap", "onActivityResult: ");
                //uploadFromDataInMemory(bitmap);
                viewImage.setImageBitmap(ImgLocationCard);
            } catch (FileNotFoundException e) {
                Log.d("FileNotFoundException", "onActivityResult: ");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("IOException", "onActivityResult: ");
                e.printStackTrace();
            }
        }
        else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK){

            getContentResolver().notifyChange(uri, null);
            ContentResolver cr = getContentResolver();


            try {

                ImgLocationCard = MediaStore.Images.Media.getBitmap(cr, uri);
                ImgLocationCard = RotateImg.Rotate(uri.getPath(),ImgLocationCard);
                //Log.d(f.getPath(), "onActivityResult: Path");
                PathImgLocationCard = uri.getPath();
                viewImage.setImageBitmap(ImgLocationCard);
                Toast.makeText(getApplicationContext()
                        , uri.getPath(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == REQUEST_ADDRESS && resultCode == RESULT_OK) {
                String address = data.getStringExtra("address");
                latitude = data.getExtras().getDouble("latitude");
                longitude = data.getExtras().getDouble("longitude");
                Log.d("latitude",Double.toString(latitude));
                Log.d("longitude",Double.toString(longitude));
                SLatLng.setText(address + " (" + latitude + " , " + longitude + " )");

        }
    }
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public boolean checkWRITE_EXTERNAL_STORAGEPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                AlertDialog.Builder builder = new AlertDialog.Builder(LocationCard.this);
                builder.setTitle("Need STORAGE Permission");
                builder.setMessage("This app needs WRITE_EXTERNAL_STORAGE permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(LocationCard.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_STORED);

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();


            }else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(LocationCard.this);
                builder.setTitle("Need STORAGE Permission");
                builder.setMessage("This app needs WRITE_EXTERNAL_STORAGE permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
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
            else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORED);
            }
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,true);
            editor.commit();
            return false;

        } else {
            return true;
        }
    }
    public boolean checkCameraPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(LocationCard.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(LocationCard.this,
                                new String[]{android.Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();


            }else if (permissionStatus.getBoolean(Manifest.permission.CAMERA,false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(LocationCard.this);
                builder.setTitle("Need Camera Permission");
                builder.setMessage("This app needs camera permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
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
            else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.CAMERA,true);
            editor.commit();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORED: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {

//                        if (mGoogleApiClient == null) {
//                            buildGoogleApiClient();
//                        }

                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                //return;
            }

            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {

//                        if (mGoogleApiClient == null) {
//                            buildGoogleApiClient();
//                        }

                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                //return;
            }
            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }
//    private void uploadFromFile(String path) {
//        Uri file = Uri.fromFile(new File(path));
//
//        imageRef = folderRef.child(people.getProfileData().getCitizenID() +"_LocationCard"+".jpg");
//        //StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg").build();
//        //UploadTask uploadTask = imageRef.putFile(file, metadata);
//        mUploadTask = imageRef.putFile(file);
//
//    }
//    private void uploadFromDataInMemory(Bitmap bitmap) {
//        //Helper.showDialog(this);
//        // Get the data from an ImageView as bytes
//        //mImageView.setDrawingCacheEnabled(true);
//        //mImageView.buildDrawingCache();
//        //Bitmap bitmap = mImageView.getDrawingCache();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        byte[] data = baos.toByteArray();
//
//        mUploadTask = imageRef.putBytes(data);
//        mUploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                //Helper.dismissDialog();
//                //mTextView.setText(String.format("Failure: %s", exception.getMessage()));
//                System.out.println(exception.getMessage());
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                //Helper.dismissDialog();
//                //mTextView.setText(taskSnapshot.getDownloadUrl().toString());
//
//            }
//        });
//    }
    private void downloadInMemory() {
    //long ONE_MEGABYTE = 1024 * 1024;
        // Helper.showDialog(this);
		imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
        @Override
        public void onSuccess(byte[] bytes) {
           // Helper.dismissDialog();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            viewImage.setImageBitmap(bitmap);
            Log.d("BIT", "onSuccess: ");
        }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            //Helper.dismissDialog();
           // mTextView.setText(String.format("Failure: %s", exception.getMessage()));
            Log.d(exception.getMessage(), "onFailure: ");
            }
        });
    }
    public class FeedTask extends AsyncTask<String, Void ,JSONArray> {
        // Asycdialog = new ProgressDialog(LocationCard.this);
        // String[] resultData;
        @Override
        protected JSONArray doInBackground(String... strings) {
//            Asycdialog.setMessage("Loading...");
//            Asycdialog.show();
            WebApiOkHttpHandler webApiOkHttpHandler = new WebApiOkHttpHandler();
            JSONArray str = webApiOkHttpHandler.GetHTTPData(strings);

            //System.out.println(jsonObject.toString());
            return str;
        }
        @Override
        protected void onPostExecute(JSONArray str) {
            System.out.println(str.toString());
//            resultData = new String[str.length];
//            for(String s : str)
//                System.out.println(s + "555");
//            for(int i = 0 ;i <= resultData.length-1 ; i++){
//                resultData[i] = str[i];
//            }
//            for(String s : resultData)
//                System.out.println(s + "55555555555555555");
//            ProvinceAmphurTambon = str;
//            for(String s : ProvinceAmphurTambon){
//                System.out.println(s);
            Log.d("str", "onPostExecute: ");

        }


        // Asycdialog.dismiss();
        // System.out.println(jsonObject.toString());
        //}

//        protected String[] GetValue() {
//            return str;
//        }
    }
}
