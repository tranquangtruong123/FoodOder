package com.example.tqtruongcnpmct2.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tqtruongcnpmct2.R;
import com.example.tqtruongcnpmct2.activity.EditProfileActivity;
import com.example.tqtruongcnpmct2.activity.LoginActivity;
import com.example.tqtruongcnpmct2.activity.MainActivity;
import com.example.tqtruongcnpmct2.adapter.OptionProfileAdapter;

import com.example.tqtruongcnpmct2.databinding.FragmentProfileBinding;
import com.example.tqtruongcnpmct2.listener.IOnClickProfileitemListener;
import com.example.tqtruongcnpmct2.model.Feedback;
import com.example.tqtruongcnpmct2.model.OptionProfile;
import com.example.tqtruongcnpmct2.model.User;
import com.example.tqtruongcnpmct2.utils.GlideUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends BaseFragment implements IOnClickProfileitemListener {

    private FragmentProfileBinding fragmentProfileBinding;
    private OptionProfileAdapter adapter;
    private MainActivity mainActivity;
    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient signInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false);
        adapter = new OptionProfileAdapter(this::onClickItemProfile);
        mainActivity = (MainActivity) getActivity();
        getIdbyEmail(mainActivity.getEmail());

        fragmentProfileBinding.profileMoreImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),EditProfileActivity.class));
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        adapter.setData(getListProfile());
        fragmentProfileBinding.profileRcyOption.setLayoutManager(linearLayoutManager);
        fragmentProfileBinding.profileRcyOption.setAdapter(adapter);
        fragmentProfileBinding.profileRcyOption.addItemDecoration(itemDecoration);
        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(getActivity(),signInOptions);

        return fragmentProfileBinding.getRoot();
    }

    private void getIdbyEmail(String email) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getValue(User.class).getEmail().equals(email)){
                        User user = child.getValue(User.class);
                        fragmentProfileBinding.profileName.setText(user.getFullname()+"");
                        fragmentProfileBinding.proflieEmail.setText(user.getEmail()+"");
                        GlideUtils.loadUrl(user.getPathImg(),fragmentProfileBinding.profileImg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private List<OptionProfile> getListProfile() {
        List<OptionProfile> list = new ArrayList<>();
        list.add(new OptionProfile(R.drawable.ic_feedback_24,"Phản Hồi",R.drawable.baseline_keyboard_arrow_down_24));
        list.add(new OptionProfile(R.drawable.baseline_person_24,"Thông Tin",R.drawable.baseline_keyboard_arrow_down_24));
        list.add(new OptionProfile(R.drawable.ic_gift,"Quà Tặng",R.drawable.baseline_keyboard_arrow_down_24));
        list.add(new OptionProfile(R.drawable.ic_local,"Địa Chỉ",R.drawable.baseline_keyboard_arrow_down_24));
        list.add(new OptionProfile(R.drawable.ic_logout,"Dang Xuat",R.color.transparent));
        return list;
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolBar(true, getString(R.string.profile));
        }
    }

    @Override
    public void onClickItemProfile(int posipo) {
        if(posipo == 0){
            feefback();
        }
        else if(posipo == 1){
            Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT).show();
        }
        else if(posipo == 2){
            Toast.makeText(getActivity(), "2", Toast.LENGTH_SHORT).show();
        }
        else if(posipo == 4){
            signOut();
        }
    }

    private void feefback() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("feedback");
        View viewdialog = getLayoutInflater().inflate(R.layout.activity_feed_back,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(viewdialog);
        EditText name = viewdialog.findViewById(R.id.feedback_name);
        EditText email = viewdialog.findViewById(R.id.feedback_email);
        EditText phone = viewdialog.findViewById(R.id.feedback_phone);
        EditText comment = viewdialog.findViewById(R.id.feedback_email);
        TextView txtbnt = viewdialog.findViewById(R.id.textviewbnt);
        Feedback feedback = new Feedback(name.getText().toString(),phone.getText().toString(),email.getText().toString(),comment.getText().toString());
        AlertDialog dialog = builder.create();
        txtbnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(email.getText().toString()).setValue(feedback).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(mainActivity, "Gui Thanh cong", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    void signOut() {
        signInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mainActivity.finish();
                startActivity(new Intent(mainActivity, LoginActivity.class));
            }
        });
    }

}
