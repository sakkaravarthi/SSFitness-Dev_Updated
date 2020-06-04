package com.app.ssfitness_dev.ui.admin;

import com.app.ssfitness_dev.ui.home.FirebaseRepository;
import com.app.ssfitness_dev.ui.home.FirebaseRepository.OnFirestoreTaskComplete;
import com.app.ssfitness_dev.ui.home.blog.BlogModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class AdminFirebaseRepository {

    private OnFirestoreTaskComplete onFirestoreTaskComplete;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private Query blogRef;



}
