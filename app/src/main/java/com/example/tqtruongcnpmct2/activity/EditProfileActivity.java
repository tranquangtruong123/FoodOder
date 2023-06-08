package com.example.tqtruongcnpmct2.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.example.tqtruongcnpmct2.databinding.ActivityEditProfileBinding;
import com.example.tqtruongcnpmct2.model.User;

import com.example.tqtruongcnpmct2.utils.GlideUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;


public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    int REQUEST_CODE_FODEL = 456;
//    private FirebaseStorage storage;
//    private StorageReference storageReference;

    String email;
    Uri uriImg;
    String uid;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        email = bundle.getString("key_email2");
        InitData(email);
        binding.editProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               OpenGallery();
            }
        });
        binding.profileBntUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatePerson();
            }
        });
    }

    private void UpdatePerson() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("user/" + uid);
        String ud_email = binding.profileEmail.getText().toString();
        String ud_fullname = binding.profileFullname.getText().toString();
        String ud_adress = binding.profileAdress.getText().toString();
        String ud_phone = binding.profilePhone.getText().toString();
        String ud_img = binding.profileImg.getText().toString();
        Map<String, Object> map = new HashMap<>();
        map.put("email",ud_email);
        map.put("pathImg",ud_img);
        map.put("fullname",ud_fullname);
        map.put("adress",ud_adress);
        map.put("phoneNumber",ud_phone);
        reference.updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(EditProfileActivity.this, "Ok", Toast.LENGTH_SHORT).show();
            }
        });
//        StorageReference ref = storageReference.child("image/" + uid);
//        ref.putFile(uriImg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                Toast.makeText(EditProfileActivity.this, "Ok", Toast.LENGTH_SHORT).show();
//            }
//        });

    }


    private void OpenGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CODE_FODEL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_FODEL && resultCode == RESULT_OK && data != null){
             uriImg = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uriImg);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                binding.editProfileImg.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    private void InitData(String email) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getValue(User.class).getEmail().equals(email)){
                        User user = child.getValue(User.class);
                        binding.profileFullname.setText(user.getFullname());
                        binding.profileEmail.setText(user.getEmail());
                        binding.profilePhone.setText(user.getPhoneNumber());
                        binding.profileAdress.setText(user.getAdress());
                        GlideUtils.loadUrl(user.getPathImg(),binding.editProfileImg);
                        uid = child.getKey();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}