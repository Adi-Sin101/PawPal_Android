package com.example.pawpal_18nov;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class RecyclerContactAdapter extends RecyclerView.Adapter<RecyclerContactAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ContactModel> arrContacts;

    public RecyclerContactAdapter(Context context, ArrayList<ContactModel> arrContacts) {
        this.context = context;
        this.arrContacts = arrContacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactModel contact = arrContacts.get(position);
        holder.txtName.setText(contact.getName());
        holder.txtNumber.setText(contact.getPhone());

        // Dynamically set image based on gender
        if ("male".equalsIgnoreCase(contact.getGender())) {
            holder.imgContact.setImageResource(R.drawable.maledoc);  // Add male icon in drawable
        } else if ("female".equalsIgnoreCase(contact.getGender())) {
            holder.imgContact.setImageResource(R.drawable.felamedoc);  // Add female icon in drawable
        } else {
            holder.imgContact.setImageResource(R.drawable.ic_launcher_background);  // Default icon if no gender specified
        }

        // Enable calling the number
        holder.callIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + contact.getPhone()));
            context.startActivity(intent);
        });

        // Enable copying name or number
        holder.itemView.setOnLongClickListener(v -> {
            String textToCopy = "Name: " + contact.getName() + "\nPhone: " + contact.getPhone();
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Contact Details", textToCopy);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            return true; // Indicate the long click is handled
        });
    }

    @Override
    public int getItemCount() {
        return arrContacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtNumber;
        ImageView imgContact, callIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtNumber = itemView.findViewById(R.id.txtNumber);
            imgContact = itemView.findViewById(R.id.imgContact);
            callIcon = itemView.findViewById(R.id.callIcon);
        }
    }
}


