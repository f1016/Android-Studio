// Kam Fung CHEUNG 1155110263
package edu.cuhk.csci3310.cublossom;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FlowerListAdapter mAdapter;

    // Initially a list storing image path
    private LinkedList<String> mImagePathList = new LinkedList<>();
    private String mDrawableFilePath = "android.resource://edu.cuhk.csci3310.cublossom/drawable/";

    // You may define more data members as needed
    // ... Rest of MainActivity code ...

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initially put random data into the image list, modify to pass correct info read from CSV
        try {
            storeRawFileToInternal(); // to store the csv to the internal space

            FileInputStream fis = openFileInput("temp_cu_flowers.csv"); // read the file from internal space
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader is = new BufferedReader(inputStreamReader);

            boolean end = false;

            String csvLine;
            is.readLine(); //skip the first line

            while(!end) {
                csvLine = is.readLine();

                if(csvLine == null){
                    end = true;
                }else{
                    mImagePathList.addLast(mDrawableFilePath + csvLine.split("\\.")[0]); // add the flower's image path
                }
            }

        } catch (Exception e) {
            mImagePathList.addLast(mDrawableFilePath + "temp");
            e.printStackTrace();
        }

        // Get a handle to the RecyclerView.
        mRecyclerView = findViewById(R.id.recyclerview);
        // Create an adapter and supply the data to be displayed,
        // initially just a list of image path
        // Update as needed...
        mAdapter = new FlowerListAdapter(this, mImagePathList);

        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void readRawFileToInternal() {
        try{
            FileInputStream fis = openFileInput("temp_cu_flowers.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                Log.d("hello", line);
            }
        } catch (Exception e) {
            // Error occurred when opening raw file for reading.
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void storeRawFileToInternal() {
        InputStream raw = getResources().openRawResource(R.raw.cu_flowers);
        File file = new File(getFilesDir().getAbsolutePath()+"/temp_cu_flowers.csv");
        if(file.exists()){
            Log.d("hello",getFilesDir().getAbsolutePath()); // check if the file is already exist
        }else{

            try (FileOutputStream fos = openFileOutput("temp_cu_flowers.csv", Context.MODE_PRIVATE)) { //copy to the internal space from res
                BufferedReader is = new BufferedReader(new InputStreamReader(raw, "UTF8"));
                boolean end = false;
                String line = is.readLine();
                while(!end) {
                    fos.write(line.getBytes());
                    fos.write("\n".getBytes());
                    line = is.readLine();
                    if(line == null){
                        end = true;
                    }
                }
                fos.flush();
                Log.d("hello","all sent!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Overriding extra callbacks, e.g. onResume(), onActivityResult() etc. here

    @Override
    protected void onRestart() {
        super.onRestart();
        mAdapter.notifyDataSetChanged(); // notice the adapter to refresh the data
    }
}
