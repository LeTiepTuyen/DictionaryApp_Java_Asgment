package com.example.dictionaryapp_v2;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dictionaryapp_v2.databinding.ActivityDefinitionBinding;

public class DefinitionActivity extends AppCompatActivity {
    private WebView webView;
    private ActivityDefinitionBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definition);


        // Bind views
        binding = ActivityDefinitionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        String word = intent.getStringExtra("word");

        // Khởi tạo đối tượng DatabaseAccess và truy vấn định nghĩa của từ
        DatabaseAccess dbAccess = DatabaseAccess.getInstance(this);
        dbAccess.open();
        String definition = dbAccess.getDefinition(word);

        // Load nội dung HTML lên WebView
        webView = binding.webView;
        webView.loadDataWithBaseURL(null, definition, "text/html", "UTF-8", null);
    }



    @Override
    protected void onResume() {
        super.onResume();
        // Mở lại cơ sở dữ liệu khi Activity được khôi phục
        DatabaseAccess.getInstance(this).open();
    }
}