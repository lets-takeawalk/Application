package org.tensorflow.lite.examples.detection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.System.exit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ImageChoiceActivity extends AppCompatActivity {
    ArrayList array = new ArrayList(); // 그 이미지 어레이
    ArrayList xyarray = new ArrayList<String>();//좌표 어레이
    ArrayList<String> narray = new ArrayList<String>(); // 이름 이미지 들어간 어레이
    BitmapFactory.Options options = new BitmapFactory.Options();
    ImageView imageView2;
    EditText keditText;
    EditText eeditText;
    int touchCnt = 0;
    int arrayCnt = 0;
    float mx = 0;
    float my = 0;
    float mmx = 0;
    float mmy = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_choice);

//        Uri uriFromImageFile = Uri.fromFile((File) imgList.get(0));
//        intent2.setDataAndType(uriFromImageFile, "image/*");
        Intent intent = getIntent();
//        array = intent.getExtras().getParcelableArrayList("imglist");
        narray = intent.getExtras().getStringArrayList("nameList");

//        Touchimg();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Touchimg();
        }
    }

    private void Touchimg() {
        if (narray.size() == arrayCnt){
            jsonCreate();
            System.exit(0);
        }

        imageView2 = findViewById(R.id.imageView2);
        keditText = findViewById(R.id.editText2);
        eeditText = findViewById(R.id.editText);
        // array 가져오기
//        File img = (File) array.get(arrayCnt);
        //imageview에 저장하는과정..
//        Matrix matrix = new Matrix();
//        matrix.postRotate(90);
//        Bitmap myBitmap = (Bitmap) array.get(arrayCnt);
////        Bitmap myBitmap = BitmapFactory.decodeFile(img.getAbsolutePath());
//        Bitmap resize = Bitmap.createScaledBitmap(myBitmap,416,416,true);
//        Bitmap rotatedBitmap = Bitmap.createBitmap(resize,0,0,resize.getWidth(),resize.getHeight(),matrix,true); // 416,416 and rotate90
//        System.out.println(resize.getWidth()+" " +resize.getHeight());
//        saveBitmapToJpg(resize,"imghyn");
        //416 416으로 저장
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://let-s-take-a-walk-76161.appspot.com");
        StorageReference storageReference = storage.getReference(narray.get(arrayCnt));
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ImageChoiceActivity.this).load(uri).into(imageView2);
            }
        });

        configImgview();

    }

    private void configImgview(){
        mx = imageView2.getX();
        my = imageView2.getY();
        mmx = imageView2.getWidth();
        mmy = imageView2.getHeight();

        imageView2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        float X = event.getX() *(416/(mmx-mx));
                        float Y = event.getY() *(416/(mmy-my));
                        String XY = Float.toString(X) + " " + Float.toString(Y);
                        xyarray.add(XY);
                        System.out.println(X +" "+ Y);
                        touchCnt ++;
                        if(touchCnt==2){
                            arrayCnt++;
                            touchCnt = 0;
                            Touchimg();
                        }
                }

                return true;
            }
        });
        //저장끝
        //바깥으로 내보내기 이미지
//        FirebaseStorage storage = FirebaseStorage.getInstance("gs://let-s-take-a-walk-76161.appspot.com");
//
//        StorageReference storageRef = storage.getReference();
//        StorageReference mountainsRef = storageRef.child(narray.get(arrayCnt));
//        StorageReference mountainImagesRef = storageRef.child("images/" + narray.get(arrayCnt));
//        System.out.println(narray.get(arrayCnt));
//        //기본셋팅
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
//        byte[] data = baos.toByteArray();
////
//        UploadTask uploadTask = mountainsRef.putBytes(data);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//            }
//        });
    }
    public void jsonCreate(){
        JSONObject obj = new JSONObject();
        try{
            JSONArray jArray = new JSONArray(); //pcis에서 배열로 쓸 예정
            JSONArray jArray2 = new JSONArray(); // building을 만들기위해 쓸것
            //안써버렸네
            for (int i = 0; i<narray.size(); i++){
                int coornum = 2*i; // 좌표 두개씩
                String xy1 = (String) xyarray.get(coornum);
                String xy2 = (String) xyarray.get(coornum+1);
                //먼저 좌표 불러오기 top, bottom
                String[] t = xy1.split(" ");
                String[] b = xy2.split(" ");
                // 쪼개기 띄어쓰기로 되어있어서 왼쪽이 x 오른쪽이 y
                String x_t = t[0];
                String y_t = t[1];
                //xy 위쪽
                String x_b = b[0];
                String y_b = b[1];
                //xy 아래쪽
                JSONObject sObject =  new JSONObject(); // 배열 내에 들어갈 json
                sObject.put("imgId", Integer.toString(i));
                sObject.put("imgURL", narray.get(i));
                sObject.put("angle","");
                sObject.put("x_t",x_t);
                sObject.put("y_t",y_t);
                sObject.put("x_b",x_b);
                sObject.put("y_b",y_b);
                jArray.put(sObject);
            }
            //img array 배열생성 완료
            JSONObject bObj = new JSONObject();
            bObj.put("buildingNameKor", keditText.getText().toString());
            bObj.put("buildingNameEng",eeditText.getText().toString());
            bObj.put("picCount", Integer.toString(narray.size()));
            //building obj 생성 완료
            obj.put("buildingInfo",bObj); // 빌딩 Object 박아버림
            obj.put("pics",jArray); // 배열을 박아버림
            System.out.println(obj.toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}