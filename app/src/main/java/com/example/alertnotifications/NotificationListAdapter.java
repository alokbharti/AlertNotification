package com.example.alertnotifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationViewHolder> {

    private List<NotificationList> notificationList;
    private Context mContext;
    private String deletedText="";

    public NotificationListAdapter(List<NotificationList> notificationList, Context mContext) {
        this.notificationList = notificationList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public NotificationListAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.notification_list,viewGroup,false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationListAdapter.NotificationViewHolder notificationViewHolder, int position) {

        final SharedPreferences sharedPref = mContext.getSharedPreferences("CATEGORY_TITLE",Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        String getDeletedText = sharedPref.getString("DELETED_TEXT","");
        Log.d("deleted text",getDeletedText);

        String[] mDeletedText={};
        if(getDeletedText.length()!=0){
            mDeletedText=getDeletedText.split(",");
            Log.d("deleted text",mDeletedText.toString());
        }

        final NotificationList list = notificationList.get(position);
        String titleOfCategory = list.getCategory()+"-"+list.getTitle();

        if(!Arrays.asList(mDeletedText).contains(titleOfCategory)){
            notificationViewHolder.dummyNotificationTextView.setText(list.getTitle());
        }else{
            notificationViewHolder.dummyCardView.setVisibility(View.GONE);
        }

        notificationViewHolder.dummyNotificationDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationViewHolder.dummyCardView.setVisibility(View.GONE);
                deletedText = sharedPref.getString("DELETED_TEXT","");
                deletedText+=(list.getCategory()+"-"+list.getTitle()+",");
                editor.putString("DELETED_TEXT",deletedText);
                editor.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }


    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView dummyNotificationTextView;
        ImageButton dummyNotificationDeleteButton;
        CardView dummyCardView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            dummyNotificationTextView = (TextView) itemView.findViewById(R.id.dummy_notification_text);
            dummyNotificationDeleteButton = (ImageButton) itemView.findViewById(R.id.third_category_delete_text);
            dummyCardView = (CardView)itemView.findViewById(R.id.dummy_CardView);

        }
    }
}
