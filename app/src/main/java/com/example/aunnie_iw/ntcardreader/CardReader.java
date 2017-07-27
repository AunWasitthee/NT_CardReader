package com.example.aunnie_iw.ntcardreader;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.feitian.readerdk.Tool.DK;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import nectec_stp.code.com.ntcardreader.Address;
import nectec_stp.code.com.ntcardreader.Card;
import nectec_stp.code.com.ntcardreader.FtException;
import nectec_stp.code.com.ntcardreader.NT_reader;

/**
 * Created by Aunnie-IW on 6/6/2560.
 */

public class CardReader extends Activity implements View.OnClickListener {
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";  /*สร้างตัวแปรใช้ประกาศการขออนุญาตใช้USB*/
    private UsbDevice mDevice;
    protected UsbManager mUsbManager;
    protected PendingIntent mPermissionIntent;  //pending = รอ
    protected NT_reader mCard;
    /*-----------------------------แสดงผล----------------------------------------*/
    //protected TextView Cid,NameTH,Address,CreateCard,NameEng,Datebirthday,ExpireCard,DateToday,Stmsg;
    //protected TextView textView;
    protected EditText inputIdCard;
    //protected Button mExit,mOpen,mSave;
    protected Button mOk, mReadData;
    private String deviceName;
    /*-------------------------------------------------------------4 */
    //protected ImageView Img ;
    /*-------------------------------------------------------------*/
    // Write a message to the database
    protected FirebaseDatabase database = FirebaseDatabase.getInstance();
    protected DatabaseReference myRef = database.getReference();
    /*-------------------------------------------------------------*/
    protected Card card = null;
    private People people;
    private ContactData contactData;
    private AddressData addressData;
    private ProfileData profileData;
    private DisabilityData disabilityData;
    /*-----------------Permission----------------------------*/
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private String[] picturePath;
    private String[] pictureUri;
//    private Bitmap[] pictureBitmap;
    /*-------------------------------------------------------------*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_reader);
         /*------------------- Permission --------------------------------------*/

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //checkAndRequestPermissions();
        }
        /*------------------------------------------------------------------------------------------------------   1*/
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);/*เช็ค เปิดค่า USB*/
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        /*-----------------------------*/
        registerReceiver(mUsbReceiver, filter);
//        mOpen = (Button) findViewById(R.id.BOpen);
//        mOpen.setOnClickListener(this);
//        mOpen.setEnabled(false);
//        mExit = (Button) findViewById(R.id.BExit);
//        mExit.setOnClickListener(this);
//        mSave = (Button) findViewById(R.id.BSave);
//        mSave.setOnClickListener(this);
        mOk = (Button) findViewById(R.id.BOk);
        mOk.setOnClickListener(this);
        //mOk.setEnabled(false);
        mReadData = (Button) findViewById(R.id.BReadData);
        mReadData.setOnClickListener(this);
       // mReadData.setEnabled(false);
//        /*-----------------------------*/
//        Stmsg = (TextView) findViewById(R.id.textView1);
 /*---------------------------------------ส่วนของการแสดงผล-----------------------------------------------   */
        inputIdCard = (EditText) findViewById(R.id.inputIdCard);
//        Cid = (TextView) findViewById(R.id.ECid);Cid.setEnabled(false);
//        NameTH = (TextView) findViewById(R.id.EName);NameTH.setEnabled(false);
//        NameEng = (TextView) findViewById(R.id.NameEng);NameEng.setEnabled(false);
//        Datebirthday = (TextView) findViewById(R.id.date);Datebirthday.setEnabled(false);
//        Address = (TextView) findViewById(R.id.EAdd);Address.setEnabled(false);
//        CreateCard = (TextView) findViewById(R.id.EIs_Ex);CreateCard.setEnabled(false);
//        ExpireCard = (TextView) findViewById(R.id.Is_Ex2);ExpireCard.setEnabled(false);
//        DateToday = (TextView) findViewById(R.id.DateToday);DateToday.setEnabled(false);
//        Img=(ImageView) findViewById(R.id.imageView1);
        people = new People();
        contactData = new ContactData();
        picturePath = new String[3];
        pictureUri = new String[3];
//        pictureBitmap = new Bitmap[3];
        for(int i=0 ; i<3 ; i++) {
            pictureUri[i]  = "";
            picturePath[i] = "";
        }
    }

    @SuppressLint("NewApi")
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @TargetApi(12)
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                UsbDevice deviceInput = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (deviceInput != null) {
                }
                //Stmsg.setText("เพิ่มอุปกรณ์:  " + deviceInput.getDeviceName()+"\n");
                deviceName = deviceInput.getDeviceName();
                //mOpen.setEnabled(true);
                //mOk.setEnabled(true);
                //mReadData.setEnabled(true);
                /*------------------------------------------------------------------------------------*/
                for (UsbDevice device : mUsbManager.getDeviceList().values()) {
                    if (deviceName.equals(device.getDeviceName())) {
                        if (mCard != null) {
                            mCard.close();
                        }
                        mDevice = device;

                        if (!mUsbManager.hasPermission(mDevice)) {
                            mUsbManager.requestPermission(mDevice, mPermissionIntent);
                        }
                        if (!mUsbManager.hasPermission(mDevice)) {
                            return;
                        }
                        break;

                    }

                }

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice deviceExit = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                //Stmsg.setText("ถอดอุปกรณ์:  " + deviceExit.getDeviceName()+"\n");
                if (null != mCard) {
                }
            }
        }
    };
    /*------------------------------------------------------------------------------------------------------   1*/
//    -------------------------------------Handler-------------------------------------------------------
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DK.CARD_STATUS:
                    switch (msg.arg1) {
                        case DK.CARD_ABSENT: //ยังไม่มีการใช้อินเทอเฟสของอุปกรณ์
                            try {
                                stat_clear();
                                mCard.PowerOff();
/*--------------------------------------Auto Clear---------------------------------------------------*/

                                //Stmsg.setText("กรุณาเสียบบัตรประชาชน"+"\n");
                            } catch (FtException e) {
                                e.printStackTrace();
                            }
                            break;
                        case DK.CARD_PRESENT:
                            //Stmsg.setText("พบการเสียบบัตรประชาชน"+"\n"); //อินเทอเฟสของอุปกรณ์ถูกใช้งานเเล้ว
                            try {
                                stat_clear();
                                mCard.PowerOff();
                                mCard.PowerOn();
/*------------------------------------Auto read-----------------------------------------------------*/
                                stat_clear();
                                mCard.PowerOff();
                                mCard.PowerOn();
/*------------------------------------Auto read-----------------------------------------------------*/
                                card = mCard.read();
                                inputIdCard.setText(card.getCitizenID()); //รหัสประจำตัวประชาชน
                                //textView.setText(card.getCitizenID());

                                //Stmsg.setText("อ่านข้อมูลเรียบร้อย"+"\n");
/*-----------------------------------------------------------------------------------------*/
                            } catch (FtException e) {
                                e.printStackTrace();
                            }
                            break;
                        case DK.CARD_UNKNOWN:
                            //Stmsg.setText("บัตรไม่ถูกต้อง"+"\n");
                            break;
                        case DK.IFD_COMMUNICATION_ERROR:
                            //Stmsg.setText("การส่งข้อมูลผิดพลาด"+"\n");
                            break;
                    }
                default:
                    break;
            }
        }
    };

    private void stat_clear() {
//        Img.setImageDrawable(null);
//        Stmsg.setText("");Cid.setText("");NameTH.setText("");Address.setText("");
//        CreateCard.setText("");NameEng.setText("");Datebirthday.setText("");ExpireCard.setText("");
//        DateToday.setText("");
    }

    @Override
    public void onClick(View view) {
        if (view == mReadData) {
//             String deviceName = (String) mSpinner.getSelectedItem(); //ทำการเลือกitem ใน spinner
            if (deviceName != null)
//            ------(*)-------------ส่วนของการเช็คชื่อเเละทำการตรวจอุปรณ์เเละทำการ connect---------------
            {
                for (UsbDevice device : mUsbManager.getDeviceList().values()) {
                    // If device name is found
                    if (deviceName.equals(device.getDeviceName())) {
                        if (mCard != null) {
                            mCard.close();
                        }
                        mCard = new NT_reader(mUsbManager, mDevice);
                        break;
                    }
                }
            }
            try {
                mCard.open();
                //mOk.setEnabled(false);
                mReadData.setEnabled(false);
//                Stmsg.setText("เปิดอุปกรณ์สำเร็จ "+"\n");
                mCard.startCardStatusMonitoring(mHandler);
            } catch (Exception e) {
                // Stmsg.setText("ไม่พบอุปกรณ์"+"\n");
            }
        }
//        if (view == mReadData) {
//            inputIdCard.setText("1809900590074");
//        }
        if (view == mOk) {
            try {
//             String deviceName = (String) mSpinner.getSelectedItem(); //ทำการเลือกitem ใน spinner
                String idcard = inputIdCard.getText().toString();
                //textView.setText(idcard);
                findPeoplefromFirebase(idcard);
                Log.d("onClick: ",people.getContactID());
            }catch (Exception e) {
                //textView.setText(e.getMessage() + "" );
            }
        }
    }

    private void findPeoplefromFirebase(String idcard) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child("Peoplee").orderByChild("profileData/citizenID").equalTo(idcard);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        People p = issue.getValue(People.class);
                        // do something with the individual "issues"
                        setPeople(p);
                        if (people.getContactID() != null) {
                            FindContactDataFromFirebase();
                        }else{
                            Intent intent = new Intent(CardReader.this, Profile.class);
                            intent.putExtra("data", people);
                            intent.putExtra("contactData", contactData);
                            intent.putExtra("picturePath",picturePath);
                            intent.putExtra("pictureUri",pictureUri);
                            startActivity(intent);
                        }
                    }
                }
                else if (card != null) {
                    try {
                        //textView.setText("อ่านข้อมูลจาก card");
                        addressData = new AddressData();
                        addressData.setAmphur(card.getAddress().getAmphur());
                        addressData.setHouseNumber(card.getAddress().getHouseNumber());
                        addressData.setMoo(card.getAddress().getMoo());
                        addressData.setProvince(card.getAddress().getProvince());
                        addressData.setTambon(card.getAddress().getTambon());
                        ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
                        card.getPicture().compress(Bitmap.CompressFormat.PNG, 100, bYtE);
                        card.getPicture().recycle();
                        byte[] byteArray = bYtE.toByteArray();
                        String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        profileData = new ProfileData();
                        profileData.setCitizenID(card.getCitizenID());
                        // profileData.setTitleEng(card.getTitleEng());
                        profileData.setPrefixThai(card.getTitleThai());
                        profileData.setBirthday(card.getBirthday());
                        profileData.setCitizenID(card.getCitizenID());
                        // profileData.setCreateCard(card.getCreateCard());
                        // profileData.setFirstNameEng(card.getFirstNameEng());
                        profileData.setFirstNameThai(card.getFirstNameThai());
                        // profileData.setLastNameEng(card.getLastNameEng());
                        profileData.setLastNameThai(card.getLastNameThai());

                        // profileData.setDateThaitoday(card.getDateThaitoday());
                        // profileData.setExpireCard(card.getExpireCard());

                        //profileData.setImg(imageFile);
                        disabilityData = new DisabilityData();
                        people.setAddressCard(addressData);
                        people.setProfileData(profileData);
                        //people.setDisabilityData(disabilityData);
                        Log.d("cardFirebase", profileData.getCitizenID());
                        Intent intent = new Intent(CardReader.this, Profile.class);
                        intent.putExtra("data", people);
                        intent.putExtra("contactData", contactData);
                        intent.putExtra("picturePath",picturePath);
                        intent.putExtra("pictureUri",pictureUri);
//                        intent.putExtra("pictureBitmap",pictureBitmap);
//                        for(int i = 0; i<3 ; i++)
//                            intent.putExtra("pictureUri"+i,pictureUri[i].toString());
//                            intent.putExtra("pictureBitmap"+i,pictureBitmap[i]);
                        startActivity(intent);
                    } catch (Exception e) {
                        //textView.setText(e.getMessage() + "" );
                    }

                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CardReader.this);

                    // set title
                    //alertDialogBuilder.setTitle("ไม่พบข้อมูล/หมายเลขบัตรไม่ถูกต้อง");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("ไม่พบข้อมูล/หมายเลขบัตรไม่ถูกต้อง")
                            .setCancelable(false)
                            .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    //MainActivity.this.finish();
                                }
                            });
//                                .setNegativeButton("No",new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        // if this button is clicked, just close
//                                        // the dialog box and do nothing
//                                        dialog.cancel();
//                                    }
//                                });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void FindContactDataFromFirebase() {
        DatabaseReference ref = database.getReference("EmergencyContact");
        //Log.d("FindCo ",people.getContactID());
        ref.orderByKey().equalTo(people.getContactID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        ContactData  data = issue.getValue(ContactData.class);
                        // do something with the individual "issues"
                        Log.d(data.getCitizenID(), "onDataChange: ");
                        Log.d("MMMMMMMMMMMMMMMMMMMMM", "onDataChange: ");
                        setContactData(data);
                    }
                }
                Intent intent = new Intent(CardReader.this, Profile.class);
                intent.putExtra("data", people);
                intent.putExtra("contactData", contactData);
                intent.putExtra("picturePath",picturePath);
                intent.putExtra("pictureUri",pictureUri);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //textView.setText(databaseError.getMessage());
                Log.d(databaseError.getMessage(), "onCancelled: ");
                Log.d("FFFFFFFFFFFF", "onDataChange: ");
            }
        });

    }


    public ContactData getContactData() {
        return this.contactData;
    }

    public void setContactData(ContactData contactData) {
        this.contactData = contactData;
    }

    public People getPeople() {
        return this.people;
    }

    public void setPeople(People people) {
        this.people = people;
    }
}