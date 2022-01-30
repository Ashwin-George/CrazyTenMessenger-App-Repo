package com.example.android.crazytenmessenger;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.crazytenmessenger.utils.DateUtils;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {
    private static final String TAG=MessageAdapter.class.getSimpleName();
    public MessageAdapter(@NonNull Context context, int resource, @NonNull List<Message> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView=((Activity)getContext()).getLayoutInflater().inflate(R.layout.item_message,parent,false);
        }
        Message message=getItem(position);
        TextView msgtextView=(TextView) convertView.findViewById(R.id.messageTextView);
        TextView nameTextView=(TextView) convertView.findViewById(R.id.nameTextView);
        ImageView photoImageView=(ImageView) convertView.findViewById(R.id.photoImageView);
        LinearLayout fullMessageView=(LinearLayout) convertView.findViewById(R.id.fullMessageBox);
        ProgressBar imageProgressBar=(ProgressBar) convertView.findViewById(R.id.image_progressbar);
        TextView timeTextView=(TextView) convertView.findViewById(R.id.timeTextView);

        boolean isPhoto=message.getPhotoUrl()!=null;

        if(isPhoto){
            imageProgressBar.setVisibility(View.VISIBLE);
            msgtextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG,"Image can't be loaded "+e.getMessage());
                            Toast.makeText(getContext(),"Still loading.... ",Toast.LENGTH_SHORT).show();
                            return false;

                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            imageProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
//                    .error(R.drawable.still_loading)
                    .fitCenter()
                    .into(photoImageView);
        }else{
            imageProgressBar.setVisibility(View.GONE);
            msgtextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            msgtextView.setText(message.getMessage());
        }
        nameTextView.setText(message.getAuthor());
        String dateString= DateUtils.getSampleFormattedDate(message.getTimeStamp());
        timeTextView.setText(dateString);

        return convertView;
    }
}
