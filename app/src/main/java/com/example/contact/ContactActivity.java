package com.example.contact;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.HashMap;

public class ContactActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_PHOTO = 101;

    private EditText etFirstName, etLastName, etPhone, etEmail;
    private ImageView imgContactPhoto;
    private Uri photoUri;
    private int contactIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        imgContactPhoto = findViewById(R.id.imgContactPhoto);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnSelectPhoto = findViewById(R.id.btnSelectPhoto);

        Intent intent = getIntent();
        if (intent.hasExtra("contact")) {
            HashMap<String, String> contact = (HashMap<String, String>) intent.getSerializableExtra("contact");
            etFirstName.setText(contact.get("firstName"));
            etLastName.setText(contact.get("lastName"));
            etPhone.setText(contact.get("phone"));
            etEmail.setText(contact.get("email"));
            photoUri = contact.get("photoUri") != null ? Uri.parse(contact.get("photoUri")) : null;
            if (photoUri != null) {
                loadPhoto(photoUri);
            }
            contactIndex = intent.getIntExtra("index", -1);
        }

        btnSave.setOnClickListener(v -> saveContact());

        btnSelectPhoto.setOnClickListener(v -> selectPhotoFromPicker());
    }

    private void saveContact() {
        HashMap<String, String> contact = new HashMap<>();
        contact.put("firstName", etFirstName.getText().toString());
        contact.put("lastName", etLastName.getText().toString());
        contact.put("phone", etPhone.getText().toString());
        contact.put("email", etEmail.getText().toString());
        contact.put("photoUri", photoUri != null ? photoUri.toString() : null);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("contact", contact);
        resultIntent.putExtra("index", contactIndex);

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void selectPhotoFromPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTO);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            photoUri = data.getData();
            if (photoUri != null) {
                loadPhoto(photoUri);
            }
        }
    }

    private void loadPhoto(Uri uri) {
        try {
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            }
            imgContactPhoto.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to open image", Toast.LENGTH_SHORT).show();
        }
    }
}
