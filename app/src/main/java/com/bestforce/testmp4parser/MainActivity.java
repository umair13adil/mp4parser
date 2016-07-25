package com.bestforce.testmp4parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.bestforce.utils.Utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;

public class MainActivity extends Activity {

    Button btn_pick, btn_merge, btn_append, btn_clear;
    TextView txt_output;

    Context context;
    int REQUEST_CHOOSER = 1234;

    List<String> filePaths;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bestforce.testmp4parser.R.layout.activity_main);
        this.context = MainActivity.this;

        filePaths = new ArrayList<String>();

        btn_append = (Button) findViewById(com.bestforce.testmp4parser.R.id.btn_append);
        btn_merge = (Button) findViewById(com.bestforce.testmp4parser.R.id.btn_merge);
        btn_pick = (Button) findViewById(com.bestforce.testmp4parser.R.id.btn_pick);
        btn_clear = (Button) findViewById(com.bestforce.testmp4parser.R.id.btn_clear);
        txt_output = (TextView) findViewById(com.bestforce.testmp4parser.R.id.textView1);

        btn_append.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                for (int i = 0; i < filePaths.size(); i++) {
                    if (Utils.getFileExtension(new File(filePaths.get(i))).equals("aac")) {
                        Toast.makeText(context, "Select only Mp4 files!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (filePaths.size() >= 2) {
                    AppendExample appendExample = new AppendExample(context, filePaths);
                    appendExample.append();
                } else {
                    Toast.makeText(context, "Select more than 1 file!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_pick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion >= Build.VERSION_CODES.KITKAT) {
                    Intent getContentIntent = FileUtils.createGetAudioIntent();
                    Intent intent = Intent.createChooser(getContentIntent, "Select a file");
                    try {
                        startActivityForResult(intent, REQUEST_CHOOSER);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();

                    }
                } else {
                    Intent intent_upload = new Intent();
                    intent_upload.setType("audio");
                    intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                    try {
                        startActivityForResult(intent_upload, REQUEST_CHOOSER);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        Intent getContentIntent = FileUtils.createGetAudioIntent();
                        Intent intent = Intent.createChooser(getContentIntent, "Select a file");
                        try {
                            startActivityForResult(intent, REQUEST_CHOOSER);
                        } catch (ActivityNotFoundException e1) {
                            e1.printStackTrace();

                        }
                    }
                }
            }
        });

        btn_merge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (filePaths.size() >= 2) {
                    MergeExample mergeExample = new MergeExample(context, filePaths);
                    mergeExample.merge();
                } else {
                    Toast.makeText(context, "Select more than 1 file!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btn_clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                txt_output.setText("");
                filePaths.clear();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHOOSER && resultCode == RESULT_OK) {
            final Uri uri = data.getData();
            String path1 = FileUtils.getPath(context, uri);

            File file_check = new File(path1);
            if (Utils.isSupportedFormat(file_check)) {
                filePaths.add(path1);
                txt_output.append(path1.toString() + "\n");
                Toast.makeText(context, "File Added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "The file you selected is not supported. Please try another file. ", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
