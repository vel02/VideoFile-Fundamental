package com.geekier.vel.videofile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final int CAMERA_REQUEST_CODE_VIDEO = 1;
    private final int REQUEST_TAKE_GALLERY_VIDEO = 2;

    private MediaController mediaController;
    private VideoView videoview;

    private Button btnRecord;
    private Button btnPlay;
    private Button btnGallery;

    private String path;
    private String vidPath;

    @SuppressLint("SetWorldReadable")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRecord = findViewById(R.id.btn_record);
        btnPlay = findViewById(R.id.btn_play);
        btnGallery = findViewById(R.id.btn_gallery);
        videoview = findViewById(R.id.videoview);


        btnRecord.setOnClickListener(view -> {

            //STEP #1: capture video
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takeVideoIntent, CAMERA_REQUEST_CODE_VIDEO);
            }

        });

        btnPlay.setOnClickListener(view -> {

//            File mydir = getDir("Videos", Context.MODE_PRIVATE);
//            File fileWithinMyDir = new File(mydir, "VID_20220515_061436.mp4");
//            fileWithinMyDir.setReadable(true, false);
//            String videoResource = fileWithinMyDir.getPath();
//            Uri intentUri = Uri.fromFile(new File(videoResource));
//
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setDataAndType(intentUri, "video/mp4");
//            startActivity(intent);

            //STEP #5: playing the video using video view by getting the path.
            //RESOURCE: https://parallelcodes.com/android-how-to-play-videos-using-videoview/
            mediaController = new MediaController(this);
            mediaController.setAnchorView(videoview);

            String filePath = Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Camera" + File.separator + "personal_safety_video.mp4"; //+ File.separator + "Video" + File.separator + "personal_safety_video.mp4";//+ "DCIM" + File.separator + "Camera" + File.separator + "VID_20220515_064941.mp4";

            Log.e("VIDEO", filePath);

            videoview.setMediaController(mediaController);
            videoview.setVideoURI(Uri.parse(filePath));//Uri.parse(filePath));
            videoview.start();
        });

        btnGallery.setOnClickListener(view -> {
//            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//            intent.setType("video/*");
//            startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);


            //STEP #1: getting a video in gallery
            Intent videoPickIntent = new Intent(Intent.ACTION_PICK);
            videoPickIntent.setType("video/*");
            startActivityForResult(Intent.createChooser(videoPickIntent, "Please pick a video"), REQUEST_TAKE_GALLERY_VIDEO);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE_VIDEO) {
            //STEP #2: Getting the uri and its path of the video
            Uri videoUri = data.getData();
            path = getRealPathFromURI(this, videoUri);

            //STEP #3: get the current filename of the video
            List<String> pathSegments = Arrays.asList(path.split("/"));
            String lastSegment = pathSegments.get(pathSegments.size() - 1);
            Log.e("VIDEO", lastSegment);


            //STEP #4: rename a file
            File loc = Environment.getExternalStorageDirectory();
            File currentFile = new File(loc.getAbsolutePath() + File.separator + "DCIM" + File.separator + "Camera" + File.separator + lastSegment);//"/sdcard/currentFile.txt");
            File newFile = new File(loc.getAbsolutePath() + File.separator + "DCIM" + File.separator + "Camera" + File.separator + "personal_safety_video.mp4");//"/sdcard/newFile.txt");

            if (rename(currentFile, newFile)) {
                //Success
                Log.i("VIDEO", "Success");
            } else {
                //Fail
                Log.i("VIDEO", "Fail");
            }


            //saveVideoToInternalStorage(path);

            manageVideo(path); //Do whatever you want with your video

        } else if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
            if (data == null) return;


            Uri selectedVideoUri = data.getData();

            // OI FILE Manager
            String filemanagerstring = selectedVideoUri.toString();
            vidPath = filemanagerstring;

            // MEDIA GALLERY
            String selectedVideoPath = getPath(selectedVideoUri);
            if (selectedVideoPath != null) {
                vidPath = selectedVideoPath;
                Log.e("VIDEO", "MEDIA PATH " + vidPath);
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        getContentResolver();
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    private boolean rename(File from, File to) {
        return from.getParentFile().exists() && from.exists() && from.renameTo(to);
    }


    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void manageVideo(String path) {
        Log.e("VIDEO", "PATH = " + path);
    }


    //    private void videoHandler() {
//        Intent intent = new Intent( MediaStore.ACTION_VIDEO_CAPTURE );
//        if ( intent.resolveActivity( getPackageManager() ) != null ) {
//            File videoFile = null;
//            try {
//                videoFile = createVideoFile();
//            } catch ( IOException e ) {
//                System.out.println( "Error creating file." );
//            }
//
//            if ( videoFile != null ) {
//                videoUri = Uri.fromFile( videoFile );
//                intent.putExtra( MediaStore.EXTRA_OUTPUT, videoUri );
//                intent.putExtra( MediaStore.EXTRA_VIDEO_QUALITY, 1 );
//                intent.putExtra( MediaStore.EXTRA_SIZE_LIMIT, 15000000L );
//                intent.putExtra( MediaStore.EXTRA_DURATION_LIMIT, 6 );
//                startActivityForResult( intent, VIDEO_REQUEST_CODE );
//            }
//        }
//    }

//    private File createVideoFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( new Date() );
//        String imageFileName = "MP4_" + timeStamp + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES );
//        File video = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".mp4",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        this.videoUri = Uri.fromFile( video );
//        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE );
//        mediaScanIntent.setData( this.videoUri );
//        this.sendBroadcast( mediaScanIntent );
//        return video;
//    }

//    private void saveVideoToInternalStorage(String filePath) {
////        UUID uuid = UUID.randomUUID();
//
//        try {
//
//            File currentFile = new File(filePath);
//            File loc = Environment.getExternalStorageDirectory();
//            File directory = new File(loc.getAbsolutePath() + File.separator + "DCIM" + File.separator + "Camera");//+ "/Video");
//            directory.mkdir();
//            String fileName = String.format("personal_safety_video" + ".mp4");
//            File newfile = new File(directory, fileName);
//
//            Log.e("VIDEO", newfile.getAbsolutePath());
//
//
//            if (currentFile.exists()) {
//
//                InputStream inputStream = new FileInputStream(currentFile);
//                OutputStream outputStream = new FileOutputStream(newfile);
//
//                byte[] buf = new byte[1024];
//                int len;
//
//                while ((len = inputStream.read(buf)) > 0) {
//                    outputStream.write(buf, 0, len);
//                }
//
//                outputStream.flush();
//                inputStream.close();
//                outputStream.close();
//
//                Toast.makeText(getApplicationContext(), "Video has just saved!!", Toast.LENGTH_LONG).show();
//
//            } else {
//                Toast.makeText(getApplicationContext(), "Video has failed for saving!!", Toast.LENGTH_LONG).show();
//            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
}