package gov.texas.tpwd.mobileranger.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gov.texas.tpwd.mobileranger.R;
import gov.texas.tpwd.mobileranger.TreeLocation;

public class TreeLocationAdapter extends RecyclerView.Adapter<TreeLocationAdapter.LocationHolder> {

    private int size;
    private List<TreeLocation> treeLocations;
    private Activity context;
    private int cameraRequestPosition = -1;

    public TreeLocationAdapter(Activity context, int size) {
        this.size = size;
        this.context = context;
        treeLocations = new ArrayList<TreeLocation>();
        for(int i = 0; i < size; i++) {
            treeLocations.add(new TreeLocation());
        }
    }

    public List<TreeLocation> getTreeLocations() {
        return treeLocations;
    }

    @Override
    public LocationHolder onCreateViewHolder(ViewGroup parent, int i) {
        Log.d("Adapter", "create view holder location " + i);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tree_report, parent, false);
        LocationHolder holder = new LocationHolder(view);
        holder.locationEditText = (EditText) view.findViewById(R.id.locationEdit);
        holder.detailsEditText = (EditText) view.findViewById(R.id.detailText);
        holder.actionTakenEditText = (EditText) view.findViewById(R.id.actionTakenText);
        holder.beforeImageView = (ImageView) view.findViewById(R.id.beforeImage);
        holder.beforeButton = (Button) view.findViewById(R.id.buttonBefore);
        holder.afterImageView = (ImageView) view.findViewById(R.id.afterImage);
        holder.afterButton = (Button) view.findViewById(R.id.buttonAfter);
        return holder;
    }

    @Override
    public void onBindViewHolder(LocationHolder locationHolder, final int position) {
        final TreeLocation treeLocation = treeLocations.get(position);
        locationHolder.treeLocation = treeLocation;
        locationHolder.locationEditText.setText(treeLocation.getLocation());
        locationHolder.detailsEditText.setText(treeLocation.getDetails());
        locationHolder.actionTakenEditText.setText(treeLocation.getActionTaken());
        Glide.with(context).load(treeLocation.getBeforeImagePath()).into(locationHolder.beforeImageView);
        Glide.with(context).load(treeLocation.getAfterImagePath()).into(locationHolder.afterImageView);

        locationHolder.beforeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                treeLocation.setBeforeImagePath(startCamera(MainActivity.BEFORE_PHOTO_REQUEST_CODE, position).getAbsolutePath());
            }
        });
        locationHolder.afterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                treeLocation.setAfterImagePath(startCamera(MainActivity.AFTER_PHOTO_REQUEST_CODE, position).getAbsolutePath());
            }
        });

    }

    private File startCamera(int requestCode, int position) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + System.currentTimeMillis() + ".png";
        File file = new File(path);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        cameraRequestPosition = position;
        context.startActivityForResult(intent, requestCode);
        return file;
    }

    public void updateBeforeImage() {
        notifyDataSetChanged();

    }

    public void updateAfterImage() {
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(LocationHolder holder) {
        super.onViewRecycled(holder);
        holder.treeLocation.setLocation(holder.locationEditText.getText().toString());
        holder.treeLocation.setDetails(holder.detailsEditText.getText().toString());
        holder.treeLocation.setActionTaken(holder.actionTakenEditText.getText().toString());

    }

    @Override
    public int getItemCount() {
        return size;
    }

    public void incrementSize() {
        size++;
        treeLocations.add(new TreeLocation());
        notifyDataSetChanged();
    }

    public static class LocationHolder extends RecyclerView.ViewHolder {
        TreeLocation treeLocation;
        EditText locationEditText;
        EditText detailsEditText;
        EditText actionTakenEditText;
        ImageView beforeImageView;
        Button beforeButton;
        ImageView afterImageView;
        Button afterButton;

        public LocationHolder(View itemView) {
            super(itemView);
        }
    }

}
