package com.aphex.minturassistent;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyProfileFragment extends Fragment {
    private static final String TAG = "log";
    private FirebaseAuth mAuth;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        TextView tvPUser = view.findViewById(R.id.tvPUser);

        tvPUser.setText("Hei bruker: \n" + currentUser.getEmail());

        Button btnLogOut = view.findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        Button btnDelete = view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBtnDeleteFinish(view);
            }
        });

        return view;
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        Navigation.findNavController(getView()).navigate(R.id.loginFragment);
    }

    //Viser en standard AlertDialog.. Tilpasset fra Werners dialogTest
    public void onBtnDeleteFinish(View view) {

        MyProfileFragment context = this;
        String title = "Bekreft sletting av bruker";
        String message = "Ønsker du virkelig å slette brukeren din?";
        String button1String = "Ja";
        String button2String = "Nei, gå tilbake!";

        AlertDialog.Builder ad = new AlertDialog.Builder(context.getContext());
        ad.setTitle(title);
        ad.setMessage(message);

        ad.setPositiveButton(button1String,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser == null) {
                            Log.d(TAG, "Logg inn før du kan slette.");
                            return;
                        }

                        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Navigation.findNavController(getView()).navigate(R.id.loginFragment);
                                Toast.makeText(context.getContext(), "Bruker slettet..", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

        ad.setNegativeButton(button2String,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        Toast.makeText(context.getContext(), "Avbryter..", Toast.LENGTH_LONG).show();
                    }
                });

        ad.setCancelable(false);

        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
        });

        ad.show();
    }
}