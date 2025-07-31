package com.example.hesalaty;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // ✅ إظهار اسم التطبيق وزر الرجوع في الشريط العلوي
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("حصّالتي");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // ✅ تحميل أول شاشة (الرئيسية)
        loadFragment(new HomeFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_exchange) {
                selectedFragment = new ExchangeFragment();
            } else if (id == R.id.nav_reports) {
                selectedFragment = new ReportFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            return loadFragment(selectedFragment);
        });
    }

    // ✅ التعامل مع زر الرجوع في الشريط العلوي
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // ✅ التعامل مع زر الرجوع الفيزيائي
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    // ✅ تحميل الفراجمنت مع السجل
    public boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            if (fragment instanceof HomeFragment) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
            return true;
        }
        return false;
    }
}
