package com.jobfinder.jobfinderandroid;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.FilenameUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class JobApply extends AppCompatActivity {

    private String jobKey;
    private DatabaseReference dbJob, dbUser, dbNotifs, dbLogs;
    private FirebaseAuth mAuth;

    private TextInputEditText fname, lname, contact, email;

    private String upLoadServerUri = null, filename, sourcePath;
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Job Information");
        setContentView(R.layout.activity_job_apply);

        jobKey = getIntent().getStringExtra("jobKey");
        mAuth = FirebaseAuth.getInstance();

        dbJob = FirebaseDatabase.getInstance().getReference("jobs/" + jobKey);
        dbUser = FirebaseDatabase.getInstance().getReference("user/applicant/" + mAuth.getUid());
        dbNotifs = FirebaseDatabase.getInstance().getReference("notification");
        dbLogs = FirebaseDatabase.getInstance().getReference("userlogs");

        fname = findViewById(R.id.jobApply_inputFName);
        lname = findViewById(R.id.jobApply_inputLName);
        contact = findViewById(R.id.jobApply_inputContact);
        email = findViewById(R.id.jobApply_inputEmail);

        upLoadServerUri = "https://www.jobfinder.cf/android/upload-resume.php";

        ActivityCompat.requestPermissions(JobApply.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        ActivityCompat.requestPermissions(JobApply.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        dbUser.get().addOnCompleteListener(task -> {
            if(task.isComplete() && task.isSuccessful()) {
                DataSnapshot data = task.getResult();

                fname.setText(data.child("fname").getValue().toString());
                lname.setText(data.child("lname").getValue().toString());
                contact.setText(data.child("phone").getValue().toString());
                email.setText(data.child("email").getValue().toString());
            }
        });
    }

    public void uploadApplication(View view) {
        if(filename != null) {
            if(checkData(fname) && checkData(lname) &&
                    checkData(contact) && checkData(email)) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String ts = String.valueOf(timestamp.getTime());
                String uid = mAuth.getUid();
                dbJob.child(uid+"/applicantID").setValue(uid);
                dbJob.child(uid+"/applicantName").setValue(fname.getText() + " " + lname.getText());
                dbJob.child(uid+"/appliedAt").setValue(ts);
                dbJob.child(uid+"/status").setValue("Processing");
                dbJob.child(uid+"/phone").setValue(contact.getText());
                dbJob.child(uid+"/email").setValue(email.getText());
                dbJob.child(uid+"/resume").setValue(jobKey+"-"+uid+"."+ filename.split("\\.")[filename.split("\\.").length-1]);

                dbUser.child("jobsApplied/"+ts).setValue(ts);

                new CommonFunctions().createLog(view.getContext(), "Job Applied",
                        uid + " has sent in their application for job: "+jobKey, "Job Finding",
                        fname.getText() + " " + lname.getText(), uid);

                dbJob.get().addOnCompleteListener(task -> {
                    if(task.isComplete() && task.isSuccessful()) {
                        new CommonFunctions().createNotification(
                                task.getResult().child("employerID").getValue().toString(),
                                "employer",
                                "An applicant has applied!",
                                fname.getText() + " has sent in their application for "+jobKey,
                                "primary"
                        );
                    }
                });

                new Thread(() -> {
                    uploadFile(sourcePath + "/" + filename, "customFilename="+jobKey+"-"+uid);
                }).start();

            } else {
                Toast.makeText(view.getContext(), "Please fill all required data!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(view.getContext(), "Please upload your resume", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkData(TextInputEditText view) {
        TextInputLayout parent = (TextInputLayout) view.getParent();

        if(view.getText().equals("")) {
            parent.setError("Please input data.");
            parent.setErrorEnabled(true);
            return false;
        }

        parent.setErrorEnabled(false);
        return true;
    }

    public void openFileChooser(View view){
        Intent data = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        data.setType("*/*");
        data = Intent.createChooser(data, "Choose File");
        someActivityResultLauncher.launch(data);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                try {
                    Uri uri = data.getData();
                    String mimeType = getContentResolver().getType(uri);
                    if (mimeType == null) {
                        String path = getPath(this, uri);
                        if (path == null) {
                            filename = FilenameUtils.getName(uri.toString());
                        } else {
                            File file = new File(path);
                            filename = file.getName();
                        }
                    } else {
                        Uri returnUri = data.getData();
                        Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        returnCursor.moveToFirst();
                        filename = returnCursor.getString(nameIndex);
                        String size = Long.toString(returnCursor.getLong(sizeIndex));
                    }

                    File fileSave = getExternalFilesDir(null);
                    sourcePath = getExternalFilesDir(null).toString();

                    try {
                        copyFileStream(new File(sourcePath + "/" + filename), uri, this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ((Button) findViewById(R.id.jobApply_btnUpload)).setText("File: " + filename);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    );

    private void copyFileStream(File dest, Uri uri, Context context)
            throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
            os.close();
        }
    }

    public static String getPath(Context context, Uri uri) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // DocumentProvider
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public int uploadFile(String sourceFileUri, String Parameters) {
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.exists()) {

            dialog.dismiss();
            Log.e("uploadFile", "Source File not exist :"+sourcePath + "/" + filename);
            Toast.makeText(JobApply.this, "Source File not exist :"+sourcePath + "/" + filename, Toast.LENGTH_SHORT).show();

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...

                dos.writeBytes(lineEnd);
                String[] posts = Parameters.split("&");
                int max = posts.length;
                for (int i = 0; i < max; i++) {
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    String[] kv = posts[i].split("=");
                    dos
                            .writeBytes("Content-Disposition: form-data; name=\""
                                    + kv[0] + "\"" + lineEnd);
                    dos.writeBytes("Content-Type: text/plain"
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(kv[1]);
                    dos.writeBytes(lineEnd);
                }
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(JobApply.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(JobApply.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(JobApply.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e);
            }

            dialog.dismiss();
            finish();

            return serverResponseCode;

        } // End else block
    }
}