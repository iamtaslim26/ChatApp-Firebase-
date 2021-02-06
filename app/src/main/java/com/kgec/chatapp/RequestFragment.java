package com.kgec.chatapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {
    private View RequestFragmentView;
    private RecyclerView myRequestList;
    private FirebaseAuth auth;
    private String currentUserId;
    private DatabaseReference RequestRef,usersRef,contactsRef;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RequestFragmentView= inflater.inflate(R.layout.fragment_request, container, false);


        myRequestList=RequestFragmentView.findViewById(R.id.chat_request_list);
        myRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        auth=FirebaseAuth.getInstance();
        currentUserId=auth.getCurrentUser().getUid();
        RequestRef= FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        contactsRef= FirebaseDatabase.getInstance().getReference().child("Contacts");

        return RequestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(RequestRef.child(currentUserId),Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts,RequestViewHolder>adapter=new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Contacts model) {

                holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);

                final String list_user_id=getRef(position).getKey();

                final DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();

                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            String type=dataSnapshot.getValue().toString();

                            if (type.equals("received")){
                                usersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("image")){


                                            final String requestUserimage=dataSnapshot.child("image").getValue().toString();


                                            Picasso.get().load(requestUserimage).placeholder(R.drawable.profile_image).into(holder.userimage);
                                        }else {
                                            final String requestUsername=dataSnapshot.child("name").getValue().toString();
                                            final String requestUserstatus=dataSnapshot.child("status").getValue().toString();


                                            holder.username.setText(requestUsername);
                                            holder.userstatus.setText(requestUserstatus);


                                        }

                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                CharSequence options[]=new CharSequence[]
                                                        {
                                                                "Accept",
                                                                "Cancel"

                                                        };

                                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                               builder.setTitle("Chat Request");
                                               builder.setItems(options, new DialogInterface.OnClickListener() {
                                                   @Override
                                                   public void onClick(DialogInterface dialog, int position) {
                                                       if (position==0){
                                                           contactsRef.child(currentUserId)
                                                                   .child(list_user_id)
                                                                   .child("Contacts")
                                                                   .setValue("saved")
                                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                       @Override
                                                                       public void onComplete(@NonNull Task<Void> task) {
                                                                           if (task.isSuccessful()){

                                                                               contactsRef.child(list_user_id)
                                                                                       .child(currentUserId)
                                                                                       .child("Contacts")
                                                                                       .setValue("saved")
                                                                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                           @Override
                                                                                           public void onComplete(@NonNull Task<Void> task) {
                                                                                               if (task.isSuccessful()){
                                                                                                   RequestRef.child(currentUserId)
                                                                                                           .child(list_user_id)
                                                                                                           .removeValue()
                                                                                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                               @Override
                                                                                                               public void onComplete(@NonNull Task<Void> task) {
                                                                                                                   if (task.isSuccessful()){

                                                                                                                       RequestRef.child(list_user_id)
                                                                                                                               .child(currentUserId)
                                                                                                                               .removeValue()
                                                                                                                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                   @Override
                                                                                                                                   public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                       Toast.makeText(getContext(), "Contacts Saved", Toast.LENGTH_LONG).show();

                                                                                                                                   }
                                                                                                                               });
                                                                                                                   }

                                                                                                               }
                                                                                                           });


                                                                                               }

                                                                                           }
                                                                                       });
                                                                           }

                                                                       }
                                                                   });




                                                       }
                                                       if (position==1){
                                                           RequestRef.child(currentUserId).child(list_user_id).removeValue()
                                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                       @Override
                                                                       public void onComplete(@NonNull Task<Void> task) {
                                                                           if (task.isSuccessful()){

                                                                               RequestRef.child(list_user_id).child(currentUserId).removeValue()
                                                                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                           @Override
                                                                                           public void onComplete(@NonNull Task<Void> task) {

                                                                                               Toast.makeText(getContext(), "Contacts Removed", Toast.LENGTH_LONG).show();


                                                                                           }
                                                                                       });
                                                                           }

                                                                       }
                                                                   });




                                                       }
                                                   }

                                               });


                                               builder.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                           else if (type.equals("sent")){

                               Button request_Sent_btn=holder.itemView.findViewById(R.id.request_accept_btn);

                               request_Sent_btn.setText("Request Sent");

                               holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.INVISIBLE);
                                usersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("image")){


                                            final String requestUserimage=dataSnapshot.child("image").getValue().toString();


                                            Picasso.get().load(requestUserimage).placeholder(R.drawable.profile_image).into(holder.userimage);
                                        }else {
                                            final String requestUsername=dataSnapshot.child("name").getValue().toString();
                                            final String requestUserstatus=dataSnapshot.child("status").getValue().toString();


                                            holder.username.setText(requestUsername);
                                            holder.userstatus.setText(requestUserstatus);


                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    Intent chatIntent=new Intent(getContext(),ChatActivity.class);
                                                    chatIntent.putExtra("visit_user_id",list_user_id);
                                                    chatIntent.putExtra("visit_user_name",requestUsername);


                                                    startActivity(chatIntent);

                                                }
                                            });


                                        }

                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                CharSequence options[]=new CharSequence[]
                                                        {
                                                                "Cancel Chat Request"

                                                        };

                                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                builder.setTitle("Already Sent request");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int position) {

                                                        if (position==0){
                                                            RequestRef.child(currentUserId).child(list_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()){

                                                                                RequestRef.child(list_user_id).child(currentUserId).removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                Toast.makeText(getContext(), "Request Cancel", Toast.LENGTH_LONG).show();


                                                                                            }
                                                                                        });
                                                                            }

                                                                        }
                                                                    });




                                                        }
                                                    }

                                                });


                                                builder.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout,viewGroup,false);
                RequestViewHolder viewHolder=new RequestViewHolder(view);

                return viewHolder;
            }
        };
        myRequestList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        TextView username,userstatus;
        CircleImageView userimage;
        Button acceptbutton,cancelbutton;
        public RequestViewHolder(@NonNull View itemView) {


            super(itemView);
            username=itemView.findViewById(R.id.user_profile_name);
            userstatus=itemView.findViewById(R.id.user_status);
            userimage=itemView.findViewById(R.id.users_profile_image);
            acceptbutton=itemView.findViewById(R.id.request_accept_btn);
            cancelbutton=itemView.findViewById(R.id.request_cancel_btn);
        }
    }

}
