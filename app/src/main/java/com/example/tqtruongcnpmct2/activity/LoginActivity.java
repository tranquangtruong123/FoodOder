package com.example.tqtruongcnpmct2.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tqtruongcnpmct2.R;
import com.example.tqtruongcnpmct2.databinding.ActivityLoginBinding;
import com.example.tqtruongcnpmct2.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private User user;

    FirebaseAuth firebaseAuth;
    private String keyEmail = "";
    DatabaseReference reference;
    GoogleSignInClient mgoogleSignInClient;
    ProgressDialog progressDialog;
    int RC_SIGN_IN = 40;
    long member;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        binding.singUp.setOnClickListener(v->{
            binding.progress.setVisibility(View.GONE);
            binding.singUp.setBackgroundResource(R.drawable.switch_trcks);
            binding.singUp.setTextColor(getResources().getColor(R.color.textColor));
            binding.logIn.setBackgroundResource(R.color.transparent);
            binding.singUpLayout.setVisibility(View.VISIBLE);
            binding.logInLayout.setVisibility(View.GONE);
            binding.logIn.setTextColor(getResources().getColor(R.color.pinkColor));
        });
        binding.logIn.setOnClickListener(v->{
            binding.progress.setVisibility(View.GONE);
            binding.logIn.setBackgroundResource(R.drawable.switch_trcks);
            binding.singUp.setTextColor(getResources().getColor(R.color.pinkColor));
            binding.singUp.setBackgroundResource(R.color.transparent);
            binding.logInLayout.setVisibility(View.VISIBLE);
            binding.singUpLayout.setVisibility(View.GONE);
            binding.logIn.setTextColor(getResources().getColor(R.color.textColor));
        });
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Creating acount");
        progressDialog.setMessage("we are crating your acount");
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                                .build();
        mgoogleSignInClient = GoogleSignIn.getClient(this,signInOptions);
        binding.bntLogingoogle.setOnClickListener(v->{
            SignInGoogle();
        });
        reference = FirebaseDatabase.getInstance().getReference().child("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    member = (snapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.bntLogIn.setOnClickListener(v-> {
            PerforLogin();
        });
        binding.bntsingIn.setOnClickListener(v->{
            PerforAuth();
        });
        binding.loginForgetPassword.setOnClickListener(v->{
            ForgetPassword();
        });

    }

    private void ForgetPassword() {

        View viewdialog = getLayoutInflater().inflate(R.layout.layput_bottom_forget_password,null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(viewdialog);
        EditText editText = viewdialog.findViewById(R.id.reset_password_edt);
        AppCompatButton bntcancel = viewdialog.findViewById(R.id.reset_password_cancle);
        AppCompatButton bntsend = viewdialog.findViewById(R.id.reset_password_send);
        bntcancel.setOnClickListener(v-> {
            bottomSheetDialog.dismiss();
        });
        bntsend.setOnClickListener(v -> {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        if (child.getValue(User.class).getEmail().equals(editText.getText().toString()+"")){
                            bottomSheetDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Kiểm tra Email", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                        Toast.makeText(LoginActivity.this, "Kiểm Tra Thông Tin Email(Email Không Tồn Tại)", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        });
        bottomSheetDialog.show();
    }

    private void SignInGoogle() {
        Intent intent = mgoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                fbAuth(account.getIdToken());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }

        }
    }


    public interface EmailCheckListener {
        void onEmailChecked(boolean isEmailAvailable);
    }

    private void checkEmail(String email, EmailCheckListener listener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("user").orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isEmailAvailable = !snapshot.exists();
                listener.onEmailChecked(isEmailAvailable);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onEmailChecked(false);
            }
        });
    }

    private void fbAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            checkEmail(firebaseUser.getEmail(), new EmailCheckListener() {
                                @Override
                                public void onEmailChecked(boolean isEmailAvailable) {
                                    if(isEmailAvailable){
                                        user = new User(firebaseUser.getEmail(),"",firebaseUser.getDisplayName(),firebaseUser.getEmail().toString(),"",firebaseUser.getPhotoUrl().toString());
                                        reference.child((member+1)+"").setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                        Intent intent1 = new Intent(LoginActivity.this,EditProfileActivity.class);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("key_email2",firebaseUser.getEmail());
                                                        intent1.putExtras(bundle);
                                                        startActivity(intent1);
                                                        intent.putExtra("key_email",firebaseUser.getEmail());
                                                        startActivity(intent);
                                                        Toast.makeText(LoginActivity.this, "Ok", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(LoginActivity.this, "No", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }else {
                                        Toast.makeText(LoginActivity.this, "email da ton tai", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                });
    }

    private void PerforLogin() {
        int checkInput = 0;
        String emaillogin = binding.loginEmail.getText().toString().trim();
        String passwordlogin = binding.loginPassword.getText().toString().trim();
        keyEmail = emaillogin;
        if(emaillogin.isEmpty()){
            binding.loginEmail.setError("Trong");
            checkInput = 1;
        }
        if(passwordlogin.isEmpty()){
            binding.loginPassword.setError("Trong");
            checkInput = 1;
        }
        if(checkInput == 0){
            firebaseAuth.signInWithEmailAndPassword(emaillogin,passwordlogin)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                Intent intent1 = new Intent(LoginActivity.this,EditProfileActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("key_email2",emaillogin);
                                intent1.putExtras(bundle);
                                startActivity(intent1);
                                intent.putExtra("key_email",emaillogin);
                                startActivity(intent);
                                Toast.makeText(LoginActivity.this, "Thanh Cong", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Khong Thanh cong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }




    private void PerforAuth() {
        binding.progress.setVisibility(View.VISIBLE);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("user");
        int checkInput = 0;
        String email = binding.registerEmail.getText().toString();
        String password = binding.registerPassword.getText().toString();
        String fullname = binding.registerFullname.getText().toString();
        String phonenumber = binding.registerPhonenumber.getText().toString();
        user = new User(email,password,fullname,phonenumber,"","");

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.registerEmail.setError("Loi Email");
            checkInput = 1;
        }
        if(password.isEmpty() || password.length() < 6){
            binding.registerPassword.setError("Mat khau lon hon 6 ky tu");
            checkInput = 1;
        }
        if(fullname.isEmpty()){
            binding.registerFullname.setError("Khong de trong");
            checkInput = 1;
        }
        if(phonenumber.isEmpty()){
            binding.registerPhonenumber.setError("khong de trong");
            checkInput = 1;
        }
        if(checkInput == 0){

            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        binding.progress.setVisibility(View.GONE);
                        reference.child((member+1)+"").setValue(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(LoginActivity.this, "Ok", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, "No", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else {
                        binding.progress.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, "Kiem Tra Thong Tin", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

}



