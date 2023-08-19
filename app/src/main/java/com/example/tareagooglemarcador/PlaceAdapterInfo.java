package com.example.tareagooglemarcador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class PlaceAdapterInfo extends RecyclerView.Adapter<PlaceAdapterInfo.ViewHolder> {

    private final List<String> infoList;
    private final String photoUrl;
    private final Context context;

    public PlaceAdapterInfo(Context context, List<String> infoList, String photoUrl) {
        this.context = context;
        this.infoList = infoList;
        this.photoUrl = photoUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adaptadorinfo, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String info = infoList.get(position);

        holder.placeNameTextView.setText(info);

        Picasso.get().load(photoUrl).into(holder.placeImageView);
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView placeImageView;
        TextView placeNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            placeImageView = itemView.findViewById(R.id.placeImageView);
            placeNameTextView = itemView.findViewById(R.id.placeNameTextView);
        }
    }
}
