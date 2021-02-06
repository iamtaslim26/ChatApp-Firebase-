package com.kgec.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchFriendsActivity extends AppCompatActivity {

    private EditText Search_name;
    private Button Search_btn;
    private RecyclerView search_lists;
    private DatabaseReference UsersRef;
    private String search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        search_lists=findViewById(R.id.recyclerview2);
        search_lists.setHasFixedSize(true);
        search_lists.setLayoutManager(new LinearLayoutManager(this));

        Search_btn=findViewById(R.id.search_product_item);
        Search_name=findViewById(R.id.Search_items);



        Search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                search=Search_name.getText().toString();

                onStart();
            }
        });




    }

    @Override
    protected void onStart() {

        super.onStart();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");

        FirebaseRecyclerOptions<Contacts>options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(UsersRef.orderByChild("name").startAt(search),Contacts.class).build();


        FirebaseRecyclerAdapter<Contacts,SearchViewHolder>adapter=new FirebaseRecyclerAdapter<Contacts, SearchViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SearchViewHolder holder, final int position, @NonNull Contacts model) {

                holder.userName.setText(model.getName());
                holder.userStatus.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);
//                        Glide.with(FindFriendsActivity.this).load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id=getRef(position).getKey();

                        Intent profileIntent=new Intent(SearchFriendsActivity.this,ProfileActivity.class);
                        profileIntent.putExtra("visit_user_id",visit_user_id);
                        startActivity(profileIntent);
                    }
                });



            }

            @NonNull
            @Override
            public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout,parent,false);

                SearchViewHolder holder=new SearchViewHolder(view);
                return holder;
            }
        };
        search_lists.setAdapter(adapter);
        adapter.startListening();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userStatus;
        CircleImageView profileImage;


        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}
