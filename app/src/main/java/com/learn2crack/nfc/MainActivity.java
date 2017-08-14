package com.learn2crack.nfc;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.learn2crack.nfc.shopowneractivity.SellfoodActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    
    public static final String TAG = MainActivity.class.getSimpleName();

    private Button mBtnAllUsers;
    private Button mBtnSell;
    private Button mBtnLogin;
    private Button mGenPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnAllUsers = (Button) findViewById(R.id.btn_allusers);
        mBtnSell = (Button) findViewById(R.id.btn_sell);
        mBtnLogin = (Button) findViewById(R.id.btn_login_staff);
        mGenPdf = (Button) findViewById(R.id.gen_pdf);

        SharedPreferences pref = getSharedPreferences("permission", 0);
        String prefRole = pref.getString("role", null);
        String role = prefRole == null ? "shop owner" : "staff" ;
        boolean isStaff = role.equals("staff");

        Toast.makeText(MainActivity.this, "You are " + role, Toast.LENGTH_SHORT).show();

        mBtnAllUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UsersListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mBtnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SellfoodActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (!isStaff) {
            mBtnLogin.setText("LOG IN AS STAFF");
        } else {
            mBtnLogin.setText("REMOVE MYSELF FROM ROLE STAFF");
        }


        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStaff) {
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    SharedPreferences pref = getSharedPreferences("permission", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.remove("role");
                    editor.commit();

                    finish();
                    startActivity(getIntent());
                }
            }
        });

        mGenPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    testGeneratePDF();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * PLEASE DO THESE FOLLOWING STEPS :
     * 1. Go to Setting
     * 2. Go to Applications
     * 3. Go to NFC application
     * 4. Take a look on Permissions
     * 5. If below Permissions display "No permissions allowed", tap it and then enable Storage permisson
     * 6. Finally the text below Permissions should be "Storage"
     * @throws Exception
     */
    private void testGeneratePDF() throws Exception {
        Document doc = new Document();
        boolean isCreateSuccess = true;
        String fileName = "/newFile.pdf";

        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if(!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, fileName);
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            Paragraph p1 = new Paragraph("THIS IS THE TEST TEXT ON PDF");
            // Font paraFont= new Font(Font.BOLDITALIC);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            // p1.setFont(paraFont);

            //add paragraph to document
            doc.add(p1);

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
            isCreateSuccess = false;
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
            isCreateSuccess = false;
        }
        finally {
            doc.close();
        }

        if (isCreateSuccess) {
            viewPdf(fileName);
        } else {
            Toast.makeText(this, "Can't create pdf file", Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    // Method for opening a pdf file
    private void viewPdf(String file) {

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File pdfFile = new File(dir, file);
        Uri path = Uri.fromFile(pdfFile);

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            //requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }
}
