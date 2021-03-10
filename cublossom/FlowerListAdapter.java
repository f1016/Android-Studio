// Kam Fung CHEUNG 1155110263
package edu.cuhk.csci3310.cublossom;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

public class FlowerListAdapter extends Adapter<FlowerListAdapter.FlowerViewHolder>  {
    private Context context;
    private LayoutInflater mInflater;
    public static final String EXTRA_MESSAGE = "edu.cuhk.csci3310.cueat.extra.MESSAGE";
    private final LinkedList<String> mImagePathList;

    class FlowerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ArrayList<View> richnessImageItemView = new ArrayList<View>();
        ImageView flowerImageItemView;
        TextView nameTextView, wikiTextView;

        final FlowerListAdapter mAdapter;

        public FlowerViewHolder(View itemView, FlowerListAdapter adapter){
            super(itemView);
            flowerImageItemView = itemView.findViewById(R.id.image);
            nameTextView = itemView.findViewById(R.id.nameText);
            wikiTextView = itemView.findViewById(R.id.wikiText);
            richnessImageItemView.add(itemView.findViewById(R.id.richnessImageView));
            richnessImageItemView.add(itemView.findViewById(R.id.richnessImageView2));
            richnessImageItemView.add(itemView.findViewById(R.id.richnessImageView3));

            this.mAdapter = adapter;
            // Event handling registration, page navigation goes here
            wikiTextView.setOnClickListener(this);
            flowerImageItemView.setOnClickListener(this);
            // End of ViewHolder initialization
        }

        @Override
        public void onClick(View view) {
            try {
                switch (view.getId()) {
                    case R.id.wikiText: // start the intent
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/" + wikiTextView.getText().toString().split("@")[0]));
                        context.startActivity(browserIntent);
                        break;

                    case R.id.image: // start the intent
                        Intent intent = new Intent(view.getContext(), UpdateFlowerDetails.class);
                        intent.putExtra(EXTRA_MESSAGE, Integer.toString(getAdapterPosition()));
                        Log.d("hello", EXTRA_MESSAGE);
                        view.getContext().startActivity(intent);
                        break;

                    default:
                        Log.d("hello", view.getContext().toString());
                }
            }catch (Exception e){
                Log.d("hello", e.getMessage());
            }
        }
    }

    public FlowerListAdapter(Context context,LinkedList<String> imagePathList) {
        mInflater = LayoutInflater.from(context);
        this.mImagePathList = imagePathList;
        this.context = context;
    }

    @NonNull
    @Override
    public FlowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.imagelist_item, parent, false);

        return new FlowerViewHolder(mItemView, this);

    }

    @Override
    public void onBindViewHolder(@NonNull FlowerViewHolder holder, int position) {
        String mImagePath = mImagePathList.get(position);
        Uri uri = Uri.parse(mImagePath);
        // Update the following to display correct information based on the given position

        // Set up View items for this row (position), modify to show correct information read from the CSV
        holder.flowerImageItemView.setImageURI(uri);
        holder.nameTextView.setText("Flower #" + (position + 1));

        try {
            String csvLine = "not found";
            FileInputStream fis = this.context.openFileInput("temp_cu_flowers.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(fis, "UTF_8");
            BufferedReader reader = new BufferedReader(inputStreamReader);

            for(int i = 0; i < position + 1; i++){             // read from the CSV
                reader.readLine();
            }
            csvLine = reader.readLine();
            holder.nameTextView.setText(csvLine.split(",")[1]); //set up name
            holder.wikiTextView.setText(csvLine.split(",")[2].concat(" @wikipedia")); //set up wiki

            for(int i = 0; i < Integer.parseInt(csvLine.split(",")[3]); i++){
                holder.richnessImageItemView.get(i).setVisibility(View.VISIBLE);
            }

            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("hello2.2", e.toString());
        }
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mImagePathList.size();
    }



}
