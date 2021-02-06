package com.kgec.chatapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
public class ContactsFragment extends Fragment {
    private View Contactsview;
    private RecyclerView mycontactslist;
    private FirebaseAuth mauth;
    private String currentuserId;
    private FirebaseUser muser;
    private DatabaseReference Contactsref,usersRef;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Contactsview= inflater.inflate(R.layout.fragment_contacts, container, false);

        mauth=FirebaseAuth.getInstance();
        muser=FirebaseAuth.getInstance().getCurrentUser();
        currentuserId=muser.getUid();
        Contactsref=FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentuserId);




        usersRef=FirebaseDatabase.getInstance().getReference().child("Users");







        mycontactslist=Contactsview.findViewById(R.id.contacts_list);
        mycontactslist.setLayoutManager(new LinearLayoutManager(getContext()));

        return Contactsview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts>options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(Contactsref,Contacts.class)
                .build();


        final FirebaseRecyclerAdapter<Contacts,ContactsViewHolder>adapter=new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {

                final String userIDs = getRef(position).getKey();

                usersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            if (dataSnapshot.child("userstate").hasChild("state"))
                            {
                                String state = dataSnapshot.child("userstate").child("state").getValue().toString();
                                String date = dataSnapshot.child("userstate").child("Date").getValue().toString();
                                String time = dataSnapshot.child("userstate").child("Time").getValue().toString();

                                if (state.equals("Online"))
                                {
                                    holder.onlineIcon.setVisibility(View.VISIBLE);
                                }
                                else if (state.equals("Offline"))
                                {
                                    holder.onlineIcon.setVisibility(View.INVISIBLE);
                                }
                            }
                            else
                            {
                                holder.onlineIcon.setVisibility(View.INVISIBLE);
                            }


                            if (dataSnapshot.hasChild("image"))
                            {
                                String userImage = dataSnapshot.child("image").getValue().toString();
                                String profileName = dataSnapshot.child("name").getValue().toString();
                                String profileStatus = dataSnapshot.child("status").getValue().toString();

                                holder.userName.setText(profileName);
                                holder.userStatus.setText(profileStatus);
                                Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                            }
                            else
                            {
                                String profileName = dataSnapshot.child("name").getValue().toString();
                                String profileStatus = dataSnapshot.child("status").getValue().toString();

                                holder.userName.setText(profileName);
                                holder.userStatus.setText(profileStatus);
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
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout,parent,false);
                ContactsViewHolder contactsViewHolder=new ContactsViewHolder(view);
                return contactsViewHolder;


            }
        };
        mycontactslist.setAdapter(adapter);
       adapter.startListening();

    }


    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userStatus;
        CircleImageView profileImage;
        ImageView onlineIcon;
        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            onlineIcon = (ImageView) itemView.findViewById(R.id.user_online_status);
        }
    }
}
