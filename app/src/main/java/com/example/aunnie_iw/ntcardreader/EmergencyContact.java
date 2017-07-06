package com.example.aunnie_iw.ntcardreader;

import android.*;
import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Aunnie-IW on 13/6/2560.
 */

public class EmergencyContact extends AppCompatActivity implements View.OnClickListener {

    private ImageView viewImage;
    private Button BSelectPhoto;
    private Bitmap bitmap;
    private Bitmap ImgLocationCard;
    public static final int MY_PERMISSIONS_REQUEST_STORED = 90;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 98;

    public static final int REQUEST_GALLERY = 1;
    public static final int REQUEST_CAMERA = 2;
    public static final int REQUEST_ADDRESS = 3;
    private Uri uri;
    private File f;
    private TextView SLatLng;
    private Double latitude;
    private Double longitude;
    // Write a message to the database
    protected FirebaseDatabase database = FirebaseDatabase.getInstance();
    protected DatabaseReference myRef = database.getReference();
    private UploadTask mUploadTask;
    private StorageReference folderRef, imageRef;
    private Spinner mRelationship,mSex;
    private People people;
    private AddressData addressData;
    private ContactData contactData;
    private String contactID;
    private String peopleKey;
    private EditText ECitizenID, ETitleTH,EFirstName ,ELastName,ETell,EHomeTell, EHouseNumber, EMoo,ESoi,ERoad,ETambon,RAmphur,EProvince,EPostcode,ELandmark,ELatitude,ELongtitude,EPhotourl;

    private Spinner mProvince,mAmphur,mTambon;

    private String[] Province,Amphur,Tambon,ProvinceId,AmphurId,TambonId;
    private JSONArray JAProvince,JAAmphur,JATambon,JAProvinceId,JAAmphurId,JATambonId;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;

    private String PathImgLocationCard,PathImgLocationNow,PathImgLocationEmergency;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.EmergencyContact);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        folderRef = storageRef.child("photos");
//        imageRef = folderRef.child("firebase.png");

        //StorageReference storageRef = storage.getReference();
        //imageRef = folderRef.child(people.getProfileData().getCitizenID() +"_LocationCard.jpg");

        addressData = new AddressData();
        contactData = new ContactData();
        /*------------------- intent ข้อมูล --------------------------------------------------------------------------------------------------*/
        Intent intent = getIntent();
        people = (People) intent.getExtras().getSerializable("data");

        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);
        PathImgLocationCard = getIntent().getExtras().getString("PathImgLocationCard");
        PathImgLocationNow = getIntent().getExtras().getString("PathImgLocationNow");


        Log.d(PathImgLocationCard, "PathImgLocationCard ");
        Log.d(PathImgLocationNow, "PathImgLocationNow");


        Log.d(people.getAddressNow().getHouseNumber(), "LocationNow: ");
        Log.d(people.getAddressNow().getMoo(), "LocationNow: ");
        Log.d(people.getAddressNow().getSoi(), "LocationNowSoi: ");
        Log.d(people.getAddressNow().getRoad(), "LocationNow: ");
        Log.d(people.getAddressNow().getPostcode(), "LocationNow: ");
        Log.d(people.getAddressNow().getLandmark(), "LocationNow: ");


        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference ref = database.getReference("EmergencyContact").child(people.getContactID());
        findContactDataOnFirebase();


//////////////////
        /*------------------- Photo--------------------------------------------------*/
        BSelectPhoto=(Button)findViewById(R.id.BSelectPhoto);
        viewImage=(ImageView)findViewById(R.id.viewImage);
        BSelectPhoto.setOnClickListener(EmergencyContact.this);
            /*------------------- TextView Update --------------------------------------------------------------------------------------------------*/
            TextView Update = (TextView) findViewById(R.id.Update);
        Update.setOnClickListener(this);

                /*------------------- Spinner Relationship--------------------------------------------------------------------------------------------------*/
        String[] Relationship = getResources().getStringArray(R.array.Relationship);
        mRelationship = (Spinner) findViewById(R.id.Relationship);
        ArrayAdapter<String> adapterRelationship = new ArrayAdapter<String>(EmergencyContact.this, android.R.layout.simple_dropdown_item_1line, Relationship);
        mRelationship.setAdapter(adapterRelationship);
        mRelationship.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l)
            {
                TextView myText = (TextView) v;
                Toast.makeText(EmergencyContact.this, "You Selected "+myText.getText(), Toast.LENGTH_SHORT).show();
                ((TextView) adapterView.getChildAt(0)).setTextSize(14);
                if(i==0){
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.RED);
                }
                else{
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLUE);
                }
            } // end onItemSelected method

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                Toast.makeText(EmergencyContact.this, "You Must Buy-In To Play", Toast.LENGTH_SHORT).show();
            } // end onNothingSelected method
        });
                /*------------------- Spinner Sex--------------------------------------------------------------------------------------------------*/
        String[] Sex = getResources().getStringArray(R.array.Sex);
        mSex = (Spinner) findViewById(R.id.Sex);
        ArrayAdapter<String> adapterSex = new ArrayAdapter<String>(EmergencyContact.this, android.R.layout.simple_dropdown_item_1line, Sex);
        mSex.setAdapter(adapterSex);
        mSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l)
            {
                TextView myText = (TextView) v;
                Toast.makeText(EmergencyContact.this, "You Selected "+myText.getText(), Toast.LENGTH_SHORT).show();
                ((TextView) adapterView.getChildAt(0)).setTextSize(14);
                if(i==0){
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.RED);
                }
                else{
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLUE);
                }
            } // end onItemSelected method

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                Toast.makeText(EmergencyContact.this, "You Must Buy-In To Play", Toast.LENGTH_SHORT).show();
            } // end onNothingSelected method
        });
        /*------------------- Button BLatLng --------------------------------------------------------------------------------------------------*/
            Button BLatLng = (Button) findViewById(R.id.BLatLng) ;
            BLatLng.setOnClickListener(EmergencyContact.this);
/*------------------- TextView ------------------------------------*/
        ECitizenID = (EditText) findViewById(R.id.ECitizenID);
        ETitleTH = (EditText) findViewById(R.id.ETitleTH);
        EFirstName = (EditText) findViewById(R.id.EFirstName);
        ELastName = (EditText) findViewById(R.id.ELastName);
        ETell = (EditText) findViewById(R.id.ETell);
        EHomeTell = (EditText) findViewById(R.id.EHomeTell);

        EHouseNumber = (EditText) findViewById(R.id.EHouseNumber);
        EMoo = (EditText) findViewById(R.id.EMoo);
        ESoi = (EditText) findViewById(R.id.ESoi);
        ERoad = (EditText) findViewById(R.id.ERoad);

        EPostcode = (EditText) findViewById(R.id.EPostCode);
        ELandmark = (EditText) findViewById(R.id.ELanmark);

        SLatLng = (TextView) findViewById(R.id.SLatLng);
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
        ArrayAdapter<String> adapterProvince = new ArrayAdapter<String>(EmergencyContact.this, android.R.layout.simple_spinner_dropdown_item, Province) {
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
        mProvince.setSelection(adapterProvince.getCount());
        mProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l) {
                TextView myText = (TextView) v;
                Toast.makeText(EmergencyContact.this, "You Selected " + myText.getText(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EmergencyContact.this, "You Must Buy-In To Play", Toast.LENGTH_SHORT).show();
            } // end onNothingSelected method
        });
    }

    private void findContactDataOnFirebase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("EmergencyContact").orderByChild(people.getContactID());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        contactData = issue.getValue(ContactData.class);
                        // do something with the individual "issues"
                        Log.d("findContactOnFirebase", "Success ");

                        setDisplayContactData(contactData);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //textView.setText(databaseError.getMessage());
                Log.d("findContactOnFirebase", databaseError.getMessage());

            }
        });
    }

    private void setDisplayContactData(ContactData contactData) {

        if(contactData.getCitizenID() !=null )
            ECitizenID.setText(contactData.getCitizenID());

        if(contactData.getPrefixThai() !=null )
            ETitleTH.setText(contactData.getPrefixThai());

        if(contactData.getFirstNameThai() !=null )
            EFirstName.setText(contactData.getFirstNameThai());

        if(contactData.getLastNameThai() !=null )
            ELastName.setText(contactData.getLastNameThai());

        if(contactData.getTell() !=null )
            ETell.setText(contactData.getTell());

        if(contactData.getHometell() !=null )
            EHomeTell.setText(contactData.getHometell());

        if (contactData.getAddressData() !=null ) {
            if (contactData.getAddressData().getHouseNumber() != null)
                EHouseNumber.setText(contactData.getAddressData().getHouseNumber());

            if (contactData.getAddressData().getMoo() != null)
                EMoo.setText(contactData.getAddressData().getMoo());

            if (contactData.getAddressData().getSoi() != null)
                ESoi.setText(contactData.getAddressData().getSoi());

            if (contactData.getAddressData().getRoad() != null)
                ERoad.setText(contactData.getAddressData().getRoad());

            if (contactData.getAddressData().getPostcode() != null)
                EPostcode.setText(contactData.getAddressData().getPostcode());

            if (contactData.getAddressData().getLandmark() != null)
                ELandmark.setText(contactData.getAddressData().getLandmark());

            if (contactData.getAddressData().getLatitude() != null && contactData.getAddressData().getLongitude() != null)
                SLatLng.setText("( " + String.valueOf(contactData.getAddressData().getLatitude()) + ", " + String.valueOf(contactData.getAddressData().getLongitude() + " )"));
        }
    }

    public void spinnerAmphur(final String[] amphur, final String province_id){
        Log.d(amphur[0], "spinnerAmphur: AAAAAA");
        mAmphur = (Spinner) findViewById(R.id.Amphur);

        ArrayAdapter<String> adapterAmphur = new ArrayAdapter<String>(EmergencyContact.this, android.R.layout.simple_spinner_dropdown_item,amphur){
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
        mAmphur.setSelection(adapterAmphur.getCount());
        mAmphur.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l) {
                TextView myText = (TextView) v;
                Toast.makeText(EmergencyContact.this, "You Selected " + myText.getText(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EmergencyContact.this, "You Must Buy-In To Play", Toast.LENGTH_SHORT).show();
            } // end onNothingSelected method
        });
    }


    public void spinnerTambon(String[] Tambon){

        mTambon = (Spinner) findViewById(R.id.Tambon);
        ArrayAdapter<String> adapterTambon = new ArrayAdapter<String>(EmergencyContact.this, android.R.layout.simple_spinner_dropdown_item,Tambon){
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
        mTambon.setSelection(adapterTambon.getCount());
        mTambon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l)
            {

                TextView myText = (TextView) v;
                Toast.makeText(EmergencyContact.this, "You Selected "+myText.getText(), Toast.LENGTH_SHORT).show();
                ((TextView) adapterView.getChildAt(0)).setTextSize(14);
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLUE);
            } // end onItemSelected method
            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                Toast.makeText(EmergencyContact.this, "You Must Buy-In To Play", Toast.LENGTH_SHORT).show();
            } // end onNothingSelected method
        });
    }


    public void onClick(View view){
        //String Uid = mUser.getUid();
        switch (view.getId()) {
            case R.id. BLatLng:
                Intent intent = new Intent(EmergencyContact.this, LocationMap.class);
                //intent.putExtra("data", cardFirebase);
                startActivityForResult(intent, REQUEST_ADDRESS);
                break;
            case R.id.BSelectPhoto:
                selectImage();
                break;

            case R.id.Update: {
                contactData.getAddressData().setHouseNumber(EHouseNumber.getText().toString());
                contactData.getAddressData().setMoo(EMoo.getText().toString());
                contactData.getAddressData().setSoi(ESoi.getText().toString());
                contactData.getAddressData().setRoad(ERoad.getText().toString());
                contactData.getAddressData().setPostcode(EPostcode.getText().toString());
                contactData.getAddressData().setLandmark(ELandmark.getText().toString());

                contactData.getAddressData().setProvince(mProvince.getSelectedItem().toString());
                contactData.getAddressData().setAmphur(mAmphur.getSelectedItem().toString());
                contactData.getAddressData().setTambon(mTambon.getSelectedItem().toString());
                if (latitude!=null)
                    contactData.getAddressData().setLatitude(latitude);
                if (longitude!=null)
                    contactData.getAddressData().setLongitude(longitude);

                contactData.setCitizenID(ECitizenID.getText().toString());
                contactData.setPrefixThai(ETitleTH.getText().toString());
                contactData.setFirstNameThai(EFirstName.getText().toString());
                contactData.setLastNameThai(ELastName.getText().toString());

                contactData.setTell(ETell.getText().toString());
                contactData.setHometell(EHomeTell.getText().toString());
                contactData.setRelationship(mRelationship.getSelectedItem().toString());
                contactData.setSex(mSex.getSelectedItem().toString());

                Log.d(contactData.getCitizenID(), "Emergency: ");
                Log.d(contactData.getFirstNameThai(), "Emergency: ");
                Log.d(contactData.getLastNameThai(), "Emergency: ");
                Log.d(contactData.getRelationship(), "Emergency: ");
                Log.d(contactData.getSex(), "Emergency: ");

                Log.d(contactData.getTell(), "Emergency: ");
                Log.d(contactData.getHometell(), "Emergency: ");
                Log.d(contactData.getAddressData().getHouseNumber(), "Emergency: ");
                Log.d(contactData.getAddressData().getMoo(), "Emergency: ");
                Log.d(contactData.getAddressData().getSoi(), "Emergency: ");
                Log.d(contactData.getAddressData().getRoad(), "Emergency: ");
                Log.d(contactData.getAddressData().getPostcode(), "Emergency: ");
                Log.d(contactData.getAddressData().getLandmark(), "Emergency: ");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                if (people.getPeopleKey() == null) {
                    peopleKey = myRef.child("Peoplee").push().getKey();
                    people.setPeopleKey(peopleKey);
                    contactID = myRef.child("EmergencyContact").push().getKey();
                    people.setContactID(contactID);
                }
                else if (people.getContactID() == null){

                    contactID = myRef.child("EmergencyContact").push().getKey();
                    people.setContactID(contactID);

                }
                    myRef.child("EmergencyContact").child(people.getContactID()).setValue(contactData);
                    myRef.child("Peoplee").child(people.getPeopleKey()).setValue(people);


            }
        }
    }
    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyContact.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))

                {/*------------------- Permission --------------------------------------*/


                    if (ContextCompat.checkSelfPermission(EmergencyContact.this,
                            android.Manifest.permission.CAMERA)
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
                    if (ContextCompat.checkSelfPermission(EmergencyContact.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

            PathImgLocationEmergency = Helper.getPath(this, Uri.parse(data.getData().toString()));
            //File imageFile = new File(getRealPathFromURI(uri));
            Log.d(uri.toString(), "onActivityResult: ");
            Log.d(uri.getPath(), "onActivityResult: ");
            Log.d(PathImgLocationEmergency, "onActivityResult: ");
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                bitmap= RotateImg.Rotate(PathImgLocationEmergency,bitmap);
                Log.d("bitmap", "onActivityResult: ");
                viewImage.setImageBitmap(bitmap);
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

                bitmap = MediaStore.Images.Media.getBitmap(cr, uri);
                bitmap = RotateImg.Rotate(uri.getPath(),bitmap);
                //Log.d(f.getPath(), "onActivityResult: Path");
                PathImgLocationEmergency = uri.getPath();
                viewImage.setImageBitmap(bitmap);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyContact.this);
                builder.setTitle("Need STORAGE Permission");
                builder.setMessage("This app needs WRITE_EXTERNAL_STORAGE permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(EmergencyContact.this,
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


            }else if (permissionStatus.getBoolean(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyContact.this);
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
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyContact.this);
                builder.setTitle("Need CAMERA Permission");
                builder.setMessage("This app needs CAMERA permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(EmergencyContact.this,
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


            }else if (permissionStatus.getBoolean(android.Manifest.permission.CAMERA,false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyContact.this);
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
            editor.putBoolean(android.Manifest.permission.CAMERA,true);
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
                            android.Manifest.permission.CAMERA)
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

    private void uploadFromFile(String path) {
        Uri file = Uri.fromFile(new File(path));
        //StorageReference imageRef = folderRef.child(file.getLastPathSegment());
        //StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg").build();
        //UploadTask uploadTask = imageRef.putFile(file, metadata);
        imageRef.putFile(file);

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
    private void uploadFromDataInMemory(Bitmap bitmap) {
        //Helper.showDialog(this);
        // Get the data from an ImageView as bytes
        //mImageView.setDrawingCacheEnabled(true);
        //mImageView.buildDrawingCache();
        //Bitmap bitmap = mImageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        folderRef = storageRef.child("photos");
        imageRef = folderRef.child("firebase.png");
        mUploadTask = imageRef.putBytes(data);
        mUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //Helper.dismissDialog();
                //mTextView.setText(String.format("Failure: %s", exception.getMessage()));
                System.out.println(exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                //Helper.dismissDialog();
                //mTextView.setText(taskSnapshot.getDownloadUrl().toString());

            }
        });
    }
}
