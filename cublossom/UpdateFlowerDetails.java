// Kam Fung CHEUNG 1155110263
package edu.cuhk.csci3310.cublossom;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import java.util.HashMap;

public class UpdateFlowerDetails extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    ImageView flowerImageView;
    TextView nameTextView, wikiTextView, rangeTextView;
    Button btn_minus, btn_plus, btn_save, btn_cancel;
    int position, temp_range;
    String temp_name, temp_wiki, temp_img;

    private String mDrawableFilePath = "android.resource://edu.cuhk.csci3310.cublossom/drawable/";
    HashMap<Integer, String> richnessMap = new HashMap<Integer, String>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_flower_details);

        Intent intent = getIntent(); // get the intent message which is the position of the flower
        position = Integer.parseInt(intent.getStringExtra(FlowerListAdapter.EXTRA_MESSAGE));
        setDefaultView();
    }

    private void setDefaultView() {
        btn_minus = (Button) findViewById(R.id.minus);  // create all view in the page
        btn_plus = (Button) findViewById(R.id.plus);
        btn_save = (Button) findViewById(R.id.save);
        btn_cancel = (Button) findViewById(R.id.cancel);
        nameTextView = (EditText) findViewById(R.id.name);
        wikiTextView = (EditText) findViewById(R.id.wiki);
        rangeTextView = (TextView) findViewById(R.id.range);
        flowerImageView = (ImageView) findViewById(R.id.flower_pic);

        btn_minus.setOnClickListener(this); // set up listener
        btn_plus.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        nameTextView.addTextChangedListener( this); // set up listener
        wikiTextView.addTextChangedListener(this);

        richnessMap.put(1, "Scattered"); // create a hash map for the mapping of 1-3
        richnessMap.put(2, "Clustered");
        richnessMap.put(3, "Hill-Full");
        try {
            String csvLine;
            FileInputStream fis = openFileInput("temp_cu_flowers.csv"); // read csv
            InputStreamReader inputStreamReader = new InputStreamReader(fis, "UTF_8");
            BufferedReader is = new BufferedReader(inputStreamReader);

            for(int i = 0; i < position + 1; i++){
                is.readLine();
            }
            csvLine = is.readLine();

            this.temp_img = csvLine.split(",")[0]; // store the initial data
            flowerImageView.setImageURI(Uri.parse(mDrawableFilePath + csvLine.split("\\.")[0]));

            this.temp_name = csvLine.split(",")[1]; // store the initial data
            nameTextView.setText(csvLine.split(",")[1]);

            this.temp_wiki = csvLine.split(",")[2]; // store the initial data
            wikiTextView.setText(csvLine.split(",")[2]);

            this.temp_range = Integer.parseInt(csvLine.split(",")[3]); // store the initial data
            rangeTextView.setText(this.richnessMap.get(Integer.parseInt(csvLine.split(",")[3])));

            is.close();
        } catch (Exception e) {
            Log.d("hello", e.toString());
        }

    }


    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.minus:
                    if(temp_range > 1){
                        temp_range--;
                    }
                    rangeTextView.setText(richnessMap.get(temp_range)); // set the text based on the range
                    break;

                case R.id.plus:
                    if(temp_range < 3){
                        temp_range++;
                    }
                    rangeTextView.setText(richnessMap.get(temp_range));  // set the text based on the range
                    break;

                case R.id.save:
                    try{
                        // Store the new csv based on the current input text
                        FileInputStream fis = openFileInput("temp_cu_flowers.csv");
                        InputStreamReader inputStreamReader = new InputStreamReader(fis, "UTF_8");
                        BufferedReader reader = new BufferedReader(inputStreamReader);

                        //buffer string
                        StringBuffer inputBuffer = new StringBuffer();

                        String line = reader.readLine();
                        inputBuffer.append("filename,flower_name,genus,richness\n");
                        for(int i = 0; i < position; i++){ // skip through the lines
                            line = reader.readLine();
                            inputBuffer.append(line);
                            inputBuffer.append('\n');
                        }

                        inputBuffer.append(temp_img +","+temp_name+","+temp_wiki+","+temp_range); // replace the specific line in CSV
                        inputBuffer.append('\n');
                        reader.readLine();

                        while((line = reader.readLine()) != null){  // skip through the remaining lines
                            inputBuffer.append(line);
                            inputBuffer.append('\n');
                        }
                        fis.close();

                        //store the new CSV
                        FileOutputStream fos = openFileOutput("temp_cu_flowers.csv", Context.MODE_PRIVATE);
                        fos.write(inputBuffer.toString().getBytes());
                        fos.flush();
                        fos.close();

                        super.finish(); //return to the last activity
                    } catch (Exception e) {
                        Log.d("hello3",e.getMessage());
                    }

                    break;

                case R.id.cancel: // cancel will reset to the defaul text
                    setDefaultView();
                    break;

                default:
                    Log.d("hello", view.getContext().toString());
            }
        }catch (Exception e){
            Log.d("hello", e.getMessage());
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        temp_wiki = wikiTextView.getText().toString();
        temp_name = nameTextView.getText().toString();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
}
