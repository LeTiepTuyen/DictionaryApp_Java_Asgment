package com.example.dictionaryapp_v2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.widget.TextView;

//import com.example.dictionaryapp_V2.databinding.ActivityMainBinding;
import com.example.dictionaryapp_v2.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity  implements  NavigationView.OnNavigationItemSelectedListener{
    private ActivityMainBinding binding;
    private AutoCompleteTextView searchBar;
    private DrawerLayout drawerLayout;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);


        drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        List<String> anhViet = databaseAccess.getWords();

        listView = binding.listView;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.items_layout, R.id.word, databaseAccess.getWords()) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.items_layout, parent, false);
                }

                // Lấy từng từ từ danh sách và hiển thị trên TextView
                String word = getItem(position);
                TextView wordTextView = convertView.findViewById(R.id.word);
                wordTextView.setText(word);
                String definition = databaseAccess.getDefinition(word);

                // Chỉ lấy nội dung của thẻ <li> đầu tiên trong thẻ danh sách <ul>
                String firstListItem = extractFirstListItemFromHtml(definition);
                TextView definitionTextView = convertView.findViewById(R.id.definition);
                definitionTextView.setText(firstListItem);

                return convertView;
            }
        };
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        // Thêm sự kiện click cho mỗi item trong ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy từ được chọn từ ListView

//                databaseAccess.open();
                String selectedWord = (String) parent.getItemAtPosition(position);

                // Tạo Intent để chuyển sang DefinitionActivity
                Intent intent = new Intent(MainActivity.this, DefinitionActivity.class);
                // Gửi từ được chọn qua Intent
                intent.putExtra("word", selectedWord);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }



    // Hàm để trích xuất nội dung của thẻ <li> đầu tiên từ chuỗi HTML
    private String extractFirstListItemFromHtml(String htmlContent) {

        // Parse chuỗi HTML thành một đối tượng Document
        Document doc = Jsoup.parse(htmlContent);

        // Tìm tất cả các thẻ <ul>
        Elements ulElements = doc.select("ul");

        // Kiểm tra xem có thẻ <ul> nào không
        if (ulElements.size() > 0) {
            // Lấy thẻ <ul> đầu tiên
            Element ulElement = ulElements.first();

            // Lấy tất cả các thẻ <li> bên trong thẻ <ul> đầu tiên
            Elements liElements = ulElement.select("li");

            // Kiểm tra xem có thẻ <li> nào không
            if (liElements.size() > 0) {
                // Lấy nội dung của thẻ <li> đầu tiên
                String firstListItem = liElements.first().text();
                // Log kết quả
                Log.d("FIRST_LIST_ITEM", firstListItem);
                return firstListItem;
            }
        }

        // Trả về chuỗi rỗng nếu không tìm thấy thẻ <ul> hoặc <li>
        return "";
    }

    // Method to perform search based on keyword

    @Override
    protected void onPause() {
        super.onPause();
        // Đóng cơ sở dữ liệu khi Activity bị tạm dừng
        DatabaseAccess.getInstance(this).close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Mở lại cơ sở dữ liệu khi Activity được khôi phục
        DatabaseAccess.getInstance(this).open();
    }
}