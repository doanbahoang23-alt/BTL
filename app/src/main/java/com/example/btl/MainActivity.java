package com.example.btl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    public BottomNavigationView bottomNav;

    private final Fragment homeFragment = new HomeFragment();
    private final Fragment docFragment = new DocumentsFragment();
    private final Fragment aiFragment = new AiFragment();
    private final Fragment manageFragment = new ManageFragment();

    // Đã xóa khai báo quizFragment ở đây
    private Fragment activeFragment = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        if (!sharedPref.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        bottomNav = findViewById(R.id.bottom_navigation);

        Menu menu = bottomNav.getMenu();
        menu.findItem(R.id.nav_manage).setVisible(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    // Đã xóa đoạn add quizFragment ẩn
                    .add(R.id.fragment_container, manageFragment, "manage").hide(manageFragment)
                    .add(R.id.fragment_container, aiFragment, "ai").hide(aiFragment)
                    .add(R.id.fragment_container, docFragment, "doc").hide(docFragment)
                    .add(R.id.fragment_container, homeFragment, "home").commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = homeFragment;
            } else if (id == R.id.nav_doc) {
                selectedFragment = docFragment;
            } else if (id == R.id.nav_ai) {
                selectedFragment = aiFragment;
            } else if (id == R.id.nav_manage) {
                selectedFragment = manageFragment;
            }

            if (selectedFragment != null && selectedFragment != activeFragment) {
                getSupportFragmentManager().beginTransaction()
                        .hide(activeFragment)
                        .show(selectedFragment)
                        .commit();
                activeFragment = selectedFragment;
                return true;
            }
            return false;
        });
    }

    public void returnToHome() {
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}