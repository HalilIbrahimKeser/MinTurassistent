package com.aphex.minturassistent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.aphex.minturassistent.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "TAG_EMAIL_LOGIN";
    BottomNavigationView bottomNav;

    private FragmentLoginBinding binding;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.hideTopNav();
        MainActivity.hideBottomNav();
    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity.showBottomNav();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //ALLEREDE INNLOGGET ?
        if (currentUser != null) {
            Navigation.findNavController(requireView()).navigate(R.id.mainFragment);
        }

        //LOGIN
        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!binding.editUser.getText().toString().isEmpty() || !binding.editPassword.getText().toString().isEmpty()) {
                    mAuth.signInWithEmailAndPassword(binding.editUser.getText().toString(),
                            binding.editPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Navigation.findNavController(requireView()).navigate(R.id.mainFragment);
                                    } else {
                                        Toast.makeText(getContext(), "Ikke registrert bruker", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(getContext(), "Du m?? skrive brukernavn og passord f??r innlogging", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //REGISTER USER
        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!binding.editUser.getText().toString().isEmpty() || !binding.editPassword.getText().toString().isEmpty()) {
                    mAuth.createUserWithEmailAndPassword(binding.editUser.getText().toString(),
                            binding.editPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Du er registrert!", Toast.LENGTH_SHORT).show();
                                        Navigation.findNavController(requireView()).navigate(R.id.mainFragment);
                                    } else {
                                        Toast.makeText(getContext(), "Kunne ikke registrere bruker.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(getContext(), "Du m?? skrive brukernavn og passord f??r registrering", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //DELETE USER
        binding.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!binding.editUser.getText().toString().isEmpty()) {
                    mAuth.sendPasswordResetEmail(binding.editUser.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Email med oppretting av passord sendt.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(getContext(), "Du m?? skrive brukernavn for ?? s?? tilsendt nytt passord", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}