package com.example.aunnie_iw.ntcardreader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Aunnie-IW on 8/6/2560.
 */

public class Profile extends AppCompatActivity implements MultiSelectionSpinner.MultiSpinnerListener ,View.OnClickListener  {
    private Spinner mMarriage,mBloodType,mSex,mReligion,mDisability;

    private TextView ECitizenID,ETitleTH,EFirstName,ELastName;
    private EditText EBirthday,ETell,EHomeTell,EEmail,EDisease,EAllergy,EHospitalNear,EHospitalUse;
    private Switch EAlive ,EHearing;
    private ImageView Img ;
    private People people;
    private ContactData contactData;
    private ProfileData profileData;
    private DisabilityData disabilityData;
    private String[] picturePath;
    private String[] pictureUri;
    Bitmap pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.Personal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        profileData = new ProfileData();
        disabilityData = new DisabilityData();
        people = new People();
        contactData = new ContactData();
        mMarriage = (Spinner) findViewById(R.id.Marriage);
/*------------------- intent ข้อมูล --------------------------------------------------------------------------------------------------*/
        Intent intent = getIntent();
        people = (People) intent.getExtras().getSerializable("data");
        contactData = (ContactData) intent.getExtras().getSerializable("contactData");
        picturePath = intent.getStringArrayExtra("picturePath");
        pictureUri = intent.getStringArrayExtra("pictureUri");

//        Log.d("onCreate: ",contactData.getCitizenID());
        Log.d("5555555555", people.getProfileData().getCitizenID());
        if(people.getProfileData().getImg() != null )
            pic = stringToBitMap(people.getProfileData().getImg());

        /*------------------- TextView Next--------------------------------------------------------------------------------------------------*/
        TextView Next = (TextView) findViewById(R.id.Next);
        Next.setOnClickListener(this);


        /*------------------- Spinner Marriage--------------------------------------------------------------------------------------------------*/
        String[] Marriage = getResources().getStringArray(R.array.Marriage);
        if(people.getProfileData() !=null && people.getProfileData().getMarriage()!=null){
            Marriage[Marriage.length-1] = people.getProfileData().getMarriage();
        }
        final ArrayAdapter<String> adapterMarriage = new ArrayAdapter<String>(Profile.this, android.R.layout.simple_dropdown_item_1line, Marriage){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                //Log.d(getItem(getCount()), "getView: ");
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    if(people.getProfileData() != null &&people.getProfileData().getMarriage()!=null)
                        ((TextView) v.findViewById(android.R.id.text1)).setText(people.getProfileData().getMarriage());
                    else
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

        mMarriage.setAdapter(adapterMarriage);
        mMarriage.setSelection(adapterMarriage.getCount());
        mMarriage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l)
            {
                TextView myText = (TextView) v;
                Toast.makeText(Profile.this, "You Selected "+myText.getText(), Toast.LENGTH_SHORT).show();
                ((TextView) adapterView.getChildAt(0)).setTextSize(14);
                if(i==adapterMarriage.getCount()&&(people.getProfileData() == null ||people.getProfileData().getMarriage()==null)){
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.RED);
                }
                else{
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLUE);
                }
            } // end onItemSelected method

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
            } // end onNothingSelected method
        });
        /*------------------- Spinner Sex--------------------------------------------------------------------------------------------------*/
        String[] Sex = getResources().getStringArray(R.array.Sex);
        if(people.getProfileData() !=null && people.getProfileData().getSex()!=null){
            Sex[Sex.length-1] = people.getProfileData().getSex();
        }
        mSex = (Spinner) findViewById(R.id.Sex);
        final ArrayAdapter<String> adapterSex = new ArrayAdapter<String>(Profile.this, android.R.layout.simple_dropdown_item_1line, Sex){
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
        mSex.setAdapter(adapterSex);
        mSex.setSelection(adapterSex.getCount());
        mSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l)
            {
                TextView myText = (TextView) v;
                Toast.makeText(Profile.this, "You Selected "+myText.getText(), Toast.LENGTH_SHORT).show();
                ((TextView) adapterView.getChildAt(0)).setTextSize(14);
                if(i==adapterSex.getCount()&&(people.getProfileData()==null||people.getProfileData().getSex()==null)){
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.RED);
                }
                else{
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLUE);
                }

            } // end onItemSelected method

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            } // end onNothingSelected method
        });
        /*------------------- Spinner Religion--------------------------------------------------------------------------------------------------*/
        String[] Religion = getResources().getStringArray(R.array.Religion);
        if(people.getProfileData() !=null && people.getProfileData().getReligion()!=null){
            Religion[Religion.length-1] = people.getProfileData().getReligion();
        }
        mReligion = (Spinner) findViewById(R.id.Religion);
        final ArrayAdapter<String> adapterReligion = new ArrayAdapter<String>(Profile.this, android.R.layout.simple_dropdown_item_1line, Religion){
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

        mReligion.setAdapter(adapterReligion);
        mReligion.setSelection(adapterReligion.getCount());//set the hint the default selection so it appears on launch.
        mReligion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l)
            {
                TextView myText = (TextView) v;
                Toast.makeText(Profile.this, "You Selected "+myText.getText(), Toast.LENGTH_SHORT).show();
                ((TextView) adapterView.getChildAt(0)).setTextSize(14);
                if(i==adapterReligion.getCount()&&(people.getProfileData() == null || people.getProfileData().getReligion()==null)){
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.RED);
                }
                else{
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLUE);
                }
            } // end onItemSelected method

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
            } // end onNothingSelected method
        });

         /*------------------- Spinner BloodType--------------------------------------------------------------------------------------------------*/
        String[] BloodType = getResources().getStringArray(R.array.BloodType);
        if(people.getProfileData() != null && people.getProfileData().getBloodType()!=null){
            BloodType[BloodType.length-1] = people.getProfileData().getBloodType();
        }
        mBloodType = (Spinner) findViewById(R.id.BloodType);
        final ArrayAdapter<String> adapterBloodType = new ArrayAdapter<String>(Profile.this, android.R.layout.simple_dropdown_item_1line,BloodType){
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
        //adapterBloodType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBloodType.setAdapter(adapterBloodType);
        mBloodType.setSelection(adapterBloodType.getCount());//set the hint the default selection so it appears on launch.
        mBloodType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l)
            {
                TextView myText = (TextView) v;
                Toast.makeText(Profile.this, "You Selected "+myText.getText(), Toast.LENGTH_SHORT).show();
                if(i==adapterBloodType.getCount()&&(people.getProfileData() == null||people.getProfileData().getBloodType()==null)){
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.RED);
                }
                else{
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLUE);
                }
            } // end onItemSelected method

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            } // end onNothingSelected method
        });
        /*------------------- Spinner Disability--------------------------------------------------------------------------------------------------*/
        String[] Disability = getResources().getStringArray(R.array.Disability);
        if(people.getDisabilityData()!=null && people.getDisabilityData().getDisability()!=null){
            Disability[Disability.length-1] = people.getDisabilityData().getDisability();
        }

        mDisability = (Spinner) findViewById(R.id.SDisability);
        final ArrayAdapter<String> adapterDisability = new ArrayAdapter<String>(Profile.this, android.R.layout.simple_dropdown_item_1line, Disability){
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
        //adapterBloodType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mDisability.setAdapter(adapterDisability);
        mDisability.setSelection(adapterDisability.getCount());//set the hint the default selection so it appears on launch.
        mDisability.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l)
            {
                TextView myText = (TextView) v;
                Toast.makeText(Profile.this, "You Selected "+myText.getText(), Toast.LENGTH_SHORT).show();
                ((TextView) adapterView.getChildAt(0)).setTextSize(14);
                if(i==adapterDisability.getCount()&&(people.getDisabilityData()==null||people.getDisabilityData().getDisability()==null)){
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.RED);
                }
                else{
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLUE);
                }
            } // end onItemSelected method

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                Toast.makeText(Profile.this, "You Must Buy-In To Play", Toast.LENGTH_SHORT).show();
            } // end onNothingSelected method
        });
/*-------------------MultiSelectionSpinner  Ability--------------------------------------------------------------------------------------------------*/
        String[] Ability = getResources().getStringArray(R.array.Ability);
        List<String> lAbility = Arrays.asList(Ability);
        MultiSelectionSpinner multiSpinner = (MultiSelectionSpinner) findViewById(R.id.Ability);
        boolean[] itemsData = new boolean[lAbility.size()];
        for (int i = 0; i < itemsData.length; i++)
            itemsData[i] = false;
        if (people.getDisabilityData()!=null){
            if (people.getDisabilityData().getSignLangTH()!=null&&people.getDisabilityData().getSignLangTH().equals("Yes"))
                itemsData[0] = true;
            if(people.getDisabilityData().getSpokenTH()!=null&&people.getDisabilityData().getSpokenTH().equals("Yes"))
                itemsData[1] = true;
            if(people.getDisabilityData().getReadTH()!=null&&people.getDisabilityData().getReadTH().equals("Yes"))
                itemsData[2] = true;
            if(people.getDisabilityData().getWriteTH()!=null&&people.getDisabilityData().getWriteTH().equals("Yes"))
                itemsData[3] = true;
            if(people.getDisabilityData().getLipRead()!=null&&people.getDisabilityData().getLipRead().equals("Yes"))
                itemsData[4] = true;
        }
        multiSpinner.setItems(lAbility, getString(R.string.LanguageSkill), this,itemsData);

        /*---------------------------------------ส่วนของการแสดงผล-----------------------------------------------   */

        Img=(ImageView) findViewById(R.id.pic);

        ECitizenID = (TextView) findViewById(R.id.ECitizenID);
        ECitizenID.setText(people.getProfileData().getCitizenID());
        ETitleTH = (TextView) findViewById(R.id.ETitleTH);
        ETitleTH.setText(people.getProfileData().getPrefixThai());
        EFirstName = (TextView) findViewById(R.id.EFirstName);
        EFirstName.setText(people.getProfileData().getFirstNameThai());
        ELastName = (TextView) findViewById(R.id.ELastName);
        ELastName.setText(people.getProfileData().getLastNameThai());
        EBirthday = (EditText) findViewById(R.id.EBirthday);
        EBirthday.setText(people.getProfileData().getBirthday());
        ETell = (EditText) findViewById(R.id.ETell);
        if(people.getProfileData()!=null&&people.getProfileData().getTell()!=null)
            ETell.setText(people.getProfileData().getTell());
        EHomeTell = (EditText) findViewById(R.id.EHomeTell);
        if(people.getProfileData()!=null&&people.getProfileData().getHometell()!=null)
            EHomeTell.setText(people.getProfileData().getHometell());
        EEmail = (EditText) findViewById(R.id.EEmail);
        if(people.getProfileData()!=null&&people.getProfileData().getEmail()!=null)
            EEmail.setText(people.getProfileData().getEmail());
        EDisease = (EditText) findViewById(R.id.EDisease);
        if(people.getProfileData()!=null&&people.getProfileData().getDisease()!=null)
            EDisease.setText(people.getProfileData().getDisease());
        EAllergy = (EditText) findViewById(R.id.EAllergy);
        if(people.getProfileData()!=null&&people.getProfileData().getAllergy()!=null)
            EAllergy.setText(people.getProfileData().getAllergy());
        EHospitalNear = (EditText) findViewById(R.id.EHospitalNear);
        if(people.getProfileData()!=null&&people.getProfileData().getHospitalNear()!=null)
            EHospitalNear.setText(people.getProfileData().getHospitalNear());
        EHospitalUse = (EditText) findViewById(R.id.EHospitalUse);
        if(people.getProfileData()!=null&&people.getProfileData().getHospitalUse()!=null)
            EHospitalUse.setText(people.getProfileData().getHospitalUse());
        EAlive = (Switch) findViewById(R.id.EAlive);

        if(people.getProfileData()!=null && people.getProfileData().getAlive() !=null ){
            if (people.getProfileData().getAlive().equals("Yes"))
                EAlive.setChecked(true);
        }
        else{
            EAlive.setChecked(false);
        }

        EHearing = (Switch) findViewById(R.id.EHearing);
        if(people.getDisabilityData()!=null && people.getDisabilityData().getHaveHearingAids() !=null ){
            if (people.getDisabilityData().getHaveHearingAids().equals("Yes"))
                EHearing.setChecked(true);
        }
        else{
            EHearing.setChecked(false);
        }

        Img.setImageBitmap(pic);//รูปภาพประจำตัว



        EAlive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    //EAlive.setText("Only Today's");  //To change the text near to switch
                    Log.d("Selected :", "ยังมีชีวิตอยู่");
                }
                else {
                    //EAlive.setText("All List");  //To change the text near to switch
                    Log.d("Selected :", "ไม่มีชีวิตอยู่แล้ว่");
                }
            }
        });


        EHearing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    //EHearing.setText("Only Today's");  //To change the text near to switch
                    Log.d("Selected :", "มีเครื่องช่วยฟัง");
                }
                else {
                    //EHearing.setText("All List");  //To change the text near to switch
                    Log.d("Selected :", "ไม่มีเครื่องช่วยฟัง");
                }
            }
        });
    }

    @Override
    public void onItemsSelected(boolean[] selected) {

        if (selected[0])
            people.getDisabilityData().setSignLangTH("Yes");
        else
            people.getDisabilityData().setSignLangTH("No");
        if (selected[1])
            people.getDisabilityData().setSpokenTH("Yes");
        else
            people.getDisabilityData().setSpokenTH("No");
        if (selected[2])
            people.getDisabilityData().setReadTH("Yes");
        else
            people.getDisabilityData().setReadTH("No");
        if (selected[3])
            people.getDisabilityData().setWriteTH("Yes");
        else
            people.getDisabilityData().setWriteTH("No");
        if (selected[4])
            people.getDisabilityData().setLipRead("Yes");
        else
            people.getDisabilityData().setLipRead("No");

        Log.d(disabilityData.getSignLangTH(), "onItemsSelected: ");
        Log.d(disabilityData.getSpokenTH(), "onItemsSelected: ");
        Log.d(disabilityData.getReadTH(), "onItemsSelected: ");
        Log.d(disabilityData.getWriteTH(), "onItemsSelected: ");
        Log.d(disabilityData.getLipRead(), "onItemsSelected: ");

    }

    @Override
    public void onPostExecute(String s) {

    }

    public Bitmap stringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onClick(View view){
        //String Uid = mUser.getUid();
        switch (view.getId()) {
            case R.id.Next:
                Intent intent = new Intent(Profile.this, LocationCard.class);
                // check current state of a Switch (true or false).
                if (EAlive.isChecked()) {
                    // The toggle is enabled
                    profileData.setAlive("Yes");
                } else {
                    // The toggle is disabled
                    profileData.setAlive("No");
                }
                if (EHearing.isChecked()) {
                    // The toggle is enabled
                    people.getDisabilityData().setHaveHearingAids("Yes");
                } else {
                    // The toggle is disabled
                    people.getDisabilityData().setHaveHearingAids("No");
                }
                people.getDisabilityData().setDisability(mDisability.getSelectedItem().toString());

                profileData.setCitizenID(ECitizenID.getText().toString());
                profileData.setPrefixThai(ETitleTH.getText().toString());
                profileData.setFirstNameThai(EFirstName.getText().toString());
                profileData.setLastNameThai(ELastName.getText().toString());
                profileData.setBirthday(EBirthday.getText().toString());
                profileData.setMarriage(mMarriage.getSelectedItem().toString());
                profileData.setSex(mSex.getSelectedItem().toString());
                profileData.setBloodType(mBloodType.getSelectedItem().toString());
                profileData.setReligion(mReligion.getSelectedItem().toString());
                profileData.setTell(ETell.getText().toString());
                profileData.setHometell(EHomeTell.getText().toString());
                profileData.setEmail(EEmail.getText().toString());
                profileData.setDisease(EDisease.getText().toString());
                profileData.setAllergy(EAllergy.getText().toString());
                profileData.setHospitalNear(EHospitalNear.getText().toString());
                profileData.setHospitalUse(EHospitalUse.getText().toString());
                //people.setDisabilityData(disabilityData);
                people.setProfileData(profileData);

                Log.d(people.getProfileData().getCitizenID(), "onClick: ");
                Log.d(people.getProfileData().getPrefixThai(), "onClick: ");
                Log.d(people.getProfileData().getFirstNameThai(), "onClick: ");
                Log.d(people.getProfileData().getLastNameThai(), "onClick: ");
                Log.d(people.getProfileData().getBirthday(), "onClick: ");
                Log.d(people.getProfileData().getMarriage(), "onClick: ");
                Log.d(people.getProfileData().getSex(), "onClick: ");
                Log.d(people.getProfileData().getBloodType(), "onClick: ");
                Log.d(people.getProfileData().getReligion(), "onClick: ");
                Log.d(people.getProfileData().getTell(), "onClick: ");
                Log.d(people.getProfileData().getHometell(), "onClick: ");
                Log.d(people.getProfileData().getDisease(), "onClick: ");
                Log.d(people.getProfileData().getAllergy(), "onClick: ");
                Log.d(people.getProfileData().getHospitalNear(), "onClick: ");
                Log.d(people.getProfileData().getHospitalUse(), "onClick: ");
                Log.d(people.getProfileData().getAlive(), "onClick: ");
                intent.putExtra("data", people);
                intent.putExtra("contactData",contactData);
                intent.putExtra("picturePath",picturePath);
                intent.putExtra("pictureUri",pictureUri);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
            finish();
            Log.e("onPressBack","Hello World");
    }

}