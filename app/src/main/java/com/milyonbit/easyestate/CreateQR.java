package com.milyonbit.easyestate;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * Created by FerdiKT on 11/12/15.
 */
public class CreateQR extends AppCompatActivity {
    Button save;
    ImageView imageView;
    String name;

    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_created);
        save = (Button) findViewById(R.id.save);
        imageView = (ImageView) findViewById(R.id.show_qr);
        Intent i = getIntent();
        String data = i.getStringExtra("Info");
        try {
            Bitmap bitmap = encodeAsBitmap(data);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CreateQR.this);
                builder.setTitle("Dosya Adı");


                final EditText input = new EditText(CreateQR.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
                builder.setView(input);


                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        name = input.getText().toString();
                        imageView.buildDrawingCache();
                        Bitmap bm = imageView.getDrawingCache();
                        OutputStream fOut = null;
                        Uri outputFileUri;

                        try {
                            File root = new File(Environment.getExternalStorageDirectory()
                                    + File.separator + "Pictures" + File.separator + "Easy Estate");
                            root.mkdirs();
                            File sdImageMainDirectory = new File(root, name + ".jpg");
                            outputFileUri = Uri.fromFile(sdImageMainDirectory);
                            fOut = new FileOutputStream(sdImageMainDirectory);
                        } catch (Exception e) {
                            Toast.makeText(CreateQR.this, "Bir hata oldu. " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("KAYDET",e.getMessage());
                        }

                        try {
                            bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);

                            Toast.makeText(CreateQR.this, "Kaydedildi", Toast.LENGTH_SHORT).show();
                            fOut.flush();
                            fOut.close();
                        } catch (Exception e) {
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 200, 200, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 200, 0, 0, w, h);
        return bitmap;
    }
}
