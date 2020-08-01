package heexa.myproject.myupload;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class Upload extends Fragment {
    ImageView imageView;
    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageReference;
    ProgressBar progressBar;
    Button btnupload,btnchoose;
    Uri filePath;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upload_fragment, container, false);

        auth = FirebaseAuth.getInstance();
        imageView =  view.findViewById(R.id.myimage);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        btnchoose = (Button) view.findViewById(R.id.btn_choose);
        btnupload = (Button) view.findViewById(R.id.btn_upload);
        storage =FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        progressBar.setVisibility(View.GONE);

        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.GONE);
                uploadimage();
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        btnchoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.GONE);
                chooseimage();
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private void chooseimage() {

        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction((Intent.ACTION_GET_CONTENT));
        startActivityForResult(Intent.createChooser(intent,"Select an image"),1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode ==  -1 && data!=null && data.getData()!=null) {
            filePath=data.getData();
            try {
               Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadimage() {
    if (filePath!=null){
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...!");
        progressDialog.show();
        StorageReference reference = storageReference.child("images/"+ UUID.randomUUID().toString());
        reference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("uploaded..."+(int)progress+"%");
            }
        });
    }
    }
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}