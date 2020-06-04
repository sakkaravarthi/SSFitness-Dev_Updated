package com.app.ssfitness_dev.ui.home_admin.blog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.ssfitness_dev.R;
import com.app.ssfitness_dev.ui.home.blog.BlogModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static com.app.ssfitness_dev.utilities.Constants.TAG_GOAL_FRAGMENT;
import static com.app.ssfitness_dev.utilities.Constants.TAG_USER_PROFILE_FRAGMENT;
import static com.app.ssfitness_dev.utilities.Constants.USERS;
import static com.app.ssfitness_dev.utilities.HelperClass.logMessage;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddBlogFragment extends Fragment {

    private Button addBlogBtn;
    private EditText blogTitle, blogDescription, blogCategory;
    private ImageView blogImage;
    String title, description, category, coverUrl;
    Date date;
    private Uri resultUri;

    //Declare Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference blogRef = db.collection("blog");


    public AddBlogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_blog, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addBlogBtn = view.findViewById(R.id.button_addblog);
        blogTitle = view.findViewById(R.id.et_title);
        blogDescription = view.findViewById(R.id.et_description);
        blogCategory = view.findViewById(R.id.et_category);
        blogImage = view.findViewById(R.id.blog_image);

        date = new Date();


        addBlogBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


                if (TextUtils.isEmpty(blogTitle.getText().toString())) {
                    blogTitle.setError("Enter Blog Title");
                } else if (TextUtils.isEmpty(blogDescription.getText().toString())) {
                    blogDescription.setError("Enter Blog Description");
                } else if (TextUtils.isEmpty(blogCategory.getText().toString())) {
                    blogCategory.setError("Enter Blog Category");
                } else {

                    title = blogTitle.getText().toString();
                    description = blogDescription.getText().toString();
                    category = blogCategory.getText().toString();

                    String id = blogRef.document().getId();
                    DocumentReference blogDocumentRef = blogRef.document(id);

                    BlogModel blogModel = new BlogModel();

                    blogModel.setAuthor("Admin");
                    blogModel.setCategory(category);
                    blogModel.setDescription(description);
                    blogModel.setTitle(title);
                    blogModel.setImageUrl("image");


                    if (resultUri != null) {
                        StorageReference filepath = FirebaseStorage.getInstance().getReference()
                                .child("blog").child(id);
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplication().getContentResolver(), resultUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //tomake the image small size
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);

                        byte[] data = baos.toByteArray();

                        //uploading the image
                        UploadTask uploadTask = filepath.putBytes(data);

                        uploadTask.addOnFailureListener(e -> {
                            logMessage(TAG_USER_PROFILE_FRAGMENT,e.toString());
                        });

                        uploadTask.addOnSuccessListener(taskSnapshot -> {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(uri -> {
                                        coverUrl = uri.toString();

                                        blogModel.setImageUrl(coverUrl);

                                        blogDocumentRef.get().addOnCompleteListener(task1 -> {
                                            DocumentSnapshot document = task1.getResult();
                                            if (!document.exists()) {
                                                blogDocumentRef.set(blogModel).addOnCompleteListener(task11 -> {
                                                    logMessage(TAG_GOAL_FRAGMENT, "Image uploaded successfully");
                                                });
                                            }
                                        });
                                    });
                                }
                            }
                        });
                    }

                    blogRef.get().addOnCompleteListener(task -> blogDocumentRef.get().addOnCompleteListener(task1 -> {
                        DocumentSnapshot document = task1.getResult();

                        if (!document.exists()) {
                            blogDocumentRef.set(blogModel).addOnCompleteListener(task11 -> {
                                Log.d("AddBlog", "New Blog added");
                                //Toast.makeText(getContext(), "New Blog Added!", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }));
                }
            }
        });

        blogImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the intent from activity code is 1
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            final Uri imageUri = data.getData();
            resultUri = imageUri;
            blogImage.setImageURI(resultUri);

        }
    }
}
