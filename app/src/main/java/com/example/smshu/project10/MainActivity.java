package com.example.smshu.project10;

import java.io.*;
import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.text.method.ScrollingMovementMethod;
import android.view.*;
import android.graphics.*;
import android.widget.*;
import android.provider.*;
import com.microsoft.projectoxford.face.*;
import com.microsoft.projectoxford.face.contract.*;

public class MainActivity extends Activity {

    private final int PICK_IMAGE = 1;
    private ProgressDialog detectionProgressDialog;
    private final String apiEndpoint="https://southeastasia.api.cognitive.microsoft.com/face/v1.0";
    private final String subscriptionKey="b8f2b3b5456a47bebdb473bec9f37209";

    private final FaceServiceClient faceServiceClient = new FaceServiceRestClient(apiEndpoint,subscriptionKey);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(
                        intent, "Select Picture"), PICK_IMAGE);
            }
        });

        detectionProgressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), uri);
                ImageView imageView = findViewById(R.id.imageView1);
                imageView.setImageBitmap(bitmap);

                // Comment out for tutorial
                detectAndFrame(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void detectAndFrame(final Bitmap imageBitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        AsyncTask<InputStream,String,Face[]> detectTask = new AsyncTask<InputStream, String, Face[]>() {
            String exceptionMessage = "";

            @Override
            protected Face[] doInBackground(InputStream... params) {
                try
                {
                    publishProgress("Detecting......");
                    FaceServiceClient.FaceAttributeType[] requiredFace = new FaceServiceClient.FaceAttributeType[]{
                            FaceServiceClient.FaceAttributeType.Age,
                            FaceServiceClient.FaceAttributeType.Emotion
                    };
                    Face[] result = faceServiceClient.detect(
                            params[0],
                            true,
                            false,
                            requiredFace
                                    );
                    if(result==null)
                    {
                        publishProgress("Detection Finished Nothing Detected");
                        return null;
                    }
                    publishProgress(String.format("Detection Finished %d Face(s) detected", result.length));

                    return result;
                }
                catch (Exception e)
                {
                        exceptionMessage = String.format("Detection Failed :%s",e.getMessage());
                        return null;
                }
            }

            @Override
            protected void onPreExecute() {
                detectionProgressDialog.show();
            }

            @Override
            protected void onProgressUpdate(String... progress) {
                detectionProgressDialog.setMessage(progress[0]);
            }

            @Override
            protected void onPostExecute(Face[] result) {
                detectionProgressDialog.dismiss();

                if(!exceptionMessage.equals(""))
                {
                    showError(exceptionMessage);
                }
                if(result==null)
                {
                    return;
                }
                ImageView imageView = findViewById(R.id.imageView1);

                imageView.setImageBitmap(drawFaceRectanglesOnBitmap(imageBitmap, result));
                imageBitmap.recycle();
            }
        };

        detectTask.execute(inputStream);
    }

    private  void showError(String message)
    {
        new AlertDialog.Builder(this).setTitle("Error").setMessage(message).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        }).create().show();
    }

    private Bitmap drawFaceRectanglesOnBitmap(Bitmap originalBitmap, Face[] faces) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        if (faces != null) {
            for (Face face : faces) {
                FaceRectangle faceRectangle = face.faceRectangle;
                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);

            }
            int i=0;
            String [] arr = new String[4];
            String ar = new String();
            for( Face face: faces) {

                FaceAttribute atrributes = face.faceAttributes;

                double age = atrributes.age;
                Emotion emotion = atrributes.emotion;

                double anger = emotion.anger;
                double happy = emotion.happiness;
                double neutral = emotion.neutral;
                double sad = emotion.sadness;

                String s= compare(anger,happy,neutral,sad);

                Intent intent = new Intent(this,Music.class);
                intent.putExtra("EMOTION",s);
                startActivity(intent);
                String text =" age: "+ age +"\n anger: " + anger + "\n Happiness: " + happy + "\n neutral: " + neutral+"\n Sadness: "+ sad;

                arr[i]= text;
                i++;
            }
            int j;
            for( j=0;j<arr.length;j++)
            {
               ar=ar+"\n\n Id: "+ j +"\n"+arr[j];
            }
            TextView text2 = findViewById(R.id.textView);
            text2.setText(ar);
            text2.setMovementMethod(new ScrollingMovementMethod());


        }
        return bitmap;
    }

    private String compare(double anger, double happy, double neutral, double sad) {

         String max;
        if(anger>happy&&anger>neutral&&anger>sad)
        {
            max="anger";
        }
        else if(happy>anger&&happy>neutral&&happy>sad)
        {
            max="happy";
        }
        else if (neutral>anger&&neutral>happy&&neutral>sad)
        {
            max="neutral";
        }
        else
        {
            max="sad";
        }

        return max;
    }

}
