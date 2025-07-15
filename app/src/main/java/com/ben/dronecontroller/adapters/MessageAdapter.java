package com.ben.dronecontroller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ben.dronecontroller.data_classes.MessageModel;
import com.ben.dronecontroller.R;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context adapterContext;

    private final ArrayList<MessageModel> messageArray = new ArrayList<>();
    private final int VIEW_TYPE_SEND = 0;
    private final int VIEW_TYPE_RECEIVE = 1;


    public static class SendMessageViewHolder extends RecyclerView.ViewHolder{
        TextView messageText, timeText;

        public SendMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.messageText);
            timeText = itemView.findViewById(R.id.timeText);
        }
    }

    public static class ReceiveViewHolder extends RecyclerView.ViewHolder{
        TextView messageText, timeText;

        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.messageText);
            timeText = itemView.findViewById(R.id.timeText);
        }
    }


    public MessageAdapter(Context context){
        adapterContext = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SEND) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_message_view, parent, false);
            return new SendMessageViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receive_message_view, parent, false);
            return new ReceiveViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SEND){
            ((SendMessageViewHolder) holder).messageText.setText(messageArray.get(position).getMessage());
            ((SendMessageViewHolder) holder).timeText.setText(messageArray.get(position).getTime());
        }
        else {
            ((ReceiveViewHolder) holder).messageText.setText(messageArray.get(position).getMessage());
            ((ReceiveViewHolder) holder).timeText.setText(messageArray.get(position).getTime());
        }


    }

    @Override
    public int getItemCount() {
        return messageArray.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messageArray.get(position).getViewType();
    }

    public void addMessage(MessageModel message){
        messageArray.add(message);
    }
}
