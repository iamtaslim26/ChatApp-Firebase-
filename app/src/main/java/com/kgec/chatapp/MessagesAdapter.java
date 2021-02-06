package com.kgec.chatapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.squareup.picasso.Picasso;

import java.net.URI;
import java.security.PublicKey;
        import java.util.List;

        import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    private List<Messages>userMessageList;
    private FirebaseAuth mauth;
    private DatabaseReference usersRef;


    public MessagesAdapter(List<Messages>userMessageList){

        this.userMessageList=userMessageList;
    }
    public class MessagesViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessageText, receiverMessageText;
        public CircleImageView receiverProfileImage;
        public ImageView senderImageView,recieverImageView;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_messsage_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
            senderImageView=itemView.findViewById(R.id.message_sender_image_view);
            recieverImageView=itemView.findViewById(R.id.message_receiver_image_view);
        }
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_message_layout,viewGroup,false);

        mauth=FirebaseAuth.getInstance();
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessagesViewHolder holder, final int position) {

        String MessageSenderId=mauth.getCurrentUser().getUid();

        final Messages messages=userMessageList.get(position);

        String fromUserId=messages.getFrom();
        final String fromMessagetype=messages.getType();

        usersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image")){

                    String recieverimage=dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(recieverimage).placeholder(R.drawable.profile_image).into(holder.receiverProfileImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (fromMessagetype.equals("text"))

        {
            holder.receiverMessageText.setVisibility(View.GONE);
            holder.receiverProfileImage.setVisibility(View.GONE);
            holder.senderImageView.setVisibility(View.GONE);
            holder.recieverImageView.setVisibility(View.GONE);

            if (fromUserId.equals(MessageSenderId))
            {
                holder.senderMessageText.setVisibility(View.VISIBLE);

                holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMessageText.setTextColor(Color.BLACK);
                holder.senderMessageText.setText(messages.getMessage()+"\n \n"+messages.getTime()+"-"+messages.getDate());

            }
            else
            {
                holder.senderMessageText.setVisibility(View.GONE);
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setBackgroundResource(R.drawable.recivermessagelayout);
                holder.receiverMessageText.setTextColor(Color.BLACK);
                holder.receiverMessageText.setText(messages.getMessage()+"\n \n"+messages.getTime()+"-"+messages.getDate());


            }
        }
        else if (fromMessagetype.equals("image")){
            if (fromUserId.equals(MessageSenderId)){


                holder.senderMessageText.setVisibility(View.GONE);
                holder.receiverMessageText.setVisibility(View.GONE);

                Picasso.get().load(messages.getMessage()).into(holder.senderImageView);
            }
            else {
                holder.senderMessageText.setVisibility(View.GONE);
                holder.receiverMessageText.setVisibility(View.GONE);


                holder.recieverImageView.setVisibility(View.VISIBLE);


                Picasso.get().load(messages.getMessage()).into(holder.recieverImageView);

            }
        }

        else if(fromMessagetype.equals("pdf")||fromMessagetype.equals("docx")){

            if (fromUserId.equals(MessageSenderId)){

                holder.senderMessageText.setVisibility(View.GONE);
                holder.receiverMessageText.setVisibility(View.GONE);

               // holder.senderImageView.setVisibility(View.VISIBLE);
               holder.senderImageView.setBackgroundResource(R.drawable.file);

                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatapp-a66ac.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=35d1c0ed-6ffb-4ad1-bd8e-53761e87e718").into(holder.senderImageView);




            }
            else
                {
                holder.senderMessageText.setVisibility(View.GONE);
                holder.receiverMessageText.setVisibility(View.GONE);

                holder.recieverImageView.setVisibility(View.VISIBLE);
             // holder.recieverImageView.setBackgroundResource(R.drawable.file);
                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatapp-a66ac.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=35d1c0ed-6ffb-4ad1-bd8e-53761e87e718").into(holder.recieverImageView);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(userMessageList.get(position).getMessage()));
                            holder.itemView.getContext().startActivity(intent);

                        }
                    });

            }

            }
        if (fromUserId.equals(MessageSenderId)){

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (userMessageList.get(position).getType().equals("pdf")|| userMessageList.get(position).getType().equals("docx")){

                        CharSequence options[]=new CharSequence[]{

                                "DELETE FOR ME",
                                "DOWNLOAD AND VIEW THIS DOCUMENTS",
                                "DELETE FOR EVERYONE"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                if (i==0){

                                    deleteSentMessage(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(),MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);


                                }
                                if (i==1){


                                    Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(userMessageList.get(position).getMessage()));
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                if(i==2){

                                    deleteMessage(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(),MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);


                                }

                            }
                        });

                        builder.show();
                    }
                    else if (userMessageList.get(position).getType().equals("text")){

                        CharSequence options[]=new CharSequence[]{

                                "DELETE FOR ME",
                                "DELETE FOR EVERYONE"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                if (i==0){
                                    deleteSentMessage(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(),MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);


                                }

                                if(i==1){
                                    deleteMessage(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(),MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);


                                }

                            }
                        });

                        builder.show();
                    }
                    else if (userMessageList.get(position).getType().equals("image")){

                        CharSequence options[]=new CharSequence[]{

                                "DELETE FOR ME",
                                "VIEW THIS IMAGE",
                                "DELETE FOR EVERYONE"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                if (i==0){
                                    deleteRecieveMessage(position,holder);

                                    Intent intent=new Intent(holder.itemView.getContext(),MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);


                                }
                                if (i==1){

                                    Intent intent=new Intent(holder.itemView.getContext(),ImgaeViewerActivity.class);
                                    intent.putExtra("url",userMessageList.get(position).getMessage());
                                    holder.itemView.getContext().startActivity(intent);



                                }
                                if(i==2){
                                    deleteMessage(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(),MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);


                                }

                            }
                        });

                        builder.show();
                    }

                }
            });


        }
        else{

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (userMessageList.get(position).getType().equals("pdf")|| userMessageList.get(position).getType().equals("docx")){

                        CharSequence options[]=new CharSequence[]{

                                "DELETE FOR ME",
                                "DOWNLOAD AND VIEW THIS DOCUMENTS",

                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                if (i==0){
                                    deleteRecieveMessage(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(),MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }
                                if (i==1){

                                    Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(userMessageList.get(position).getMessage()));
                                    holder.itemView.getContext().startActivity(intent);


                                }


                            }
                        });

                        builder.show();
                    }
                    else if (userMessageList.get(position).getType().equals("text")){

                        CharSequence options[]=new CharSequence[]{

                                "DELETE FOR ME"

                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                if (i==0){
                                    deleteRecieveMessage(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(),MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);


                                }


                            }
                        });

                        builder.show();
                    }
                    else if (userMessageList.get(position).getType().equals("image")){

                        CharSequence options[]=new CharSequence[]{

                                "DELETE FOR ME",
                                "VIEW THIS IMAGE"

                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {

                                if (i==0){
                                    deleteRecieveMessage(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(),MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);


                                }
                                if(i==1){

                                    Intent intent=new Intent(holder.itemView.getContext(),ImgaeViewerActivity.class);
                                    intent.putExtra("url",userMessageList.get(position).getMessage());
                                    holder.itemView.getContext().startActivity(intent);




                                }

                            }
                        });

                        builder.show();
                    }

                }
            });




        }


    }

    private void deleteSentMessage(final int position,final MessagesViewHolder messagesViewHolder)
    {

        DatabaseReference RootRef=FirebaseDatabase.getInstance().getReference();
        RootRef.child("Messages")
                .child(userMessageList.get(position).getFrom())
                .child(userMessageList.get(position).getTo())
                .child(userMessageList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){


                    Toast.makeText(messagesViewHolder.itemView.getContext(), "Deleted Succesfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    String e=task.getException().getMessage();
                    Toast.makeText(messagesViewHolder.itemView.getContext(), "Error occured  "+e, Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void deleteRecieveMessage(final int position,final MessagesViewHolder messagesViewHolder)
    {

        DatabaseReference RootRef=FirebaseDatabase.getInstance().getReference();
        RootRef.child("Messages")
                .child(userMessageList.get(position).getTo())
                .child(userMessageList.get(position).getFrom())
                .child(userMessageList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){


                    Toast.makeText(messagesViewHolder.itemView.getContext(), "Deleted Succesfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    String e=task.getException().getMessage();
                    Toast.makeText(messagesViewHolder.itemView.getContext(), "Error occured  "+e, Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void deleteMessage(final int position,final MessagesViewHolder messagesViewHolder)
    {

        final DatabaseReference RootRef=FirebaseDatabase.getInstance().getReference();
        RootRef.child("Messages")
                .child(userMessageList.get(position).getTo())
                .child(userMessageList.get(position).getFrom())
                .child(userMessageList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    RootRef.child("Messages")
                            .child(userMessageList.get(position).getFrom())
                            .child(userMessageList.get(position).getTo())
                            .child(userMessageList.get(position).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                Toast.makeText(messagesViewHolder.itemView.getContext(), "Deleted Succesfully", Toast.LENGTH_SHORT).show();


                            }
                            else {
                                String e=task.getException().getMessage();
                                Toast.makeText(messagesViewHolder.itemView.getContext(), "Error occured  "+e, Toast.LENGTH_SHORT).show();
                            }


                        }
                    });




                }
                else {
                    String e=task.getException().getMessage();
                    Toast.makeText(messagesViewHolder.itemView.getContext(), "Error occured  "+e, Toast.LENGTH_SHORT).show();
                }

            }
        });


    }














    @Override
    public int getItemCount() {
        return userMessageList.size();
    }


}
