package com.app.ssfitness_dev.ui.user.userprofile;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ssfitness_dev.R;
import com.app.ssfitness_dev.ui.home.HomeActivity;
import com.app.ssfitness_dev.ui.splash.SplashActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;
import static com.app.ssfitness_dev.utilities.Constants.TAG_GOAL_FRAGMENT;
import static com.app.ssfitness_dev.utilities.Constants.TAG_USER_PROFILE_FRAGMENT;
import static com.app.ssfitness_dev.utilities.Constants.USERS;
import static com.app.ssfitness_dev.utilities.HelperClass.logMessage;
import static com.app.ssfitness_dev.utilities.HelperClass.makeSnackBarMessage;


public class UserProfileFragment extends Fragment {
    private AutoCompleteTextView countryAutoCompleteDropText;
    private Button continueToCongrats;
    private FirebaseAuth firebaseAuth;

    //For Chat purpose
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    private NavController navController;
    private FirebaseFirestore rootRef;
    private CollectionReference usersRef;
    private Button maleBtn, femaleBtn;
    private MaterialDatePicker materialDatePicker;
    private EditText firstName, lastName;
    private Uri resultUri;
    private ImageView mProfileImage;
    private MaterialToolbar materialToolbar;
    private TextView dateOfBirth, tv_height, tv_weight;
    private RelativeLayout userRelativeLayout;

    String[] countries;
    private String currentUid, gender = "",country, name, dob="", photoUrl;
    private int height=0,weight=0;
    private double bmi=0;


    public UserProfileFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get Arguments from past fragments to store in database
        String goal = getArguments().getString("goal");
        String activitylevel = getArguments().getString("activitylevel");
        String diet = getArguments().getString("diet");

        //Bind ui elements
        initUi(view);
        //Change and set values on click - gender, calender
        clickListeners();


       // continueToCongrats.setOnClickListener(view1 -> navController.navigate(R.id.action_userProfileFragment_to_finishProfileDetailsFragment));

        continueToCongrats.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(firstName.getText().toString())){
                    firstName.setError("Enter your first name");
                }
                else if(TextUtils.isEmpty(lastName.getText().toString())){
                    lastName.setError("Enter your last name");
                }
                else if(gender.equals("")){
                    makeSnackBarMessage(userRelativeLayout,"Please select your gender");
                }
                else if (dob.equals("")){
                    makeSnackBarMessage(userRelativeLayout,"Please enter your date of birth");
                }
                else if (TextUtils.isEmpty(countryAutoCompleteDropText.getText().toString())){
                    countryAutoCompleteDropText.setError("Enter your country");
                }
                else if(height==0){
                    makeSnackBarMessage(userRelativeLayout,"Please enter your height");
                }
                else if(weight==0){
                    makeSnackBarMessage(userRelativeLayout,"Please enter your weight");
                }
                else {
                    Map user = new HashMap<>();

                    //Users - > ID(Documents) -> Data(fields)
                    rootRef = FirebaseFirestore.getInstance();
                    usersRef = rootRef.collection(USERS);

                    mCurrentUser = firebaseAuth.getCurrentUser();
                    currentUid = mCurrentUser.getUid();

                    //For chat purpose realtime  db
                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(currentUid);

                    //Firestore
                    DocumentReference uidRef = usersRef.document(currentUid);

                    name = firstName.getText().toString() +" "+lastName.getText().toString();
                    country = countryAutoCompleteDropText.getText().toString();
                    bmi = weight*10000/(height*height);

                    user.put("userName",name);
                    user.put("gender",gender);
                    user.put("dateofbirth",dob);
                    user.put("country",country);
                    user.put("height",height);
                    user.put("weight",weight);
                    user.put("bmi",bmi);
                    user.put("goal",goal);
                    user.put("activitylevel",activitylevel);
                    user.put("diet",diet);
                    user.put("profileComplete",true);

                    if (resultUri != null) {
                        StorageReference filepath = FirebaseStorage.getInstance().getReference()
                                .child("profilephotos").child(firebaseAuth.getCurrentUser().getUid());

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
                                        photoUrl = uri.toString();
                                        user.put("photoUrl", photoUrl);

                                        mUserDatabase.updateChildren(user);

                                        uidRef.get().addOnCompleteListener(task -> {
                                            if(task.isSuccessful()){
                                                uidRef.update(user).addOnCompleteListener(task1 -> {
                                                    logMessage(TAG_GOAL_FRAGMENT, "Image uploaded successfully");
                                                    navController.navigate(R.id.action_userProfileFragment_to_finishProfileDetailsFragment);
                                                });
                                            }
                                        }).addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Server timeout. Please try again later.", Toast.LENGTH_SHORT).show();
                                            logMessage(TAG_GOAL_FRAGMENT, e.getMessage());
                                        });

                                    });
                                }
                            } });
                    }
                    else {
                        mUserDatabase.updateChildren(user);

                        uidRef.update(user)
                         .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        logMessage(TAG_GOAL_FRAGMENT, "User profile is saved successfully");
                                        navController.navigate(R.id.action_userProfileFragment_to_finishProfileDetailsFragment);
                                    }
                                })
                         .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Server timeout. Please try again later.", Toast.LENGTH_SHORT).show();
                            logMessage(TAG_GOAL_FRAGMENT, e.getMessage());
                        });
                    }

                }
            }
        });
    }

    private void clickListeners() {

        materialToolbar.setNavigationOnClickListener(view -> navController.navigate(R.id.action_userProfileFragment_to_dietFragment));

        maleBtn.setOnClickListener(view -> {

            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                femaleBtn.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                femaleBtn.setTextColor(Color.parseColor("#ffffff"));
                maleBtn.setTextColor(Color.parseColor("#61A92A"));
                maleBtn.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#61A92A")));
            }
            gender = "male";
        });

        femaleBtn.setOnClickListener(view -> {
            //maleBtn.setCompoundDrawables(null,null,null,null);
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                maleBtn.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                maleBtn.setTextColor(Color.parseColor("#ffffff"));
                femaleBtn.setTextColor(Color.parseColor("#61A92A"));
                femaleBtn.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#61A92A")));
            }
            gender = "female";
        });

        dateOfBirth.setOnClickListener(view -> materialDatePicker.show(getActivity().getSupportFragmentManager(),"DATE_PICKER" ));

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            dateOfBirth.setText(materialDatePicker.getHeaderText());
            dob = materialDatePicker.getHeaderText();
        });

        tv_height.setOnClickListener(view -> {
            AlertDialog myAlertdialog;
            Switch heightSwitch;
            EditText et_ft, et_in, et_cm;
            TextView tv_ft, tv_in, tv_cm;

            View mDialogView = LayoutInflater.from(view.getContext())
                    .inflate(R.layout.alertdialog_height, null);

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(view.getContext());
            builder.setTitle("Height")
                    .setView(mDialogView)
                    .setIcon(R.drawable.ic_height)
                    .setCancelable(true)
                    .setBackground(view.getResources().getDrawable(R.drawable.alert_dialog_bg))
                    .setMessage("Enter your height. Use the switch if you want to change the units.");

            myAlertdialog = builder.create();
            myAlertdialog.show();

            Button set = mDialogView.findViewById(R.id.btn_height_set);
            Button dismiss = mDialogView.findViewById(R.id.btn_height_dismiss);

            et_ft = mDialogView.findViewById(R.id.et_ft);
            et_in = mDialogView.findViewById(R.id.et_in);
            et_cm = mDialogView.findViewById(R.id.et_cm);

            tv_ft = mDialogView.findViewById(R.id.tv_ft);
            tv_in = mDialogView.findViewById(R.id.tv_in);
            tv_cm = mDialogView.findViewById(R.id.tv_cm);
            heightSwitch = mDialogView.findViewById(R.id.switchHeight);

            et_ft.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if(et_ft.getText().toString().equals("")){
                        return;
                    }

                    int feet = Integer.parseInt(et_ft.getText().toString());

                    if(feet <2 || feet > 8){
                        et_ft.setError("Enter valid height");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            et_in.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                   if(et_in.getText().toString().equals("")){
                       return;
                   }
                    int inches = Integer.parseInt(et_in.getText().toString());

                       if(inches<1 || inches >11)
                       {
                           et_in.setError("Enter valid height");
                       }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            et_cm.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                   if(et_cm.getText().toString().equals("")){
                       return;
                   }
                    int cm = Integer.parseInt(et_cm.getText().toString());

                    if(cm>255 || cm < 89)
                    {
                        et_cm.setError("Enter valid height");
                    }else{
                        height= cm;
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            heightSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked){
                        et_ft.setVisibility(View.GONE);
                        tv_ft.setVisibility(View.GONE);

                        et_in.setVisibility(View.GONE);
                        tv_in.setVisibility(View.GONE);

                        tv_cm.setVisibility(View.VISIBLE);
                        et_cm.setVisibility(View.VISIBLE);
                    }
                    else {
                        et_ft.setVisibility(View.VISIBLE);
                        tv_ft.setVisibility(View.VISIBLE);

                        et_in.setVisibility(View.VISIBLE);
                        tv_in.setVisibility(View.VISIBLE);

                        tv_cm.setVisibility(View.GONE);
                        et_cm.setVisibility(View.GONE);
                    }
                }
            });

           set.setOnClickListener(view12 -> {
               if(!TextUtils.isEmpty(et_ft.getText())
                       &&!TextUtils.isEmpty(et_in.getText())){

                   double cm1 = (Integer.parseInt(et_in.getText().toString())*2.54);
                   double cm2 = (Integer.parseInt(et_ft.getText().toString())*30.48);
                   double totalCm = cm1 + cm2;
                   height = (int)totalCm;
               }

               if(height >= 256)
               {
                   height = 254;
               }
               else if (height<89)
               {
                   height = 89;
               }

               tv_height.setText(height+"");
               myAlertdialog.dismiss();
           });

            dismiss.setOnClickListener(view1 -> myAlertdialog.dismiss());

        });

        tv_weight.setOnClickListener(view -> {
            AlertDialog myAlertdialog;
            Switch weightSwitch;
            EditText et_kgs, et_lbs;
            TextView tv_kgs, tv_lbs;

            View mDialogView = LayoutInflater.from(view.getContext())
                    .inflate(R.layout.alertdialog_weight, null);

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(view.getContext());
            builder.setTitle("Weight")
                    .setView(mDialogView)
                    .setIcon(R.drawable.ic_height)
                    .setCancelable(true)
                    .setBackground(view.getResources().getDrawable(R.drawable.alert_dialog_bg))
                    .setMessage("Enter your weight. Use the switch if you want to change the units.");

            myAlertdialog = builder.create();
            myAlertdialog.show();

            Button set = mDialogView.findViewById(R.id.btn_weight_set);
            Button dismiss = mDialogView.findViewById(R.id.btn_weight_dismiss);
            et_kgs = mDialogView.findViewById(R.id.et_kg);
            et_lbs = mDialogView.findViewById(R.id.et_lb);

            tv_kgs = mDialogView.findViewById(R.id.tv_kg);
            tv_lbs = mDialogView.findViewById(R.id.tv_lb);

            weightSwitch = mDialogView.findViewById(R.id.switchWeight);

            weightSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked){
                        et_kgs.setVisibility(View.GONE);
                        tv_kgs.setVisibility(View.GONE);

                        et_lbs.setVisibility(View.VISIBLE);
                        tv_lbs.setVisibility(View.VISIBLE);
                    }
                    else {
                        et_kgs.setVisibility(View.VISIBLE);
                        tv_kgs.setVisibility(View.VISIBLE);

                        et_lbs.setVisibility(View.GONE);
                        tv_lbs.setVisibility(View.GONE);
                    }
                }
            });

            et_kgs.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if(et_kgs.getText().toString().equals("")){
                        return;
                    } else if(Integer.parseInt(et_kgs.getText().toString()) < 20
                            || Integer.parseInt(et_kgs.getText().toString()) > 452) {
                        et_kgs.setError("Enter valid weight");
                    }else {
                        weight = Integer.parseInt(et_kgs.getText().toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            et_lbs.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(et_lbs.getText().toString().equals("")){
                        return;
                    } else if(Integer.parseInt(et_lbs.getText().toString()) < 44
                            || Integer.parseInt(et_lbs.getText().toString()) > 996)
                    {
                        et_lbs.setError("Enter valid weight");
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            set.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!TextUtils.isEmpty(et_lbs.getText()))
                    {
                        double lbgToKgs = (Integer.parseInt(et_lbs.getText().toString())/2.205);
                        weight = (int)lbgToKgs;
                    }

                    if(weight > 452)
                    {
                        weight = 452;
                    }
                    else if (weight<20)
                    {
                        weight = 20;
                    }
                    tv_weight.setText(weight+"");
                    myAlertdialog.dismiss();
                     }
            });


            dismiss.setOnClickListener(view1 -> myAlertdialog.dismiss());
        });

        mProfileImage.setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"SELECT IMAGE"), 1);



        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the intent from activity code is 1
        if (requestCode == 1 && resultCode == RESULT_OK) {

            final Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(getContext(),this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri mResultUri = result.getUri();
                resultUri = mResultUri;
                mProfileImage.setImageURI(resultUri);
               } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void initUi(View view) {
        countryAutoCompleteDropText = view.findViewById(R.id.dropdown_country_text);
        firebaseAuth = FirebaseAuth.getInstance();
        continueToCongrats = view.findViewById(R.id.button_confirm_profile);
        navController = Navigation.findNavController(view);
        userRelativeLayout = view.findViewById(R.id.user_profile_relative_layout);

        //init all ui components
        materialToolbar = view.findViewById(R.id.topAppBar);

        firstName = view.findViewById(R.id.text_input_first_name);
        lastName = view.findViewById(R.id.text_input_last_name);
        maleBtn = view.findViewById(R.id.btn_male);
        femaleBtn = view.findViewById(R.id.btn_female);
        mProfileImage = view.findViewById(R.id.profile_image);

        tv_height = view.findViewById(R.id.user_height);
        tv_weight = view.findViewById(R.id.user_weight);
        dateOfBirth = view.findViewById(R.id.user_dob);

        datePickerInit();
        countriesInit();

    }

    private void countriesInit() {
        countries = new String[]{
                "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
                "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize",
                "Benin", "Bermuda", "Bhutan", "Bolivia", "Bonaire, Sint Eustatius and Saba", "Bosnia and Herzegovina", "Botswana", "Bouvet Island",
                "Brazil", "British Indian Ocean Territory", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cabo Verde", "Cambodia", "Cameroon",
                "Canada", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands",
                "Colombia", "Comoros", "Congo", "Cook Islands", "Costa Rica", "Croatia", "Cuba", "Curaçao", "Cyprus", "Czechia", "Côte d'Ivoire",
                "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea",
                "Estonia", "Eswatini", "Ethiopia", "Falkland Islands", "Faroe Islands", "Fiji", "Finland", "France", "French Guiana", "French Polynesia",
                "French Southern Territories", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe",
                "Guam", "Guatemala", "Guernsey", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard Island and McDonald Islands", "Holy See",
                "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Isle of Man", "Israel", "Italy", "Jamaica",
                "Japan", "Jersey", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "North Korea", "South Korea", "Kuwait", "Kyrgyzstan", "Lao People's Democratic Republic ",
                "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macao", "Madagascar", "Malawi", "Malaysia",
                "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico",
                "Micronesia", "Moldova", "Monaco", "Mongolia", "Montenegro", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal",
                "Netherlands", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Mariana Islands", "Norway",
                "Oman", "Pakistan", "Palau", "Palestine", "Panama", "Papua New Guinea", "Paraguay", "Peru",
                "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico", "Qatar", "Republic of North Macedonia", "Romania",
                "Russian Federation", "Rwanda", "Réunion", "Saint Barthélemy", "Saint Helena, Ascension and Tristan da Cunha", "Saint Kitts and Nevis",
                "Saint Lucia", "Saint Martin", "Saint Pierre and Miquelon", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe",
                "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Sint Maarten", "Slovakia", "Slovenia", "Somalia",
                "South Africa", "South Georgia and the South Sandwich Islands", "South Sudan", "Spain", "Sri Lanka", "Sudan", "Suriname",
                "Svalbard and Jan Mayen", "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor-Leste",
                "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda",
                "Ukraine", "United Arab Emirates", "United Kingdom of Great Britain and Northern Ireland", "United States Minor Outlying Islands",
                "United States of America", "Uruguay", "Uzbekistan", "Vanuatu",
                "Venezuela", "Viet Nam", "Virgin Islands (British)", "Virgin Islands (U.S.)", "Wallis and Futuna", "Western Sahara", "Yemen", "Zambia",
                "Zimbabwe"
        };

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.country_dropdown_item,
                countries
        );

        countryAutoCompleteDropText.setAdapter(countryAdapter);
        countryAutoCompleteDropText.setOnItemClickListener((adapterView, view, position, l) -> {
            country = adapterView.getItemAtPosition(position).toString();
        });
    }

    private void datePickerInit() {
        //Calender settings
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        long today = MaterialDatePicker.todayInUtcMilliseconds();
        calendar.setTimeInMillis(today);

        //Only for >13 and <90
        long thirtheenYearsAgo = (long) (today - (1000 * 60 * 60 * 24 * 365.25 * 13));
        long nintyYearsAgo = (long) (today - (1000 * 60 * 60 * 24 * 365.25 * 90));

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select your Date of Birth");
        builder.setSelection(thirtheenYearsAgo);

        builder.setCalendarConstraints(new CalendarConstraints.Builder()
                .setStart(nintyYearsAgo)
                .setEnd(thirtheenYearsAgo)
                .build());

        materialDatePicker = builder.build();


    }

}
