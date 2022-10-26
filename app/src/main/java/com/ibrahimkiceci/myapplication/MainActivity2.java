package com.ibrahimkiceci.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.ibrahimkiceci.myapplication.databinding.ActivityMain2Binding;
import com.ibrahimkiceci.myapplication.databinding.ActivityMainBinding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class MainActivity2 extends AppCompatActivity {

    private ActivityMain2Binding binding;
    ActivityResultLauncher<Intent> activityResultLauncher; // gallerye gitmek icin
    ActivityResultLauncher<String> permissionLauncher; //izni istemek icin
    Bitmap selectedImage;
    SQLiteDatabase database;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();
        database = this.openOrCreateDatabase("Notes", MODE_PRIVATE, null);

        // simdi burada menuden yeni bir add mi yapacak yoksa eskiye mi tiklayacak onu kontrol ediyoruz;

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if (info.equals("new")){
            binding.editText1.setText("");
            binding.editText2.setText("");
            binding.editText3.setText("");
            binding.button.setVisibility(View.VISIBLE);

            binding.imageView.setImageResource(R.drawable.add);


        }else{

            int noteId = intent.getIntExtra("noteId", 0);
            binding.button.setVisibility(View.INVISIBLE);

            try {
                Cursor cursor = database.rawQuery("SELECT * FROM notes WHERE id = ?", new String[] {String.valueOf(noteId)});
                int noteNameIx = cursor.getColumnIndex("notename");
                int noteIx = cursor.getColumnIndex("note");
                int yearIx = cursor.getColumnIndex("year");
                int imageIx = cursor.getColumnIndex("image");

                while (cursor.moveToNext()){

                    binding.editText1.setText(cursor.getString(noteNameIx));
                    binding.editText2.setText(cursor.getString(noteIx));
                    binding.editText3.setText(cursor.getString(yearIx));

                    byte[] bytes =cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    binding.imageView.setImageBitmap(bitmap);

                }

                cursor.close();



            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }


    public void save(View view){

        String name = binding.editText1.getText().toString();
        String note = binding.editText2.getText().toString();
        String year = binding.editText3.getText().toString();

        // Oncelikle selected image'i kucultmen lazim cunku sqlite buyuk bir sekilde veri kaydetmene cok izin vermez app cokebilir.

        //Bunu diger uygulamlarada da yapabilirsin simdi bu kucultme icin  yeni bir metod olusturalim

        Bitmap smallImage = makeSmallerImage(selectedImage,300);

        // Sqlt icerisine koymak icin bytelara cevirmen gerek

        ByteArrayOutputStream byteArrayOutputStream =  new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        try {


            database.execSQL("CREATE TABLE IF NOT EXISTS notes(id INTEGER PRIMARY KEY, notename VARCHAR, note VARCHAR, year VARCHAR,image BLOB)");
            String sqlString  = "INSERT INTO notes (notename, note, year, image ) VALUES(?, ?, ?, ?)";

            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindString(1, name);
            sqLiteStatement.bindString(2, note);
            sqLiteStatement.bindString(3, year);
            sqLiteStatement.bindBlob(4,byteArray);
            sqLiteStatement.execute();

        }catch (Exception e){
            e.printStackTrace();
        }

        Intent intent = new Intent(MainActivity2.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


    }

    public Bitmap makeSmallerImage(Bitmap image, int maxSize){
        // Burada kullanici yatay da tutup foto cekebilir yada dikey de tutarak foto cekebilir bu nedenle kuculturken iyi bir sekilde kucultmesi icin sunlari yapmak gerekir,

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float)height;

        if (bitmapRatio > 1) { // eger birden buyukse bu bir dikdortgen

            //landscape image

            width = maxSize;
            height = (int)(width/bitmapRatio);


        }else {

            //portrait image

            height = maxSize;
            width = (int)(height * bitmapRatio);


        }


        return image.createScaledBitmap(image,width,height,true);


    }


    public void selectImage(View view){

        // Izinleri kontrol edelim, izini her defasinda degil bir kere isteyecegiz ve android bunu kayit edip izin alinip alinmadigini anlayacak;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // Context Compat API 19 oncesi icin, yani yaklasik 10-12 yil once o eski telefonlar icin izin gerkmiyordu.
            // Yukaridaki if state de sunu soyledik eger izin verilmemisse, izin isteyecegiz,
            // request permission
            // Yani bu tum if statementda daha once izin verilmis mi verilmemis mi onu anlamaya calisiyoruz
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                // android neden ulasmak isedigini acikliyor...

                Snackbar.make(view, "Permisson needed for Gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Tiklaninca izin istiyecegiz

                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                    }
                }).show(); // Kullanici herhangi bir butona basana kadar gosterir


            }else{

                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

            }

        }else {

            // zaten izin verilmisse gallery'e gidecegiz

            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // Gallry'e gidicem ordan bir picture alicam
            // Galeriden bir sey secilince ne yapacagiz. Bunun icin Activity Result Launcher kullaniyoruz.
            // (Eskiden startActivityForResult kullaniliyordu ama artik Activity Result Launcher kullaniliyor)
            activityResultLauncher.launch(intentToGallery);

        }

    }


    private void registerLauncher(){

        // Tum register etme islemlerini yani activity launcherin neler yapacagini burada tanimlayacagim,
        // Activity Launcher ise galleriye gidience ne olacak onu yaziyoruz

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == RESULT_OK){ // Kullanici bir sey secti demek
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null){

                      Uri imageData =  intentFromResult.getData();
                      //binding.imageView.setImageURI(imageData);
                      //resmi bitmape de cevirmen gerek, cunku veritabanina kayit edecegiz.
                      // bitmap gorselleri icersinde tuttugumuz sinif.

                        try {

                            if(Build.VERSION.SDK_INT >= 28){

                                ImageDecoder.Source source = ImageDecoder.createSource(MainActivity2.this.getContentResolver(),imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage); // Bu sadece yeni telefonlarda calisiyor.


                            }else {

                                selectedImage = MediaStore.Images.Media.getBitmap(MainActivity2.this.getContentResolver(), imageData);
                                binding.imageView.setImageBitmap(selectedImage);


                            }



                        }catch (Exception e){

                            e.printStackTrace();


                        }


                    }




                }

            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {

                if (result){

                    //permission granted

                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }else {
                    //permission denied

                    Toast.makeText(MainActivity2.this, "Permission needed", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}