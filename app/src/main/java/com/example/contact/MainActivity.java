package com.example.contact;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_CONTACT = 1;

    private ArrayList<HashMap<String, String>> contactList;
    private LinearLayout contactContainer;
    private int selectedContactIndex = -1;
    private View selectedContactView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<>();
        contactContainer = findViewById(R.id.contactContainer);

        Button btnAddContact = findViewById(R.id.btnAddContact);
        Button btnEditContact = findViewById(R.id.btnEditContact);
        Button btnDeleteContact = findViewById(R.id.btnDeleteContact);

        btnAddContact.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ContactActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_CONTACT);
        });

        btnEditContact.setOnClickListener(v -> {
            if (selectedContactIndex != -1) {
                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                intent.putExtra("contact", contactList.get(selectedContactIndex));
                intent.putExtra("index", selectedContactIndex);
                startActivityForResult(intent, REQUEST_CODE_ADD_CONTACT);
            } else {
                Toast.makeText(this, "No contact selected to edit", Toast.LENGTH_SHORT).show();
            }
        });

        btnDeleteContact.setOnClickListener(v -> {
            if (selectedContactIndex != -1) {
                contactList.remove(selectedContactIndex);
                contactContainer.removeViewAt(selectedContactIndex);
                resetSelection();
                Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No contact selected to delete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_CONTACT && resultCode == Activity.RESULT_OK && data != null) {
            HashMap<String, String> contact = (HashMap<String, String>) data.getSerializableExtra("contact");
            int index = data.getIntExtra("index", -1);

            if (index == -1) { // Adding a new contact
                contactList.add(contact);
                addContactToView(contact);
            } else { // Editing an existing contact
                contactList.set(index, contact);
                updateContactInView(index, contact);
            }
            resetSelection();
        }
    }

    private void addContactToView(HashMap<String, String> contact) {
        View contactView = getLayoutInflater().inflate(R.layout.contact_item, contactContainer, false);

        TextView tvName = contactView.findViewById(R.id.tvName);
        TextView tvPhone = contactView.findViewById(R.id.tvPhone);
        TextView tvEmail = contactView.findViewById(R.id.tvEmail);
        ImageView imgContactPhoto = contactView.findViewById(R.id.imgContactPhoto);

        tvName.setText(contact.get("firstName") + " " + contact.get("lastName"));
        tvPhone.setText(contact.get("phone"));
        tvEmail.setText(contact.get("email"));

        // Load contact photo if available
        String photoUriString = contact.get("photoUri");
        if (photoUriString != null) {
            Uri photoUri = Uri.parse(photoUriString);
            try (InputStream inputStream = getContentResolver().openInputStream(photoUri)) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgContactPhoto.setImageBitmap(bitmap);
            } catch (Exception e) {
                imgContactPhoto.setImageResource(R.drawable.def); // Default image
            }
        } else {
            imgContactPhoto.setImageResource(R.drawable.def); // Default image
        }

        // Set up click listener for selecting the contact
        contactView.setOnClickListener(v -> selectContact(contactContainer.indexOfChild(contactView), contactView));

        contactContainer.addView(contactView);
    }

    private void updateContactInView(int index, HashMap<String, String> contact) {
        View contactView = contactContainer.getChildAt(index);

        TextView tvName = contactView.findViewById(R.id.tvName);
        TextView tvPhone = contactView.findViewById(R.id.tvPhone);
        TextView tvEmail = contactView.findViewById(R.id.tvEmail);
        ImageView imgContactPhoto = contactView.findViewById(R.id.imgContactPhoto);

        tvName.setText(contact.get("firstName") + " " + contact.get("lastName"));
        tvPhone.setText(contact.get("phone"));
        tvEmail.setText(contact.get("email"));

        // Update contact photo if available
        String photoUriString = contact.get("photoUri");
        if (photoUriString != null) {
            Uri photoUri = Uri.parse(photoUriString);
            try (InputStream inputStream = getContentResolver().openInputStream(photoUri)) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgContactPhoto.setImageBitmap(bitmap);
            } catch (Exception e) {
                imgContactPhoto.setImageResource(R.drawable.def); // Default image
            }
        } else {
            imgContactPhoto.setImageResource(R.drawable.def); // Default image
        }
    }

    private void selectContact(int index, View contactView) {
        // Reset the background color of the previously selected contact
        if (selectedContactView != null) {
            selectedContactView.setBackgroundColor(Color.TRANSPARENT);
        }

        // Update selected contact and highlight it
        selectedContactIndex = index;
        selectedContactView = contactView;
        selectedContactView.setBackgroundColor(Color.LTGRAY);

        // Enable Edit and Delete buttons
        findViewById(R.id.btnEditContact).setEnabled(true);
        findViewById(R.id.btnDeleteContact).setEnabled(true);
    }

    private void resetSelection() {
        // Reset the selection state
        if (selectedContactView != null) {
            selectedContactView.setBackgroundColor(Color.TRANSPARENT);
        }
        selectedContactIndex = -1;
        selectedContactView = null;

        // Disable Edit and Delete buttons
        findViewById(R.id.btnEditContact).setEnabled(false);
        findViewById(R.id.btnDeleteContact).setEnabled(false);
    }
}
