// Объявление пакета приложения
package com.example.studybuddy;

// Импорт классов Android SDK
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

// Главная активность приложения
public class MainActivity extends AppCompatActivity {
    // Метод onCreate вызывается при создании активности
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Вызов родительского метода onCreate
        super.onCreate(savedInstanceState);
        // Установка макета активности из XML-ресурса
        setContentView(R.layout.activity_main);

        // Инициализация кнопки входа
        Button btnLogin = findViewById(R.id.btnLogin);
        // Инициализация кнопки поиска партнера
        Button btnFindPartner = findViewById(R.id.btnFindPartner);

        // Установка обработчика клика для кнопки входа
        btnLogin.setOnClickListener(v -> {
            // Создание интента для перехода к активности входа
            Intent intent = new Intent(this, LoginActivity.class);
            // Запуск активности входа
            startActivity(intent);
        });

        // Установка обработчика клика для кнопки поиска партнера
        btnFindPartner.setOnClickListener(v -> {
            // Создание интента для перехода к активности поиска партнера
            Intent intent = new Intent(this, FindPartnerActivity.class);
            // Запуск активности поиска партнера
            startActivity(intent);
        });
    }
}