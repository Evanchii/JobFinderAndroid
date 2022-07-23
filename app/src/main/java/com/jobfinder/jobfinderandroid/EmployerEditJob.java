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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class EmployerEditJob extends AppCompatActivity {

    private String mode, jobKey;
    private DatabaseReference dbJob, dbUser;
    private EditText title, loc, vacancy, salary, desc, skill, exp;
    private Spinner spec, nature;
    private ImageView iv;
    private Button btnIcon;

    private String upLoadServerUri = null, filename, sourcePath;
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("Employer JobFinder");
        setContentView(R.layout.activity_employer_edit_job);

        jobKey = getIntent().getStringExtra("jobKey");
        if(jobKey != null) {
            getSupportActionBar().setTitle("Edit Job");
            mode = "update";
        } else {
            getSupportActionBar().setTitle("New Job");
            mode = "create";
        }

        dbJob = FirebaseDatabase.getInstance().getReference("jobs");
        title = findViewById(R.id.editJob_inputTitle);
        loc = findViewById(R.id.editJob_inputLoc);
        vacancy = findViewById(R.id.editJob_inputVacancy);
        salary = findViewById(R.id.editJob_inputSalary);
        desc = findViewById(R.id.editJob_inputDesc);
        skill = findViewById(R.id.editJob_inputSkills);
        exp = findViewById(R.id.editJob_inputExp);

        spec = findViewById(R.id.editJob_spnSpec);
        nature = findViewById(R.id.editJob_spnNature);

        btnIcon = findViewById(R.id.editJob_btnIcon);
        iv = findViewById(R.id.editJob_imgJobIcon);

        upLoadServerUri = "https://www.jobfinder.cf/android/upload-job.php";

        ActivityCompat.requestPermissions(EmployerEditJob.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        if(mode.equals("update")) {
            dbJob = dbJob.child(jobKey);
            dbJob.get().addOnCompleteListener(task -> {
                if(task.isComplete() && task.isSuccessful()) {
                    DataSnapshot data = task.getResult();
                    title.setText(data.child("jobTitle").getValue().toString());
                    loc.setText(data.child("jobLocation").getValue().toString());
                    vacancy.setText(data.child("vacancy").getValue().toString());
                    salary.setText(data.child("salary").getValue().toString());
                    desc.setText(data.child("description").getValue().toString());
                    skill.setText(data.child("reqSkill").getValue().toString());
                    exp.setText(data.child("reqExperience").getValue().toString());

                    int posSpec = 0, posNature = 0;

                    switch(data.child("jobNature").getValue().toString()) {
                        case "Full Time":
                            posNature = 0;
                            break;
                        case "Part Time Job":
                            posNature = 1;
                            break;
                        case "Internship":
                            posNature = 2;
                            break;
                        case "Temporary Job":
                            posNature = 3;
                            break;
                        case "Summer Job":
                            posNature = 4;
                            break;
                    }

                    switch(data.child("category").getValue().toString()) {
                        case "Accounting/Finance":
                            posSpec = 0;
                            break;
                        case "Admin/Human Resource":
                            posSpec = 1;
                            break;
                        case "Sales/Marketing":
                            posSpec = 2;
                            break;
                        case "Arts/Media/Communication":
                            posSpec = 3;
                            break;
                        case "Hotel/Restaurant Services":
                            posSpec = 4;
                            break;
                        case "Education/Training":
                            posSpec = 5;
                            break;
                        case "Computer/Information Technology":
                            posSpec = 6;
                            break;
                        case "Engineering":
                            posSpec = 7;
                            break;
                        case "Manufacturing":
                            posSpec = 8;
                            break;
                        case "Building/Construction":
                            posSpec = 9;
                            break;
                        case "Science":
                            posSpec = 10;
                            break;
                        case "Healthcare":
                            posSpec = 11;
                            break;
                        case "Others":
                            posSpec = 12;
                            break;
                    }

                    spec.setSelection(posSpec);
                    nature.setSelection(posNature);
                }
            });

            new DownloadImageTask(iv)
                    .execute("https://www.jobfinder.cf/uploads/jobs/"+jobKey+".png");

        }
    }

    public void postJob(View view) {
        if(filename == null) {
            Toast.makeText(view.getContext(), "Please upload job icon!", Toast.LENGTH_SHORT).show();
        } else {
            dialog = ProgressDialog.show(EmployerEditJob.this, "", "Uploading data...", true);

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            dbUser = FirebaseDatabase.getInstance().getReference("user/employer/"+mAuth.getUid());
            dbUser.get().addOnCompleteListener(task -> {
                if(task.isComplete() && task.isSuccessful()) {
                    DataSnapshot data = task.getResult();
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    jobKey = jobKey != null ? jobKey : String.valueOf(timestamp.getTime());
                    dbJob = FirebaseDatabase.getInstance().getReference("jobs/"+jobKey);

                    dbJob.child("address").setValue(data.child("address").getValue().toString());
                    dbJob.child("category").setValue(spec.getSelectedItem().toString());
                    dbJob.child("companyName").setValue(data.child("company").getValue().toString());
                    dbJob.child("description").setValue(data.child("description").getValue().toString());
                    dbJob.child("email").setValue(mAuth.getCurrentUser().getEmail());
                    dbJob.child("employerID").setValue(mAuth.getUid());
                    dbJob.child("jobDescription").setValue(desc.getText().toString());
                    dbJob.child("jobLocation").setValue(loc.getText().toString());
                    dbJob.child("jobNature").setValue(nature.getSelectedItem().toString());
                    dbJob.child("jobTitle").setValue(title.getText().toString());
                    dbJob.child("phone").setValue(data.child("phone").getValue().toString());
                    dbJob.child("reqExperience").setValue(exp.getText().toString());
                    dbJob.child("reqSkill").setValue(skill.getText().toString());
                    dbJob.child("salary").setValue(salary.getText().toString());
                    dbJob.child("vacancy").setValue(vacancy.getText().toString());
                    dbJob.child("website").setValue(data.child("website").getValue().toString());

                    dbUser.child("postedJobs/"+jobKey).setValue(jobKey);

                    new Thread(() -> {
                        uploadFile(sourcePath + "/" + filename, "jobKey="+jobKey);
                    }).start();
                }
            });
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openFileChooser(View view){
        Intent data = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        data.setType("image/*");
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

                        btnIcon.setText("File: " + filename);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        iv.setImageBitmap(bitmap);

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
            Toast.makeText(EmployerEditJob.this, "Source File not exist :"+sourcePath + "/" + filename, Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(EmployerEditJob.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(EmployerEditJob.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(EmployerEditJob.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
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
