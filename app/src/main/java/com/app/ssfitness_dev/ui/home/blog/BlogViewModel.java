package com.app.ssfitness_dev.ui.home.blog;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.ssfitness_dev.ui.home.FirebaseRepository;

import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class BlogViewModel extends ViewModel implements FirebaseRepository.OnFirestoreTaskComplete {

    private MutableLiveData<List<BlogModel>> blogListModelData = new MutableLiveData<>();

    public LiveData<List<BlogModel>> getBlogListModelData() {
        return blogListModelData;
    }



    String category;

    private FirebaseRepository firebaseRepository = new FirebaseRepository(this);

    public BlogViewModel(String category){
        this.category = category;
        firebaseRepository.getBlogData(category);
    }

    //Needed for displaying data
    public BlogViewModel(){
        firebaseRepository.getBlogData(category);
    }

    @Override
    public void blogListDataAdded(List<BlogModel> blogListModelsList) {
        blogListModelData.setValue(blogListModelsList);
    }

    @Override
    public void onError(Exception e) {
        Log.d(TAG, "onError: " + e);
    }
}
