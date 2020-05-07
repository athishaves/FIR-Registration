package com.athishWorks.policeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FirForm
        extends AppCompatActivity
        implements FragmentPage2.FragmentPage2Listener, FragmentPage1.FragmentPage1Listener {

    PaintView paintView;

    int screenWidth, screenHeight;

    TextView psDist, psPoliceStation;

    String psDistName, psPoliceStationName;

    ProgressDialog progressDialog;

    int count;

    Fragment fragmentPage1, fragmentPage2;

    String cName, cFatherName, cDOB, cNationality, cAddress;
    String cComplaint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fir_form);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        cDeclarations();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Keys");
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        ref.child(user.substring(0, user.indexOf("@"))).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String a;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    switch (ds.getKey()) {
                        case "Dist":
                            psDistName = ds.getValue().toString();
                            a = "District : " + psDistName;
                            psDist.setText(a);
                            break;
                        case "PS":
                            psPoliceStationName = ds.getValue().toString();
                            a = "Police Station : " + psPoliceStationName;
                            psPoliceStation.setText(a);
                            break;
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ActivityCompat.requestPermissions(
                this,
                 new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);

        paintView = findViewById(R.id.paintView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        ViewGroup.LayoutParams params = paintView.getLayoutParams();
        params.width = screenWidth = 98*displayMetrics.widthPixels/100;
        params.height = screenHeight = 15*displayMetrics.heightPixels/100;

        paintView.setLayoutParams(params);
        paintView.initialise(displayMetrics);


        FrameLayout f = findViewById(R.id.paintFrameLayout);
        params = f.getLayoutParams();
        params.width = displayMetrics.widthPixels;
        params.height = 16*displayMetrics.heightPixels/100;
        f.setLayoutParams(params);

        defaultFragment();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                if (mAuth.getCurrentUser()!=null) {
                    mAuth.signOut();
                }
                startActivity(new Intent(this, SignInSignUp.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cDeclarations() {
        psDist = findViewById(R.id.psDistrict);
        psPoliceStation = findViewById(R.id.psPoliceStation);

        fragmentPage1 = new FragmentPage1();
        fragmentPage2 = new FragmentPage2();

        count = 1000;

        cName = "";
        cFatherName = "";
        cDOB = "";
        cNationality = "";
        cAddress = "";
        cComplaint = "";
    }

    public void createPDF(View view) {

        SaveSignature();

        int pageWidth = 1000, pageHeight = 1500;
        int margin = 25;
        int linespace = 5;
        SharedPreferences preferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int complaintNumber = preferences.getInt("ComplaintNumber", 7500);

        ProgressDialog progressDialog = new ProgressDialog(FirForm.this);
        progressDialog.show();


        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();

        // Title
        paint.setTextSize(55f);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(getString(R.string.first_information_report).toUpperCase(), (float) pageWidth/2, 90, paint);

        // Sub-Title
        paint.setTextSize(35f);
        canvas.drawText(getString(R.string.first_information_report_subtitle), (float) pageWidth/2, 140, paint);


        // Complaint Number
        paint.setTypeface(Typeface.DEFAULT);

        paint.setTextSize(30f);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setColor(Color.RED);
        canvas.drawText(String.valueOf(complaintNumber), (float) 9*pageWidth/10, 200, paint);


        // 1. Station Info
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(30f);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String date = dateFormat.format(c);
        String psInfo = "Dist : " + psDistName + "    " + "P.S : " + psPoliceStationName + "    " + "Date : " + date;
        canvas.drawText(psInfo, (float) pageWidth/2, 250, paint);



        // 6. Complainant's Title
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(getString(R.string.fir_complainant_title), margin - 5, 310, paint);


        // 6. Complainant's Details
        int x = margin, y = 315;
        String string = "(a) Name : " + cName + "\n"
                + "(b) Father's / Husband's Name : " + cFatherName + "\n"
                + "(c) Date / Year of Birth : " + cDOB;
        for (String line : string.split("\n")) {
            y += paint.descent() - paint.ascent() + linespace;
            canvas.drawText(line, x, y, paint);
        }

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("(d) Nationality : " + cNationality, pageWidth - x, y, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        for (String line : ("(e) Address : " + cAddress).split("\n")) {
            y += paint.descent() - paint.ascent() + linespace;
            canvas.drawText(line, x, y, paint);
            x += 100;
        }


        // 2. Complaint Title
        y += paint.descent() - paint.ascent() + linespace + 10;
        canvas.drawText(getString(R.string.complaint_text), margin - 5, y, paint);

        // Complaint
        x = margin+5;
        for (String line : cComplaint.split("\n")) {
            y += paint.descent() - paint.ascent() + linespace;
            canvas.drawText(line, x, y, paint);
        }



        // Signature Image Part

        String filePath;
        try {
            filePath = Environment.getExternalStorageDirectory().getPath() + "/FIR/Signatures";
            filePath += "/" + complaintNumber + ".png";
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            bitmap = Bitmap.createScaledBitmap(bitmap, 200, 100, true);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawBitmap(bitmap, 65, pageHeight - 260, paint);

            if (!(new File(filePath).delete())) {
                callAToast("Signature file isn't deleted");
            }

        } catch (Exception e) {
            e.printStackTrace();
            callAToast(e.getMessage());
            Log.i("Error", "Signature Error = " + e.getMessage());
        }


        // Signature Part
        x = 170;
        y = pageHeight - 100 - margin;
        paint.setTextSize(25f);


        canvas.drawText("(" + cName + ")", x, y-10, paint);

        for (String line : getString(R.string.fir_complainant_signature).split("\n")) {
            y += paint.descent() - paint.ascent() + linespace;
            canvas.drawText(line, x, y, paint);
        }



        pdfDocument.finishPage(page);

        filePath = Environment.getExternalStorageDirectory().getPath() + "/FIR/";
        File file = new File(filePath);
        if (!file.exists() && !file.mkdir()) {
            callAToast("FIR Folder couldn't be created");
        }
        filePath += "FIR" + complaintNumber + ".pdf";
        file = new File(filePath);

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
            callAToast(e.getMessage());
        }

        pdfDocument.close();
        progressDialog.dismiss();
        editor.putInt("ComplaintNumber", complaintNumber+1);
        editor.apply();
        callAToast("FIR Registered");


        if (!file.exists()) {
            return;
        }



        Uri uri = FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".provider", file);
        Intent share = new Intent();
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        this.startActivity(Intent.createChooser(share, "Choose a file to open PDF"));
    }



    private void callAToast(String a) {
        Toast.makeText(this, a, Toast.LENGTH_SHORT).show();
    }


    public void SaveSignature() {
        paintView.saveImage(String.valueOf(getSharedPreferences("SharedPrefs", MODE_PRIVATE)
                .getInt("ComplaintNumber", 7500)));
    }

    public void ClearSignature(View view) {
        paintView.clearCanvas();
    }


    public void ChangeFragment(View view) {

        if (view.getTag().toString().equals("0")) {
            count--;
        } else if (view.getTag().toString().equals("1")) {
            count++;
        }

        if (count<0) {
            count = 1000;
            return;
        }

        Fragment fragment;
        if (count%2==0) {
            fragment = fragmentPage1;
        } else {
            fragment = fragmentPage2;

        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fir_fragment, fragment)
                .commit();

    }

    private void defaultFragment() {
        Fragment fragment = fragmentPage1;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fir_fragment, fragment)
                .commit();
    }

    @Override
    public void sendPage2(String complaint) {
        cComplaint = complaint;
        Log.i("Complaint", "Complaint from Fragment " + complaint);
    }

    @Override
    public void sendPage1(String name, String fatherName, String DOB, String nationality, String address) {
        cName = name;
        cFatherName = fatherName;
        cDOB = DOB;
        cNationality = nationality;
        cAddress = address;
        Log.i("Complaint", "Complaint from Fragment \n" +
                cName + "\n" +
                cFatherName + "\n" +
                cDOB + "\n" +
                cNationality + "\n" +
                cAddress + "\n");
    }

}
