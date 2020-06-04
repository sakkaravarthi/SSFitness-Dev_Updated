package com.app.ssfitness_dev.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.ssfitness_dev.ui.home.blog.BlogModel;
import com.app.ssfitness_dev.ui.home.blog.BlogViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FirebaseRepository {

    private OnFirestoreTaskComplete onFirestoreTaskComplete;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private Query blogRef;

    public FirebaseRepository(OnFirestoreTaskComplete onFirestoreTaskComplete) {
        this.onFirestoreTaskComplete = onFirestoreTaskComplete;
    }

    public void getBlogData(String category) {
        blogRef = firebaseFirestore.collection("blog").whereEqualTo("category", category);;
        blogRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    onFirestoreTaskComplete.blogListDataAdded(task.getResult().toObjects(BlogModel.class));
                } else {
                    onFirestoreTaskComplete.onError(task.getException());
                }
            }
        });
    }

   // public void setCategory(String category){
   //     this.category = category;
  //  }

    public interface OnFirestoreTaskComplete {
        void blogListDataAdded(List<BlogModel> blogListModelsList);
        void onError(Exception e);
    }


}
